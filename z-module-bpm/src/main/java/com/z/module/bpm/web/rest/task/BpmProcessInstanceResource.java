package com.z.module.bpm.web.rest.task;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.bpm.service.task.BpmProcessInstanceService;
import com.z.module.bpm.web.vo.instance.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "管理后台 - 流程实例") // 流程实例，通过流程定义创建的一次“申请”
@RestController
@RequestMapping("/api/bpm/process-instance")
@Validated
public class BpmProcessInstanceResource {

    @Resource
    private BpmProcessInstanceService processInstanceService;

    @GetMapping("/my-page")
    @Operation(summary = "获得我的实例分页列表", description = "在【我的流程】菜单中，进行调用")
    public ResponseEntity<ResponseData<PageResult<BpmProcessInstancePageItemRespVO>>> getMyProcessInstancePage(
            @Valid BpmProcessInstanceMyPageReqVO pageReqVO) {
        return ResponseData.ok(processInstanceService.getMyProcessInstancePage(SecurityUtils.getUserId(), pageReqVO));
    }

    @PostMapping("/create")
    @Operation(summary = "新建流程实例")
    public ResponseEntity<ResponseData<String>> createProcessInstance(@Valid @RequestBody BpmProcessInstanceCreateReqVO createReqVO) {
        return ResponseData.ok(processInstanceService.createProcessInstance(SecurityUtils.getUserId(), createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得指定流程实例", description = "在【流程详细】界面中，进行调用")
    @Parameter(name = "id", description = "流程实例的编号", required = true)
    public ResponseEntity<ResponseData<BpmProcessInstanceRespVO>> getProcessInstance(@RequestParam("id") String id) {
        return ResponseData.ok(processInstanceService.getProcessInstanceVO(id));
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "取消流程实例", description = "撤回发起的流程")
    public ResponseEntity<ResponseData<Boolean>> cancelProcessInstance(@Valid @RequestBody BpmProcessInstanceCancelReqVO cancelReqVO) {
        processInstanceService.cancelProcessInstance(SecurityUtils.getUserId(), cancelReqVO);
        return ResponseData.ok(true);
    }
}
