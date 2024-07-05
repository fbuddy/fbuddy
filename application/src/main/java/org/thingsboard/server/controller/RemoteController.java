package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.remote.RemoteFilter;
import org.thingsboard.server.common.data.remote.RemoteInfo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.remote.TbRemoteService;

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
public class RemoteController extends BaseController {

    @Autowired
    private final TbRemoteService tbRemoteService;

    @ApiOperation(value = "Create Or Update Remote",
            notes = "This is Remote-onboarding API" + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN','USER_MANAGEMENT_SEARCH_EDIT')")
    @PostMapping(value = "/device/remote")
    @ResponseBody
    public ResponseEntity<?> saveRemote(@ApiParam(value = "A JSON value representing the remote data.") @RequestBody @Valid Remote remote, BindingResult bindingResult) throws ThingsboardException {
        if (remote == null) {
            log.error("Invalid request: remote details is null");
            throw new ThingsboardException("Invalid request: remote details cannot be null ", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        if (bindingResult.hasErrors() && remote.getRemoteNo() == null) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error("Validation errors: {}", errors);
            throw new ThingsboardException(errors, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        return ResponseEntity.ok(tbRemoteService.saveRemote(remote, getCurrentUser()));

    }


    @ApiOperation(value = "Bulk Upload Remotes",
            notes = "This API allows bulk upload of remote onboarding data through CSV files." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN','SYS_ADMIN','USER_MANAGEMENT_SEARCH_EDIT')")
    @PostMapping(value = "/device/remote/bulk-upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.TEXT_PLAIN_VALUE, "text/csv"})
    public ResponseEntity<?> bulkUploadRemotes(@RequestParam("file") MultipartFile file) throws ThingsboardException {

        return tbRemoteService.processBulkUpload(file, getCurrentUser());

    }


    @ApiOperation(value = "Get Remote (getRemote)",
            notes = "Returns a list of remotes.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/remotes", method = RequestMethod.POST)
    @ResponseBody
    public PageData<RemoteInfo> getRemote(@RequestBody RemoteFilter remoteFilter,
                                          @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
                                          @RequestParam int pageSize,
                                          @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
                                          @RequestParam int page,
                                          @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = REMOTE_SORT_PROPERTY_ALLOWABLE_VALUES)
                                          @RequestParam(required = false) String sortProperty,
                                          @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
                                          @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        return checkNotNull(tbRemoteService.getRemote(remoteFilter, createPageLink(pageSize, page, "", sortProperty, sortOrder)));
    }

    @ApiOperation(value = "Get Remote Summary (getRemoteSummary)",
            notes = "Returns summary of remotes.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/remote/summary", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Map<String, Long>> getRemoteSummary() throws ThingsboardException {
        return checkNotNull(tbRemoteService.getRemoteSummary());
    }
}
