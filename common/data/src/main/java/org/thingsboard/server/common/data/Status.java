package org.thingsboard.server.common.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.StatusId;
import org.thingsboard.server.common.data.id.UserId;

@ApiModel
@Data
public class Status extends BaseData<StatusId> {
    @ApiModelProperty(position = 3, value = "Type")
    private String type;
    @ApiModelProperty(position = 4, value = "Name")
    private String name;
    @ApiModelProperty(position = 5, value = "Value")
    private String value;
    @ApiModelProperty(position = 6, value = "Is Active")
    private boolean isActive;
    @ApiModelProperty(position = 7, value = "Id of user who is creating the Status")
    private UserId createdBy;
    @ApiModelProperty(position = 8, value = "Id of user who is updating the Status")
    private UserId updatedBy;
    @ApiModelProperty(position = 9, value = "Timestamp of the status updation")
    protected Long updatedTime;

    @ApiModelProperty(position = 1, value = "JSON object with the status Id. " +
            "Specify this field to update the status. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new status.")
    @Override
    public StatusId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the asset installation creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    public Status() {
        super();
    }

    public Status(StatusId statusId) {
        super(statusId);
    }

    public Status(Status status) {
        super(status);
        this.type = status.getType();
        this.name = status.getName();
        this.value = status.getValue();
        this.isActive = status.isActive();
        this.createdBy = status.getCreatedBy();
        this.updatedBy = status.getUpdatedBy();
        this.updatedTime = status.getUpdatedTime();
    }
}
