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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;

import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Tag(name = "GoView 项目")
@RestController
@RequestMapping("/goview/project")
@Validated
@Transactional(rollbackFor = Exception.class)
public class GoViewProjectResource {

    private final GoViewProjectService goViewProjectService;

    private final GoViewProjectConvert goViewProjectConvert;

    private final ScreenProperties screenProperties;

    private final GoViewFileRepository goViewFileRepository;

    private final GoViewProjectRepository goViewProjectRepository;

    public GoViewProjectResource(GoViewProjectService goViewProjectService,
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
    public Long createProject(@Valid @RequestBody GoViewProjectCreateReqVO createReqVO) {
        return goViewProjectService.createProject(createReqVO);
    }

    @PostMapping("/edit")
    @Operation(summary = "更新项目")
    public boolean updateProject(@Valid @RequestBody GoViewProjectUpdateReqVO updateReqVO) {
        goViewProjectService.updateProject(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 GoView 项目")
    @Parameter(name = "ids", description = "编号", required = true, example = "1024")
    public boolean deleteProject(@RequestParam("ids") Long id) {
        goViewProjectService.deleteProject(id);
        return true;
    }

    @Operation(summary = "项目重命名")
    @PostMapping("/rename")
    public boolean rename(@RequestBody GoViewProjectUpdateReqVO goViewProjectUpdateReqVO) {
        final Optional<GoViewProjectDO> byId = goViewProjectRepository.findById(goViewProjectUpdateReqVO.getId());
        if (byId.isPresent()) {
            final GoViewProjectDO goViewProjectDO = byId.get();
            goViewProjectDO.setName(goViewProjectUpdateReqVO.getProjectName());
            return true;
        }
        throw new RuntimeException("没有找到该项目");
    }

    //发布/取消项目状态
    @PutMapping("/publish")
    public boolean updateVisible(@RequestBody GoViewProjectUpdateReqVO goViewProjectUpdateReqVO) {

        if (goViewProjectUpdateReqVO.getState() == -1 || goViewProjectUpdateReqVO.getState() == 1) {

            final Optional<GoViewProjectDO> byId = goViewProjectRepository.findById(goViewProjectUpdateReqVO.getId());
            if (byId.isPresent()) {
                final GoViewProjectDO goViewProjectDO = byId.get();
                goViewProjectDO.setStatus(goViewProjectUpdateReqVO.getState());
                return true;
            }
            throw new RuntimeException("没有找到该项目");
        }
        throw new RuntimeException("警告非法字段");
    }

    /**
     * @data: 2023/5/31-下午11:25
     * @User: zhaozhiwei
     * @method: saveData
      * @param data :
     * @return: org.springframework.http.ResponseEntity<com.z.framework.common.web.rest.vm.ResponseData<java.lang.Object>>
     * @Description: 项目明细数据
     */
    @Operation(summary = "保存项目数据")
    @PostMapping("/save/data")
    public String saveData(GoViewProjectUpdateReqVO data) {

        final Optional<GoViewProjectDO> byId = goViewProjectRepository.findById(data.getProjectId());
        if (!byId.isPresent()) {
            throw new RuntimeException("找不到该项目: " + data.getProjectId());
        }
        // 查询项目读应的数据
        final GoViewProjectDO goViewProjectDO = byId.get();
        goViewProjectDO.setContent(data.getContent());
        return "保存成功";
    }

    @GetMapping("/getData")
    @Operation(summary = "获得项目")
    @Parameter(name = "projectId", description = "编号", required = true, example = "1024")
    public GoViewProjectRespVO getProject(@RequestParam("projectId") Long id) {
        GoViewProjectDO project = goViewProjectService.getProject(id);
        return goViewProjectConvert.convert(project);
    }

    @GetMapping("/list")
    @Operation(summary = "获得我的项目分页")
    public ResponseData<List<GoViewProjectRespVO>> getMyProjectPage(@Valid PageParam pageVO) {
        final Page<GoViewProjectDO> myProjectPage = goViewProjectService.getMyProjectPage(pageVO);
        return ResponseData.ok(goViewProjectConvert.convert(myProjectPage.getContent()),
                myProjectPage.getNumberOfElements());
    }

    /**
     * 上传文件
     *
     * @param object 文件流对象
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public GoViewFileVO upload(@RequestBody MultipartFile object) throws IOException {
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

        GoViewFileVO goViewFileVO = BeanUtil.copyProperties(viewFileDO, GoViewFileVO.class);
        if (uploadType.equals("local")) {
            File desc = getAbsoluteFile(uploadPath + File.separator + filepath, fileSuffixName);
            // 写入磁盘
            object.transferTo(desc);
            goViewFileVO.setFileurl(screenProperties.getGoView().getHttpUrl() + "/api/goview/project/" + viewFileDO.getVirtualKey() + "/" + viewFileDO.getRelativePath() + "/" + viewFileDO.getFileName());
        }
        return goViewFileVO;
    }

    /**
     * @param request :
     * @data: 2022/8/31-上午10:09
     * @User: zhaozhiwei
     * @method: getCaptchaCode
     * @return: org.springframework.web.servlet.ModelAndView
     * @Description: 获取本地图片, 跟上述upload构建路径格式要匹配
     * local: http://ip:port/api/goview/project/local/时间/fileName
     */
    @GetMapping("/local/{relativePath}/{fileName}")
    public ResponseEntity<byte[]> downImg(HttpServletRequest request,
                                          @PathVariable("relativePath") String relativePath,
                                          @PathVariable("fileName") String fileName) throws IOException {
        // 获取本地图片
        final String uploadPath = screenProperties.getGoView().getPath().getUpload();
        File file = getAbsoluteFile(uploadPath + File.separator + relativePath, fileName);
        if (!file.exists()) {
            return null;
        }

        // 创建验证码图片
        final FileInputStream fileInputStream = new FileInputStream(file);
        BufferedImage bi = ImageIO.read(fileInputStream);

        // 将验证码图片转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", outputStream);
        byte[] captchaBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // 这个可以在前端通过response.headers.responsetype获取
        headers.set("responseType", "text"); // 设置自定义的responseType头部

//        arraybuffer形式
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(captchaBytes);
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
