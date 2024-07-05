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
package org.thingsboard.server.dao.sql.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.UserRole;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserRoleEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.user.UserRoleDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@SqlDao
public class JpaUserRoleDao extends JpaAbstractDao<UserRoleEntity, UserRole> implements UserRoleDao {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    protected Class<UserRoleEntity> getEntityClass() {
        return UserRoleEntity.class;
    }

    @Override
    protected JpaRepository<UserRoleEntity, UUID> getRepository() {
        return userRoleRepository;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.USER_ROLE;
    }

    @Override
    public UserRole save(TenantId tenantId, UserRole userRole) {
        return super.save(TenantId.SYS_TENANT_ID, userRole);
    }

    @Override
    public List<UserRole> findByUserId(TenantId tenantId, UserId userId) {
        return DaoUtil.convertDataList(userRoleRepository.findAllByUserId(userId.getId()));
    }
    @Override
    public List<UserRole> findByRoleId(TenantId tenantId, RoleId roleId) {
        return DaoUtil.convertDataList(userRoleRepository.findAllByRoleId(roleId.getId()));
    }
    @Override
    public List<UserRole> findByRoleId(RoleId id) {
        return DaoUtil.convertDataList(userRoleRepository.findAllByRoleId(UUID.fromString(id.toString())));
    }

    /*@Override
    public UserSettings findById(TenantId tenantId, UserSettingsCompositeKey id) {
        return DaoUtil.getData(userSettingsRepository.findById(id));
    }*/



}
