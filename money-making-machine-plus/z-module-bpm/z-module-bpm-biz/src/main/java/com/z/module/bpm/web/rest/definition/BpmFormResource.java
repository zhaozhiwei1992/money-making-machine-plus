package com.z.module.bpm.web.rest.definition;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.service.definition.BpmFormService;
import com.z.module.bpm.web.mapper.definition.BpmFormConvert;
import com.z.module.bpm.web.vo.definition.form.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 动态表单制作
 * 1. 绘制一些动态页面, 用户来填报, 然后表单跟工作流绑定, 来进行审核
 * 2. 实际业务可能存在大批量数据统一走流程, 简单业务可以走这个
 */
@Tag(name = "动态表单")
@RestController
@RequestMapping("/bpm/form")
@Validated
public class BpmFormResource {

    @Autowired
    private BpmFormService formService;

    @Autowired
    private BpmFormConvert bpmFormConvert;

    @PostMapping("/create")
    @Operation(summary = "创建动态表单")
    
    public Long createForm(@Valid @RequestBody BpmFormCreateReqVO createReqVO) {
        return formService.createForm(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新动态表单")
    
    public boolean updateForm(@Valid @RequestBody BpmFormUpdateReqVO updateReqVO) {
        formService.updateForm(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除动态表单")
    @Parameter(name = "id", description = "编号", required = true)
    
    public boolean deleteForm(@RequestParam("id") Long id) {
        formService.deleteForm(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得动态表单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    
    public BpmFormRespVO getForm(@RequestParam("id") Long id) {
        BpmFormDO form = formService.getForm(id);
        return bpmFormConvert.convert(form);
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得动态表单的精简列表", description = "用于表单下拉框")
    public List<BpmFormSimpleRespVO> getSimpleForms() {
        List<BpmFormDO> list = formService.getFormList();
        return bpmFormConvert.convertList2(list);
    }

    @GetMapping("/page")
    @Operation(summary = "获得动态表单分页")
    
    public PageResult<BpmFormRespVO> getFormPage(@Valid BpmFormPageReqVO pageVO) {
        PageResult<BpmFormDO> pageResult = formService.getFormPage(pageVO);
        return bpmFormConvert.convertPage(pageResult);
    }

}
