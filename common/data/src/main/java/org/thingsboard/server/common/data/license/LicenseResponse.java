package org.thingsboard.server.common.data.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LicenseResponse {
    private int id;
    private String uri;
    private String type;
    private Parameters parameters;
    private KantarLicense result;
    private String creator;
    private String pool;
    @JsonProperty("creation_time")
    private String creationTime;
}
