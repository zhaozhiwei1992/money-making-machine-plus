package com.z.module.system.repository;

import com.z.module.system.domain.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LoginLogging entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {}
