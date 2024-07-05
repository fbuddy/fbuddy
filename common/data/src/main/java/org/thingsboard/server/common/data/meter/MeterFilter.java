package org.thingsboard.server.common.data.meter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class MeterFilter {
    @ApiModelProperty(position = 1, value = "Meter Type")
    private String meterType;
    @ApiModelProperty(position = 2, value = "Meter No")
    private String meterNo;
    @ApiModelProperty(position = 3, value = "Status")
    private String statusId;
    @ApiModelProperty(position = 4, value = "Hardware Version")
    private String hwVersion;
    @ApiModelProperty(position = 5, value = "Production Date From")
    private Long productionDateFrom;
    @ApiModelProperty(position = 6, value = "Production Date To")
    private Long productionDateTo;
}
