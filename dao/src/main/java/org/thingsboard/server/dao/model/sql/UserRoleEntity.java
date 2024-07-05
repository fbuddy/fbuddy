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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.UserRole;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Prashant Gupta on 3/13/1024.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_ROLE_HIBERNATE_TABLE_NAME)
public class UserRoleEntity extends BaseSqlEntity<UserRole> {

    @Column(name = ModelConstants.USER_ID_PROPERTY)
    private UUID userId;

    @Column(name = ModelConstants.USER_ROLE_ID_PROPERTY)
    private UUID roleId;

    @Column(name = ModelConstants.IS_ACTIVE_PROPERTY)
    private Boolean isActive;


    public UserRoleEntity() {
    }

    public UserRoleEntity(UserRole userRole) {
        if (userRole.getId() != null) {
            this.setUuid(userRole.getId().getId());
        }
        this.setCreatedTime(userRole.getCreatedTime());
        this.userId = userRole.getUserId().getId();
        this.roleId = userRole.getRoleId().getId();
        this.isActive = userRole.isActive();
    }

    @Override
    public UserRole toData() {
        UserRole userRole = new UserRole(new UserRoleId(this.getUuid()));
        userRole.setCreatedTime(createdTime);

        userRole.setUserId(new UserId(userId));
        userRole.setRoleId(new RoleId(roleId));
        userRole.setActive(isActive);
        return userRole;
    }

}
