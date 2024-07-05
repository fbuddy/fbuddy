package org.thingsboard.server.dao.asset;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.common.data.asset.AssetInstallationFilter;
import org.thingsboard.server.common.data.asset.AssetInstallationInfo;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.List;

@Service("AssetInstallationDaoService")
@Slf4j
public class AssetInstallationServiceImpl extends AbstractEntityService implements AssetInstallationService {
    @Autowired
    private AssetInstallationDao assetInstallationDao;

    @Override
    public AssetInstallation findByAssetInstallationData(Long houseHoldId, Long meterNo, Long remoteNo, Integer tvId, String meterType, boolean isActive) {
        return assetInstallationDao.findByAssetInstallationData(houseHoldId, meterNo, remoteNo, tvId, meterType, isActive);
    }

    @Override
    @Transactional
    public AssetInstallation save(AssetInstallation assetInstallation) throws ThingsboardException {
        return assetInstallationDao.save(TenantId.SYS_TENANT_ID, assetInstallation);
    }

    @Override
    public PageData<AssetInstallationInfo> getAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink) {
        return assetInstallationDao.findAssetInstallation(assetInstallationFilter, pageLink);
    }

    @Override
    public List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId) {
        return assetInstallationDao.getHoulseholdUninstalledTVIds(houseHoldId);
    }
}
