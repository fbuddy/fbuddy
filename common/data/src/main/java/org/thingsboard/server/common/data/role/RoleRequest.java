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
package org.thingsboard.server.common.data.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.List;

@ApiModel
@Data
@ToString(exclude = {"profileDataBytes"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class RoleRequest extends BaseData<RoleId> implements HasName {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 3, value = "Name of the Role", example = "Role 1")
    private String name;

    @NoXss
    @Length(fieldName = "description")
    @ApiModelProperty(position = 4, value = "Description of the Role", example = "Role 1")
    private String description;


    @NoXss
    @Length(fieldName = "modules")
    @ApiModelProperty(position = 5, value = "JSON object with the module Id and permission of the Role")
    private List<RolePermissionRequest> modules;

    @ApiModelProperty(position = 6, value = "Status of the Role", example = "true")
    private boolean isActive = true;

    public RoleRequest() {
        super();
    }

    public RoleRequest(RoleId roleId) {
        super(roleId);
    }

    public RoleRequest(RoleRequest role) {
        super(role);
        this.name = role.getName();
        this.description = role.getDescription();
        this.isActive = role.isActive();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the role Id. " +
            "Specify this field to update the role. " +
            "Referencing non-existing Role Id will cause error. " +
            "Omit this field to create new role.")
    @Override
    public RoleId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Role creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @Override
    public String getName() {
        return name;
    }




}
