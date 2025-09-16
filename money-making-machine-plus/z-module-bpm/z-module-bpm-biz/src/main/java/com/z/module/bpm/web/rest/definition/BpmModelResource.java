package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.util.io.IoUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.service.definition.BpmModelService;
import com.z.module.bpm.web.mapper.definition.BpmModelConvert;
import com.z.module.bpm.web.vo.definition.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.Valid;

import java.io.IOException;

/**
 * 流程定义, 绘制流程入口
 */
@Tag(name = "流程模型定义")
@RestController
@RequestMapping("/bpm/model")
@Validated
public class BpmModelResource {

    @Resource
    private BpmModelService modelService;

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public PageResult<BpmModelPageItemRespVO> getModelPage(BpmModelPageReqVO pageVO) {
        return modelService.getModelPage(pageVO);
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public BpmModelRespVO getModel(@RequestParam("id") String id) {
        BpmModelRespVO model = modelService.getModel(id);
        return model;
    }

    @PostMapping("/create")
    @Operation(summary = "新建模型")

    public String createModel(@Valid @RequestBody BpmModelCreateReqVO createRetVO) {
        return modelService.createModel(createRetVO, null);
    }

    @PutMapping("/update")
    @Operation(summary = "修改模型")

    public boolean updateModel(@Valid @RequestBody BpmModelUpdateReqVO modelVO) {
        modelService.updateModel(modelVO);
        return true;
    }

    @Autowired
    private BpmModelConvert bpmModelConvert;

    @PostMapping("/import")
    @Operation(summary = "导入模型")
    public String importModel(@Valid BpmModeImportReqVO importReqVO) throws IOException {
        BpmModelCreateReqVO createReqVO = bpmModelConvert.convert(importReqVO);
        // 读取文件
        String bpmnXml = IoUtils.readUtf8(importReqVO.getBpmnFile().getInputStream(), false);
        return modelService.createModel(createReqVO, bpmnXml);
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")

    public boolean deployModel(@RequestParam("id") String id) {
        modelService.deployModel(id);
        return true;
    }

    @PutMapping("/update-state")
    @Operation(summary = "修改模型的状态", description = "实际更新的部署的流程定义的状态")

    public boolean updateModelState(@Valid @RequestBody BpmModelUpdateStateReqVO reqVO) {
        modelService.updateModelState(reqVO.getId(), reqVO.getState());
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")

    public boolean deleteModel(@RequestParam("id") String id) {
        modelService.deleteModel(id);
        return true;
    }
}
