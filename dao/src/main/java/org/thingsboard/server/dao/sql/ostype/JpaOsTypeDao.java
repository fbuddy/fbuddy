package org.thingsboard.server.dao.sql.ostype;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.OsType;
import org.thingsboard.server.dao.model.sql.OSTypeEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Optional;
import java.util.UUID;

@SqlDao
@Slf4j
@Component
public class JpaOsTypeDao extends JpaAbstractDao<OSTypeEntity, OsType> {

    @Autowired
    private OsTypeRepository osTypeRepository;

    @Override
    protected Class<OSTypeEntity> getEntityClass() {
        return OSTypeEntity.class;
    }

    @Override
    protected JpaRepository<OSTypeEntity, UUID> getRepository() {
        return osTypeRepository;
    }

    public OSTypeEntity findByOsTypeAndHversionName(String osType,String hversionName){
        if (osType==null || hversionName==null)
            return null;
        Optional<OSTypeEntity> osTypeEntity=osTypeRepository.findByOsTypeAndHversionName(osType,hversionName);
        return osTypeEntity.orElse(null);
    }

    public OSTypeEntity findById(UUID id) {
        Optional<OSTypeEntity> osTypeEntity = osTypeRepository.findById(id);
        return osTypeEntity.orElse(null);
    }

}
