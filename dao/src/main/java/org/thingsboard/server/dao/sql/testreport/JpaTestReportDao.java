package org.thingsboard.server.dao.sql.testreport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Status;
import org.thingsboard.server.common.data.TestReport;
import org.thingsboard.server.dao.model.sql.StatusEntity;
import org.thingsboard.server.dao.model.sql.TestReportEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;
@Component
@SqlDao
@Slf4j
public class JpaTestReportDao extends JpaAbstractDao<TestReportEntity, TestReport> {
    @Autowired
    private TestReportRepository testReportRepository;

    @Override
    protected Class<TestReportEntity> getEntityClass() {
        return TestReportEntity.class;
    }

    @Override
    protected JpaRepository<TestReportEntity, UUID> getRepository() {
        return testReportRepository;
    }

    public List<TestReportEntity> findByMeterNo(Long meterNo){
        return testReportRepository.findByMeterNo(meterNo);
    }

    @Transactional
    public TestReport saveAndFlush(TestReport testReport) {
        TestReport result = this.save(null, testReport);
        testReportRepository.flush();
        return result;
    }
}
