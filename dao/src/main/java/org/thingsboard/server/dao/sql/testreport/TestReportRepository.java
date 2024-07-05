package org.thingsboard.server.dao.sql.testreport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.DeviceEntity;
import org.thingsboard.server.dao.model.sql.TestReportEntity;

import java.util.List;
import java.util.UUID;

public interface TestReportRepository extends JpaRepository<TestReportEntity, UUID>{
    List<TestReportEntity> findByMeterNo(Long meterNo);
}
