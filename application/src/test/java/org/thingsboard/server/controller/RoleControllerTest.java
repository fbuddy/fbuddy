/**
 * Copyright © 2016-2024 The Thingsboard Authors
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.role.ModuleDisplayResponse;
import org.thingsboard.server.common.data.role.RoleInfoDetail;
import org.thingsboard.server.common.data.role.RolePermissionRequest;
import org.thingsboard.server.common.data.role.RoleRequest;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.model.SecuritySettings;
import org.thingsboard.server.dao.service.DaoSqlTest;
import org.thingsboard.server.service.security.auth.rest.LoginRequest;
import org.thingsboard.server.service.security.model.ChangePasswordRequest;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DaoSqlTest
public class RoleControllerTest extends AbstractControllerTest {

    @Test
    public void testGetModuleList() throws Exception {
        
        doGet("/api/role/modules")
        .andExpect(status().isUnauthorized());
        
        loginSysAdmin();
        doGet("/api/role/modules")
        .andExpect(status().isOk());
        

    }

    @Test
    public void testGetRoleList() throws Exception {

        doGet("/api/role/")
                .andExpect(status().isUnauthorized());

        loginSysAdmin();
        doGet("/api/role/")
                .andExpect(status().isOk());


    }

    @Test
    public void testCreateAndUpdateRole() throws Exception {

        doPost("/api/role/")
                .andExpect(status().isUnauthorized());
        loginSysAdmin();
        ResultActions response = doGet("/api/role/modules");
        List<ModuleDisplayResponse> moduleDisplayResponse = readResponse(response, new  TypeReference<List<ModuleDisplayResponse>>(){});


        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Test Role");
        roleRequest.setDescription("Test Role Description");
        roleRequest.setActive(true);
        List<RolePermissionRequest> modules = new ArrayList<>();
        RolePermissionRequest module = new RolePermissionRequest();
        module.setModuleId(moduleDisplayResponse.get(0).getModules().get(0).getSubModules().get(0).getId());
        module.setActive(true);
        modules.add(module);
        roleRequest.setModules(modules);

        RoleInfoDetail savedRoleInfoDetail = doPost("/api/role/", roleRequest, RoleInfoDetail.class);

        RoleInfoDetail fetchedRoleInfoDetail = doGet("/api/role/permission/"+savedRoleInfoDetail.getId(), RoleInfoDetail.class);

        Assert.assertEquals(savedRoleInfoDetail,fetchedRoleInfoDetail);

        //Update role permissions
        roleRequest.setModules(roleRequest.getModules().stream().map(m->{m.setActive(true); return m;}).collect(Collectors.toList()));
        roleRequest.setId(savedRoleInfoDetail.getId());
        savedRoleInfoDetail = doPost("/api/role/", roleRequest, RoleInfoDetail.class);
        fetchedRoleInfoDetail = doGet("/api/role/permission/"+savedRoleInfoDetail.getId(), RoleInfoDetail.class);
        Assert.assertEquals(savedRoleInfoDetail,fetchedRoleInfoDetail);
        Assert.assertEquals(true,fetchedRoleInfoDetail.isActive());

        //Deactivate role
        roleRequest.setModules(roleRequest.getModules().stream().map(m->{m.setActive(false); return m;}).collect(Collectors.toList()));
        roleRequest.setId(savedRoleInfoDetail.getId());
        roleRequest.setActive(false);
        savedRoleInfoDetail = doPost("/api/role/", roleRequest, RoleInfoDetail.class);
        fetchedRoleInfoDetail = doGet("/api/role/permission/"+savedRoleInfoDetail.getId(), RoleInfoDetail.class);
        Assert.assertEquals(savedRoleInfoDetail,fetchedRoleInfoDetail);
        Assert.assertFalse(fetchedRoleInfoDetail.isActive());
    }
    

}
