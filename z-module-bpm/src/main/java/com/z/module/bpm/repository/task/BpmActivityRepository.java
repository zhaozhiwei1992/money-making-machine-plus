package com.z.module.bpm.repository.task;

import com.z.module.bpm.domain.task.BpmActivityDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link BpmActivityDO} entity.
 */
@Repository
public interface BpmActivityRepository extends JpaRepository<BpmActivityDO, Long> {
    void deleteAllByIdIn(List<Long> idList);
}
