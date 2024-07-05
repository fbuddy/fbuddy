package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.id.RemoteEntityId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.AppConstant;
import org.thingsboard.server.dao.util.TimeUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractRemoteEntity<T extends Remote> extends BaseSqlEntity<T> {
    @Column(name = REMOTE_NO)
    private Long remoteNo;

    @Column(name = REMOTE_SERIAL_NO)
    private String remoteSerialNo;

    @Column(name = REMOTE_SW_VERSION)
    private String swVersion;

    @Column(name = REMOTE_HW_VERSION)
    private String hwVersion;

    @Column(name = REMOTE_PCB_SERIAL)
    private String pcbSerial;

    @Column(name = REMOTE_BLE_ADDRESS)
    private String bleAddress;

    @Column(name = REMOTE_UPDATE_FROM)
    private Long validFrom;

    @Column(name = REMOTE_UPDATE_TO)
    private Long validTill;

    @Column(name = REMOTE_PRODUCTION_DATE)
    private Long productionDate;

    @Column(name = IS_ACTIVE_PROPERTY)
    private Boolean isActive;

    @Column(name = REMOTE_CREATED_BY)
    private UUID createdBy;

    @Column(name = UPDATED_BY_PROPERTY)
    private UUID updatedBy;

    @Column(name = REMOTE_STATUS_ID, columnDefinition = "uuid")
    private UUID statusId;

    @Column(name = UPDATED_TIME_PROPERTY)
    protected Long updatedTime;

    @Column(name = REMARKS)
    private String remarks;

    public AbstractRemoteEntity(Remote remote) {
        if (remote.getId() != null)
            this.id = remote.getId().getId();
        this.remoteNo = remote.getRemoteNo();
        this.remoteSerialNo = remote.getRemoteSerialNo();
        this.swVersion = remote.getSwVersion();
        this.hwVersion = remote.getHwVersion();
        this.pcbSerial = remote.getPcbSerial();
        this.bleAddress = remote.getBleAddress();
        this.productionDate = TimeUtils.convertDateToMillis(remote.getProductionDate(), AppConstant.DATE_FORMAT);
        this.validFrom = remote.getValidFrom();
        this.validTill = remote.getValidTill();
        this.statusId = remote.getStatusId();
        this.remarks = remote.getRemarks();
        this.isActive = remote.getIsActive();
        this.createdTime = remote.getCreatedTime();
        if (remote.getUpdatedTime() != null)
            this.updatedTime = remote.getUpdatedTime();
        this.createdBy = remote.getCreatedBy();
        this.updatedBy = remote.getUpdatedBy();

    }

    public AbstractRemoteEntity(RemoteEntity remoteEntity) {
        if (remoteEntity.getId() != null)
            this.id = remoteEntity.getId();
        this.remoteNo = remoteEntity.getRemoteNo();
        this.remoteSerialNo = remoteEntity.getRemoteSerialNo();
        this.swVersion = remoteEntity.getSwVersion();
        this.hwVersion = remoteEntity.getHwVersion();
        this.pcbSerial = remoteEntity.getPcbSerial();
        this.bleAddress = remoteEntity.getBleAddress();
        this.productionDate = remoteEntity.getProductionDate();
        this.validFrom = remoteEntity.getValidFrom();
        this.validTill = remoteEntity.getValidTill();
        this.statusId = remoteEntity.getStatusId();
        this.remarks = remoteEntity.getRemarks();
        this.isActive = remoteEntity.getIsActive();
        this.createdTime = remoteEntity.getCreatedTime();
        this.updatedTime = remoteEntity.getUpdatedTime();
        this.createdBy = remoteEntity.getCreatedBy();
        this.updatedBy = remoteEntity.getUpdatedBy();
    }

    public Remote toRemoteData() {
        Remote remote = new Remote(new RemoteEntityId(id));
        remote.setRemoteNo(remoteNo);
        remote.setRemoteSerialNo(remoteSerialNo);
        remote.setBleAddress(bleAddress);
        remote.setProductionDate(TimeUtils.convertMillisToDate(productionDate));
        remote.setSwVersion(swVersion);
        remote.setHwVersion(hwVersion);
        remote.setStatusId(statusId);
        remote.setRemarks(remarks);
        remote.setIsActive(isActive);
        remote.setPcbSerial(pcbSerial);
        remote.setValidFrom(validFrom);
        remote.setValidTill(validTill);
        remote.setUpdatedTime(updatedTime);
        remote.setCreatedTime(createdTime);
        remote.setUpdatedBy(updatedBy);
        remote.setCreatedBy(createdBy);
        return remote;
    }

    public void setUpdatedTime(long updatedTime) {
        if (updatedTime > 0) {
            this.updatedTime = updatedTime;
        }
    }
}
