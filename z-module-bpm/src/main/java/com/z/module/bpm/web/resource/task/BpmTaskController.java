package com.z.module.bpm.web.resource.task;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.bpm.service.task.BpmTaskService;
import com.z.module.bpm.web.vo.task.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "管理后台 - 流程任务实例")
@RestController
@RequestMapping("/bpm/task")
@Validated
public class BpmTaskController {

    @Resource
    private BpmTaskService taskService;

    @GetMapping("todo-page")
    @Operation(summary = "获取 Todo 待办任务分页")
    
    public ResponseEntity<ResponseData<PageResult<BpmTaskTodoPageItemRespVO>>> getTodoTaskPage(@Valid BpmTaskTodoPageReqVO pageVO) {
        return ResponseData.ok(taskService.getTodoTaskPage(SecurityUtils.getUserId(), pageVO));
    }

    @GetMapping("done-page")
    @Operation(summary = "获取 Done 已办任务分页")
    
    public ResponseEntity<ResponseData<PageResult<BpmTaskDonePageItemRespVO>>> getDoneTaskPage(@Valid BpmTaskDonePageReqVO pageVO) {
        return ResponseData.ok(taskService.getDoneTaskPage(SecurityUtils.getUserId(), pageVO));
    }

    @GetMapping("/list-by-process-instance-id")
    @Operation(summary = "获得指定流程实例的任务列表", description = "包括完成的、未完成的")
    @Parameter(name = "processInstanceId", description = "流程实例的编号", required = true)
    
    public ResponseEntity<ResponseData<List<BpmTaskRespVO>>> getTaskListByProcessInstanceId(
        @RequestParam("processInstanceId") String processInstanceId) {
        return ResponseData.ok(taskService.getTaskListByProcessInstanceId(processInstanceId));
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    
    public ResponseEntity<ResponseData<Boolean>> approveTask(@Valid @RequestBody BpmTaskApproveReqVO reqVO) {
        taskService.approveTask(SecurityUtils.getUserId(), reqVO);
        return ResponseData.ok(true);
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    
    public ResponseEntity<ResponseData<Boolean>> rejectTask(@Valid @RequestBody BpmTaskRejectReqVO reqVO) {
        taskService.rejectTask(SecurityUtils.getUserId(), reqVO);
        return ResponseData.ok(true);
    }

    @PutMapping("/update-assignee")
    @Operation(summary = "更新任务的负责人", description = "用于【流程详情】的【转派】按钮")
    
    public ResponseEntity<ResponseData<Boolean>> updateTaskAssignee(@Valid @RequestBody BpmTaskUpdateAssigneeReqVO reqVO) {
        taskService.updateTaskAssignee(SecurityUtils.getUserId(), reqVO);
        return ResponseData.ok(true);
    }

}
