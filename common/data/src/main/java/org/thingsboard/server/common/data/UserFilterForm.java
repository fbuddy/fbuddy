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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@ApiModel
public class UserFilterForm implements HasName {

    private String email;
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
    private UserProfileStatus userProfileStatus;
    @NoXss
    private UserType userType;

    @NoXss
    private String company;
    @NoXss
    private String department;
    @NoXss
    private String designation;
    @NoXss
    private Long serialNumber;
    @NoXss
    private String userName;

    @NoXss
    private Long startDate;
    @NoXss
    private Long endDate;

    @NoXss
    @Length(fieldName = "roles")
    private List<UUID> roleIds = new ArrayList<>();

    public UserFilterForm() {
        super();
    }


    public UserFilterForm(UserFilterForm user) {
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.roleIds = user.getRoleIds();

        this.userProfileType = user.getUserProfileType();
        this.userType = user.getUserType();
        this.company = user.getCompany();
        this.department = user.getDepartment();
        this.designation = user.getDesignation();

        this.userName = user.getUserName();
        this.serialNumber = user.getSerialNumber();
        this.userProfileStatus = user.getUserProfileStatus();
    }



    @ApiModelProperty(position = 5, value = "Email of the user", example = "user@example.com")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @ApiModelProperty(position = 6, accessMode = ApiModelProperty.AccessMode.READ_ONLY, value = "Duplicates the email of the user, readonly", example = "user@example.com")
    @Override
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String getName() {
        return email;
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

    @ApiModelProperty(position = 12, value = "Role Ids of users")
    public List<UUID> getRoleIds() {
        return roleIds;
    }
    public void setRoleIds(List<UUID> roleIds) {
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

    @ApiModelProperty(position = 22, required = false, value = "Profile Status of the user", example = "ACTIVE or IN_ACTIVE")
    public UserProfileStatus getUserProfileStatus() {
        return this.userProfileStatus;
    }
    public void setUserProfileStatus(UserProfileStatus userProfileStatus) {
        this.userProfileStatus = userProfileStatus;
    }
    @ApiModelProperty(position = 23, required = false, value = "Start Date for date filter")
    public Long getStartDate() {
        return this.startDate;
    }
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }
    @ApiModelProperty(position = 24, required = false, value = "End Date for date filter")
    public Long getEndDate() {
        return this.endDate;
    }
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [");
        builder.append("email=");
        builder.append(email);
        builder.append(", firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append("]");
        return builder.toString();
    }
}
