package org.thingsboard.server.dao.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
public abstract class PrimaryEntity<D> extends BaseSqlEntity<D> {

    @Column(name = ModelConstants.UPDATED_TIME_PROPERTY, updatable = false)
    protected long updatedTime;

}
