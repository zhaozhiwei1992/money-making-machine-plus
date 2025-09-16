package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.service.definition.BpmProcessDefinitionService;
import com.z.module.bpm.web.vo.definition.process.BpmProcessDefinitionListReqVO;
import com.z.module.bpm.web.vo.definition.process.BpmProcessDefinitionPageItemRespVO;
import com.z.module.bpm.web.vo.definition.process.BpmProcessDefinitionPageReqVO;
import com.z.module.bpm.web.vo.definition.process.BpmProcessDefinitionRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 获取流程定义表的信息
 */
@Tag(name = "流程定义信息")
@RestController
@RequestMapping("/bpm/process-definition")
@Validated
public class BpmProcessDefinitionResource {

    @Resource
    private BpmProcessDefinitionService bpmDefinitionService;

    @GetMapping("/page")
    @Operation(summary = "获得流程定义分页")
    
    public PageResult<BpmProcessDefinitionPageItemRespVO> getProcessDefinitionPage(
            BpmProcessDefinitionPageReqVO pageReqVO) {
        return bpmDefinitionService.getProcessDefinitionPage(pageReqVO);
    }

    @GetMapping ("/list")
    @Operation(summary = "获得流程定义列表")
    
    public List<BpmProcessDefinitionRespVO> getProcessDefinitionList(
            BpmProcessDefinitionListReqVO listReqVO) {
        return bpmDefinitionService.getProcessDefinitionList(listReqVO);
    }

    @GetMapping ("/get-bpmn-xml")
    @Operation(summary = "获得流程定义的 BPMN XML")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    
    public String getProcessDefinitionBpmnXML(@RequestParam("id") String id) {
        return bpmDefinitionService.getProcessDefinitionBpmnXML(id);
    }
}
