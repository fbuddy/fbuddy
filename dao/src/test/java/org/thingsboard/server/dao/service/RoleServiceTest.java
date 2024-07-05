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
package org.thingsboard.server.dao.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.*;
import org.thingsboard.server.common.data.role.Module;

import org.thingsboard.server.dao.role.ModuleService;
import org.thingsboard.server.dao.role.RoleService;


import java.util.ArrayList;

import java.util.List;

@DaoSqlTest
public class RoleServiceTest extends AbstractServiceTest {

    @Autowired
    RoleService roleService;

    @Autowired
    ModuleService moduleService;

    @Before
    public void before() {

    }
    @Test
    public void testFindAllModules() {
        List<Module> moduleList =  moduleService.findAll();
        Assert.assertNotNull(moduleList);
        Assert.assertTrue(moduleList.size()>0);
    }
    @Test
    public void testSaveRole() {
        List<Module> moduleList = moduleService.findAll();
        Assert.assertNotNull(moduleList);
        Assert.assertTrue(moduleList.size()>0);
        RoleRequest roleRequest = new RoleRequest();
        roleRequest.setName("Test Role");
        roleRequest.setDescription("Test Role Description");
        roleRequest.setActive(true);
        List<RolePermissionRequest> modules = new ArrayList<>();
        RolePermissionRequest module = new RolePermissionRequest();
        module.setModuleId(moduleList.get(0).getId());
        module.setActive(true);
        modules.add(module);
        roleRequest.setModules(modules);
        RoleInfoDetail roleInfoDetail = roleService.saveRole(roleRequest, null);
        Assert.assertNotNull(roleInfoDetail);
        Assert.assertTrue(roleInfoDetail.getModules().size()>0);
        Assert.assertTrue(roleInfoDetail.isActive());
        Assert.assertTrue(roleInfoDetail.getModules().get(0).getModuleId().equals(moduleList.get(0).getId()));
        //Assert.assertTrue(roleInfoDetail.getModules().get(0).isViewPermission());
        //Assert.assertFalse(roleInfoDetail.getModules().get(0).isEditPermission());
    }
    @Test
    public void testFindRoleById() {
        PageLink pageLink = new PageLink(33);

        List<Role> roleList = roleService.findAllRoleByFilter(pageLink, new RoleFilter()).getData();
        Assert.assertNotNull(roleList);
        Assert.assertTrue(!roleList.isEmpty());


        RoleInfoDetail roleInfoDetail = roleService.findRoleById(roleList.get(0).getId());
        Assert.assertNotNull(roleInfoDetail);
        Assert.assertTrue(roleInfoDetail.getModules().size()>0);
        Assert.assertTrue(roleInfoDetail.isActive());

    }
}
