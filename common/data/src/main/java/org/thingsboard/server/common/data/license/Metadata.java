package org.thingsboard.server.common.data.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Metadata {
    private String channels;
    private String customer;
    private String languages;
    private String usage;
    private String comment;
    @JsonProperty("manufacturer_sn")
    private String manufacturerSn;
    @JsonProperty("platform_type")
    private String platformType;
    private String version;
    @JsonProperty("custom_metadata")
    private String customMetaData;

}
