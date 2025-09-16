package com.z.module.bpm.web.rest.task;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.bpm.service.task.BpmTaskService;
import com.z.module.bpm.web.vo.task.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "管理后台 - 流程任务实例")
@RestController
@RequestMapping("/bpm/task")
@Validated
public class BpmTaskResource {

    @Resource
    private BpmTaskService taskService;

    @GetMapping("todo-page")
    @Operation(summary = "获取 Todo 待办任务分页")
    public PageResult<BpmTaskTodoPageItemRespVO> getTodoTaskPage(@Valid BpmTaskTodoPageReqVO pageVO) {
        return taskService.getTodoTaskPage(SecurityUtils.getUserId(), pageVO);
    }

    @GetMapping("done-page")
    @Operation(summary = "获取 Done 已办任务分页")
    public PageResult<BpmTaskDonePageItemRespVO> getDoneTaskPage(@Valid BpmTaskDonePageReqVO pageVO) {
        return taskService.getDoneTaskPage(SecurityUtils.getUserId(), pageVO);
    }

    @GetMapping("/list-by-process-instance-id")
    @Operation(summary = "获得指定流程实例的任务列表", description = "包括完成的、未完成的")
    @Parameter(name = "processInstanceId", description = "流程实例的编号", required = true)
    public List<BpmTaskRespVO> getTaskListByProcessInstanceId(
        @RequestParam("processInstanceId") String processInstanceId) {
        return taskService.getTaskListByProcessInstanceId(processInstanceId);
    }

    @PutMapping("/approve")
    @Operation(summary = "通过任务")
    public boolean approveTask(@Valid @RequestBody BpmTaskApproveReqVO reqVO) {
        taskService.approveTask(SecurityUtils.getUserId(), reqVO);
        return true;
    }

    @PutMapping("/reject")
    @Operation(summary = "不通过任务")
    public boolean rejectTask(@Valid @RequestBody BpmTaskRejectReqVO reqVO) {
        taskService.rejectTask(SecurityUtils.getUserId(), reqVO);
        return true;
    }

    @PutMapping("/update-assignee")
    @Operation(summary = "更新任务的负责人", description = "用于【流程详情】的【转派】按钮")
    public boolean updateTaskAssignee(@Valid @RequestBody BpmTaskUpdateAssigneeReqVO reqVO) {
        taskService.updateTaskAssignee(SecurityUtils.getUserId(), reqVO);
        return true;
    }

}
