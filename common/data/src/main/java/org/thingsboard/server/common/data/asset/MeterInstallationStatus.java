package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MeterInstallationStatus {
    INSTALL_IN_PROGRESS("INSTALL_IN_PROGRESS"),
    INSTALLED("INSTALLED"),
    STANDBY("STANDBY"),
    UNINSTALL_IN_PROGRESS("UNINSTALL_IN_PROGRESS"),
    IN_SERVICING("IN_SERVICING"),
    RETIRED("RETIRED"),
    UNACCOUNTED("UNACCOUNTED");

    private String id;
    private String name;

    MeterInstallationStatus(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }

    public static MeterInstallationStatus getByName(String name) {
        for (MeterInstallationStatus e : values()) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }

}
