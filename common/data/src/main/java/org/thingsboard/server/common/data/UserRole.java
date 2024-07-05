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
package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.notification.targets.NotificationRecipient;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class UserRole extends BaseDataWithAdditionalInfo<UserRoleId> {

    private static final long serialVersionUID = 8250339805336035966L;

    @NoXss
    @Length(fieldName = "user id")
    private UserId userId;
    @NoXss
    @Length(fieldName = "role id")
    private RoleId roleId;

    @ApiModelProperty(position = 5, value = "Status of the User Role Mapping", example = "true")
    private boolean isActive;

    public UserRole() {
        super();
    }

    public UserRole(UserRoleId id) {
        super(id);
    }

    public UserRole(UserRole userRole) {
        super(userRole);
        this.userId = userRole.getUserId();
        this.roleId = userRole.getRoleId();
        this.isActive = true;

    }



    @ApiModelProperty(position = 1, value = "JSON object with the User Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer.")
    @Override
    public UserRoleId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the user creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @ApiModelProperty(position = 3, required = false, value = "First name of the user", example = "John")
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    @ApiModelProperty(position = 4, required = false, value = "Last name of the user", example = "Doe")
    public RoleId getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleId roleId) {
        this.roleId = roleId;
    }
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserRole [userId=");
        builder.append(userId);
        builder.append(", roleId=");
        builder.append(roleId);
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }


    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
