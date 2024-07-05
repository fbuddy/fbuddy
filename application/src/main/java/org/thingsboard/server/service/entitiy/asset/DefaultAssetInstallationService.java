package org.thingsboard.server.service.entitiy.asset;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.asset.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.asset.AssetInstallationService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;

import java.util.List;

@AllArgsConstructor
@TbCoreComponent
@Service
@Slf4j
public class DefaultAssetInstallationService extends AbstractTbEntityService implements TbAssetInstallationService {
    private final AssetInstallationService assetInstallationService;

    @Override
    public AssetInstallation save(AssetInstallation assetInstallation, User user, AssetInstallationAction assetInstallationAction) throws ThingsboardException {
        AssetInstallation oldAssetInstallation;
        AssetInstallation updatedAssetInstallation;
        Long updatedTime = System.currentTimeMillis();
        Long meterNo = AssetInstallationAction.METER_REPLACE.equals(assetInstallationAction) ? assetInstallation.getOldMeterNo() : assetInstallation.getMeterNo();
        Long remoteNo = AssetInstallationAction.REMOTE_REPLACE.equals(assetInstallationAction) ? assetInstallation.getOldRemoteNo() : assetInstallation.getRemoteNo();
        ActionType actionType = AssetInstallationAction.ASSET_INSTALL.equals(assetInstallationAction) ? ActionType.ADDED : ActionType.UPDATED;

        try {
            if (AssetInstallationAction.ASSET_INSTALL.equals(assetInstallationAction)) {
                assetInstallation.setMeterInstallationStatus(MeterInstallationStatus.INSTALL_IN_PROGRESS);
                assetInstallation.setRemoteInstallationStatus(RemoteInstallationStatus.INSTALL_IN_PROGRESS);
            }
            if (!AssetInstallationAction.ASSET_INSTALL.equals(assetInstallationAction)) {
                oldAssetInstallation = checkNotNull(assetInstallationService.findByAssetInstallationData(assetInstallation.getHouseHoldId(), meterNo, remoteNo, assetInstallation.getTvId(), assetInstallation.getMeterType(), true));
                if (oldAssetInstallation != null) {
                    oldAssetInstallation.setActive(false);
                    oldAssetInstallation.setUpdatedBy(user.getId());
                    oldAssetInstallation.setUpdatedTime(updatedTime);
                    oldAssetInstallation.setMInstallationActiveTo(updatedTime);
                    oldAssetInstallation.setRPairingActiveTo(updatedTime);

                    assetInstallationService.save(oldAssetInstallation);
                }
                if (AssetInstallationAction.METER_UNINSTALL.equals(assetInstallationAction)) {
                    assetInstallation.setMeterInstallationStatus(MeterInstallationStatus.UNINSTALL_IN_PROGRESS);
                    assetInstallation.setRemoteInstallationStatus(oldAssetInstallation.getRemoteInstallationStatus());
                }
                if (AssetInstallationAction.REMOTE_UNINSTALL.equals(assetInstallationAction)) {
                    assetInstallation.setMeterInstallationStatus(oldAssetInstallation.getMeterInstallationStatus());
                    assetInstallation.setRemoteInstallationStatus(RemoteInstallationStatus.UNINSTALL_IN_PROGRESS);
                }
                if (AssetInstallationAction.METER_REPLACE.equals(assetInstallationAction)) {
                    assetInstallation.setMeterInstallationStatus(MeterInstallationStatus.INSTALL_IN_PROGRESS);
                    assetInstallation.setRemoteInstallationStatus(oldAssetInstallation.getRemoteInstallationStatus());
                }
                if (AssetInstallationAction.REMOTE_REPLACE.equals(assetInstallationAction)) {
                    assetInstallation.setMeterInstallationStatus(oldAssetInstallation.getMeterInstallationStatus());
                    assetInstallation.setRemoteInstallationStatus(RemoteInstallationStatus.INSTALL_IN_PROGRESS);
                }
            }
            assetInstallation.setActive(true);
            assetInstallation.setCreatedBy(user.getId());
            assetInstallation.setUpdatedBy(user.getId());
            assetInstallation.setUpdatedTime(updatedTime);
            assetInstallation.setMInstallationActiveFrom(updatedTime);
            assetInstallation.setRPairingActiveFrom(updatedTime);
            updatedAssetInstallation = checkNotNull(assetInstallationService.save(assetInstallation));
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, updatedAssetInstallation.getId(), updatedAssetInstallation, actionType, user);
            return updatedAssetInstallation;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.ASSET_INSTALLATION), assetInstallation, actionType, user, e);
            throw e;
        }
    }

    @Override
    public PageData<AssetInstallationInfo> getAssetInstallation(AssetInstallationFilter assetInstallationFilter, PageLink pageLink) {
        return assetInstallationService.getAssetInstallation(assetInstallationFilter, pageLink);
    }

    @Override
    public List<Integer> getHoulseholdUninstalledTVIds(Long houseHoldId) {
        return assetInstallationService.getHoulseholdUninstalledTVIds(houseHoldId);
    }

}
