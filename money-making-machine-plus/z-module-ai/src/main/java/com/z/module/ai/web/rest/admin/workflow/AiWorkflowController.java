package com.z.module.ai.web.rest.admin.workflow;

import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.workflow.AiWorkflowDO;
import com.z.module.ai.service.workflow.AiWorkflowService;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowPageReqVO;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowRespVO;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowSaveReqVO;
import com.z.module.ai.web.rest.admin.workflow.vo.AiWorkflowTestReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@Tag(name = "管理后台 - AI 工作流")
@RestController
@RequestMapping("/ai/workflow")
@Slf4j
public class AiWorkflowController {

    @Resource
    private AiWorkflowService workflowService;

    @PostMapping("/create")
    @Operation(summary = "创建 AI 工作流")
    public Long createWorkflow(@Valid @RequestBody AiWorkflowSaveReqVO createReqVO) {
        return workflowService.createWorkflow(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新 AI 工作流")
    public Boolean updateWorkflow(@Valid @RequestBody AiWorkflowSaveReqVO updateReqVO) {
        workflowService.updateWorkflow(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 AI 工作流")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteWorkflow(@RequestParam("id") Long id) {
        workflowService.deleteWorkflow(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得 AI 工作流")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiWorkflowRespVO getWorkflow(@RequestParam("id") Long id) {
        AiWorkflowDO workflow = workflowService.getWorkflow(id);
        return BeanUtils.toBean(workflow, AiWorkflowRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 AI 工作流分页")
    public PageResult<AiWorkflowRespVO> getWorkflowPage(Pageable pageable, @Valid AiWorkflowDO aiWorkflowDO) {
        PageResult<AiWorkflowDO> pageResult = workflowService.getWorkflowPage(pageable, aiWorkflowDO);
        return BeanUtils.toBean(pageResult, AiWorkflowRespVO.class);
    }

    @PostMapping("/test")
    @Operation(summary = "测试 AI 工作流")
    public Object testWorkflow(@Valid @RequestBody AiWorkflowTestReqVO testReqVO) {
        return workflowService.testWorkflow(testReqVO);
    }

}
