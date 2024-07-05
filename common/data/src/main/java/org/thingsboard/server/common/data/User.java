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
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class User extends BaseDataWithAdditionalInfo<UserId> implements HasName, HasTenantId, HasCustomerId, NotificationRecipient {

    private static final long serialVersionUID = 8250339805336035966L;

    private TenantId tenantId;
    private CustomerId customerId;
    private String userName;
    private String password;
    private String confirmPassword;
    private String email;
    private Authority authority;
    private Set<String> newAuthorities = new HashSet<>();
    @NoXss
    @Length(fieldName = "first name")
    private String firstName;
    @NoXss
    @Length(fieldName = "last name")
    private String lastName;
    @NoXss
    private String phone;

    @NoXss
    private UserProfileType userProfileType;
    @NoXss
    private UserType userType;

    @NoXss
    private String company;
    @NoXss
    private String department;
    @NoXss
    private String designation;

    @NoXss
    private List<UserRole> roles = new ArrayList<>();

    @NoXss
    private Set<Module> modules = new HashSet<>();

    @NoXss
    @Length(fieldName = "roles")
    private List<RoleId> roleIds = new ArrayList<>();

    @NoXss
    private String roleNames;

    private Long serialNumber;
    private Integer maxRecordPerPage;

    private UserProfileStatus userProfileStatus = UserProfileStatus.ACTIVE;

    @NoXss
    private List<UserPenalAccessType> userPenalAccessType;

    public User() {
        super();
    }

    public User(UserId id) {
        super(id);
    }

    public User(User user) {
        super(user);
        this.tenantId = user.getTenantId();
        this.customerId = user.getCustomerId();
        this.email = user.getEmail();
        //this.authority = user.getAuthority();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.roles = user.getRoles();
        this.modules = user.getModules();
        this.roleIds = user.getRoleIds();
        this.newAuthorities = user.getNewAuthorities();

        this.userProfileType = user.getUserProfileType();
        this.userType = user.getUserType();
        this.company = user.getCompany();
        this.department = user.getDepartment();
        this.designation = user.getDesignation();
        this.userProfileStatus = user.getUserProfileStatus();
        this.userPenalAccessType = user.getUserPenalAccessType();

        this.userName = user.getUserName();
    }
    @ApiModelProperty(position = 4, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Set<Module> getModules() {
        return this.modules;
    }
    public void setModules(Set<Module> modules) {
        this.modules = modules;
        for(Module module:modules)
        {
            if(module.getAuthority() == null) continue;
            if(Authority.SYS_ADMIN.equals(module.getAuthority())) {
                this.newAuthorities = new HashSet<>();
                newAuthorities.add(Authority.SYS_ADMIN.name());
                break;
            }
            else if(Authority.TENANT_ADMIN.equals(module.getAuthority())) {
                this.newAuthorities = new HashSet<>();
                this.newAuthorities.add(Authority.TENANT_ADMIN.name());
                break;
            }
            else if(Authority.CUSTOMER_USER.equals(module.getAuthority())) {
                this.newAuthorities = new HashSet<>();
                this.newAuthorities.add(Authority.CUSTOMER_USER.name());
                break;
            }

            this.newAuthorities.add(module.getAuthority());
        }
    }

    @ApiModelProperty(position = 1, value = "JSON object with the User Id. " +
            "Specify this field to update the device. " +
            "Referencing non-existing User Id will cause error. " +
            "Omit this field to create new customer.")
    @Override
    public UserId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the user creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @JsonIgnore
    public TenantId getTenantId() {
        return tenantId;
    }

    public void setTenantId(TenantId tenantId) {
        this.tenantId = tenantId;
    }

    @JsonIgnore
    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    @ApiModelProperty(position = 5, required = true, value = "Email of the user", example = "user@example.com")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ApiModelProperty(position = 6, accessMode = ApiModelProperty.AccessMode.READ_ONLY, value = "User Name, readonly", example = "user@example.com")
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return userName;
    }

    @ApiModelProperty(position = 7, required = true, value = "Authority", example = "SYS_ADMIN, TENANT_ADMIN or CUSTOMER_USER")
    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    @ApiModelProperty(position = 3, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Set<String> getNewAuthorities() {
        return newAuthorities;
    }
    public void setNewAuthorities(Set<String> authorities) {
        this.newAuthorities = authorities;
    }


    @ApiModelProperty(position = 8, required = false, value = "First name of the user", example = "John")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ApiModelProperty(position = 9, required = false, value = "Last name of the user", example = "Doe")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ApiModelProperty(position = 10, required = false, value = "Phone number of the user", example = "38012345123")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ApiModelProperty(position = 11, value = "Additional parameters of the user", dataType = "com.fasterxml.jackson.databind.JsonNode")
    @Override
    public JsonNode getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @ApiModelProperty(position = 12, required = true, value = "Roles of the user")
    public List<RoleId> getRoleIds() {
        return roleIds;
    }
    public void setRoleIds(List<RoleId> roleIds) {
        this.roleIds = roleIds;
    }

    @ApiModelProperty(position = 13, required = false, value = "User Profile type of the user" , example = "HUMAN or COMPUTER")
    public UserProfileType getUserProfileType() {
        return userProfileType;
    }

    public void setUserProfileType(UserProfileType userProfileType) {
        this.userProfileType = userProfileType;
    }

    @ApiModelProperty(position = 14, required = false, value = "User type of the user", example = "INTERNAL or EXTERNAL")
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    @ApiModelProperty(position = 15, required = false, value = "company of the user", example = "ABC")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @ApiModelProperty(position = 16, required = false, value = "Department of the user", example = "HR")
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @ApiModelProperty(position = 17, required = false, value = "Designation", example = "Manager")
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    @ApiModelProperty(position = 18, required = false, value = "UserId for display", example = "123456789")
    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    @ApiModelProperty(position = 19, required = false, value = "Username of the user", example = "user@example.com")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @ApiModelProperty(position = 20, required = false, value = "Password of the user", example = "user@example.com")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ApiModelProperty(position = 21, required = true, value = "Confirm Password of the user", example = "user@example.com")
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @ApiModelProperty(position = 22, required = true, value = "Profile Status of the user", example = "ACTIVE or IN_ACTIVE")
    public UserProfileStatus getUserProfileStatus() {
        return this.userProfileStatus;
    }
    public void setUserProfileStatus(UserProfileStatus userProfileStatus) {
        this.userProfileStatus = userProfileStatus;
    }

    @ApiModelProperty(position = 23, required = false, value = "House Hold Type of the user", example = "PMA_ID_WID or HOUSEHOLD_ID")
    public List<UserPenalAccessType> getUserPenalAccessType() {
        return userPenalAccessType;
    }

    public void setUserPenalAccessType(List<UserPenalAccessType> userPenalAccessType) {
        this.userPenalAccessType = userPenalAccessType;
    }

    @ApiModelProperty(position = 24, required = true, value = "maxRecordPerPage", example = "25")
    public Integer getMaxRecordPerPage() {
        return maxRecordPerPage;
    }

    public void setMaxRecordPerPage(Integer maxRecordPerPage) {
        this.maxRecordPerPage = maxRecordPerPage;
    }

    public String getRoleNames(){
        return roleNames;
    }
    public void setRoleNames(String roleNames){
        this.roleNames = roleNames;
    }

    @JsonIgnore
    public List<UserRole> getRoles() {
        return roles;
    }
    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
        for(UserRole role: roles){
            this.roleIds.add(role.getRoleId());
        }
    }

    @JsonIgnore
    public String getTitle() {
        return getTitle(email, firstName, lastName);
    }

    public static String getTitle(String email, String firstName, String lastName) {
        String title = "";
        if (isNotEmpty(firstName)) {
            title += firstName;
        }
        if (isNotEmpty(lastName)) {
            if (!title.isEmpty()) {
                title += " ";
            }
            title += lastName;
        }
        if (title.isEmpty()) {
            title = email;
        }
        return title;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [tenantId=");
        builder.append(tenantId);
        builder.append(", customerId=");
        builder.append(customerId);
        builder.append(", email=");
        builder.append(email);
        //builder.append(", authority=");
        //builder.append(authority);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", additionalInfo=");
        builder.append(getAdditionalInfo());
        builder.append(", createdTime=");
        builder.append(createdTime);
        builder.append(", id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

    @JsonIgnore
    public boolean isSystemAdmin() {
        return tenantId == null || EntityId.NULL_UUID.equals(tenantId.getId());
    }

    @JsonIgnore
    public boolean isTenantAdmin() {
        return !isSystemAdmin() && (customerId == null || EntityId.NULL_UUID.equals(customerId.getId()));
    }

    @JsonIgnore
    public boolean isCustomerUser() {
        return !isSystemAdmin() && !isTenantAdmin();
    }
}
