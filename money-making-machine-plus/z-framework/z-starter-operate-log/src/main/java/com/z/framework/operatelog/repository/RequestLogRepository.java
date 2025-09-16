package com.z.framework.operatelog.repository;

import com.z.framework.operatelog.domain.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the RequestLogging entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    Optional<RequestLog> findOneByTraceId(String traceId);
}
