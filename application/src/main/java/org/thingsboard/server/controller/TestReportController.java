package org.thingsboard.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.TestReport;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.report.TestReportService;

import javax.validation.constraints.NotNull;
import java.util.List;
@RestController
@TbCoreComponent
@RequestMapping("/api/test_report")
@RequiredArgsConstructor
@Slf4j
public class TestReportController extends BaseController{

    @Autowired
    private TestReportService testReportService;

    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN')")
    @PostMapping
    public ResponseEntity<TestReport> saveTestReport(@RequestBody TestReport testReport) {
        TestReport savedReport = testReportService.saveTestReport(testReport);
        return ResponseEntity.ok(savedReport);
    }
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN')")
    @GetMapping("/{meterNo}")
    public ResponseEntity<List<TestReport>> getTestReportsByMeterNo(@PathVariable @NotNull Long meterNo) {
        List<TestReport> reports = testReportService.getTestReportsByMeterNo(meterNo);
        return ResponseEntity.ok(reports);
    }
}
