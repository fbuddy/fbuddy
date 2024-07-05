package org.thingsboard.server.common.data.asset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.AssetInstallationId;
import org.thingsboard.server.common.data.id.UserId;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class AssetInstallation extends BaseData<AssetInstallationId> implements HasName {

    private static final long serialVersionUID = 6998485460273302018L;

    private MeterInstallationStatus meterInstallationStatus = MeterInstallationStatus.STANDBY;
    private RemoteInstallationStatus remoteInstallationStatus = RemoteInstallationStatus.STANDBY;

    @ApiModelProperty(position = 3, value = "Household Id")
    private Long houseHoldId;
    @ApiModelProperty(position = 4, value = "Meter No")
    private Long meterNo;
    @ApiModelProperty(position = 5, value = "Remote No")
    private Long remoteNo;
    @ApiModelProperty(position = 6, value = "TV Id")
    private Integer tvId;
    @ApiModelProperty(position = 7, value = "PMA W Id")
    private String pmaWId;
    @ApiModelProperty(position = 8, value = "Meter Type")
    private String meterType;
    @ApiModelProperty(position = 9, value = "Field Mode")
    private String fieldMode;
    @ApiModelProperty(position = 10, value = "Meter Installation Active From")
    private long mInstallationActiveFrom;
    @ApiModelProperty(position = 11, value = "Meter Installation Active To")
    private Long mInstallationActiveTo;
    @ApiModelProperty(position = 12, value = "Meter Remarks")
    private String meterRemarks;
    @ApiModelProperty(position = 13, value = "Remote Pairing Active From")
    private long rPairingActiveFrom;
    @ApiModelProperty(position = 14, value = "Remote Pairing Active To")
    private Long rPairingActiveTo;
    @ApiModelProperty(position = 15, value = "Remote Remarks")
    private String remoteRemarks;
    @ApiModelProperty(position = 16, value = "Is Active")
    private boolean isActive;
    @ApiModelProperty(position = 17, value = "Id of user who is creating the Asset Installation")
    private UserId createdBy;
    @ApiModelProperty(position = 18, value = "Id of user who is updating the Asset Installation")
    private UserId updatedBy;
    @ApiModelProperty(position = 19, value = "Timestamp of the asset installation updation")
    protected Long updatedTime;
    @ApiModelProperty(position = 20, value = "Old Meter No")
    private Long oldMeterNo;
    @ApiModelProperty(position = 21, value = "Old Remote No")
    private Long oldRemoteNo;

    public AssetInstallation() {
        super();
    }

    public AssetInstallation(AssetInstallationId assetInstallationId) {
        super(assetInstallationId);
    }

    public AssetInstallation(AssetInstallation assetInstallation) {
        super(assetInstallation);
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
        this.createdBy = assetInstallation.getCreatedBy();
        this.updatedBy = assetInstallation.getUpdatedBy();
        this.updatedTime = assetInstallation.getUpdatedTime();
        this.oldMeterNo = assetInstallation.getOldMeterNo();
        this.oldRemoteNo = assetInstallation.getOldRemoteNo();
    }

    @Override
    public String getName() {
        return this.houseHoldId.toString();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the asset installation Id. " +
            "Specify this field to update the asset. " +
            "Referencing non-existing asset Id will cause error. " +
            "Omit this field to create new asset.")
    @Override
    public AssetInstallationId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the asset installation creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }
}
