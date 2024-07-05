package org.thingsboard.server.dao.model.sql;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.common.data.asset.MeterInstallationStatus;
import org.thingsboard.server.common.data.asset.RemoteInstallationStatus;
import org.thingsboard.server.common.data.id.AssetInstallationId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
public abstract class AbstractAssetInstallationEntity<T extends AssetInstallation> extends BaseSqlEntity<T> {
    @Enumerated(EnumType.STRING)
    @Column(name = METER_INSTALLATION_STATUS)
    private MeterInstallationStatus meterInstallationStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = REMOTE_INSTALLATION_STATUS)
    private RemoteInstallationStatus remoteInstallationStatus;
    @Column(name = HOUSE_HOLD_ID)
    private Long houseHoldId;
    @Column(name = METER_NO)
    private Long meterNo;
    @Column(name = REMOTE_NO)
    private Long remoteNo;
    @Column(name = TV_ID)
    private Integer tvId;
    @Column(name = PMA_W_ID)
    private String pmaWId;
    @Column(name = FIELD_MODE)
    private String fieldMode;
    @Column(name = METER_REMARKS)
    private String meterRemarks;
    @Column(name = REMOTE_REMARKS)
    private String remoteRemarks;
    @Column(name = M_INSTALLATION_ACTIVE_FROM)
    private long mInstallationActiveFrom;
    @Column(name = M_INSTALLATION_ACTIVE_TO)
    private Long mInstallationActiveTo;
    @Column(name = R_PAIRING_ACTIVE_FROM)
    private long rPairingActiveFrom;
    @Column(name = R_PAIRING_ACTIVE_TO)
    private Long rPairingActiveTo;
    @Column(name = METER_TYPE)
    private String meterType;
    @Column(name = IS_ACTIVE_PROPERTY)
    private boolean isActive;
    @Column(name = CREATED_BY)
    private UUID createdBy;
    @Column(name = UPDATED_BY_PROPERTY)
    private UUID updatedBy;
    @Column(name = UPDATED_TIME_PROPERTY)
    protected Long updatedTime;

    public AbstractAssetInstallationEntity() {
        super();
    }

    public AbstractAssetInstallationEntity(AssetInstallation assetInstallation) {
        if (assetInstallation.getId() != null) {
            this.setUuid(assetInstallation.getId().getId());
        }
        this.setCreatedTime(assetInstallation.getCreatedTime());
        this.houseHoldId = assetInstallation.getHouseHoldId();
        this.meterNo = assetInstallation.getMeterNo();
        this.remoteNo = assetInstallation.getRemoteNo();
        this.tvId = assetInstallation.getTvId();
        this.pmaWId = assetInstallation.getPmaWId();
        this.fieldMode = assetInstallation.getFieldMode();
        this.meterInstallationStatus = assetInstallation.getMeterInstallationStatus();
        this.remoteInstallationStatus = assetInstallation.getRemoteInstallationStatus();
        this.meterType = assetInstallation.getMeterType();
        this.mInstallationActiveFrom = assetInstallation.getMInstallationActiveFrom();
        this.mInstallationActiveTo = assetInstallation.getMInstallationActiveTo();
        this.rPairingActiveFrom = assetInstallation.getRPairingActiveFrom();
        this.rPairingActiveTo = assetInstallation.getRPairingActiveTo();
        this.meterRemarks = assetInstallation.getMeterRemarks();
        this.remoteRemarks = assetInstallation.getRemoteRemarks();
        this.isActive = assetInstallation.isActive();
        this.createdBy = assetInstallation.getCreatedBy().getId();
        this.updatedBy = assetInstallation.getUpdatedBy().getId();
        this.updatedTime = assetInstallation.getUpdatedTime();
    }

    public AbstractAssetInstallationEntity(AssetInstallationEntity assetInstallationEntity) {
        this.setUuid(assetInstallationEntity.getId());
        this.setCreatedTime(assetInstallationEntity.getCreatedTime());
        this.houseHoldId = assetInstallationEntity.getHouseHoldId();
        this.meterNo = assetInstallationEntity.getMeterNo();
        this.remoteNo = assetInstallationEntity.getRemoteNo();
        this.tvId = assetInstallationEntity.getTvId();
        this.pmaWId = assetInstallationEntity.getPmaWId();
        this.fieldMode = assetInstallationEntity.getFieldMode();
        this.meterInstallationStatus = assetInstallationEntity.getMeterInstallationStatus();
        this.remoteInstallationStatus = assetInstallationEntity.getRemoteInstallationStatus();
        this.meterType = assetInstallationEntity.getMeterType();
        this.mInstallationActiveFrom = assetInstallationEntity.getMInstallationActiveFrom();
        this.mInstallationActiveTo = assetInstallationEntity.getMInstallationActiveTo();
        this.rPairingActiveFrom = assetInstallationEntity.getRPairingActiveFrom();
        this.rPairingActiveTo = assetInstallationEntity.getRPairingActiveTo();
        this.meterRemarks = assetInstallationEntity.getMeterRemarks();
        this.remoteRemarks = assetInstallationEntity.getRemoteRemarks();
        this.isActive = assetInstallationEntity.isActive();
        this.createdBy = assetInstallationEntity.getCreatedBy();
        this.updatedBy = assetInstallationEntity.getUpdatedBy();
        this.updatedTime = assetInstallationEntity.getUpdatedTime();
    }

    public AssetInstallation toAssetInstallation() {
        AssetInstallation assetInstallation = new AssetInstallation();
        assetInstallation.setId(new AssetInstallationId(id));
        assetInstallation.setHouseHoldId(houseHoldId);
        assetInstallation.setMeterNo(meterNo);
        assetInstallation.setRemoteNo(remoteNo);
        assetInstallation.setTvId(tvId);
        assetInstallation.setPmaWId(pmaWId);
        assetInstallation.setFieldMode(fieldMode);
        assetInstallation.setMeterInstallationStatus(meterInstallationStatus);
        assetInstallation.setRemoteInstallationStatus(remoteInstallationStatus);
        assetInstallation.setMeterType(meterType);
        assetInstallation.setMInstallationActiveFrom(mInstallationActiveFrom);
        assetInstallation.setMInstallationActiveTo(mInstallationActiveTo);
        assetInstallation.setRPairingActiveFrom(rPairingActiveFrom);
        assetInstallation.setRPairingActiveTo(rPairingActiveTo);
        assetInstallation.setMeterRemarks(meterRemarks);
        assetInstallation.setRemoteRemarks(remoteRemarks);
        assetInstallation.setActive(isActive);
        assetInstallation.setCreatedBy(new UserId(createdBy));
        assetInstallation.setUpdatedBy(new UserId(updatedBy));
        assetInstallation.setUpdatedTime(updatedTime);
        assetInstallation.setCreatedTime(createdTime);
        return assetInstallation;
    }
}
