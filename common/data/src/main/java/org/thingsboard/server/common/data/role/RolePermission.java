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
package org.thingsboard.server.common.data.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.RolePermissionId;

@ApiModel
@Data
@ToString(exclude = {"profileDataBytes"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class RolePermission extends BaseData<RolePermissionId> {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @ApiModelProperty(position = 1, value = "JSON object with Role id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private RoleId roleId;

    @ApiModelProperty(position = 2, value = "JSON object with Module id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private ModuleId moduleId;

    @ApiModelProperty(position = 4, value = "Activation Status of the Permission", example = "true")
    private boolean isActive;

    public RolePermission() {
        super();
    }

    public RolePermission(RolePermissionId rolePermissionId) {
        super(rolePermissionId);
    }

    public RolePermission(RolePermission rolePermission) {
        super(rolePermission);
        this.roleId = rolePermission.getRoleId();
        this.moduleId = rolePermission.getModuleId();
        this.isActive = rolePermission.isActive();
        //this.viewPermission = rolePermission.isViewPermission();
        //this.editPermission = rolePermission.isEditPermission();
    }
    public RolePermission(RoleId roleId, RolePermissionRequest rolePermission) {
        super(rolePermission);
        this.roleId = roleId;
        this.moduleId = rolePermission.getModuleId();
        this.isActive = true;
    }

    @ApiModelProperty(position = 1, value = "JSON object with the role Id. " +
            "Specify this field to update the role. " +
            "Referencing non-existing Role Id will cause error. " +
            "Omit this field to create new role.")
    @Override
    public RolePermissionId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Role creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }






}
