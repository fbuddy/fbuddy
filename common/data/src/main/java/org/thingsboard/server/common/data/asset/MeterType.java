package org.thingsboard.server.common.data.asset;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MeterType {
    LINUX("LINUX"),
    ANDROID("ANDROID");

    private String id;
    private String name;

    MeterType(String displayText) {
        this.name = displayText;
        this.id = this.name();
    }

}
