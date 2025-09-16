package com.z.module.report.repository;

import com.z.module.report.domain.ReportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReportItemRepository extends JpaRepository<ReportItem, Long> {
    List<ReportItem> findAllByReportCodeIn(Collection<String> reportCodeSet);
}
