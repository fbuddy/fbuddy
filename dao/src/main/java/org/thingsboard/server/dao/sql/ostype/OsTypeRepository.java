package org.thingsboard.server.dao.sql.ostype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.ExportableEntityRepository;
import org.thingsboard.server.dao.model.sql.OSTypeEntity;
import org.thingsboard.server.dao.model.sql.TestReportEntity;

import java.util.Optional;
import java.util.UUID;

public interface OsTypeRepository extends JpaRepository<OSTypeEntity, UUID> {

    Optional<OSTypeEntity> findByOsTypeAndHversionName(String osType, String hversionName);
}
