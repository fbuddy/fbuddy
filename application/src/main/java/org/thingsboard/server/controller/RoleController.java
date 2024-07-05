/**
 * Copyright © 2016-2024 The BARC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.role.ModuleService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.component.CsvFileGenerator;
import org.thingsboard.server.service.entitiy.role.DefaultRoleService;
import org.thingsboard.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.thingsboard.server.controller.ControllerConstants.*;

@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/role")
public class RoleController extends BaseController {

    public static final String ROLE_ID = "roleId";
    public static final String PATHS = "paths";
    public static final String YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION = "You don't have permission to perform this operation!";

    //private final ApplicationEventPublisher eventPublisher;
    private final ModuleService moduleService;

    private final DefaultRoleService defaultRoleService;

    @Autowired
    private CsvFileGenerator csvGenerator;


    @ApiOperation(value = "Get Modules (getModules)",
            notes = "Returns a list of modules.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/modules", method = RequestMethod.GET)
    public ResponseEntity<List<ModuleDisplayResponse>> getModules() throws ThingsboardException {
        return new ResponseEntity(checkNotNull(moduleService.prepareModuleListingResponse()), HttpStatus.OK);

    }
    @ApiOperation(value = "Get Modules (getModules)",
            notes = "Returns a list of modules.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/update-modules", method = RequestMethod.GET)
    public ResponseEntity<List<ModuleDisplayResponse>> updateModules() throws ThingsboardException {
        moduleService.clearModuleMap();
        return new ResponseEntity(checkNotNull(moduleService.prepareModuleListingResponse()), HttpStatus.OK);

    }
    @ApiOperation(value = "Get Roles with pagination (getRolesWithPagination)",
            notes = "Returns a list of modules.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<PageData<Role>> getRolesWithPagination(
            @ApiParam(value = PAGE_SIZE_DESCRIPTION, defaultValue = "100")
            @RequestParam(required = false, defaultValue = "100") int pageSize,
            @ApiParam(value = PAGE_NUMBER_DESCRIPTION, defaultValue = "0")
            @RequestParam(required = false, defaultValue = "0") int page,
            @ApiParam(value = USER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Valid RoleFilter roleFilter,
            @ApiParam(value = SORT_PROPERTY_DESCRIPTION, allowableValues = ROLE_SORT_PROPERTY_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortProperty,
            @ApiParam(value = SORT_ORDER_DESCRIPTION, allowableValues = SORT_ORDER_ALLOWABLE_VALUES)
            @RequestParam(required = false) String sortOrder
    ) throws ThingsboardException {
        if(sortProperty == null) {
            sortProperty = "createdTime";
            sortOrder = "DESC";
        }
        PageLink pageLink = createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return new ResponseEntity(checkNotNull(defaultRoleService.findAllRoleByFilter(pageLink, roleFilter)), HttpStatus.OK);

    }
    @ApiOperation(value = "Get Roles for User Creation (getRoles)",
            notes = "Returns a list of modules.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<Role>> getRoles() throws ThingsboardException {
        return new ResponseEntity(checkNotNull(defaultRoleService.findAll()), HttpStatus.OK);

    }
    @ApiOperation(value = "Get Permission of role (getRoles)",
            notes = "Returns a list of modules.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/permission/{roleId}", method = RequestMethod.GET)
    public ResponseEntity<RoleInfoDetail> getPermissionByRoles(
            @PathVariable(ROLE_ID) String strRoleId) throws ThingsboardException {
        checkParameter(ROLE_ID, strRoleId);
        RoleId roleId = new RoleId(toUUID(strRoleId));
        return new ResponseEntity<>(checkNotNull(defaultRoleService.findRoleById(roleId)), HttpStatus.OK);

    }
    @ApiOperation(value = "Create or Update Role (saveRoles)",
            notes = "Save the role and Returns the created Role.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_EDIT')")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Role> saveRoles(@ApiParam(value = "A JSON value representing the role with Permission.")
                              @RequestBody RoleRequest role) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        return new ResponseEntity<>(defaultRoleService.save(role, currentUser), HttpStatus.OK);
    }
    @ApiOperation(value = "Delete role (deleteRoleById)",
            notes = "Returns success or error.")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_VIEW')")
    @RequestMapping(value = "/{roleId}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteRoleById(
            @PathVariable(ROLE_ID) String strRoleId) throws ThingsboardException {
        SecurityUser currentUser = getCurrentUser();
        checkParameter(ROLE_ID, strRoleId);
        RoleId roleId = new RoleId(toUUID(strRoleId));
        defaultRoleService.delete(roleId, currentUser);

    }
    @GetMapping("/export-to-csv")
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN','CREATE_ROLE_EXPORT')")
    public void exportIntoCSV(
            @ApiParam(value = USER_TEXT_SEARCH_DESCRIPTION)
            @RequestParam(required = false) String textSearch,
            @Valid RoleFilter roleFilter,
            HttpServletResponse response) throws ThingsboardException, IOException {
        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"role.csv\"");
        PageLink pageLink = createPageLink(99999999, 0, textSearch, null, null);
        csvGenerator.writeRolesToCsv(defaultRoleService.findAllRoleByFilter(pageLink, roleFilter).getData(), response.getWriter());
    }
}
