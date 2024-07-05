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
package org.thingsboard.server.service.entitiy.role;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.rule.engine.api.MailService;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.Role;
import org.thingsboard.server.common.data.role.RoleFilter;
import org.thingsboard.server.common.data.role.RoleInfoDetail;
import org.thingsboard.server.common.data.role.RoleRequest;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
import org.thingsboard.server.service.entitiy.user.TbUserService;
import org.thingsboard.server.service.security.system.SystemSecurityService;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.thingsboard.server.controller.UserController.ACTIVATE_URL_PATTERN;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultRoleService extends AbstractTbEntityService implements TbRoleService {

    private final RoleService roleService;

    @Override
    public Role save(RoleRequest tbRole, User user) throws ThingsboardException {
        ActionType actionType = tbRole.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {
            Role savedRole = checkNotNull(roleService.saveRole(tbRole, user));
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, savedRole.getId(), savedRole, actionType, user);
            return savedRole;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.ROLE), tbRole, actionType, user, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(RoleId roleId, User responsibleUser) throws ThingsboardException {
        ActionType actionType = ActionType.DELETED;

        try {
            roleService.deleteRoleByRoleId(roleId);
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, roleId, null, actionType, responsibleUser);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.ROLE),
                    actionType, responsibleUser, e, roleId.toString());
            throw e;
        }
    }

    @Override
    public RoleInfoDetail findRoleById(RoleId roleId) {
        return roleService.findRoleById(roleId);
    }

    @Override
    public PageData<Role> findAllRoleByFilter(PageLink pageLink, RoleFilter roleFilter) {
        return roleService.findAllRoleByFilter(pageLink, roleFilter);
    }
    @Override
    public List<Role> findAll() {
        return roleService.findAll();
    }
}
