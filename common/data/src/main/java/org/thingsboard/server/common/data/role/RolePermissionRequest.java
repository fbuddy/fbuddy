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
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class RolePermissionRequest extends BaseData<RolePermissionId> {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @ApiModelProperty(position = 2, value = "JSON object with Module id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private ModuleId moduleId;

    @ApiModelProperty(position = 3, value = "Status of the View Permission", example = "true")
    private boolean active;


    public RolePermissionRequest() {
        super();
    }

    public RolePermissionRequest(RolePermissionId rolePermissionId) {
        super(rolePermissionId);
    }

    public RolePermissionRequest(RolePermissionRequest rolePermission) {
        super(rolePermission);
        this.moduleId = rolePermission.getModuleId();
        this.active = rolePermission.isActive();

    }

    @ApiModelProperty(position = 1, value = "JSON object with the role Id. " +
            "Specify this field to update the role. " +
            "Referencing non-existing Role Id will cause error. " +
            "Omit this field to create new role.")
    @Override
    public RolePermissionId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Role Permission creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }






}
