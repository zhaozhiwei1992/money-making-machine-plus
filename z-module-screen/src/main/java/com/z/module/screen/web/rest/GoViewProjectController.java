package com.z.module.screen.web.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.screen.config.ScreenProperties;
import com.z.module.screen.domain.GoViewFileDO;
import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.repository.GoViewFileRepository;
import com.z.module.screen.repository.GoViewProjectRepository;
import com.z.module.screen.service.GoViewProjectService;
import com.z.module.screen.web.mapper.GoViewProjectConvert;
import com.z.module.screen.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "GoView 项目")
@RestController
@RequestMapping("/goview/project")
@Validated
@Transactional(rollbackFor = Exception.class)
public class GoViewProjectController {

    private final GoViewProjectService goViewProjectService;

    private final GoViewProjectConvert goViewProjectConvert;

    private final ScreenProperties screenProperties;

    private final GoViewFileRepository goViewFileRepository;

    private final GoViewProjectRepository goViewProjectRepository;

    public GoViewProjectController(GoViewProjectService goViewProjectService,
                                   GoViewProjectConvert goViewProjectConvert, ScreenProperties screenProperties,
                                   GoViewFileRepository goViewFileRepository,
                                   GoViewProjectRepository goViewProjectRepository) {
        this.goViewProjectService = goViewProjectService;
        this.goViewProjectConvert = goViewProjectConvert;
        this.screenProperties = screenProperties;
        this.goViewFileRepository = goViewFileRepository;
        this.goViewProjectRepository = goViewProjectRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    public ResponseEntity<ResponseData<Long>> createProject(@Valid @RequestBody GoViewProjectCreateReqVO createReqVO) {
        return ResponseData.ok(goViewProjectService.createProject(createReqVO));
    }

    @PutMapping("/edit")
    @Operation(summary = "更新项目")
    public ResponseEntity<ResponseData<Boolean>> updateProject(@Valid @RequestBody GoViewProjectUpdateReqVO updateReqVO) {
        goViewProjectService.updateProject(updateReqVO);
        return ResponseData.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 GoView 项目")
    @Parameter(name = "ids", description = "编号", required = true, example = "1024")
    public ResponseEntity<ResponseData<Boolean>> deleteProject(@RequestParam("ids") Long id) {
        goViewProjectService.deleteProject(id);
        return ResponseData.ok(true);
    }

    @Operation(summary = "保存项目数据")
    @PostMapping("/save/data")
    public ResponseEntity<ResponseData<Object>> saveData(@RequestBody GoViewProjectUpdateReqVO data) {

        final Optional<GoViewProjectDO> byId = goViewProjectRepository.findById(data.getProjectId());
        if (!byId.isPresent()) {
            return ResponseData.fail("找不到该项目: " + data.getProjectId());
        }
        // 查询项目读应的数据
        final GoViewProjectDO goViewProjectDO = byId.get();
        goViewProjectDO.setContent(data.getContent());
        return ResponseData.ok("保存成功");
    }

    @GetMapping("/getData")
    @Operation(summary = "获得项目")
    @Parameter(name = "projectId", description = "编号", required = true, example = "1024")
    public ResponseEntity<ResponseData<GoViewProjectRespVO>> getProject(@RequestParam("projectId") Long id) {
        GoViewProjectDO project = goViewProjectService.getProject(id);
        return ResponseData.ok(goViewProjectConvert.convert(project));
    }

    @GetMapping("/list")
    @Operation(summary = "获得我的项目分页")
    public ResponseEntity<ResponseData<List<GoViewProjectRespVO>>> getMyProjectPage(@Valid PageParam pageVO) {
        final Page<GoViewProjectDO> myProjectPage = goViewProjectService.getMyProjectPage(pageVO);
        final ResponseData<List<GoViewProjectRespVO>> tResponseData = new ResponseData<>();
        tResponseData.setCode("200");
        tResponseData.setMsg("请求成功");
        tResponseData.setData(goViewProjectConvert.convert(myProjectPage.getContent()));
        tResponseData.setCount(myProjectPage.getNumberOfElements());
        tResponseData.setTimestamps(new Date());
        return ResponseEntity.ok().body(tResponseData);
    }

    /**
     * 上传文件
     *
     * @param object 文件流对象
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseData<GoViewFileVO>> upload(@RequestBody MultipartFile object) throws IOException {
        String fileName = object.getOriginalFilename();
        //默认文件格式
        String mediaKey = UUID.randomUUID().toString().replace("-", "");
        Long filesize = object.getSize();
        String suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        //文件名字
        String fileSuffixName = mediaKey + suffixName;

        GoViewFileDO viewFileDO = new GoViewFileDO();
        viewFileDO.setFileName(fileSuffixName);
        viewFileDO.setFileSize(Integer.parseInt(String.valueOf(filesize)));
        viewFileDO.setFileSuffix(suffixName);
        String filepath = DateUtil.formatDate(new Date());
        viewFileDO.setRelativePath(filepath);
        String uploadType = screenProperties.getGoView().getUploadType();
        viewFileDO.setVirtualKey(uploadType);
        final String uploadPath = screenProperties.getGoView().getPath().getUpload();
        viewFileDO.setAbsolutePath(uploadPath);
        goViewFileRepository.save(viewFileDO);

        File desc = getAbsoluteFile(uploadPath + File.separator + filepath, fileSuffixName);
        object.transferTo(desc);
        GoViewFileVO goViewFileVO = BeanUtil.copyProperties(viewFileDO, GoViewFileVO.class);
        goViewFileVO.setFileurl(screenProperties.getGoView().getHttpUrl() + viewFileDO.getVirtualKey() + "/" + viewFileDO.getRelativePath() + "/" + viewFileDO.getFileName());
        return ResponseData.ok(goViewFileVO);
    }

    public static File getAbsoluteFile(String uploadDir, String filename) throws IOException {
        File desc = new File(uploadDir + File.separator + filename);

        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists()) {
            desc.createNewFile();
        }
        return desc;
    }

}
