package com.z.module.screen.repository;

import com.z.module.screen.domain.GoViewProjectDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link GoViewProjectDO} entity.
 */
@Repository
public interface GoViewProjectRepository extends JpaRepository<GoViewProjectDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    Page<GoViewProjectDO> findAllByCreatedBy(PageRequest pageReqVO, String loginName);
}
