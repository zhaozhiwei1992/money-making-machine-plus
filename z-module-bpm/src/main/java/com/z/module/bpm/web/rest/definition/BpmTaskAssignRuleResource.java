package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.bpm.service.definition.BpmTaskAssignRuleService;
import com.z.module.bpm.web.vo.definition.rule.BpmTaskAssignRuleCreateReqVO;
import com.z.module.bpm.web.vo.definition.rule.BpmTaskAssignRuleRespVO;
import com.z.module.bpm.web.vo.definition.rule.BpmTaskAssignRuleUpdateReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "管理后台 - 任务分配规则")
@RestController
@RequestMapping("/bpm/task-assign-rule")
@Validated
public class BpmTaskAssignRuleResource {

    @Resource
    private BpmTaskAssignRuleService taskAssignRuleService;

    @GetMapping("/list")
    @Operation(summary = "获得任务分配规则列表")
    @Parameters({
            @Parameter(name = "modelId", description = "模型编号", example = "1024"),
            @Parameter(name = "processDefinitionId", description = "流程定义的编号", example = "2048")
    })
    
    public ResponseEntity<ResponseData<List<BpmTaskAssignRuleRespVO>>> getTaskAssignRuleList(
            @RequestParam(value = "modelId", required = false) String modelId,
            @RequestParam(value = "processDefinitionId", required = false) String processDefinitionId) {
        return ResponseData.ok(taskAssignRuleService.getTaskAssignRuleList(modelId, processDefinitionId));
    }

    @PostMapping("/create")
    @Operation(summary = "创建任务分配规则")
    
    public ResponseEntity<ResponseData<Long>> createTaskAssignRule(@Valid @RequestBody BpmTaskAssignRuleCreateReqVO reqVO) {
        return ResponseData.ok(taskAssignRuleService.createTaskAssignRule(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新任务分配规则")
    
    public ResponseEntity<ResponseData<Boolean>> updateTaskAssignRule(@Valid @RequestBody BpmTaskAssignRuleUpdateReqVO reqVO) {
        taskAssignRuleService.updateTaskAssignRule(reqVO);
        return ResponseData.ok(true);
    }
}
