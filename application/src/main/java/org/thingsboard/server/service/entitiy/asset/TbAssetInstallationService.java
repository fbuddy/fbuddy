package org.thingsboard.server.service.entitiy.asset;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.common.data.asset.AssetInstallationAction;
import org.thingsboard.server.common.data.asset.AssetInstallationFilter;
import org.thingsboard.server.common.data.asset.AssetInstallationInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;

import java.util.List;

public interface TbAssetInstallationService {
    public AssetInstallation save(AssetInstallation assetInstallation, User user, AssetInstallationAction assetInstallationAction) throws ThingsboardException;

    public PageData<AssetInstallationInfo> getAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink);

    public List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId);
}
