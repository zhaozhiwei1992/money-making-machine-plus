package com.z.module.report.repository;

import com.z.module.report.domain.ReportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReportDataRepository extends JpaRepository<ReportData, Long> {
    List<ReportData> findAllByReportCodeIn(Collection<String> reportCodeSet);

    void deleteAllByReportCodeIn(List<String> reportCodeList);
}
