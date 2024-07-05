package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.Meter;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.meter.MeterFilter;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.device.TbDeviceService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RestController
@TbCoreComponent
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MeterController extends BaseController {

    private final TbDeviceService tbDeviceService;

    @ApiOperation(value = "Create Or Update Meter ",
            notes = "This is Meter-onboarding API" +
                    TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN','USER_MANAGEMENT_SEARCH_EDIT')")
    @RequestMapping(value = "/device/meter", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> saveMeter(@ApiParam(value = "A JSON value representing the Meter data.") @RequestBody @Valid Meter meter, BindingResult bindingResult,
                                       @ApiParam(value = "access token will be auto-generated.") @RequestParam(name = "accessToken", required = false) String accessToken) throws ThingsboardException {

        if (meter == null) {
            log.error("Invalid request: meter details are null");
            throw new ThingsboardException("Invalid request: meter details cannot be null ", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }

        if (bindingResult.hasErrors() && meter.getMeterNo() == null) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error("Validation errors: {}", errors);
            throw new ThingsboardException(errors, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }

        return new ResponseEntity<>(tbDeviceService.saveMeter(meter, accessToken, getCurrentUser()), HttpStatus.OK);
    }

    @ApiOperation(value = "Bulk Upload Meters",
            notes = "This API allows bulk upload of meter onboarding data through CSV files." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN')")
    @PostMapping(value = "/device/meter/bulk-upload", consumes = "multipart/form-data")
    public ResponseEntity<?> bulkUploadMeters(@RequestParam("file") MultipartFile file) throws ThingsboardException {
        return tbDeviceService.uploadBulkMeters(file, getCurrentUser());
    }

    @ApiOperation(value = "Get Meter Summary (getMeterSummary)",
            notes = "Returns summary of meter.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/meter/summary", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Map<String, Long>> getMeterSummary(@RequestBody MeterFilter meterFilter) throws ThingsboardException {
        return checkNotNull(tbDeviceService.getMeterSummary(meterFilter));
    }

    @ApiOperation(value = "Get Meter (getMeter)",
            notes = "Returns a list of meters.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/meters", method = RequestMethod.POST)
    @ResponseBody
    public PageData<Device> getMeter(@RequestBody MeterFilter meterFilter,
                                     @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
                                     @RequestParam int pageSize,
                                     @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
                                     @RequestParam int page,
                                     @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = METER_SORT_PROPERTY_ALLOWABLE_VALUES)
                                     @RequestParam(required = false) String sortProperty,
                                     @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
                                     @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        return checkNotNull(tbDeviceService.getMeter(meterFilter, createPageLink(pageSize, page, "", sortProperty, sortOrder)));
    }
}
