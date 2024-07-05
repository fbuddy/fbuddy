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
package org.thingsboard.server.service.entitiy.device;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.edge.Edge;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.EdgeId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.license.KantarLicense;
import org.thingsboard.server.common.data.meter.MeterFilter;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.dao.device.ClaimDevicesService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.device.claim.ClaimResponse;
import org.thingsboard.server.dao.device.claim.ClaimResult;
import org.thingsboard.server.dao.device.claim.ReclaimResult;
import org.thingsboard.server.dao.model.sql.OSTypeEntity;
import org.thingsboard.server.dao.model.sql.StatusEntity;
import org.thingsboard.server.dao.sql.ostype.JpaOsTypeDao;
import org.thingsboard.server.dao.sql.util.JpaStatusDao;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
import org.thingsboard.server.service.entitiy.device.license.KantarLicensingService;
import org.thingsboard.server.service.entitiy.report.TestReportService;
import org.thingsboard.server.utils.AppConstants;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@AllArgsConstructor
@TbCoreComponent
@Service
@Slf4j
public class DefaultTbDeviceService extends AbstractTbEntityService implements TbDeviceService {

    private final DeviceService deviceService;
    private final DeviceCredentialsService deviceCredentialsService;
    private final ClaimDevicesService claimDevicesService;
    private final TenantService tenantService;
    private final TestReportService testReportService;
    private final KantarLicensingService licensingService;
    private final JpaStatusDao jpaStatusDao;
    private final JpaOsTypeDao jpaOsTypeDao;

    public ResponseEntity<?> uploadBulkMeters(MultipartFile file, User user) {
        List<Long> onboardedMeterNo = new ArrayList<>();
        List<Long> notOnboardedMeterNo = new ArrayList<>();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> updatedCsvLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Meter meter = new Meter();
                meter.setMeterNo(Long.parseLong(data[0]));
                OsType osType = new OsType(data[1], data[2]);
                meter.setOsType(osType);
                meter.setSwVersion(data[3]);
                meter.setProductionDate(data[4]);
                meter.setOtaCommitHash(data[5]);
                meter.setImeiBaseboard(data[6]);
                meter.setCpuSerial(data[7]);
                meter.setEthernetMac(data[8]);
                meter.setBleAddress(data[9]);
                meter.setWifiMac(data[10]);
                meter.setPowerPcbSerial(data[11]);
                meter.setSdCardPartNo(data[12]);
                meter.setSdCardSerial(data[13]);
                Set<ConstraintViolation<Meter>> violations = meter.getMeterNo() == null ? validator.validate(meter) : new HashSet<>();
                if (!violations.isEmpty()) {
                    List<String> errorList = new ArrayList<>();
                    for (ConstraintViolation<Meter> violation : violations) {
                        errorList.add(violation.getMessage());
                    }
                    String updatedLine = "Not Onboarded" + "," + meter.getMeterNo() + "," + line + "," + errorList;
                    updatedCsvLines.add(updatedLine);
                    continue;
                }

                try {
                    Device savedDevice = saveMeter(meter, null, user);
                    onboardedMeterNo.add(savedDevice.getMeterPayload().getMeterNo());
                    String updatedLine = "Onboarded," + savedDevice.getMeterPayload().getMeterNo() + "," + line;
                    updatedCsvLines.add(updatedLine);
                } catch (Exception e) {
                    notOnboardedMeterNo.add(meter.getMeterNo());
                    String updatedLine = "Not Onboarded," + meter.getMeterNo() + "," + line;
                    updatedCsvLines.add(updatedLine);
                }
            }
            StringBuilder csvContent = new StringBuilder();
            for (String updatedLine : updatedCsvLines) {
                csvContent.append(updatedLine).append("\n");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=onboarding_results.csv");
            headers.setContentType(MediaType.parseMediaType("text/csv"));

            Map<String, Integer> response = new HashMap<>();
            response.put("onboardedCount", onboardedMeterNo.size());
            response.put("notOnboardedCount", notOnboardedMeterNo.size());
            ByteArrayResource resource = new ByteArrayResource(csvContent.toString().getBytes());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e);
        }
    }

    @Override
    public Device saveMeter(Meter meter, String accessToken, User user) throws ThingsboardException {
        Device oldDevice = null;
        Device device = new Device();
        if (meter.getMeterNo() != null) {
            oldDevice = deviceService.findDeviceByMeterNo(meter.getMeterNo());
            if (oldDevice == null) {
                throw new ThingsboardException("Invalid meterNo", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            Meter oldMeterData = oldDevice.getMeterPayload();
            updateMeterData(oldMeterData, meter, user);
            device.setMeterPayload(oldMeterData);
            device.setId(oldDevice.getId());
        } else {
            StatusEntity statusEntity = jpaStatusDao.findByTypeAndName(AppConstants.METER_STATUS, AppConstants.STANDBY_STATUS);
            if (statusEntity != null)
                meter.setStatusId(statusEntity.getId());
            Long newMeterNo = deviceService.findMaxMeterNo() + 1;
            meter.setMeterNo(newMeterNo);
            device.setMeterPayload(meter);
            meter.setCreatedBy(user.getId().getId());
            OsType osType = meter.getOsType();
            if (osType != null)
                device.setName(meter.getOsType().getOsType() + "-" + newMeterNo);
            device.setTenantId(user.getTenantId());
            if (licensingService.getLicenseEnabled()) {
                KantarLicense kantarLicense = licensingService.createLicense(device, user);
                if (kantarLicense != null) {
                    meter.setLicenseNo(kantarLicense.getCivolutionSerialNumber());
                    meter.setLicenseKey(kantarLicense.getCivolutionLicense());
                }
            }
            device.setMeterPayload(meter);
        }
        try {
            return save(device, oldDevice, accessToken, user);
        } catch (Exception e) {
            throw new ThingsboardException(e.getMessage(), ThingsboardErrorCode.GENERAL);
        }
    }

    @Override
    public Device save(Device device, Device oldDevice, String accessToken, User user) throws Exception {
        ActionType actionType = device.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = device.getTenantId();
        addStatusAndOsTypeId(device);

        try {
            Device savedDevice = checkNotNull(deviceService.saveDeviceWithAccessToken(device.getId() == null ? device : oldDevice, accessToken));
            autoCommit(user, savedDevice.getId());
            TestReport testReport = device.getMeterPayload().getTestReport();
            if (testReport != null && savedDevice.getId() != null) {
                testReport.setMeterNo(savedDevice.getMeterPayload().getMeterNo());
                testReportService.saveTestReport(testReport);
            }
            notificationEntityService.notifyCreateOrUpdateDevice(tenantId, savedDevice.getId(), savedDevice.getCustomerId(),
                    savedDevice, oldDevice, actionType, user);

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), device, actionType, user, e);
            throw e;
        }
    }

    void addStatusAndOsTypeId(Device device) throws Exception {
        Meter meter = device.getMeterPayload();
        if (meter.getStatusId() != null)
            device.setMeterPayload(meter);
        if (Objects.isNull(meter.getOsTypeId()) && meter.getOsType() != null) {
            OSTypeEntity osTypeEntity = jpaOsTypeDao.findByOsTypeAndHversionName(meter.getOsType().getOsType(), meter.getOsType().getHwVersion());
            if (osTypeEntity == null) {
                throw new Exception("Incorrect osType or hversion");
            }
            meter.setOsTypeId(osTypeEntity.getId());
            device.setMeterPayload(meter);
        }
    }

    @Override
    public Device saveDeviceWithCredentials(Device device, DeviceCredentials credentials, User user) throws ThingsboardException {
        boolean isCreate = device.getId() == null;
        ActionType actionType = isCreate ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = device.getTenantId();
        try {
            Device oldDevice = isCreate ? null : deviceService.findDeviceById(tenantId, device.getId());
            Device savedDevice = checkNotNull(deviceService.saveDeviceWithCredentials(device, credentials));
            notificationEntityService.notifyCreateOrUpdateDevice(tenantId, savedDevice.getId(), savedDevice.getCustomerId(),
                    savedDevice, oldDevice, actionType, user);

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), device, actionType, user, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Device device, User user) {
        TenantId tenantId = device.getTenantId();
        DeviceId deviceId = device.getId();
        try {
            removeAlarmsByEntityId(tenantId, deviceId);
            deviceService.deleteDevice(tenantId, deviceId);
            notificationEntityService.notifyDeleteDevice(tenantId, deviceId, device.getCustomerId(), device,
                    user, deviceId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), ActionType.DELETED,
                    user, e, deviceId.toString());
            throw e;
        }
    }

    @Override
    public Device assignDeviceToCustomer(TenantId tenantId, DeviceId deviceId, Customer customer, User user) throws ThingsboardException {
        ActionType actionType = ActionType.ASSIGNED_TO_CUSTOMER;
        CustomerId customerId = customer.getId();
        try {
            Device savedDevice = checkNotNull(deviceService.assignDeviceToCustomer(tenantId, deviceId, customerId));
            notificationEntityService.logEntityAction(tenantId, deviceId, savedDevice, customerId, actionType, user,
                    deviceId.toString(), customerId.toString(), customer.getName());

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), actionType, user,
                    e, deviceId.toString(), customerId.toString());
            throw e;
        }
    }

    @Override
    public Device unassignDeviceFromCustomer(Device device, Customer customer, User user) throws ThingsboardException {
        ActionType actionType = ActionType.UNASSIGNED_FROM_CUSTOMER;
        TenantId tenantId = device.getTenantId();
        DeviceId deviceId = device.getId();
        try {
            Device savedDevice = checkNotNull(deviceService.unassignDeviceFromCustomer(tenantId, deviceId));
            CustomerId customerId = customer.getId();

            notificationEntityService.logEntityAction(tenantId, deviceId, savedDevice, customerId, actionType, user,
                    deviceId.toString(), customerId.toString(), customer.getName());

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), actionType,
                    user, e, deviceId.toString());
            throw e;
        }
    }

    @Override
    public Device assignDeviceToPublicCustomer(TenantId tenantId, DeviceId deviceId, User user) throws ThingsboardException {
        ActionType actionType = ActionType.ASSIGNED_TO_CUSTOMER;
        Customer publicCustomer = customerService.findOrCreatePublicCustomer(tenantId);
        try {
            Device savedDevice = checkNotNull(deviceService.assignDeviceToCustomer(tenantId, deviceId, publicCustomer.getId()));

            notificationEntityService.logEntityAction(tenantId, deviceId, savedDevice, savedDevice.getCustomerId(),
                    actionType, user, deviceId.toString(), publicCustomer.getId().toString(), publicCustomer.getName());

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE), actionType,
                    user, e, deviceId.toString());
            throw e;
        }
    }

    @Override
    public DeviceCredentials getDeviceCredentialsByDeviceId(Device device, User user) throws ThingsboardException {
        TenantId tenantId = device.getTenantId();
        DeviceId deviceId = device.getId();
        try {
            DeviceCredentials deviceCredentials = checkNotNull(deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, deviceId));
            notificationEntityService.logEntityAction(tenantId, deviceId, device, device.getCustomerId(),
                    ActionType.CREDENTIALS_READ, user, deviceId.toString());
            return deviceCredentials;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE),
                    ActionType.CREDENTIALS_READ, user, e, deviceId.toString());
            throw e;
        }
    }

    @Override
    public DeviceCredentials updateDeviceCredentials(Device device, DeviceCredentials deviceCredentials, User user) throws ThingsboardException {
        TenantId tenantId = device.getTenantId();
        DeviceId deviceId = device.getId();
        try {
            DeviceCredentials result = checkNotNull(deviceCredentialsService.updateDeviceCredentials(tenantId, deviceCredentials));
            notificationEntityService.notifyUpdateDeviceCredentials(tenantId, deviceId, device.getCustomerId(), device, result, user);
            return result;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE),
                    ActionType.CREDENTIALS_UPDATED, user, e, deviceCredentials);
            throw e;
        }
    }

    @Override
    public ListenableFuture<ClaimResult> claimDevice(TenantId tenantId, Device device, CustomerId customerId, String secretKey, User user) {
        ListenableFuture<ClaimResult> future = claimDevicesService.claimDevice(device, customerId, secretKey);

        return Futures.transform(future, result -> {
            if (result != null && result.getResponse().equals(ClaimResponse.SUCCESS)) {
                notificationEntityService.logEntityAction(tenantId, device.getId(), result.getDevice(), customerId,
                        ActionType.ASSIGNED_TO_CUSTOMER, user, device.getId().toString(), customerId.toString(),
                        customerService.findCustomerById(tenantId, customerId).getName());
            }
            return result;
        }, MoreExecutors.directExecutor());
    }

    @Override
    public ListenableFuture<ReclaimResult> reclaimDevice(TenantId tenantId, Device device, User user) {
        ListenableFuture<ReclaimResult> future = claimDevicesService.reClaimDevice(tenantId, device);

        return Futures.transform(future, result -> {
            Customer unassignedCustomer = result.getUnassignedCustomer();
            if (unassignedCustomer != null) {
                notificationEntityService.logEntityAction(tenantId, device.getId(), device, device.getCustomerId(),
                        ActionType.UNASSIGNED_FROM_CUSTOMER, user, device.getId().toString(),
                        unassignedCustomer.getId().toString(), unassignedCustomer.getName());
            }
            return result;
        }, MoreExecutors.directExecutor());
    }

    @Override
    public Device assignDeviceToTenant(Device device, Tenant newTenant, User user) {
        TenantId tenantId = device.getTenantId();
        TenantId newTenantId = newTenant.getId();
        DeviceId deviceId = device.getId();
        try {
            Tenant tenant = tenantService.findTenantById(tenantId);
            Device assignedDevice = deviceService.assignDeviceToTenant(newTenantId, device);

            notificationEntityService.notifyAssignDeviceToTenant(tenantId, newTenantId, deviceId,
                    assignedDevice.getCustomerId(), assignedDevice, tenant, user, newTenantId.toString(), newTenant.getName());

            return assignedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE),
                    ActionType.ASSIGNED_TO_TENANT, user, e, deviceId.toString());
            throw e;
        }
    }

    @Override
    public Device assignDeviceToEdge(TenantId tenantId, DeviceId deviceId, Edge edge, User user) throws ThingsboardException {
        ActionType actionType = ActionType.ASSIGNED_TO_EDGE;
        EdgeId edgeId = edge.getId();
        try {
            Device savedDevice = checkNotNull(deviceService.assignDeviceToEdge(tenantId, deviceId, edgeId));
            notificationEntityService.logEntityAction(tenantId, deviceId, savedDevice, savedDevice.getCustomerId(),
                    actionType, user, deviceId.toString(), edgeId.toString(), edge.getName());

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE),
                    actionType, user, e, deviceId.toString(), edgeId.toString());
            throw e;
        }
    }

    @Override
    public Device unassignDeviceFromEdge(Device device, Edge edge, User user) throws ThingsboardException {
        ActionType actionType = ActionType.UNASSIGNED_FROM_EDGE;
        TenantId tenantId = device.getTenantId();
        DeviceId deviceId = device.getId();
        EdgeId edgeId = edge.getId();
        try {
            Device savedDevice = checkNotNull(deviceService.unassignDeviceFromEdge(tenantId, deviceId, edgeId));
            notificationEntityService.logEntityAction(tenantId, deviceId, savedDevice, savedDevice.getCustomerId(),
                    actionType, user, deviceId.toString(), edgeId.toString(), edge.getName());

            return savedDevice;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE),
                    actionType, user, e, deviceId.toString(), edgeId.toString());
            throw e;
        }
    }


    public void updateMeterData(Meter oldMeter, Meter newMeter, User user) {
        if (newMeter == null) {
            return; // No new data to update
        }

        if (newMeter.getMeterNo() != null && !Objects.equals(newMeter.getMeterNo(), oldMeter.getMeterNo())) {
            oldMeter.setMeterNo(newMeter.getMeterNo());
        }
        if (newMeter.getSwVersion() != null && !Objects.equals(newMeter.getSwVersion(), oldMeter.getSwVersion())) {
            oldMeter.setSwVersion(newMeter.getSwVersion());
        }
        if (newMeter.getOsType() != null && !Objects.equals(newMeter.getOsType(), oldMeter.getOsType())) {
            oldMeter.setOsType(newMeter.getOsType());
        }

        if (newMeter.getOtaCommitHash() != null && !Objects.equals(newMeter.getOtaCommitHash(), oldMeter.getOtaCommitHash())) {
            oldMeter.setOtaCommitHash(newMeter.getOtaCommitHash());
        }
        if (newMeter.getImeiBaseboard() != null && !Objects.equals(newMeter.getImeiBaseboard(), oldMeter.getImeiBaseboard())) {
            oldMeter.setImeiBaseboard(newMeter.getImeiBaseboard());
        }
        if (newMeter.getCpuSerial() != null && !Objects.equals(newMeter.getCpuSerial(), oldMeter.getCpuSerial())) {
            oldMeter.setCpuSerial(newMeter.getCpuSerial());
        }
        if (newMeter.getPowerPcbSerial() != null && !Objects.equals(newMeter.getPowerPcbSerial(), oldMeter.getPowerPcbSerial())) {
            oldMeter.setPowerPcbSerial(newMeter.getPowerPcbSerial());
        }
        if (newMeter.getEthernetMac() != null && !Objects.equals(newMeter.getEthernetMac(), oldMeter.getEthernetMac())) {
            oldMeter.setEthernetMac(newMeter.getEthernetMac());
        }
        if (newMeter.getBleAddress() != null && !Objects.equals(newMeter.getBleAddress(), oldMeter.getBleAddress())) {
            oldMeter.setBleAddress(newMeter.getBleAddress());
        }
        if (newMeter.getWifiMac() != null && !Objects.equals(newMeter.getWifiMac(), oldMeter.getWifiMac())) {
            oldMeter.setWifiMac(newMeter.getWifiMac());
        }
        if (newMeter.getSdCardPartNo() != null && !Objects.equals(newMeter.getSdCardPartNo(), oldMeter.getSdCardPartNo())) {
            oldMeter.setSdCardPartNo(newMeter.getSdCardPartNo());
        }
        if (newMeter.getSdCardSerial() != null && !Objects.equals(newMeter.getSdCardSerial(), oldMeter.getSdCardSerial())) {
            oldMeter.setSdCardSerial(newMeter.getSdCardSerial());
        }
        if (newMeter.getValidFrom() != null && !Objects.equals(newMeter.getValidFrom(), oldMeter.getValidFrom())) {
            oldMeter.setValidFrom(newMeter.getValidFrom());
        }
        if (newMeter.getValidTo() != null && !Objects.equals(newMeter.getValidTo(), oldMeter.getValidTo())) {
            oldMeter.setValidTo(newMeter.getValidTo());
        }
        if (newMeter.getAuthcode() != null && !Objects.equals(newMeter.getAuthcode(), oldMeter.getAuthcode())) {
            oldMeter.setAuthcode(newMeter.getAuthcode());
        }
        if (newMeter.getDeviceId() != null && !Objects.equals(newMeter.getDeviceId(), oldMeter.getDeviceId())) {
            oldMeter.setDeviceId(newMeter.getDeviceId());
        }
        if (newMeter.getTestReport() != null && !Objects.equals(newMeter.getTestReport(), oldMeter.getTestReport())) {
            oldMeter.setTestReport(newMeter.getTestReport());
        }
        oldMeter.setUpdatedTime(System.currentTimeMillis());
        oldMeter.setUpdatedBy(user.getId().getId());
        if (newMeter.getStatusId() != null)
            oldMeter.setStatusId(newMeter.getStatusId());
        if (newMeter.getRemarks() != null)
            oldMeter.setRemarks(newMeter.getRemarks());
    }

    @Override
    public Map<String, Map<String, Long>> getMeterSummary(MeterFilter meterFilter) {
        return deviceService.getMeterSummary(meterFilter);
    }

    @Override
    public PageData<Device> getMeter(MeterFilter meterFilter, PageLink pageLink) {
        return deviceService.findMeter(meterFilter, pageLink);
    }
}
