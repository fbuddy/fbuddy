package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.asset.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldStatus;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.sql.util.JpaStatusDao;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.asset.TbAssetInstallationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api")
public class AssetInstallationController extends BaseController {

    private final TbAssetInstallationService tbAssetInstallationService;

    private final JpaStatusDao jpaStatusDao;

    @ApiOperation(value = "Create or Update Asset Installation Info (saveAssetInstallation)",
            notes = "Save the AssetInstallation Info and Returns the created AssetInstallation Info.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/assetinstallation/{ASSET_INSTALLATION_ACTION}", method = RequestMethod.POST)
    public ResponseEntity<AssetInstallation> saveAssetInstallation(@ApiParam(value = "A JSON value representing the asset installation.")
                                                                   @RequestBody AssetInstallation assetInstallation, @PathVariable("ASSET_INSTALLATION_ACTION") AssetInstallationAction assetInstallationAction,
                                                                   @RequestParam(required = false) boolean isDataRequired) throws ThingsboardException {
        AssetInstallation updatedAssetInstallation = tbAssetInstallationService.save(assetInstallation, getCurrentUser(), assetInstallationAction);
        if (isDataRequired)
            return new ResponseEntity<>(updatedAssetInstallation, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Get the filter meta data (getFilterMetaData)",
            notes = "Get the asset installation meta data. ")
    @RequestMapping(value = "/filter-meta-data", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getFilterMetaData() throws ThingsboardException {
        PageLink pageLink = createPageLink(99999, 0, null, null, null);
        Map<String, Object> data = new HashMap<>();

        data.put("METER_TYPE", MeterType.values());
        data.put("ASSET_TYPE", AssetType.values());
        data.put("FIELD_MODE", FieldMode.values());
        data.put("ASSET_INSTALLATION_ACTION", AssetInstallationAction.values());
        data.put("METER_INSTALLATION_STATUS", MeterInstallationStatus.values());
        data.put("REMOTE_INSTALLATION_STATUS", RemoteInstallationStatus.values());
        data.put("HOUSEHOLD_ACTION_TYPE", HouseHoldActionType.values());
        data.put("HOUSEHOLD_STATUS", HouseHoldStatus.values());
        data.put("PROFILES", deviceProfileService.findDeviceProfiles(TenantId.SYS_TENANT_ID, pageLink));
        data.put("STATUS", jpaStatusDao.findAll(pageLink));
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiOperation(value = "Get Asset Installation (getAssetInstallation)",
            notes = "Returns a list of asset installations.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/assetinstallations", method = RequestMethod.POST)
    @ResponseBody
    public PageData<AssetInstallationInfo> getAssetInstallation(@RequestBody AssetInstallationFilter assetInstallationFilter,
                                                                @ApiParam(value = PAGE_SIZE_DESCRIPTION, required = true)
                                                                @RequestParam int pageSize,
                                                                @ApiParam(value = PAGE_NUMBER_DESCRIPTION, required = true)
                                                                @RequestParam int page,
                                                                @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ASSETINSTALLATION_SORT_PROPERTY_ALLOWABLE_VALUES)
                                                                @RequestParam(required = false) String sortProperty,
                                                                @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
                                                                @RequestParam(required = false) String sortOrder) throws ThingsboardException {
        return checkNotNull(tbAssetInstallationService.getAssetInstallation(assetInstallationFilter, createPageLink(pageSize, page, "", sortProperty, sortOrder)));
    }

    @ApiOperation(value = "Get Houlsehold Uninstalled TVIds (getAssetInstallation)",
            notes = "Returns a list of houlsehold uninstalled tvids.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN')")
    @RequestMapping(value = "/household/uninstalled/tvids", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<Integer>> getHoulseholdUninstalledTVIds(
            @ApiParam(value = HOUSEHOLD_ID_DESCRIPTION, required = true)
            @RequestParam Long houseHoldId) throws ThingsboardException {
        List<Integer> houlseholdUninstalledTVIds = tbAssetInstallationService.getHoulseholdUninstalledTVIds(houseHoldId);
        if (!houlseholdUninstalledTVIds.isEmpty())
            return new ResponseEntity<>(houlseholdUninstalledTVIds, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
