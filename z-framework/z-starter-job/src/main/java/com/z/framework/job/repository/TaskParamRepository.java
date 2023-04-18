package com.z.framework.job.repository;

import com.z.framework.job.domain.TaskParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the TaskParam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskParamRepository extends JpaRepository<TaskParam, Long> {
    List<TaskParam> findAllByEnable(boolean enable);

    Optional<TaskParam> findOneByStartClass(String startClass);
}
