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
package org.thingsboard.server.dao.role;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.*;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleService extends EntityDaoService {

    List<Role> findAll();

    PageData<Role> findAll(PageLink pageLink);

    PageData<Role> findAllRoleByFilter(PageLink pageLink, RoleFilter roleFilter);

    RoleInfoDetail findRoleById(RoleId roleId);

    RoleInfoDetail saveRole(RoleRequest role, User user);

    void deleteRoleByRoleId(RoleId roleId) throws ThingsboardException;

    List<RolePermission> findAllPermissionByRoleId(RoleId roleId);


    EntityType getEntityType();

    Set<String> findAuthoritiesByRoleIds(List<RoleId> roleIds);
    Set<Module> findModulesByRoleIds(List<RoleId> roleIds);

    String getRoleNamesByRoleIds(List<RoleId> roleIds);
}
