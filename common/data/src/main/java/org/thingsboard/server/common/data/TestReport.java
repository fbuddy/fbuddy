package org.thingsboard.server.common.data;

import lombok.Data;

@Data
public class TestReport {

    private String status;

    private String testedBy;

    private String sim1Report;

    private String sim2Report;

    private String rtcVolt;

    private String testDate;

    private Long MeterNo;

}
