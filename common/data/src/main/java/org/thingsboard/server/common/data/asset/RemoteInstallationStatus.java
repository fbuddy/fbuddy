package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RemoteInstallationStatus {
    INSTALL_IN_PROGRESS("INSTALL_IN_PROGRESS"),
    INSTALLED("INSTALLED"),
    STANDBY("STANDBY"),
    UNINSTALL_IN_PROGRESS("UNINSTALL_IN_PROGRESS");

    private String id;
    private String name;

    RemoteInstallationStatus(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }

    public static RemoteInstallationStatus getByName(String name) {
        for (RemoteInstallationStatus e : values()) {
            if (e.name.equalsIgnoreCase(name)) {
                return e;
            }
        }
        throw new IllegalArgumentException("No enum constant with name " + name);
    }

}
