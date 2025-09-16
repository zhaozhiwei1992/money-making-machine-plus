package com.z.module.screen.repository;

import com.z.module.screen.domain.GoViewFileDO;
import com.z.module.screen.domain.GoViewProjectDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link GoViewFileDO} entity.
 */
@Repository
public interface GoViewFileRepository extends JpaRepository<GoViewFileDO, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
