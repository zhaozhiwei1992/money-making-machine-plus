package com.z.module.ai.repository;

import com.z.module.ai.domain.workflow.AiWorkflowDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AI Workflow Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiWorkflowRepository extends JpaRepository<AiWorkflowDO, Long> {

    /**
     * Find by code
     *
     * @param code workflow code
     * @return AI workflow
     */
    AiWorkflowDO findByCode(String code);
}
