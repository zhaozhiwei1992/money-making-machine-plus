package com.z.module.report.repository;

import com.z.module.report.domain.ReportField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ReportFieldRepository extends JpaRepository<ReportField, Long> {
    List<ReportField> findAllByReportCodeIn(Collection<String> reportCodeSet);
}
