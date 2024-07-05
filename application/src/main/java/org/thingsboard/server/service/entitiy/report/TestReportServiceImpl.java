package org.thingsboard.server.service.entitiy.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.TestReport;
import org.thingsboard.server.dao.sql.testreport.JpaTestReportDao;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@TbCoreComponent
@Service
@Slf4j
public class TestReportServiceImpl extends AbstractTbEntityService implements TestReportService {
    @Autowired
    private JpaTestReportDao jpaTestReport;

    public TestReport saveTestReport(TestReport testReport) {
        return jpaTestReport.saveAndFlush(testReport);
    }

    public List<TestReport> getTestReportsByMeterNo(Long meterNo) {
        return jpaTestReport.findByMeterNo(meterNo).stream().map(a->a.toData()).collect(Collectors.toList());
    }
}
