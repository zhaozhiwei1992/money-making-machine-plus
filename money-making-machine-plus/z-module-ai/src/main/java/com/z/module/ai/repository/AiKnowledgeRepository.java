package com.z.module.ai.repository;

import com.z.module.ai.domain.knowledge.AiKnowledgeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Knowledge Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiKnowledgeRepository extends JpaRepository<AiKnowledgeDO, Long> {

    /**
     * Find by status
     *
     * @param status status
     * @return list of AI knowledge
     */
    List<AiKnowledgeDO> findByStatus(Integer status);
}
