package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FieldMode {
    WITH_REMOTE("WITH_REMOTE"),
    WITHOUT_REMOTE("WITHOUT_REMOTE");

    private String id;
    private String name;

    FieldMode(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }

}
