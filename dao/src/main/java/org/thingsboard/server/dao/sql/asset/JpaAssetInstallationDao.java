package org.thingsboard.server.dao.sql.asset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.asset.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.util.TypeCastUtil;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.asset.AssetInstallationDao;
import org.thingsboard.server.dao.model.sql.AssetInstallationEntity;
import org.thingsboard.server.dao.model.sql.AssetInstallationInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

@Component
@SqlDao
public class JpaAssetInstallationDao extends JpaAbstractDao<AssetInstallationEntity, AssetInstallation> implements AssetInstallationDao {
    @Autowired
    private AssetInstallationRepository assetInstallationRepository;

    @Override
    protected Class<AssetInstallationEntity> getEntityClass() {
        return AssetInstallationEntity.class;
    }

    @Override
    protected JpaRepository<AssetInstallationEntity, UUID> getRepository() {
        return assetInstallationRepository;
    }

    @Override
    public AssetInstallation findByAssetInstallationData(Long houseHoldId, Long meterNo, Long remoteNo, Integer tvId, String meterType, boolean isActive) {
        return DaoUtil.getData(assetInstallationRepository.findByAssetInstallationData(houseHoldId, meterNo, remoteNo, tvId, meterType, isActive));
    }

    @Override
    public PageData<AssetInstallationInfo> findAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink) {
        List<Long> meterNos = null;
        List<Long> houseHoldIds = null;
        List<Long> remoteNos = null;

        if (assetInstallationFilter.getMeterNo() != null)
            meterNos = TypeCastUtil.getSerialNumberData(assetInstallationFilter.getMeterNo());
        if (assetInstallationFilter.getRemoteNo() != null)
            remoteNos = TypeCastUtil.getSerialNumberData(assetInstallationFilter.getRemoteNo());
        if (assetInstallationFilter.getHouseHoldId() != null)
            houseHoldIds = TypeCastUtil.getSerialNumberData(assetInstallationFilter.getHouseHoldId());

        return DaoUtil.toPageData(assetInstallationRepository.findAssetInstallation(houseHoldIds != null && houseHoldIds.size() == 1 ? houseHoldIds.get(0) : null,
                houseHoldIds != null && houseHoldIds.size() > 1 && !houseHoldIds.get(0).equals(0L) ? houseHoldIds : null,
                houseHoldIds != null && houseHoldIds.size() > 1 && houseHoldIds.get(0).equals(0L) ? houseHoldIds.get(1) : null,
                houseHoldIds != null && houseHoldIds.size() > 1 && houseHoldIds.get(0).equals(0L) ? houseHoldIds.get(2) : null,
                meterNos != null && meterNos.size() == 1 ? meterNos.get(0) : null,
                meterNos != null && meterNos.size() > 1 && !meterNos.get(0).equals(0L) ? meterNos : null,
                meterNos != null && meterNos.size() > 1 && meterNos.get(0).equals(0L) ? meterNos.get(1) : null,
                meterNos != null && meterNos.size() > 1 && meterNos.get(0).equals(0L) ? meterNos.get(2) : null,
                remoteNos != null && remoteNos.size() == 1 ? remoteNos.get(0) : null,
                remoteNos != null && remoteNos.size() > 1 && !remoteNos.get(0).equals(0L) ? remoteNos : null,
                remoteNos != null && remoteNos.size() > 1 && remoteNos.get(0).equals(0L) ? remoteNos.get(1) : null,
                remoteNos != null && remoteNos.size() > 1 && remoteNos.get(0).equals(0L) ? remoteNos.get(2) : null, assetInstallationFilter.getStatus() != null ? true : null,
                assetInstallationFilter.getStatus() != null ? MeterInstallationStatus.getByName(assetInstallationFilter.getStatus()) : null, assetInstallationFilter.getStatus() != null ? RemoteInstallationStatus.getByName(assetInstallationFilter.getStatus()) : null,
                assetInstallationFilter.getMeterType(),
                assetInstallationFilter.getAssetType() == null && assetInstallationFilter.getInstallationDateFrom() != null ? assetInstallationFilter.getInstallationDateFrom() : null,
                assetInstallationFilter.getAssetType() == null && assetInstallationFilter.getInstallationDateTo() != null ? assetInstallationFilter.getInstallationDateTo() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.METER.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getInstallationDateFrom() != null ? assetInstallationFilter.getInstallationDateFrom() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.METER.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getInstallationDateTo() != null ? assetInstallationFilter.getInstallationDateTo() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.REMOTE.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getInstallationDateFrom() != null ? assetInstallationFilter.getInstallationDateFrom() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.REMOTE.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getInstallationDateTo() != null ? assetInstallationFilter.getInstallationDateTo() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.REMOTE.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getHardwareVersion() != null ? assetInstallationFilter.getHardwareVersion() : null,
                assetInstallationFilter.getAssetType() != null && AssetType.METER.getName().equals(assetInstallationFilter.getAssetType()) && assetInstallationFilter.getHardwareVersion() != null ? assetInstallationFilter.getHardwareVersion() : null,
                DaoUtil.toPageable(pageLink, AssetInstallationInfoEntity.assetInstallationInfoColumnMap)));

    }

    @Override
    public List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId) {
        return assetInstallationRepository.findHoulseholdUninstalledTVIds(houseHoldId);
    }

}
