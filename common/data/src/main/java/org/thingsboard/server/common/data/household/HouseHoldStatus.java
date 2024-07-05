package org.thingsboard.server.common.data.household;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HouseHoldStatus {
    ACCEPTED("ACCEPTED"),
    EXCLUDED("EXCLUDED"),
    ABSENT("ABSENT");

    private String id;
    private String name;

    HouseHoldStatus(String displayText)
    {
        this.name = displayText;
        this.id = this.name();
    }
}
