package com.example.repository;

import com.example.domain.TaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the TaskLogging entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskLogRepository extends JpaRepository<TaskLog, Long> {
    Optional<TaskLog> findOneByTraceId(String traceId);
}
