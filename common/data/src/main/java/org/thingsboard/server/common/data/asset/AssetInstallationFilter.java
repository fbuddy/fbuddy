package org.thingsboard.server.common.data.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AssetInstallationFilter {
    @ApiModelProperty(position = 1, value = "Asset Type")
    private String assetType;
    @ApiModelProperty(position = 2, value = "Household Id")
    private String houseHoldId;
    @ApiModelProperty(position = 3, value = "Meter No")
    private String meterNo;
    @ApiModelProperty(position = 4, value = "Remote No")
    private String remoteNo;
    @ApiModelProperty(position = 5, value = "Status")
    private String status;
    @ApiModelProperty(position = 6, value = "Meter Type")
    private String meterType;
    @ApiModelProperty(position = 7, value = "Hardware Version")
    private String hardwareVersion;
    @ApiModelProperty(position = 8, value = "Installation Date From")
    private Long installationDateFrom;
    @ApiModelProperty(position = 9, value = "Installation Date To")
    private Long installationDateTo;

}
