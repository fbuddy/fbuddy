package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.household.*;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.household.DefaultHouseHoldService;
import org.thingsboard.server.service.security.model.SecurityUser;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/household")
public class HouseHoldController extends BaseController {

    private final DefaultHouseHoldService defaultHouseHoldService;

    @ApiOperation(value = "Create or Update Household Info (saveHousehold)",
            notes = "Save the Household Info and Returns the created Household Info.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<HouseHold> saveHousehold(@ApiParam(value = "A JSON value representing the household.")
                                          @RequestBody HouseHold houseHold,
                                          @RequestParam(required = false, defaultValue = "false") boolean overwriteExisting) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        return new ResponseEntity<>(defaultHouseHoldService.save(houseHold, currentUser, overwriteExisting), HttpStatus.OK);
    }
    @ApiOperation(value = "Get the houseHold filter meta data (getHouseHoldFilterMetaData)",
            notes = "Get the houseHold history. " )
    @RequestMapping(value = "/filter-meta-data", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHouseHoldFilterMetaData() throws ThingsboardException {
        PageLink pageLink = createPageLink(99999, 0, null,null,null);
        Map<String, Object> data = new HashMap<>();
        data.put("HOUSEHOLD_ACTION_TYPE", HouseHoldActionType.values());
        data.put("HOUSEHOLD_STATUS", HouseHoldStatus.values());
        data.put("PROFILES", deviceProfileService.findDeviceProfiles(TenantId.SYS_TENANT_ID, pageLink));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    @ApiOperation(value = "Get the houseHold Log (getHouseHoldHistory)",
            notes = "Get the houseHold history. " )
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/history/{HOUSEHOLD_ACTION_TYPE}", method = RequestMethod.GET)
    @ResponseBody
    public PageData<HouseHoldLog> getHouseHoldHistory(
            @PathVariable("HOUSEHOLD_ACTION_TYPE") HouseHoldActionType houseHoldActionType,
            HttpServletRequest request,
            @Valid HouseHoldFilterRequest houseHoldFilterRequest,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        return checkNotNull(defaultHouseHoldService.getHistory(houseHoldFilterRequest, pageLink, houseHoldActionType, currentUser));
    }
    @ApiOperation(value = "Export the houseHold Log (getHouseHoldHistory)",
            notes = "Export the houseHold history. " )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/export/history/{HOUSEHOLD_ACTION_TYPE}", method = RequestMethod.GET)
    @ResponseBody
    public void getHouseHoldHistory(
            @PathVariable("HOUSEHOLD_ACTION_TYPE") HouseHoldActionType houseHoldActionType,
            HttpServletRequest request,
            @Valid HouseHoldFilterRequest houseHoldFilterRequest,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder,
            HttpServletResponse response) throws ThingsboardException, IOException {
        SecurityUser currentUser = getCurrentUser();
        PageLink pageLink = createPageLink(9999999, 0, "", sortProperty, sortOrder);
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"household-log.csv\"");
        csvGenerator.writeHouseHoldLogToCsv(houseHoldActionType, defaultHouseHoldService.getHistory(houseHoldFilterRequest, pageLink, houseHoldActionType, currentUser).getData(), response.getWriter());
    }
    @ApiOperation(value = "Get the houseHold (getHouseHoldList)",
            notes = "Get the houseHold history. " )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/list/{HOUSEHOLD_ACTION_TYPE}", method = RequestMethod.GET)
    @ResponseBody
    public PageData<HouseHold> getHouseHoldList(
            @PathVariable("HOUSEHOLD_ACTION_TYPE") HouseHoldActionType houseHoldActionType,
            HttpServletRequest request,
            @Valid HouseHoldFilterRequest houseHoldFilterRequest,
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
            @RequestParam int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
            @RequestParam int page,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        PageLink pageLink = createPageLink(pageSize, page, "", sortProperty, sortOrder);
        return checkNotNull(defaultHouseHoldService.getList(houseHoldFilterRequest, pageLink, houseHoldActionType, currentUser));
    }
    @ApiOperation(value = "export the houseHold (getHouseHoldList)",
            notes = "export the houseHold history. " )
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/export/list/{HOUSEHOLD_ACTION_TYPE}", method = RequestMethod.GET)
    @ResponseBody
    public void getHouseHoldList(
            @PathVariable("HOUSEHOLD_ACTION_TYPE") HouseHoldActionType houseHoldActionType,
            HttpServletRequest request,
            @Valid HouseHoldFilterRequest houseHoldFilterRequest,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = USER_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder,
            HttpServletResponse response) throws ThingsboardException, IOException {
        SecurityUser currentUser = getCurrentUser();
        PageLink pageLink = createPageLink(9999999, 0, "", sortProperty, sortOrder);
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"household.csv\"");
        csvGenerator.writeHouseHoldInfoToCsv(houseHoldActionType, defaultHouseHoldService.getList(houseHoldFilterRequest, pageLink, houseHoldActionType, currentUser).getData(), response.getWriter());
    }
    @ApiOperation(value = "Update Household field Status (updateHouseholdStatus)",
            notes = "Save the Household field status and Returns the updated Household Info.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<HouseHold> updateHouseholdStatus(@ApiParam(value = "A JSON value representing the household Log.")
                                                   @RequestBody HouseHoldLog houseHoldLog) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        preValidations(houseHoldLog);
        return new ResponseEntity<>(defaultHouseHoldService.updateFieldStatus(houseHoldLog, currentUser), HttpStatus.OK);
    }
    @ApiOperation(value = "Update Household profile (updateHouseholdProfile)",
            notes = "Save the Household field status and Returns the updated Household Info.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/update-profile", method = RequestMethod.POST)
    public ResponseEntity<HouseHold> updateHouseholdProfile(@ApiParam(value = "A JSON value representing the household Log.")
                                                           @RequestBody HouseHoldLog houseHoldLog) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        preValidations(houseHoldLog);
        return new ResponseEntity<>(defaultHouseHoldService.updateProfile(houseHoldLog, currentUser), HttpStatus.OK);
    }

    private void preValidations(@RequestBody @ApiParam("A JSON value representing the household Log.") HouseHoldLog houseHoldLog) throws ThingsboardException {
        checkNotNull(houseHoldLog);
        checkNotNull(houseHoldLog.getHouseHoldEntityId());
        checkNotNull(houseHoldLog.getActionData());
        checkNotNull(houseHoldLog.getValidFrom());
        if(houseHoldLog.getCurrentValidTill()==null)
        {
            houseHoldLog.setCurrentValidTill(houseHoldLog.getValidFrom());
        }
        if(houseHoldLog.getValidTill() != null && houseHoldLog.getValidTill() <= houseHoldLog.getValidFrom() )
        {
            throw new ThingsboardException("Valid till should be greater than Valid from", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
        checkNotNull(houseHoldLog.getCurrentValidTill());
    }
}
