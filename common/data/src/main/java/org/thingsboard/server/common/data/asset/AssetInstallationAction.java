package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssetInstallationAction {
    ASSET_INSTALL("ASSET_INSTALL"),
    METER_REPLACE("METER_REPLACE"),
    REMOTE_REPLACE("REMOTE_REPLACE"),
    METER_UNINSTALL("METER_UNINSTALL"),
    REMOTE_UNINSTALL("REMOTE_UNINSTALL"),
    METER_UNINSTALLED("METER_UNINSTALLED"),
    REMOTE_UNINSTALLED("REMOTE_UNINSTALLED");

    private String id;
    private String name;

    AssetInstallationAction(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }
}