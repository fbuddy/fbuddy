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
import org.springframework.data.annotation.LastModifiedDate;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.common.data.id.RolePermissionId;
import org.thingsboard.server.common.data.role.RolePermission;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.role.RolePermissionCompositeKey;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_ROLE_PERMISSION_TABLE_NAME)
//@IdClass(RolePermissionCompositeKey.class)
public final class RolePermissionEntity extends BaseSqlEntity<RolePermission> {

    @Column(name = ModelConstants.UPDATED_TIME_PROPERTY)
    @LastModifiedDate
    protected long updatedTime;

    @Column(name = ModelConstants.USER_ROLE_ID_PROPERTY)
    private UUID roleId;

    @Column(name = ModelConstants.USER_MODULE_ID_PROPERTY)
    private UUID moduleId;

    @Column(name = ModelConstants.IS_ACTIVE_PROPERTY)
    private Boolean isActive;


    public RolePermissionEntity() {
        super();
    }

    public RolePermissionEntity(RolePermission rolePermission) {
        if (rolePermission.getId() != null) {
            this.setUuid(rolePermission.getId().getId());
        }
        this.setCreatedTime(rolePermission.getCreatedTime());
        this.isActive = rolePermission.isActive();
        this.roleId = rolePermission.getRoleId().getId();
        this.moduleId = rolePermission.getModuleId().getId();
        //this.viewPermission = rolePermission.isViewPermission();
        //this.editPermission = rolePermission.isEditPermission();

    }

    @Override
    public RolePermission toData() {
        RolePermission rolePermission = new RolePermission(new RolePermissionId(this.getUuid()));
        rolePermission.setRoleId(new RoleId(this.getRoleId()));
        rolePermission.setModuleId(new ModuleId(this.getModuleId()));
        //rolePermission.setViewPermission(this.viewPermission);
        //rolePermission.setEditPermission(this.editPermission);
        rolePermission.setActive(this.isActive);

        return rolePermission;
    }

}
