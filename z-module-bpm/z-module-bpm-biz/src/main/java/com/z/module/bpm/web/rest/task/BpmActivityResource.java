package com.z.module.bpm.web.rest.task;

import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.bpm.service.task.BpmActivityService;
import com.z.module.bpm.web.vo.activity.BpmActivityRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@Tag(name = "管理后台 - 流程活动实例")
@RestController
@RequestMapping("/bpm/activity")
@Validated
public class BpmActivityResource {

    @Resource
    private BpmActivityService activityService;

    @GetMapping("/list")
    @Operation(summary = "生成指定流程实例的高亮流程图",
            description = "只高亮进行中的任务。不过要注意，该接口暂时没用，通过前端的 ProcessViewer.vue 界面的 highlightDiagram 方法生成")
    @Parameter(name = "processInstanceId", description = "流程实例的编号", required = true)
    public ResponseEntity<ResponseData<List<BpmActivityRespVO>>> getActivityList(
            @RequestParam("processInstanceId") String processInstanceId) {
        return ResponseData.ok(activityService.getActivityListByProcessInstanceId(processInstanceId));
    }
}
