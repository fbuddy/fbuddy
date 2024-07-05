package org.thingsboard.server.service.entitiy.remote;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.remote.RemoteFilter;
import org.thingsboard.server.common.data.remote.RemoteInfo;
import org.thingsboard.server.dao.model.sql.StatusEntity;
import org.thingsboard.server.dao.sql.remote.JpaRemoteDao;
import org.thingsboard.server.dao.sql.util.JpaStatusDao;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
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
public class DefaultTbRemoteServiceImpl extends AbstractTbEntityService implements TbRemoteService {

    @Autowired
    private JpaRemoteDao jpaRemoteDao;
    @Autowired
    private JpaStatusDao jpaStatusDao;

    public ResponseEntity<?> processBulkUpload(MultipartFile file, User currentUser) {
        List<String> onboardedRemoteIds = new ArrayList<>();
        List<String> notOnboardedRemoteIds = new ArrayList<>();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        List<String> updatedCsvLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                Remote remote = new Remote();
                remote.setRemoteNo(parseLong(data[0]));
                remote.setRemoteSerialNo(parseString(data[1]));
                remote.setSwVersion(parseString(data[2]));
                remote.setHwVersion(parseString(data[3]));
                remote.setPcbSerial(parseString(data[4]));
                remote.setBleAddress(parseString(data[5]));
                remote.setProductionDate(parseString(data[6]));
                Set<ConstraintViolation<Remote>> violations = remote.getRemoteNo() == null ? validator.validate(remote) : new HashSet<>();
                if (!violations.isEmpty()) {
                    List<String> errorList = new ArrayList<>();
                    for (ConstraintViolation<Remote> violation : violations) {
                        errorList.add(violation.getMessage());
                    }
                    String updatedLine = "Not Onboarded" + "," + remote.getRemoteNo() + "," + line + "," + errorList;
                    updatedCsvLines.add(updatedLine);
                    continue;
                }
                try {
                    Remote savedRemote = saveRemote(remote, currentUser);
                    onboardedRemoteIds.add(savedRemote.getRemoteNo().toString());
                    String updatedLine = "Onboarded" + "," + remote.getRemoteNo() + "," + line;
                    updatedCsvLines.add(updatedLine);
                } catch (Exception e) {
                    notOnboardedRemoteIds.add(remote.getRemoteNo().toString());
                    String updatedLine = "Not Onboarded" + "," + remote.getRemoteNo() + "," + line;
                    updatedCsvLines.add(updatedLine);
                }
            }
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("onboardedCount: " + onboardedRemoteIds.size()).append("\n");
            csvContent.append("notOnboardedCount: " + notOnboardedRemoteIds.size()).append("\n");
            for (String updatedLines : updatedCsvLines) {
                csvContent.append(updatedLines).append("\n");
            }

            ByteArrayResource resource = new ByteArrayResource(csvContent.toString().getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=onboarding_results.csv");
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentLength(resource.contentLength());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error processing file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }

    @Override
    public Remote saveRemote(Remote remote, User user) throws ThingsboardException {
        if (remote == null) {
            log.error("Invalid request: remote object is null");
            throw new ThingsboardException("Invalid Request: remote data cannot be null", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        ActionType actionType = remote.getRemoteNo() != null ? ActionType.UPDATED : ActionType.ADDED;
        try {
            Remote savedRemote;
            if (remote.getRemoteNo() != null) {
                Remote oldRemote = jpaRemoteDao.findRemoteDetailsByRemoteNo(remote.getRemoteNo());
                if (oldRemote == null) {
                    log.error("Invalid remote_no: no remote found with remoteNo {}", remote.getRemoteNo());
                    throw new ThingsboardException("Invalid remoteId: remote details not found", ThingsboardErrorCode.ITEM_NOT_FOUND);
                }
                oldRemote.updateRemoteData(remote);
                oldRemote.setUpdatedBy(user.getId().getId());
                oldRemote.setUpdatedTime(System.currentTimeMillis());
                savedRemote = jpaRemoteDao.saveAndFlush(oldRemote);
            } else {
                StatusEntity statusEntity = jpaStatusDao.findByTypeAndName(AppConstants.REMOTE_STATUS, AppConstants.STANDBY_STATUS);
                if (statusEntity != null)
                    remote.setStatusId(statusEntity.getId());
                remote.setIsActive(true);
                remote.setCreatedBy(user.getId().getId());
                remote.setRemoteNo(jpaRemoteDao.findMaxRemoteNo() + 1);
                savedRemote = jpaRemoteDao.saveAndFlush(remote);
            }
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, savedRemote.getId(), savedRemote, actionType, user);
            return savedRemote;

        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.REMOTE), remote, actionType, user, e);
            throw e;
        }
    }

    @Override
    public PageData<RemoteInfo> getRemote(RemoteFilter remoteFilter, PageLink pageLink) {
        return jpaRemoteDao.findRemote(remoteFilter, pageLink);
    }

    @Override
    public Map<String, Map<String, Long>> getRemoteSummary() {
        return jpaRemoteDao.findRemoteSummary();
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    private String parseString(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value)) {
            return null;
        }
        return value;
    }
}
