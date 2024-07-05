package org.thingsboard.server.common.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.id.RemoteEntityId;
import org.thingsboard.server.common.data.validation.DateFormat;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Remote extends BaseData<RemoteEntityId> implements HasName {
    private Long remoteNo;
    @NotNull(message = "remoteSerialNo cannot be null !")
    private String remoteSerialNo;
    @NotNull(message = "swVersion cannot be null !")
    private String swVersion;
    @NotNull(message = "hwVersion cannot be null !")
    private String hwVersion;
    @NotNull(message = "pcbSerial cannot be null !")
    private String pcbSerial;
    @NotNull(message = "bleAddress cannot be null !")
    private String bleAddress;
    @NotNull(message = "productionDate cannot be null !")
    @DateFormat(pattern = "dd-MM-yyyy", message = "Production date must be in the format dd-MM-yyyy")
    private String productionDate;
    private Long validFrom;
    private Long validTill;
    private UUID statusId;
    private String remarks;
    private Boolean isActive;
    private Long updatedTime;
    private UUID createdBy;
    private UUID updatedBy;

    public Remote(RemoteEntityId id) {
        super(id);
    }

    public void updateRemoteData(Remote newRemote) {

        if (newRemote.getRemoteSerialNo() != null) {
            this.remoteSerialNo = newRemote.getRemoteSerialNo();
        }
        if (newRemote.getSwVersion() != null) {
            this.swVersion = newRemote.getSwVersion();
        }
        if (newRemote.getHwVersion() != null) {
            this.hwVersion = newRemote.getHwVersion();
        }
        if (newRemote.getPcbSerial() != null) {
            this.pcbSerial = newRemote.getPcbSerial();
        }
        if (newRemote.getBleAddress() != null) {
            this.bleAddress = newRemote.getBleAddress();
        }
        if (newRemote.getProductionDate() != null) {
            this.productionDate = newRemote.getProductionDate();
        }
        if (newRemote.getValidFrom() != null) {
            this.validFrom = newRemote.getValidFrom();
        }
        if (newRemote.getValidTill() != null) {
            this.validTill = newRemote.getValidTill();
        }
        if (newRemote.getIsActive() != null) {
            this.isActive = newRemote.getIsActive();
        }

        if (newRemote.getUpdatedTime() != null) {
            this.updatedTime = newRemote.getUpdatedTime();
        }
        if (newRemote.getStatusId() != null)
            this.statusId = newRemote.getStatusId();
        if (newRemote.getRemarks() != null)
            this.remarks = newRemote.getRemarks();

    }

    @Override
    public String getName() {
        return remoteNo.toString();
    }

    public Remote(Remote remote) {
        super(remote);
        this.remoteNo = remote.getRemoteNo();
        this.remoteSerialNo = remote.getRemoteSerialNo();
        this.swVersion = remote.getSwVersion();
        this.hwVersion = remote.getHwVersion();
        this.pcbSerial = remote.getPcbSerial();
        this.bleAddress = remote.getBleAddress();
        this.productionDate = remote.getProductionDate();
        this.validFrom = remote.getValidFrom();
        this.validTill = remote.getValidTill();
        this.statusId = remote.getStatusId();
        this.remarks = remote.getRemarks();
        this.isActive = remote.getIsActive();
        this.createdTime = remote.getCreatedTime();
        if (remote.getUpdatedTime() != null)
            this.updatedTime = remote.getUpdatedTime();

    }
}
