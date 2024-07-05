package org.thingsboard.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.EntityType;

import java.util.UUID;

@ApiModel
public class StatusId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public StatusId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static StatusId fromString(String statusId) {
        return new StatusId(UUID.fromString(statusId));
    }

    @Override
    @ApiModelProperty(position = 2, required = true, value = "string", example = "STATUS", allowableValues = "STATUS")
    public EntityType getEntityType() {
        return EntityType.STATUS;
    }
}
