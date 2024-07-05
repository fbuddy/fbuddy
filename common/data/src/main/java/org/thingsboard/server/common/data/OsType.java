package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class OsType {
    @NotNull(message = "OsType cannot be null !")
    private String osType;
    @NotNull(message = "hardware version cannot be null !")
    private String hwVersion;
    @JsonIgnore
    private Boolean isActive;


    public OsType( String osType,String hwVersion) {
        this.hwVersion = hwVersion;
        this.osType = osType;
    }
    public OsType( String osType,String hwVersion,Boolean isActive) {
        this.hwVersion = hwVersion;
        this.osType = osType;
        this.isActive=isActive;
    }
}
