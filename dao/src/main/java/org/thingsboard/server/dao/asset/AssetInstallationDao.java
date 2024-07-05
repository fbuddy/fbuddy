package org.thingsboard.server.dao.asset;

import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.common.data.asset.AssetInstallationFilter;
import org.thingsboard.server.common.data.asset.AssetInstallationInfo;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;

public interface AssetInstallationDao extends Dao<AssetInstallation> {

    AssetInstallation findByAssetInstallationData(Long houseHoldId, Long meterNo, Long remoteNo, Integer tvId, String meterType, boolean isActive);

    PageData<AssetInstallationInfo> findAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink);

    List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId);
}


