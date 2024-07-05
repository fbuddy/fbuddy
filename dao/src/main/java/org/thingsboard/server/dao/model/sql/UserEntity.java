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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/21/2017.
 */
/**
 * Updated by Prashant Gupta (BARC) on 3/14/2024.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_PG_HIBERNATE_TABLE_NAME)
public class UserEntity extends BaseSqlEntity<User> {

    @Column(name = ModelConstants.USER_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.USER_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_AUTHORITY_PROPERTY)
    private Authority authority;

    @Column(name = ModelConstants.USER_USERNAME_PROPERTY, unique = true)
    private String userName;

    @Column(name = ModelConstants.USER_EMAIL_PROPERTY, unique = true)
    private String email;

    @Column(name = ModelConstants.USER_FIRST_NAME_PROPERTY)
    private String firstName;

    @Column(name = ModelConstants.USER_LAST_NAME_PROPERTY)
    private String lastName;

    @Column(name = ModelConstants.PHONE_PROPERTY)
    private String phone;

    @Type(type = "json")
    @Column(name = ModelConstants.USER_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_PROFILE_STATUS_PROPERTY)
    private UserProfileStatus userProfileStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_PROFILE_TYPE_PROPERTY)
    private UserProfileType userProfileType;
    @Enumerated(EnumType.STRING)
    @Column(name = ModelConstants.USER_TYPE_PROPERTY)
    private UserType userType;

    @Column(name = ModelConstants.USER_COMPANY_PROPERTY)
    private String company;
    @Column(name = ModelConstants.USER_DEPARTMENT_PROPERTY)
    private String department;
    @Column(name = ModelConstants.USER_DESIGNATION_PROPERTY)
    private String designation;

    @Column(insertable = false, updatable = false, columnDefinition="serial", name = ModelConstants.USER_SERIAL_NUMBER_PROPERTY)
    private Long serialNumber;

    @Type(type = "json")
    @Column(name = ModelConstants.USER_HOUSE_HOLD_TYPE)
    private JsonNode userPenalAccessType;

    @Column(name = ModelConstants.USER_MAX_RECORD_PER_PAGE_PROPERTY)
    private Integer maxRecordPerPage;

    public UserEntity() {
    }

    public UserEntity(User user) {
        if (user.getId() != null) {
            this.setUuid(user.getId().getId());
        }
        this.setCreatedTime(user.getCreatedTime());
        //this.authority = user.getAuthority();
        if (user.getTenantId() != null) {
            this.tenantId = user.getTenantId().getId();
        }
        if (user.getCustomerId() != null) {
            this.customerId = user.getCustomerId().getId();
        }
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.additionalInfo = user.getAdditionalInfo();

        this.userProfileStatus = user.getUserProfileStatus();
        this.userProfileType = user.getUserProfileType();
        this.userType = user.getUserType();
        this.company = user.getCompany();
        this.department = user.getDepartment();
        this.designation = user.getDesignation();
        this.serialNumber = user.getSerialNumber();
        this.userPenalAccessType = JacksonUtil.valueToTree(user.getUserPenalAccessType());
        this.maxRecordPerPage = user.getMaxRecordPerPage();
    }

    @Override
    public User toData() {
        User user = new User(new UserId(this.getUuid()));
        user.setCreatedTime(createdTime);
        //user.setAuthority(authority);
        if (tenantId != null) {
            user.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            user.setCustomerId(new CustomerId(customerId));
        }
        user.setEmail(email);
        user.setUserName(userName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setAdditionalInfo(additionalInfo);

        user.setUserProfileStatus(userProfileStatus);
        user.setUserProfileType(userProfileType);
        user.setUserType(userType);
        user.setCompany(company);
        user.setDepartment(department);
        user.setDesignation(designation);
        user.setSerialNumber(serialNumber);
        user.setUserPenalAccessType(JacksonUtil.convertValue(userPenalAccessType, new TypeReference<List<UserPenalAccessType>>() {
        }));
        user.setMaxRecordPerPage(maxRecordPerPage);
        return user;
    }

}
