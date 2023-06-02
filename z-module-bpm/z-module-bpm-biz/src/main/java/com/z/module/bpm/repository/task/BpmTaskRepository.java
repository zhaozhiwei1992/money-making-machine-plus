package com.z.module.bpm.repository.task;

import com.z.module.bpm.domain.task.BpmTaskExtDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link BpmTaskExtDO} entity.
 */
@Repository
public interface BpmTaskRepository extends JpaRepository<BpmTaskExtDO, Long> {
    void deleteAllByIdIn(List<Long> idList);

    List<BpmTaskExtDO> findAllByTaskIdIn(Set<String> strings);

    Optional<BpmTaskExtDO> findOneByTaskId(String id);
}
