package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.asset.AssetInstallationInfo;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AssetInstallationInfoEntity extends AbstractAssetInstallationEntity<AssetInstallationInfo> {
    public static final Map<String, String> assetInstallationInfoColumnMap = new HashMap<>();

    static {
        assetInstallationInfoColumnMap.put("meterHwVersion", "os.hversionName");
        assetInstallationInfoColumnMap.put("remoteHwVersion", "r.hwVersion");
    }

    private String meterHwVersion;
    private String remoteHwVersion;

    public AssetInstallationInfoEntity() {
        super();
    }

    public AssetInstallationInfoEntity(AssetInstallationEntity assetInstallationEntity,
                                       String meterHwVersion,
                                       String remoteHwVersion) {
        super(assetInstallationEntity);
        this.meterHwVersion = meterHwVersion;
        this.remoteHwVersion = remoteHwVersion;
    }

    @Override
    public AssetInstallationInfo toData() {
        return new AssetInstallationInfo(super.toAssetInstallation(), meterHwVersion, remoteHwVersion);
    }
}
