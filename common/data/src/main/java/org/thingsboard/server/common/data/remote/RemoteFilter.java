package org.thingsboard.server.common.data.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class RemoteFilter {
    @ApiModelProperty(position = 1, value = "Remote No")
    private String remoteNo;
    @ApiModelProperty(position = 2, value = "Status")
    private String statusId;
    @ApiModelProperty(position = 3, value = "Hardware Version")
    private String hwVersion;
    @ApiModelProperty(position = 4, value = "Production Date From")
    private Long productionDateFrom;
    @ApiModelProperty(position = 5, value = "Production Date To")
    private Long productionDateTo;
}
