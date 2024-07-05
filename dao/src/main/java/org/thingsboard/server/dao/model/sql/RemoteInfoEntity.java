package org.thingsboard.server.dao.model.sql;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.remote.RemoteInfo;

import java.util.HashMap;
import java.util.Map;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class RemoteInfoEntity extends AbstractRemoteEntity<RemoteInfo> {
    public static final Map<String, String> remoteStatusColumnMap = new HashMap<>();

    static {
        remoteStatusColumnMap.put("statusName", "s.name");
    }

    private String statusName;

    public RemoteInfoEntity() {
        super();
    }

    public RemoteInfoEntity(RemoteEntity remoteEntity, String statusName) {
        super(remoteEntity);
        this.statusName = statusName;
    }

    @Override
    public RemoteInfo toData() {
        return new RemoteInfo(super.toRemoteData(), statusName);
    }
}
