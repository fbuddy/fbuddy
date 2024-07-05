package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

public class RemoteEntityId extends UUIDBased implements EntityId{
    private static final long serialVersionUID = 1L;

    @JsonCreator
    public RemoteEntityId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static RemoteEntityId fromString(String remoteEntityId) {
        return new RemoteEntityId(UUID.fromString(remoteEntityId));
    }
    @Override
    @ApiModelProperty(position = 2, required = true, value = "string", example = "REMOTE", allowableValues = "REMOTE")
    public EntityType getEntityType() {
        return EntityType.REMOTE;
    }
}
