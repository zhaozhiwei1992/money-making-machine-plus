package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.ResponseData;
import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.service.definition.BpmFormService;
import com.z.module.bpm.web.mapper.definition.BpmFormConvert;
import com.z.module.bpm.web.vo.definition.form.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "管理后台 - 动态表单")
@RestController
@RequestMapping("/api/bpm/form")
@Validated
public class BpmFormResource {

    @Autowired
    private BpmFormService formService;

    @Autowired
    private BpmFormConvert bpmFormConvert;

    @PostMapping("/create")
    @Operation(summary = "创建动态表单")
    
    public ResponseEntity<ResponseData<Long>> createForm(@Valid @RequestBody BpmFormCreateReqVO createReqVO) {
        return ResponseData.ok(formService.createForm(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新动态表单")
    
    public ResponseEntity<ResponseData<Boolean>> updateForm(@Valid @RequestBody BpmFormUpdateReqVO updateReqVO) {
        formService.updateForm(updateReqVO);
        return ResponseData.ok(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除动态表单")
    @Parameter(name = "id", description = "编号", required = true)
    
    public ResponseEntity<ResponseData<Boolean>> deleteForm(@RequestParam("id") Long id) {
        formService.deleteForm(id);
        return ResponseData.ok(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得动态表单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    
    public ResponseEntity<ResponseData<BpmFormRespVO>> getForm(@RequestParam("id") Long id) {
        BpmFormDO form = formService.getForm(id);
        return ResponseData.ok(bpmFormConvert.convert(form));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得动态表单的精简列表", description = "用于表单下拉框")
    public ResponseEntity<ResponseData<List<BpmFormSimpleRespVO>>> getSimpleForms() {
        List<BpmFormDO> list = formService.getFormList();
        return ResponseData.ok(bpmFormConvert.convertList2(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得动态表单分页")
    
    public ResponseEntity<ResponseData<PageResult<BpmFormRespVO>>> getFormPage(@Valid BpmFormPageReqVO pageVO) {
        PageResult<BpmFormDO> pageResult = formService.getFormPage(pageVO);
        return ResponseData.ok(bpmFormConvert.convertPage(pageResult));
    }

}
