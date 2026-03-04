package com.z.module.ai.repository;

import com.z.module.ai.domain.knowledge.AiKnowledgeDocumentDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Knowledge Document Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiKnowledgeDocumentRepository extends JpaRepository<AiKnowledgeDocumentDO, Long> {

    /**
     * Find by knowledge id
     *
     * @param knowledgeId knowledge id
     * @return list of AI knowledge documents
     */
    List<AiKnowledgeDocumentDO> findByKnowledgeId(Long knowledgeId);
}
