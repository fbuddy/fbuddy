package org.thingsboard.server.common.data;

import lombok.Data;

@Data
public class SimDetails {
    private Integer simSlot;
    private String imsi;
    private String simSerial;
    private String serviceProvider;
    private String phoneNumber;
}
