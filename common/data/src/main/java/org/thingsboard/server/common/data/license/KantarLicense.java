package org.thingsboard.server.common.data.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class KantarLicense {
    @JsonProperty("civolution_serial_number")
    private String civolutionSerialNumber;
    @JsonProperty("watermark_id_list")
    private List<Integer> watermarkIdList;
    @JsonProperty("language_list")
    private List<String> languageList;
    @JsonProperty("civolution_license")
    private String civolutionLicense;
    @JsonProperty("audience_license")
    private String audienceLicense;
    @JsonProperty("demo_audience_license")
    private String demoAudienceLicense;
}
