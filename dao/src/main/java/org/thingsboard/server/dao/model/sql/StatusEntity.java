package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.Status;
import org.thingsboard.server.common.data.id.StatusId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = STATUS_TABLE_NAME)
public class StatusEntity extends BaseSqlEntity<Status> {

    @Column(name = TYPE_PROPERTY)
    private String type;

    @Column(name = NAME_PROPERTY)
    private String name;

    @Column(name = VALUE_PROPERTY)
    private String value;

    @Column(name = IS_ACTIVE_PROPERTY)
    private boolean isActive;

    @Column(name = CREATED_BY)
    private UUID createdBy;
    @Column(name = UPDATED_BY_PROPERTY)
    private UUID updatedBy;
    @Column(name = UPDATED_TIME_PROPERTY)
    protected Long updatedTime;

    public StatusEntity() {
        super();
    }

    public StatusEntity(Status status) {
        if (status.getId() != null) {
            this.setUuid(status.getId().getId());
        }
        this.setCreatedTime(status.getCreatedTime());
        this.type = status.getType();
        this.name = status.getName();
        this.value = status.getValue();
        this.isActive = status.isActive();
        this.createdBy = status.getCreatedBy().getId();
        this.updatedBy = status.getUpdatedBy().getId();
        this.updatedTime = status.getUpdatedTime();
    }

    @Override
    public Status toData() {
        Status status = new Status();
        status.setId(new StatusId(this.id));
        status.setType(this.type);
        status.setName(this.name);
        status.setValue(this.value);
        status.setActive(this.isActive);
        status.setCreatedBy(new UserId(this.createdBy));
        status.setUpdatedBy(new UserId(this.updatedBy));
        status.setUpdatedTime(this.updatedTime);
        status.setCreatedTime(this.createdTime);
        return status;
    }
}
