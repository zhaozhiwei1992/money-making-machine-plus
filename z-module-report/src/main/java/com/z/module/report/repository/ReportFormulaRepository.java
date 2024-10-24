package com.z.module.report.repository;

import com.z.module.report.domain.Report;
import com.z.module.report.domain.ReportFormula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportFormulaRepository extends JpaRepository<ReportFormula, Long> {
    List<ReportFormula> findAllByReportCodeAndFormulaType(String reportCode, String number);

    List<ReportFormula> findAllByReportCodeInAndFormulaType(List<String> currTypeReportCodeList, String number);
}
