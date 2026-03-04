package com.z.module.ai.repository;

import com.z.module.ai.domain.model.AiChatRoleDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * AI Chat Role Repository
 *
 * @author zhaozhiwei
 * @email zhaozhiweishanxi@gmail.com
 */
@Repository
public interface AiChatRoleRepository extends JpaRepository<AiChatRoleDO, Long> {

    /**
     * Select list grouped by category
     *
     * @param status status
     * @return list of AI chat roles
     */
    @Query("SELECT r FROM AiChatRoleDO r WHERE r.status = :status ORDER BY r.category")
    List<AiChatRoleDO> selectListGroupByCategory(@Param("status") Integer status);

    /**
     * Select list by name
     *
     * @param name name
     * @return list of AI chat roles
     */
    List<AiChatRoleDO> findAllByName(String name);

    /**
     * Find all by id in collection
     *
     * @param ids collection of ids
     * @return list of AI chat roles
     */
    List<AiChatRoleDO> findAllByIdIn(Collection<Long> ids);
}
