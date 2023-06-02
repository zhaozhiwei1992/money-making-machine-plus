package com.z.module.bpm.repository.definition;

import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.domain.definition.BpmUserGroupDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link BpmFormDO} entity.
 */
@Repository
public interface BpmUserGroupRepository extends JpaRepository<BpmUserGroupDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<BpmUserGroupDO> findAllByStatus(Integer status);

    Page<BpmUserGroupDO> findAllByNameAndStatus(PageRequest page, String name, Integer status);
}
