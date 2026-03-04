package com.z.module.ai.web.rest.admin.model;

import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiModelDO;
import com.z.module.ai.service.model.AiModelService;
import com.z.module.ai.web.rest.admin.model.vo.model.AiModelPageReqVO;
import com.z.module.ai.web.rest.admin.model.vo.model.AiModelRespVO;
import com.z.module.ai.web.rest.admin.model.vo.model.AiModelSaveReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.z.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - AI 模型")
@RestController
@RequestMapping("/ai/model")
@Validated
public class AiModelController {

    @Resource
    private AiModelService modelService;

    @PostMapping("/create")
    @Operation(summary = "创建模型")
    public Long createModel(@Valid @RequestBody AiModelSaveReqVO createReqVO) {
        return modelService.createModel(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新模型")
    public Boolean updateModel(@Valid @RequestBody AiModelSaveReqVO updateReqVO) {
        modelService.updateModel(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模型")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteModel(@RequestParam("id") Long id) {
        modelService.deleteModel(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得模型")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiModelRespVO getModel(@RequestParam("id") Long id) {
        AiModelDO model = modelService.getModel(id);
        return BeanUtils.toBean(model, AiModelRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获得模型分页")
    public PageResult<AiModelRespVO> getModelPage(Pageable pageable, @Valid AiModelDO aiModelDO) {
        PageResult<AiModelDO> pageResult = modelService.getModelPage(pageable, aiModelDO);
        return BeanUtils.toBean(pageResult, AiModelRespVO.class);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得模型列表")
    @Parameter(name = "type", description = "类型", required = true, example = "1")
    @Parameter(name = "platform", description = "平台", example = "midjourney")
    public List<AiModelRespVO> getModelSimpleList(
            @RequestParam("type") Integer type,
            @RequestParam(value = "platform", required = false) String platform) {
        List<AiModelDO> list = modelService.getModelListByStatusAndType(
                CommonStatusEnum.ENABLE.getStatus(), type, platform);
        return convertList(list, model -> new AiModelRespVO().setId(model.getId())
                .setName(model.getName()).setModel(model.getModel()).setPlatform(model.getPlatform()));
    }

}