/**
 * Copyright © 2016-2024 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Meter;
import org.thingsboard.server.common.data.SimDetails;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.AppConstant;
import org.thingsboard.server.dao.util.TimeUtils;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.REMARKS;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDefs({@TypeDef(name = "json", typeClass = JsonStringType.class), @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(name = ModelConstants.DEVICE_TABLE_NAME)
public final class DeviceEntity extends AbstractDeviceEntity<Device> {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = ModelConstants.METER_NO)
    private Long meterNo;

    @Column(name = ModelConstants.FIELD_ALARM_RAISED)
    private Boolean fieldAlarmRaised;

    @Column(name = ModelConstants.MOTHERBOARD_SERIAL_NO)
    private String motherboardSerialNo;

    @Column(name = ModelConstants.IMEI_BASEBOARD_SERIAL_NO)
    private String imeiBaseboardSerialNo;

    @Column(name = ModelConstants.POWER_PCB_SERIAL_NO)
    private String powerPcbSerialNo;

    @Column(name = ModelConstants.SOFTWARE_VERSION)
    private String softwareVersion;

    @Column(name = ModelConstants.ETHERNET_MAC)
    private String ethernetMac;

    @Column(name = ModelConstants.BLE_ADDRESS)
    private String bleAddress;

    @Column(name = ModelConstants.WIFI_MAC)
    private String wifiMac;

    @Column(name = ModelConstants.VALID_FROM)
    private Long validFrom;

    @Column(name = ModelConstants.VALID_TO)
    private Long validTo;

    @Column(name = ModelConstants.PRODUCTION_DATE)
    private Long productionDate;

    @Column(name = ModelConstants.LAST_CONNECTED_DATE)
    private Long last_connected_date;

    @Column(name = ModelConstants.K_LICENCE_KEY)
    private String kLicenceKey;

    @Column(name = ModelConstants.K_LICENCE_NO)
    private String kLicenceNo;

    @Column(name = REMARKS)
    private String remarks;

    @Column(name = ModelConstants.AUTH_CODE)
    private String authCode;

    @Column(name = ModelConstants.DEVICE_ID_PROPERTY)
    private String deviceId;

    @Column(name = ModelConstants.IS_ACTIVE_PROPERTY)
    private Boolean isActive;

    @Column(name = ModelConstants.CREATED_BY, columnDefinition = "uuid")
    private UUID createdBy;

    @Column(name = ModelConstants.UPDATED_BY_PROPERTY, columnDefinition = "uuid")
    private UUID updatedBy;

    @Column(name = ModelConstants.OTA_COMMIT_HASH)
    private String otaCommitHash;

    @Column(name = ModelConstants.STATUS_ID, columnDefinition = "uuid")
    private UUID statusId;

    @Column(name = ModelConstants.OS_TYPE_ID, columnDefinition = "uuid")
    private UUID osTypeId;

    @Type(type = "jsonb")
    @Column(name = ModelConstants.SIM_DETAILS, columnDefinition = "jsonb")
    private List<SimDetails> simDetails;

    @Column(name = ModelConstants.UPDATED_TIME_PROPERTY)
    private Long updatedTime;

    public DeviceEntity() {
        super();
    }

    public DeviceEntity(Device device) {
        super(device);
        Meter meter = device.getMeterPayload();
        if (meter != null) {
            if (meter.getMeterNo() != null) {
                this.meterNo = meter.getMeterNo();
            }
            if (meter.getCpuSerial() != null) {
                this.motherboardSerialNo = meter.getCpuSerial();
            }
            if (meter.getImeiBaseboard() != null) {
                this.imeiBaseboardSerialNo = meter.getImeiBaseboard();
            }
            if (meter.getPowerPcbSerial() != null) {
                this.powerPcbSerialNo = meter.getPowerPcbSerial();
            }
            if (meter.getSwVersion() != null) {
                this.softwareVersion = meter.getSwVersion();
            }
            if (meter.getEthernetMac() != null) {
                this.ethernetMac = meter.getEthernetMac();
            }
            if (meter.getBleAddress() != null) {
                this.bleAddress = meter.getBleAddress();
            }
            if (meter.getWifiMac() != null) {
                this.wifiMac = meter.getWifiMac();
            }
            if (meter.getValidFrom() != null) {
                this.validFrom = TimeUtils.convertDateToMillis(meter.getValidFrom(), AppConstant.DATE_FORMAT);
            }
            if (meter.getValidTo() != null) {
                this.validTo = TimeUtils.convertDateToMillis(meter.getValidTo(), AppConstant.DATE_FORMAT);
            }
            if (meter.getProductionDate() != null) {
                this.productionDate = TimeUtils.convertDateToMillis(meter.getProductionDate(), AppConstant.DATE_FORMAT);
            }
            if (meter.getAuthcode() != null) {
                this.authCode = meter.getAuthcode();
            }
            if (meter.getDeviceId() != null) {
                this.deviceId = meter.getDeviceId();
            }
            if (meter.getStatusId() != null) {
                this.statusId = meter.getStatusId();
            }
            if (meter.getOsTypeId() != null) {
                this.osTypeId = meter.getOsTypeId();
            }
            if (meter.getUpdatedTime() != null) {
                this.updatedTime = meter.getUpdatedTime();
            }
            if (meter.getUpdatedBy() != null) {
                this.updatedBy = meter.getUpdatedBy();
            }
            if (meter.getCreatedBy() != null) {
                this.createdBy = meter.getCreatedBy();
            }
            if (meter.getSimDetails() != null) {
                this.simDetails = meter.getSimDetails();
            }
            if (meter.getLicenseNo() != null) {
                this.kLicenceNo = meter.getLicenseNo();
            }
            if (meter.getLicenseKey() != null) {
                this.kLicenceKey = meter.getLicenseKey();
            }
            if (meter.getRemarks() != null) {
                this.remarks = meter.getRemarks();
            }
        }
    }

    @Override
    public Device toData() {
        Device device = super.toDevice();
        Meter meter = new Meter();
        meter.setMeterNo(meterNo);
        meter.setDeviceId(deviceId);
        meter.setBleAddress(bleAddress);
        meter.setEthernetMac(ethernetMac);
        meter.setSwVersion(softwareVersion);
        meter.setCpuSerial(motherboardSerialNo);
        meter.setImeiBaseboard(imeiBaseboardSerialNo);
        meter.setProductionDate(TimeUtils.convertMillisToDate(productionDate));
        meter.setAuthcode(authCode);
        meter.setValidFrom(TimeUtils.convertMillisToDate(validFrom));
        meter.setValidTo(TimeUtils.convertMillisToDate(validTo));
        meter.setOtaCommitHash(otaCommitHash);
        meter.setSimDetails(simDetails);
        meter.setCreatedTime(createdTime);
        meter.setCreatedBy(createdBy);
        meter.setUpdatedBy(updatedBy);
        meter.setStatusId(statusId);
        meter.setOsTypeId(osTypeId);
        meter.setUpdatedTime(updatedTime);
        meter.setRemarks(remarks);
        device.setMeterPayload(meter);
        device.setName(getName());
        device.setType(getType());
        device.setCreatedTime(createdTime);
        return device;
    }
}
