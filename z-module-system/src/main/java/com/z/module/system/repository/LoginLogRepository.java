package com.z.module.system.repository;

import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.mapper.LoginLogCount;
import com.z.module.system.repository.mapper.LoginLogMonthlyGroupCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data SQL repository for the LoginLogging entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    @Query("select new com.z.module.system.repository.mapper.LoginLogCount(browser, os, count(p) ) from LoginLog p group by p.browser, p.os")
    List<LoginLogCount> getCountByBrowserAndOs();

    @Query("select new com.z.module.system.repository.mapper.LoginLogMonthlyGroupCount( month(createdDate), count(1) ) from LoginLog t where year(createdDate) = ?1 group by month(createdDate)")
    List<LoginLogMonthlyGroupCount> findAllByYearGroupByMonth(Integer year);

    List<LoginLog> findAllByCreatedDateBetween(Instant startDayOfWeek, Instant endDayOfWeek);
}

