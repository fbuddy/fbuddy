package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import org.thingsboard.server.common.data.OsType;
import org.thingsboard.server.dao.model.BaseEntity;
import org.thingsboard.server.dao.model.PrimaryEntity;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "os_details")
public class OSTypeEntity extends PrimaryEntity<OsType> {
    @Column(name = "os_type")
    private String osType;

    @Column(name = "hardware_version")
    private String hversionName;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;


    @Override
    public OsType toData() {
        return new OsType(osType,hversionName);
    }
}
