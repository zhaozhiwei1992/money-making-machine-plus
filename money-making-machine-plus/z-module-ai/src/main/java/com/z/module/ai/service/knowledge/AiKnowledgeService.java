package com.z.module.ai.service.knowledge;

import cn.hutool.core.util.ObjUtil;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.util.object.BeanUtils;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgePageReqVO;
import com.z.module.ai.web.rest.admin.knowledge.vo.knowledge.AiKnowledgeSaveReqVO;
import com.z.module.ai.domain.knowledge.AiKnowledgeDO;
import com.z.module.ai.domain.model.AiModelDO;
import com.z.module.ai.repository.AiKnowledgeRepository;
import com.z.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.KNOWLEDGE_NOT_EXISTS;

/**
 * AI 知识库-基础信息 Service 实现类
 *
 * @author xiaoxin
 */
@Service
@Slf4j
public class AiKnowledgeService {

    @Resource
    private AiKnowledgeRepository knowledgeRepository;

    @Resource
    private AiModelService modelService;
    @Resource
    private AiKnowledgeSegmentService knowledgeSegmentService;
    @Resource
    private AiKnowledgeDocumentService knowledgeDocumentService;

    public Long createKnowledge(AiKnowledgeSaveReqVO createReqVO) {
        // 1. 校验模型配置
        AiModelDO model = modelService.validateModel(createReqVO.getEmbeddingModelId());

        // 2. 插入知识库
        AiKnowledgeDO knowledge = BeanUtils.toBean(createReqVO, AiKnowledgeDO.class)
                .setEmbeddingModel(model.getModel());
        knowledgeRepository.save(knowledge);
        return knowledge.getId();
    }

    public void updateKnowledge(AiKnowledgeSaveReqVO updateReqVO) {
        // 1.1 校验知识库存在
        AiKnowledgeDO oldKnowledge = validateKnowledgeExists(updateReqVO.getId());
        // 1.2 校验模型配置
        AiModelDO model = modelService.validateModel(updateReqVO.getEmbeddingModelId());

        // 2. 更新知识库
        AiKnowledgeDO updateObj = BeanUtils.toBean(updateReqVO, AiKnowledgeDO.class)
                .setEmbeddingModel(model.getModel());
        knowledgeRepository.save(updateObj);

        // 3. 如果模型变化，需要 reindex 所有的文档
        if (ObjUtil.notEqual(oldKnowledge.getEmbeddingModelId(), updateReqVO.getEmbeddingModelId())) {
//            knowledgeSegmentService.reindexByKnowledgeIdAsync(updateReqVO.getId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledge(Long id) {
        // 1. 校验存在
        validateKnowledgeExists(id);

        // 2. 删除知识库下的所有文档及段落
        knowledgeDocumentService.deleteKnowledgeDocumentByKnowledgeId(id);

        // 3. 删除知识库
        // 特殊：知识库需要最后删除，不然相关的配置会找不到
        knowledgeRepository.deleteById(id);
    }

    public AiKnowledgeDO getKnowledge(Long id) {
        return knowledgeRepository.findById(id).orElse(null);
    }

    public AiKnowledgeDO validateKnowledgeExists(Long id) {
        AiKnowledgeDO knowledge = knowledgeRepository.findById(id).orElse(null);
        if (knowledge == null) {
            throw exception(KNOWLEDGE_NOT_EXISTS);
        }
        return knowledge;
    }

    public PageResult<AiKnowledgeDO> getKnowledgePage(Pageable pageable, AiKnowledgeDO query) {
        log.debug("REST request to get all AiKnowledge for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiKnowledgeDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiKnowledgeDO> ex = Example.of(query, matcher);
        page = knowledgeRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    public List<AiKnowledgeDO> getKnowledgeSimpleListByStatus(Integer status) {
        return knowledgeRepository.findByStatus(status);
    }

}
