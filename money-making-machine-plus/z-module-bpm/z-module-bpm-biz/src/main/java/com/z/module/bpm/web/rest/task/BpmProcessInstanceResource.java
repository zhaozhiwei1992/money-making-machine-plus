package com.z.module.bpm.web.rest.task;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.security.util.SecurityUtils;
import com.z.module.bpm.service.task.BpmProcessInstanceService;
import com.z.module.bpm.web.vo.instance.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.Valid;

/**
 * 流程实例，通过流程定义创建的一次“申请”, 相当与definition是java类, 流程实例是java对象
 */
@Tag(name = "流程实例")
@RestController
@RequestMapping("/bpm/process-instance")
@Validated
public class BpmProcessInstanceResource {

    @Resource
    private BpmProcessInstanceService processInstanceService;

    @GetMapping("/my-page")
    @Operation(summary = "获得我的实例分页列表", description = "在【我的流程】菜单中，进行调用")
    public PageResult<BpmProcessInstancePageItemRespVO> getMyProcessInstancePage(
            @Valid BpmProcessInstanceMyPageReqVO pageReqVO) {
        return processInstanceService.getMyProcessInstancePage(SecurityUtils.getUserId(), pageReqVO);
    }

    @PostMapping("/create")
    @Operation(summary = "新建流程实例")
    public String createProcessInstance(@Valid @RequestBody BpmProcessInstanceCreateReqVO createReqVO) {
        return processInstanceService.createProcessInstance(SecurityUtils.getUserId(), createReqVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得指定流程实例", description = "在【流程详细】界面中，进行调用")
    @Parameter(name = "id", description = "流程实例的编号", required = true)
    public BpmProcessInstanceRespVO getProcessInstance(@RequestParam("id") String id) {
        return processInstanceService.getProcessInstanceVO(id);
    }

    @DeleteMapping("/cancel")
    @Operation(summary = "取消流程实例", description = "撤回发起的流程")
    public boolean cancelProcessInstance(@Valid @RequestBody BpmProcessInstanceCancelReqVO cancelReqVO) {
        processInstanceService.cancelProcessInstance(SecurityUtils.getUserId(), cancelReqVO);
        return true;
    }
}
