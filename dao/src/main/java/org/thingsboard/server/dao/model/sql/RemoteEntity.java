package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.Remote;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.REMOTE_TABLE_NAME;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = REMOTE_TABLE_NAME)
public class RemoteEntity extends AbstractRemoteEntity<Remote> {
    public RemoteEntity() {
        super();
    }

    public RemoteEntity(Remote remote) {
        super(remote);
    }

    @Override
    public Remote toData() {
        return super.toRemoteData();
    }


}
