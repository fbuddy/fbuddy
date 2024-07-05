package org.thingsboard.server.service.entitiy.device.license;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.OsType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.license.*;
import org.thingsboard.server.dao.sql.ostype.JpaOsTypeDao;

import java.util.HashMap;
import java.util.Map;

@Service
public class KantarLicensingService {
    @Getter
    @Value("${kantar.enabled}")
    Boolean licenseEnabled;

    @Value("${kantar.basepath}")
    private String basepath;

    @Value("${kantar.android.username}")
    private String androidUsername;

    @Value("${kantar.android.password}")
    private String androidPassword;

    @Value("${kantar.android.type}")
    private String androidType;

    @Value("${kantar.android.pool}")
    private String androidPool;

    @Value("${kantar.android.lock_password}")
    private boolean androidLockPassword;

    @Value("${kantar.android.lock_mac}")
    private boolean androidLockMac;

    @Value("${kantar.android.metadata.comment}")
    private String androidComment;

    @Value("${kantar.android.metadata.version}")
    private String androidVersion;

    @Value("${kantar.android.metadata.customer}")
    private String androidCustomer;

    @Value("${kantar.android.metadata.custom_metadata}")
    private String androidCustomMetadata;

    @Value("${kantar.linux.username}")
    private String linuxUsername;

    @Value("${kantar.linux.password}")
    private String linuxPassword;

    @Value("${kantar.linux.type}")
    private String linuxType;

    @Value("${kantar.linux.pool}")
    private String linuxPool;

    @Value("${kantar.linux.lock_password}")
    private boolean linuxLockPassword;

    @Value("${kantar.linux.lock_mac}")
    private boolean linuxLockMac;

    @Value("${kantar.linux.metadata.comment}")
    private String linuxComment;

    @Value("${kantar.linux.metadata.custom_metadata}")
    private String linuxCustomMetadata;

    @Value("${kantar.linux.metadata.customer}")
    private String linuxCustomer;

    @Value("${kantar.linux.metadata.version}")
    private String linuxVersion;

    private final RestTemplate restTemplate;
    private final JpaOsTypeDao jpaOsTypeDao;
    private static final String LINUX = "Linux";
    private static final String ANDROID = "Android";

    public KantarLicensingService(RestTemplate restTemplate,JpaOsTypeDao jpaOsTypeDao) {
        this.restTemplate = restTemplate;
        this.jpaOsTypeDao = jpaOsTypeDao;
    }

    private String getAccessToken(String username,String password) {
        String url = basepath + "online/logon";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("login", username);
        body.put("password", password);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        Map<String, String> responseBody = response.getBody();
        return responseBody != null ? responseBody.get("token") : null;
    }


    public KantarLicense createLicense(Device device, User user) throws ThingsboardException {
        String url = basepath + "licenses";
        HttpEntity<LicenseRequest> licenseRequest = checkAndValidateOsType(device,user);
        LicenseResponse response = restTemplate.postForObject(url, licenseRequest, LicenseResponse.class);
        if (response != null) {
            return response.getResult();
        } else {
            throw new ThingsboardException("License creation failed", ThingsboardErrorCode.GENERAL);
        }
    }

    private HttpEntity<LicenseRequest> checkAndValidateOsType(Device device,User user) throws ThingsboardException {
        String osType = null;
        if (device.getMeterPayload() != null && device.getMeterPayload().getOsType() != null) {
            osType = device.getMeterPayload().getOsType().getOsType();
        } else if (device.getMeterPayload().getStatusId() != null) {
            OsType osdetails = jpaOsTypeDao.findById(user.getTenantId(),device.getMeterPayload().getStatusId());
            if (osdetails != null) {
                osType = osdetails.getOsType();
            }
        }

        if (osType == null) {
            throw new ThingsboardException("Unable to create licence for meter :OS Type not specified", ThingsboardErrorCode.ITEM_NOT_FOUND);
        }
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        if (osType.equalsIgnoreCase(LINUX)) {
            headers.set("x-access-token", getAccessToken(linuxUsername,linuxPassword));
            return prepareLinuxRequest(device.getMeterPayload().getAuthcode(),headers);
        } else if (osType.equalsIgnoreCase(ANDROID)) {
            headers.set("x-access-token", getAccessToken(androidUsername,androidPassword));
            return prepareAndroidRequest(device.getMeterPayload().getAuthcode(),headers);
        } else {
            throw new ThingsboardException("Unable to create licence for meter :Unsupported OS Type: " + osType, ThingsboardErrorCode.GENERAL);
        }
    }

    private HttpEntity<LicenseRequest>  prepareLinuxRequest(String authCode,HttpHeaders headers) {
        Metadata metadata = new Metadata();
        metadata.setCustomer(linuxCustomer);
        metadata.setComment(linuxComment);
        metadata.setVersion(linuxVersion);
        metadata.setCustomMetaData(linuxCustomMetadata);

        Parameters parameters = new Parameters();
        parameters.setMetadata(metadata);
        parameters.setLockMac(linuxLockMac);
        parameters.setAuthorizationCode(authCode);
        parameters.setLockPassword(linuxLockPassword);

        LicenseRequest request = new LicenseRequest();
        request.setType(linuxType);
        request.setPool(linuxPool);
        request.setParameters(parameters);
        return new HttpEntity<>(request, headers);
    }

    private HttpEntity<LicenseRequest> prepareAndroidRequest(String authCode,HttpHeaders headers) {
        Metadata metadata = new Metadata();
        metadata.setCustomer(androidCustomer);
        metadata.setComment(androidComment);
        metadata.setVersion(androidVersion);
        metadata.setCustomMetaData(androidCustomMetadata);

        Parameters parameters = new Parameters();
        parameters.setMetadata(metadata);
        parameters.setLockMac(androidLockMac);
        parameters.setAuthorizationCode(authCode);
        parameters.setLockPassword(androidLockPassword);

        LicenseRequest request = new LicenseRequest();
        request.setType(androidType);
        request.setPool(androidPool);
        request.setParameters(parameters);

        return new HttpEntity<>(request, headers);
    }
}

