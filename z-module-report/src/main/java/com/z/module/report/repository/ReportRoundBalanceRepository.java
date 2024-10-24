package com.z.module.report.repository;

import com.z.module.report.domain.ReportRoundBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRoundBalanceRepository extends JpaRepository<ReportRoundBalance, Long> {
    List<ReportRoundBalance> findAllByReportCodeIn(List<String> reportCodeList);

    void deleteAllByReportCodeIn(List<String> reportCodeList);
}
