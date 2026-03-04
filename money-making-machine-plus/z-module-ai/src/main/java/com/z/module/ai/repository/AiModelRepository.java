package com.z.module.ai.repository;

import com.z.module.ai.domain.model.AiModelDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI Model Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiModelRepository extends JpaRepository<AiModelDO, Long> {

    /**
     * Find first by status and type order by sort asc
     *
     * @param status status
     * @param type type
     * @return AI model
     */
    AiModelDO findFirstByStatusAndTypeOrderBySortAsc(Integer status, Integer type);

    /**
     * Find by status and type and platform
     *
     * @param status status
     * @param type type
     * @param platform platform
     * @return list of AI models
     */
    List<AiModelDO> findByStatusAndTypeAndPlatform(Integer status, Integer type, String platform);
}
