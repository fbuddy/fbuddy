package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssetType {
    METER("METER"),
    REMOTE("REMOTE");

    private String id;
    private String name;

    AssetType(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }

}
