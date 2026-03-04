package com.z.module.ai.service.model;

import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.util.object.BeanUtils;
import com.z.module.ai.repository.AiApiKeyRepository;
import com.z.module.ai.web.rest.admin.model.vo.apikey.AiApiKeyPageReqVO;
import com.z.module.ai.web.rest.admin.model.vo.apikey.AiApiKeySaveReqVO;
import com.z.module.ai.domain.model.AiApiKeyDO;
import groovy.util.logging.Slf4j;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.API_KEY_DISABLE;
import static com.z.module.ai.enums.ErrorCodeConstants.API_KEY_NOT_EXISTS;

/**
 * AI API 密钥 Service 实现类
 *
 * @author 芋道源码
 */
@lombok.extern.slf4j.Slf4j
@Service
@Validated
@Slf4j
public class AiApiKeyService {

    @Resource
    private AiApiKeyRepository apiKeyRepository;

    /**
     * 创建 API 密钥
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    public Long createApiKey(@Valid AiApiKeySaveReqVO createReqVO) {
        // 插入
        AiApiKeyDO apiKey = BeanUtils.toBean(createReqVO, AiApiKeyDO.class);
        apiKeyRepository.save(apiKey);
        // 返回
        return apiKey.getId();
    }

    /**
     * 更新 API 密钥
     *
     * @param updateReqVO 更新信息
     */
    public void updateApiKey(@Valid AiApiKeySaveReqVO updateReqVO) {
        // 校验存在
        validateApiKeyExists(updateReqVO.getId());
        // 更新
        AiApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, AiApiKeyDO.class);
        apiKeyRepository.save(updateObj);
    }

    /**
     * 删除 API 密钥
     *
     * @param id 编号
     */
    public void deleteApiKey(Long id) {
        // 校验存在
        validateApiKeyExists(id);
        // 删除
        apiKeyRepository.deleteById(id);
    }

    private AiApiKeyDO validateApiKeyExists(Long id) {
        AiApiKeyDO apiKey = apiKeyRepository.findById(id).orElse(null);
        if (apiKey == null) {
            throw exception(API_KEY_NOT_EXISTS);
        }
        return apiKey;
    }

    /**
     * 获得 API 密钥
     *
     * @param id 编号
     * @return API 密钥
     */
    public AiApiKeyDO getApiKey(Long id) {
        return apiKeyRepository.findById(id).orElse(null);
    }

    /**
     * 校验 API 密钥
     *
     * @param id 编号
     * @return API 密钥
     */
    public AiApiKeyDO validateApiKey(Long id) {
        AiApiKeyDO apiKey = validateApiKeyExists(id);
        if (CommonStatusEnum.isDisable(apiKey.getStatus())) {
            throw exception(API_KEY_DISABLE);
        }
        return apiKey;
    }

    /**
     * 获得 API 密钥分页
     *
     * @param pageable 分页查询
     * @param query 查询条件
     * @return API 密钥分页
     */
    public PageResult<AiApiKeyDO> getApiKeyPage(Pageable pageable, AiApiKeyDO query) {
        log.debug("REST request to get all AiApiKey for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiApiKeyDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiApiKeyDO> ex = Example.of(query, matcher);
        page = apiKeyRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    /**
     * 获得 API 密钥列表
     *
     * @return API 密钥列表
     */
    public List<AiApiKeyDO> getApiKeyList() {
        return apiKeyRepository.findAll();
    }

    /**
     * 获得默认的 API 密钥
     *
     * @param platform 平台
     * @param status 状态
     * @return API 密钥
     */
    public AiApiKeyDO getRequiredDefaultApiKey(String platform, Integer status) {
        AiApiKeyDO apiKey = apiKeyRepository.findFirstByPlatformAndStatus(platform, status);
        if (apiKey == null) {
            throw exception(API_KEY_NOT_EXISTS);
        }
        return apiKey;
    }

}
