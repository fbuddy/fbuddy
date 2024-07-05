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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thingsboard.server.cache.role.RoleCacheEvictEvent;
import org.thingsboard.server.cache.role.RoleCacheKey;
import org.thingsboard.server.cache.role.RoleCacheValue;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserRole;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.*;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.entity.AbstractCachedEntityService;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.user.UserRoleDao;


import java.util.*;
import java.util.stream.Collectors;

import static org.thingsboard.server.dao.service.Validator.validateId;


@Service("RoleDaoService")
@Slf4j
public class RoleServiceImpl extends AbstractCachedEntityService<RoleCacheKey, RoleCacheValue, RoleCacheEvictEvent> implements RoleService {


    private static final String INCORRECT_ROLE_ID = "Incorrect RoleId ";
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ModuleDao moduleDao;

    @Autowired
    private RolePermissionDao rolePermissionDao;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserRoleDao userRoleDao;

    //private final ApplicationEventPublisher eventPublisher;
    //private final EntityCountService countService;

    @Override
    public List<Role> findAll() {
        return roleDao.findAll()
                .stream()
                .collect(Collectors.toList());

    }

    @Override
    public PageData<Role> findAll(PageLink pageLink) {
        return roleDao.findAll(pageLink);

    }
    @Override
    public PageData<Role> findAllRoleByFilter(PageLink pageLink, RoleFilter roleFilter) {
        return roleDao.findAllRoleByFilter(pageLink, roleFilter);

    }
    @Override
    @Transactional
    public RoleInfoDetail saveRole(RoleRequest roleRequest, User user) {
        RoleInfoDetail roleInfoDetailOld = null;
        RoleCacheValue oldRoleCacheValue = null;
        if(roleRequest.getId() != null) {
            oldRoleCacheValue = new RoleCacheValue(getRoleEntityFromCache(roleRequest.getId()), getRolePermissionEntitiesFromCache(roleRequest.getId()));
            roleInfoDetailOld = findRoleById(roleRequest.getId());
            //roleInfoDetailOld = convertToRoleInfo(roleInfoDetailOld, findAllPermissionByRoleId(roleInfoDetailOld.getId()));
            for(RoleInfoDetail.RolePermissionDetail existingPermission: roleInfoDetailOld.getModules())
            {
                boolean isFound = false;
                for(RolePermissionRequest rolePermissionRequest: roleRequest.getModules())
                {
                    if(existingPermission.getModuleId().equals(rolePermissionRequest.getModuleId()))
                    {
                        rolePermissionRequest.setId(existingPermission.getId());
                        isFound = true;
                        break;
                    }
                }
                if(!isFound)
                {
                    rolePermissionDao.removeById(TenantId.SYS_TENANT_ID, existingPermission.getId().getId());
                }
            }
        }
        else{
            Role existRoleByName = roleDao.findFirstByName(roleRequest.getName());
            if(existRoleByName!=null)
            {
                throw new IncorrectParameterException("Role Name Already Exists");
            }
        }
        Role role = new Role(roleRequest);
        role.setUpdatedBy(user.getId());
        role.setUpdatedTime(System.currentTimeMillis());
        role = roleDao.save(TenantId.SYS_TENANT_ID, role);


        List<RolePermission> rolePermissionList = new ArrayList<>();
        for(RolePermissionRequest rolePermissionRequest: roleRequest.getModules())
        {
            RolePermission rolePermission = new RolePermission(role.getId(), rolePermissionRequest);
            rolePermissionDao.save(TenantId.SYS_TENANT_ID, rolePermission);
            rolePermissionList.add(rolePermission);
        }
        RoleCacheValue newRoleCacheValue = new RoleCacheValue(role, rolePermissionList);
        RoleCacheEvictEvent roleCacheEvictEvent = new RoleCacheEvictEvent(role.getId(), oldRoleCacheValue, newRoleCacheValue);
        publishEvictEvent(roleCacheEvictEvent);
        RoleInfoDetail roleInfoDetail = new RoleInfoDetail(role);
        roleInfoDetail = convertToRoleInfo(roleInfoDetail, findAllPermissionByRoleId(role.getId()));
        return roleInfoDetail;
    }

    @Override
    public void deleteRoleByRoleId(RoleId roleId) throws ThingsboardException {
        if(roleDao.existsById(TenantId.SYS_TENANT_ID, roleId.getId())) {
            List<UserRole> userRoles = userRoleDao.findByRoleId(TenantId.SYS_TENANT_ID, roleId);
            if (userRoles.isEmpty()) {
                roleDao.removeById(TenantId.SYS_TENANT_ID, roleId.getId());
            } else {
                throw new ThingsboardException("This role is assigned to some Users. So it cannot be deleted", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
        }else {
            throw new ThingsboardException("This role id is not exists", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    private RoleInfoDetail convertToRoleInfo(RoleInfoDetail roleInfoDetail, List<RolePermission> permissions)
    {
        for(RolePermission permission:permissions)
        {
            RoleInfoDetail.RolePermissionDetail rolePermissionDetail = new RoleInfoDetail.RolePermissionDetail(permission);
            rolePermissionDetail.setName(moduleService.getModuleById(permission.getModuleId()).getName());
            roleInfoDetail.getModules().add(rolePermissionDetail);
        }
        return roleInfoDetail;
    }

    @Override
    public RoleInfoDetail findRoleById(RoleId roleId)
    {
        log.trace("Executing findDeviceById [{}]", roleId);
        validateId(roleId, INCORRECT_ROLE_ID + roleId);

        Role role = getRoleEntityFromCache(roleId);

        List<RolePermission> rolePermissions = getRolePermissionEntitiesFromCache(roleId);

        RoleInfoDetail roleInfoDetail = new RoleInfoDetail(role);
        roleInfoDetail = convertToRoleInfo(roleInfoDetail, rolePermissions);
        return roleInfoDetail;
    }
    private Role getRoleEntityFromCache(RoleId roleId){
        return cache.getAndPutInTransaction(new RoleCacheKey(roleId),
                () -> findRoleEntityById(roleId),
                RoleCacheValue::getRole,
                v -> RoleCacheValue.builder().role(v).rolePermissionList(findAllPermissionByRoleId(roleId)).build() ,
                false );
    }
    private List<RolePermission> getRolePermissionEntitiesFromCache(RoleId roleId){
        Role role = getRoleEntityFromCache(roleId);
        return cache.getAndPutInTransaction(new RoleCacheKey(roleId),
                () -> findAllPermissionByRoleId(roleId),
                RoleCacheValue::getRolePermissionList,
                v -> RoleCacheValue.builder().role(role).rolePermissionList(v).build(),
                false );
    }
    @Override
    public List<RolePermission> findAllPermissionByRoleId(RoleId roleId) {
        return rolePermissionDao.findByRoleId(roleId.getId());
    }


    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findRoleEntityById( new RoleId(entityId.getId())));
    }

    private Role findRoleEntityById(RoleId roleId)
    {
        return roleDao.findById(TenantId.SYS_TENANT_ID, roleId.getId());
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return RoleService.super.countByTenantId(tenantId);
    }

    @Override
    public void deleteEntity(TenantId tenantId, EntityId id) {
        RoleService.super.deleteEntity(tenantId, id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ROLE;
    }
    @Override
    public Set<Module> findModulesByRoleIds(List<RoleId> roleIds) {
        Set<Module> modules = new HashSet<>();
        for(RoleId roleId:roleIds)
        {
            RoleInfoDetail roleInfoDetail = findRoleById(roleId);
            for(RoleInfoDetail.RolePermissionDetail permission: roleInfoDetail.getModules()){
                modules.add(moduleService.getModuleById(permission.getModuleId()));
            }
        }
        return modules;
    }
    @Override
    public String getRoleNamesByRoleIds(List<RoleId> roleIds) {
        List<String> roleNames = new ArrayList<>();
        for(RoleId roleId:roleIds)
        {
            RoleInfoDetail roleInfoDetail = findRoleById(roleId);
            roleNames.add(roleInfoDetail.getName());
        }
        return String.join(", ", roleNames);
    }
    @Override
    public Set<String> findAuthoritiesByRoleIds(List<RoleId> roleIds) {
        Set<String> authorities = new HashSet<>();
        for(RoleId roleId:roleIds)
        {
            RoleInfoDetail roleInfoDetail = findRoleById(roleId);
            for(RoleInfoDetail.RolePermissionDetail permission: roleInfoDetail.getModules()){
                String authority = moduleService.getModuleById(permission.getModuleId()).getAuthority();
                if(Authority.SYS_ADMIN.equals(authority)) {
                    authorities = new HashSet<>();
                    authorities.add(Authority.SYS_ADMIN.name());
                    return authorities;
                }
                else if(Authority.TENANT_ADMIN.equals(authority)) {
                    authorities = new HashSet<>();
                    authorities.add(Authority.TENANT_ADMIN.name());
                    return authorities;
                }
                else if(Authority.CUSTOMER_USER.equals(authority)) {
                    authorities = new HashSet<>();
                    authorities.add(Authority.CUSTOMER_USER.name());
                    return authorities;
                }

                authorities.add(authority);
            }
        }
        return authorities;
    }

    @TransactionalEventListener(classes = RoleCacheEvictEvent.class)
    @Override
    public void handleEvictEvent(RoleCacheEvictEvent event) {
        List<RoleCacheKey> keys = new ArrayList<>(2);
        keys.add(new RoleCacheKey(event.getRoleId(), event.getNewValue()));

        keys.add(new RoleCacheKey(event.getRoleId(), event.getOldValue()));

        /*if (event.getOldValue()!=null && !event.getOldValue().equals(event.getNewValue())) {
            keys.add(new RoleCacheKey(event.getRoleId(), event.getOldValue()));
        }*/
        cache.evict(keys);
    }
}
