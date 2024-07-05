package org.thingsboard.server.common.data.license;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Parameters {
    @JsonProperty("board_serial_number")
    private String boardSerialNumber;
    @JsonProperty("nb_stereo")
    private int nbStereo;
    @JsonProperty("nb_dolby")
    private int nbDolby;
    @JsonProperty("nb_wmk_id")
    private int nbWmkId;
    @JsonProperty("authorization_code")
    private String authorizationCode;
    @JsonProperty("lock_password")
    private Boolean lockPassword;
    @JsonProperty("lock_mac")
    private Boolean lockMac;
    private Metadata metadata;
}
