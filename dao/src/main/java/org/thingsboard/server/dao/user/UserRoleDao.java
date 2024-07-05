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
package org.thingsboard.server.dao.user;

import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.UserRole;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.Dao;
import org.thingsboard.server.dao.TenantEntityDao;

import java.util.List;


public interface UserRoleDao extends Dao<UserRole> {

    /**
     * Save or update user role object
     *
     * @param userRole the user object
     * @return saved user entity
     */
    UserRole save(TenantId tenantId, UserRole userRole);

    /**
     * Find user by user.
     *
     * @param userId the user
     * @return the user entity
     */
    List<UserRole> findByUserId(TenantId tenantId, UserId userId);
    List<UserRole> findByRoleId(TenantId tenantId, RoleId roleId);
    List<UserRole> findByRoleId(RoleId id);


}
