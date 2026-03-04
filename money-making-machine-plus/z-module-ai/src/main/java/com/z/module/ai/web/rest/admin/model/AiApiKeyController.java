package com.z.module.ai.web.rest.admin.model;

import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.model.AiApiKeyDO;
import com.z.module.ai.service.model.AiApiKeyService;
import com.z.module.ai.web.rest.admin.model.vo.apikey.AiApiKeyPageReqVO;
import com.z.module.ai.web.rest.admin.model.vo.apikey.AiApiKeyRespVO;
import com.z.module.ai.web.rest.admin.model.vo.apikey.AiApiKeySaveReqVO;
import com.z.module.ai.web.rest.admin.model.vo.model.AiModelRespVO;
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

@Tag(name = "管理后台 - AI API 密钥")
@RestController
@RequestMapping("/ai/api-key")
@Validated
public class AiApiKeyController {

    @Resource
    private AiApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建 API 密钥")
    public Long createApiKey(@Valid @RequestBody AiApiKeySaveReqVO createReqVO) {
        return apiKeyService.createApiKey(createReqVO);
    }

    @PutMapping("/update")
    @Operation(summary = "更新 API 密钥")
    public Boolean updateApiKey(@Valid @RequestBody AiApiKeySaveReqVO updateReqVO) {
        apiKeyService.updateApiKey(updateReqVO);
        return true;
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 API 密钥")
    @Parameter(name = "id", description = "编号", required = true)
    public Boolean deleteApiKey(@RequestParam("id") Long id) {
        apiKeyService.deleteApiKey(id);
        return true;
    }

    @GetMapping("/get")
    @Operation(summary = "获得 API 密钥")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public AiApiKeyRespVO getApiKey(@RequestParam("id") Long id) {
        AiApiKeyDO apiKey = apiKeyService.getApiKey(id);
        return BeanUtils.toBean(apiKey, AiApiKeyRespVO.class);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 API 密钥分页")
    public PageResult<AiApiKeyRespVO> getApiKeyPage(Pageable pageable, @Valid AiApiKeyDO pageReqVO) {
        PageResult<AiApiKeyDO> pageResult = apiKeyService.getApiKeyPage(pageable, pageReqVO);
        return BeanUtils.toBean(pageResult, AiApiKeyRespVO.class);
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得 API 密钥分页列表")
    public List<AiModelRespVO> getApiKeySimpleList() {
        List<AiApiKeyDO> list = apiKeyService.getApiKeyList();
        return convertList(list, key -> new AiModelRespVO().setId(key.getId()).setName(key.getName()));
    }

}