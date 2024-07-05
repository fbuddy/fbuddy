package org.thingsboard.server.common.data.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetInstallationInfo extends AssetInstallation {
    @ApiModelProperty(position = 22, value = "Meter Hardware Version.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String meterHwVersion;
    @ApiModelProperty(position = 23, value = "Remote Hardware Version.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String remoteHwVersion;

    public AssetInstallationInfo() {
        super();
    }

    public AssetInstallationInfo(AssetInstallation assetInstallation, String meterHwVersion, String remoteHwVersion) {
        super(assetInstallation);
        this.meterHwVersion = meterHwVersion;
        this.remoteHwVersion = remoteHwVersion;
    }
}
