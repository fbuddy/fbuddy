package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.TestReport;
import org.thingsboard.server.dao.model.PrimaryEntity;
import org.thingsboard.server.dao.util.TimeUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@Table(name = "meter_test_report")
public class TestReportEntity extends PrimaryEntity<TestReport> {

    @Column(name = "meter_no", nullable = false)
    private Long meterNo;

    @Column(name = "status")
    private String status;

    @Column(name = "test_by")
    private String testBy;

    @Column(name = "sim1_report")
    private String sim1Report;

    @Column(name = "sim2_report")
    private String sim2Report;

    @Column(name = "rtc_volt")
    private String rtcVolt;

    @Column(name = "test_date")
    private Long testDate;

    public TestReportEntity(TestReport testReport) {
        this.meterNo = testReport.getMeterNo();
        this.status = testReport.getStatus();
        this.testBy = testReport.getTestedBy();
        this.sim1Report = testReport.getSim1Report();
        this.sim2Report = testReport.getSim2Report();
        this.rtcVolt = testReport.getRtcVolt();
        this.testDate = TimeUtils.convertDateToMillis(testReport.getTestDate());
    }

    @Override
    public TestReport toData() {
        TestReport dto = new TestReport();
        dto.setStatus(status);
        dto.setTestedBy(testBy);
        dto.setSim1Report(sim1Report);
        dto.setSim2Report(sim2Report);
        dto.setRtcVolt(rtcVolt);
        dto.setTestDate(TimeUtils.convertMillisToDate(testDate));
        dto.setMeterNo(meterNo);
        return dto;
    }
}
