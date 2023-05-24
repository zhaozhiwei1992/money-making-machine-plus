package com.z.module.bpm.web.resource.definition;

import com.z.framework.common.util.io.IoUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.bpm.service.definition.BpmModelService;
import com.z.module.bpm.web.mapper.definition.BpmModelConvert;
import com.z.module.bpm.web.vo.definition.model.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.io.IOException;

@Tag(name = "管理后台 - 流程模型")
@RestController
@RequestMapping("/bpm/model")
@Validated
public class BpmModelResource {

    @Resource
    private BpmModelService modelService;

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public ResponseEntity<ResponseData<PageResult<BpmModelPageItemRespVO>>> getModelPage(BpmModelPageReqVO pageVO) {
        return ResponseData.ok(modelService.getModelPage(pageVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public ResponseEntity<ResponseData<BpmModelRespVO>> getModel(@RequestParam("id") String id) {
        BpmModelRespVO model = modelService.getModel(id);
        return ResponseData.ok(model);
    }

    @PostMapping("/create")
    @Operation(summary = "新建模型")

    public ResponseEntity<ResponseData<String>> createModel(@Valid @RequestBody BpmModelCreateReqVO createRetVO) {
        return ResponseData.ok(modelService.createModel(createRetVO, null));
    }

    @PutMapping("/update")
    @Operation(summary = "修改模型")

    public ResponseEntity<ResponseData<Boolean>> updateModel(@Valid @RequestBody BpmModelUpdateReqVO modelVO) {
        modelService.updateModel(modelVO);
        return ResponseData.ok(true);
    }

    @PostMapping("/import")
    @Operation(summary = "导入模型")
    public ResponseEntity<ResponseData<String>> importModel(@Valid BpmModeImportReqVO importReqVO) throws IOException {
        BpmModelCreateReqVO createReqVO = BpmModelConvert.INSTANCE.convert(importReqVO);
        // 读取文件
        String bpmnXml = IoUtils.readUtf8(importReqVO.getBpmnFile().getInputStream(), false);
        return ResponseData.ok(modelService.createModel(createReqVO, bpmnXml));
    }

    @PostMapping("/deploy")
    @Operation(summary = "部署模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")

    public ResponseEntity<ResponseData<Boolean>> deployModel(@RequestParam("id") String id) {
        modelService.deployModel(id);
        return ResponseData.ok(true);
    }

    @PutMapping("/update-state")
    @Operation(summary = "修改模型的状态", description = "实际更新的部署的流程定义的状态")

    public ResponseEntity<ResponseData<Boolean>> updateModelState(@Valid @RequestBody BpmModelUpdateStateReqVO reqVO) {
        modelService.updateModelState(reqVO.getId(), reqVO.getState());
        return ResponseData.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")

    public ResponseEntity<ResponseData<Boolean>> deleteModel(@RequestParam("id") String id) {
        modelService.deleteModel(id);
        return ResponseData.ok(true);
    }
}
