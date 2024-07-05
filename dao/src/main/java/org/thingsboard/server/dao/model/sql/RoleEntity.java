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
package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.role.Role;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_ROLE_TABLE_NAME)
public final class RoleEntity extends BaseSqlEntity<Role> {



    @Column(name = ModelConstants.NAME_PROPERTY)
    private String name;
    @Column(name = ModelConstants.USER_ROLE_DESCRIPTION_PROPERTY)
    private String description;

    @Column(name = ModelConstants.IS_ACTIVE_PROPERTY)
    private Boolean isActive;

    @Column(name = ModelConstants.UPDATED_BY_PROPERTY)
    private UUID updatedBy;

    @Column(name = ModelConstants.UPDATED_TIME_PROPERTY)
    protected Long updatedTime;

    @Column(insertable = false, updatable = false, columnDefinition="serial", name = ModelConstants.USER_ROLE_ROLE_ID_PROPERTY)
    private Long roleId;

    @Transient
    private String updatedByUserName;


    public RoleEntity() {
        super();
    }

    public RoleEntity(Role role) {
        if (role.getId() != null) {
            this.setUuid(role.getId().getId());
        }
        this.setCreatedTime(role.getCreatedTime());
        this.name = role.getName();
        this.description = role.getDescription();
        this.isActive = role.isActive();
        this.updatedBy = role.getUpdatedBy().getId();
        this.updatedTime = role.getUpdatedTime();
        this.roleId = role.getRoleId();
    }
    public RoleEntity(RoleEntity role, String userName) {
        this.setUuid(role.getId());
        this.setCreatedTime(role.getCreatedTime());
        this.name = role.getName();
        this.description = role.getDescription();
        this.isActive = role.getIsActive();
        this.updatedBy = role.getUpdatedBy();
        this.updatedTime = role.getUpdatedTime();
        this.roleId = role.getRoleId();
        this.updatedByUserName = userName;
    }

    @Override
    public Role toData() {
        Role role = new Role(new RoleId(this.getUuid()));
        role.setName(this.name);
        role.setActive(this.isActive);
        role.setDescription(this.description);
        if(this.updatedBy != null)
        {
            role.setUpdatedBy(new UserId(this.updatedBy));
        }
        if(this.updatedTime != null)
        {
            role.setUpdatedTime(this.updatedTime);
        }
        role.setCreatedTime(this.createdTime);
        role.setUpdatedByUserName(this.updatedByUserName);
        role.setRoleId(this.roleId);
        return role;
    }

}
