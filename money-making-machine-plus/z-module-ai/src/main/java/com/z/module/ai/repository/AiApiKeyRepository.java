package com.z.module.ai.repository;

import com.z.module.ai.domain.model.AiApiKeyDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * AI Api Key Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiApiKeyRepository extends JpaRepository<AiApiKeyDO, Long> {

    /**
     * Find first by platform and status
     *
     * @param platform platform
     * @param status status
     * @return AI api key
     */
    AiApiKeyDO findFirstByPlatformAndStatus(String platform, Integer status);
}
