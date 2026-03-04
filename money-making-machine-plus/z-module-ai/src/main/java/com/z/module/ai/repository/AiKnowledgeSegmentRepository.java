package com.z.module.ai.repository;

import com.z.module.ai.domain.knowledge.AiKnowledgeSegmentDO;
import com.z.module.ai.web.rest.admin.knowledge.vo.segment.AiKnowledgeSegmentProcessRespVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Knowledge Segment Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiKnowledgeSegmentRepository extends JpaRepository<AiKnowledgeSegmentDO, Long> {

    /**
     * Find by document id
     *
     * @param documentId document id
     * @return list of AI knowledge segments
     */
    List<AiKnowledgeSegmentDO> findByDocumentId(Long documentId);

    /**
     * Find by knowledge id and status
     *
     * @param knowledgeId knowledge id
     * @param status status
     * @return list of AI knowledge segments
     */
    List<AiKnowledgeSegmentDO> findByKnowledgeIdAndStatus(Long knowledgeId, Integer status);

    /**
     * Find all by vector id in list
     *
     * @param vectorIds list of vector ids
     * @return list of AI knowledge segments
     */
    List<AiKnowledgeSegmentDO> findAllByVectorIdIn(List<String> vectorIds);

    List<AiKnowledgeSegmentDO> findAllByDocumentIdIn(List<Long> documentIds);
}
