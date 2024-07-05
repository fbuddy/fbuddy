package org.thingsboard.server.service.entitiy.report;

import org.thingsboard.server.common.data.TestReport;

import java.util.List;

public interface TestReportService {
    TestReport saveTestReport(TestReport testReport);
    List<TestReport> getTestReportsByMeterNo(Long meterNo);
}
