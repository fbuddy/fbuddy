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
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Role extends BaseData<RoleId> implements HasName {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 3, value = "Name of the Role", example = "Role 1")
    private String name;

    @NoXss
    @Length(fieldName = "description")
    @ApiModelProperty(position = 4, value = "Description of the Role", example = "Role 1 Description")
    private String description;

    @ApiModelProperty(position = 5, value = "Status of the Role", example = "true")
    private boolean isActive;

    @ApiModelProperty(position = 6, value = "Id of user who is updating the Role")
    private UserId updatedBy;

    @ApiModelProperty(position = 7, value = "Time when Role was updated")
    protected long updatedTime;

    @ApiModelProperty(position = 8, value = "Name of user who updated the Role")
    private String updatedByUserName;
    private Long roleId;


    public Role() {
        super();
    }

    public Role(RoleId roleId) {
        super(roleId);
    }

    public Role(Role role) {
        super(role);
        this.name = role.getName();
        this.isActive = role.isActive();
        this.description = role.getDescription();
        this.updatedTime = role.getUpdatedTime();
        this.updatedBy = role.getUpdatedBy();
        this.updatedByUserName = role.getUpdatedByUserName();
        this.roleId = role.getRoleId();
    }
    public Role(RoleRequest role) {
        super(role);
        this.name = role.getName();
        this.isActive = role.isActive();
        this.description = role.getDescription();
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

    @ApiModelProperty(position = 8, value = "Timestamp of the Role updated, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    public long getUpdatedTime() {
        return this.updatedTime;
    }
    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String getName() {
        return name;
    }


    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
