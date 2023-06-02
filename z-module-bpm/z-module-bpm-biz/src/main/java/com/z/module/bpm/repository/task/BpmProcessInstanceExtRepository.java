package com.z.module.bpm.repository.task;

import com.z.module.bpm.domain.task.BpmProcessInstanceExtDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link BpmProcessInstanceExtDO} entity.
 */
@Repository
public interface BpmProcessInstanceExtRepository extends JpaRepository<BpmProcessInstanceExtDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    Optional<BpmProcessInstanceExtDO> findOneByProcessInstanceId(String id);
}
