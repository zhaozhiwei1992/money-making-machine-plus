package com.z.module.ai.repository;

import com.z.module.ai.domain.model.AiToolDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * AI Tool Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiToolRepository extends JpaRepository<AiToolDO, Long> {

    /**
     * Find by status
     *
     * @param status status
     * @return list of AI tools
     */
    List<AiToolDO> findByStatus(Integer status);

    /**
     * Find all by id in collection
     *
     * @param ids collection of ids
     * @return list of AI tools
     */
    List<AiToolDO> findAllByIdIn(Collection<Long> ids);
}
