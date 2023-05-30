package com.z.module.screen.web.rest;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.screen.domain.GoViewProjectDO;
import com.z.module.screen.service.GoViewProjectService;
import com.z.module.screen.web.mapper.GoViewProjectConvert;
import com.z.module.screen.web.vo.GoViewProjectCreateReqVO;
import com.z.module.screen.web.vo.GoViewProjectRespVO;
import com.z.module.screen.web.vo.GoViewProjectUpdateReqVO;
import com.z.module.screen.web.vo.PageParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "GoView 项目")
@RestController
@RequestMapping("/goview/project")
@Validated
@Transactional(rollbackFor = Exception.class)
public class GoViewProjectController {

    private final GoViewProjectService goViewProjectService;

    private final GoViewProjectConvert goViewProjectConvert;

    public GoViewProjectController(GoViewProjectService goViewProjectService, GoViewProjectConvert goViewProjectConvert) {
        this.goViewProjectService = goViewProjectService;
        this.goViewProjectConvert = goViewProjectConvert;
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

    @GetMapping("/getData")
    @Operation(summary = "获得项目")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public ResponseEntity<ResponseData<GoViewProjectRespVO>> getProject(@RequestParam("id") Long id) {
        GoViewProjectDO project = goViewProjectService.getProject(id);
        return ResponseData.ok(goViewProjectConvert.convert(project));
    }

    @GetMapping("/list")
    @Operation(summary = "获得我的项目分页")
    public ResponseEntity<ResponseData<PageResult<GoViewProjectRespVO>>> getMyProjectPage(@Valid PageParam pageVO) {
        PageResult<GoViewProjectDO> pageResult = goViewProjectService.getMyProjectPage(pageVO);
        return ResponseData.ok(goViewProjectConvert.convertPage(pageResult));
    }

}
