package org.thingsboard.server.dao.asset;

import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.common.data.asset.AssetInstallationFilter;
import org.thingsboard.server.common.data.asset.AssetInstallationInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

public interface AssetInstallationService {

    AssetInstallation findByAssetInstallationData(Long houseHoldId, Long meterNo, Long remoteNo, Integer tvId, String meterType, boolean isActive);

    AssetInstallation save(AssetInstallation assetInstallation) throws ThingsboardException;

    PageData<AssetInstallationInfo> getAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink);

    List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId);
}
