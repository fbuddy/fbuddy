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
package org.thingsboard.server.dao.sql.user;

import org.aspectj.apache.bcel.classfile.Constant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.UserProfileStatus;
import org.thingsboard.server.common.data.UserProfileType;
import org.thingsboard.server.common.data.UserType;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.sql.UserEntity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import java.util.List;

/**
 * @author Valerii Sosliuk
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u FROM UserEntity u WHERE u.email=:userName " +
            "OR u.phone=:userName OR u.userName=:userName ")
    UserEntity findByEmailOrUserNameOrPhone(@Param("userName") String userName);
    UserEntity findByEmail(String email);

    UserEntity findByUserName(String userName);

    UserEntity findByTenantIdAndEmail(UUID tenantId, String email);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE u.tenantId = :tenantId " +
            "AND u.customerId = :customerId AND exists(select 1 from ModuleEntity m " +
            "INNER JOIN UserRoleEntity ur on u.id = ur.userId " +
            "INNER JOIN RolePermissionEntity rp on ur.roleId = rp.roleId AND rp.isActive = true " +
            "WHERE  m.authority = :authority and rp.moduleId = m.id )" +
            "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findUsersByAuthority(@Param("tenantId") UUID tenantId,
                                          @Param("customerId") UUID customerId,
                                          @Param("searchText") String searchText,
                                          @Param("authority") String authority,
                                          Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " +
            "AND u.customerId IN (:customerIds) " +
            "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findTenantAndCustomerUsers(@Param("tenantId") UUID tenantId,
                                                @Param("customerIds") Collection<UUID> customerIds,
                                                @Param("searchText") String searchText,
                                                Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId " +
            "AND (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true)")
    Page<UserEntity> findByTenantId(@Param("tenantId") UUID tenantId,
                                    @Param("searchText") String searchText,
                                    Pageable pageable);

    @Query("SELECT u FROM UserEntity u " +
            "WHERE exists(select 1 from ModuleEntity m " +
            "INNER JOIN UserRoleEntity ur on u.id = ur.userId " +
            "INNER JOIN RolePermissionEntity rp on ur.roleId = rp.roleId " +
            "WHERE  m.authority = :authority and rp.moduleId = m.id )" )
    Page<UserEntity> findAllByAuthority(@Param("authority") String authority, Pageable pageable);

    Page<UserEntity> findByAuthorityAndTenantIdIn(Authority authority, Collection<UUID> tenantsIds, Pageable pageable);

    @Query("SELECT u FROM UserEntity u INNER JOIN TenantEntity t ON u.tenantId = t.id AND u.authority = :authority " +
            "INNER JOIN TenantProfileEntity p ON t.tenantProfileId = p.id " +
            "WHERE p.id IN :profiles")
    Page<UserEntity> findByAuthorityAndTenantProfilesIds(@Param("authority") Authority authority,
                                                         @Param("profiles") Collection<UUID> tenantProfilesIds,
                                                         Pageable pageable);

    Long countByTenantId(UUID tenantId);


    @Query("SELECT u FROM UserEntity u " +
            " WHERE (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true " +
            " OR ilike(u.firstName, CONCAT('%', :searchText, '%')) = true" +
            " OR ilike(u.lastName, CONCAT('%', :searchText, '%')) = true" +
            " OR ilike(u.userName, CONCAT('%', :searchText, '%')) = true)" +
            " AND (:firstName IS NULL OR ilike(u.firstName, CONCAT('%', :firstName, '%')) = true)" +
            " AND (:lastName IS NULL OR ilike(u.lastName, CONCAT('%', :lastName, '%')) = true)" +
            " AND (:email IS NULL OR ilike(u.email, CONCAT('%', :email, '%')) = true)" +
            " AND (:phone IS NULL OR ilike(u.phone, CONCAT('%', :phone, '%')) = true)" +
            " AND (:company IS NULL OR ilike(u.company, CONCAT('%', :company, '%')) = true)" +
            " AND (:department IS NULL OR ilike(u.department, CONCAT('%', :department, '%')) = true)" +
            " AND (:designation IS NULL OR ilike(u.designation, CONCAT('%', :designation, '%')) = true)" +
            " AND (:userType IS NULL OR u.userType = :userType)" +
            " AND (:userProfileType IS NULL OR u.userProfileType = :userProfileType)" +
            " AND (:userName IS NULL OR ilike(u.userName, CONCAT('%', :userName, '%')) = true)" +
            " AND (:serialNumber IS NULL OR u.serialNumber = :serialNumber)" +
            " AND (:userProfileStatus IS NULL OR u.userProfileStatus = :userProfileStatus)" +
            " AND (:startDate IS NULL OR u.createdTime >= :startDate) " +
            " AND (:endDate IS NULL OR u.createdTime <= :endDate) "
    )
    Page<UserEntity> findAllWithFilter(@Param("searchText") String searchText,
                                              @Param("firstName") String firstName,
                                              @Param("lastName") String lastName,
                                              @Param("email") String email,
                                              @Param("phone") String phone,
                                              @Param("userType") UserType userType,
                                              @Param("userProfileType") UserProfileType userProfileType,
                                              @Param("company") String company,
                                              @Param("department") String department,
                                              @Param("designation") String designation,
                                              @Param("userName") String userName,
                                              @Param("serialNumber") Long serialNumber,
                                              @Param("userProfileStatus") UserProfileStatus userProfileStatus,
                                              @Param("startDate") Long startDate,
                                              @Param("endDate") Long endDate,
                                              Pageable pageable);

    @Query("SELECT u FROM UserEntity u "+
            " INNER JOIN UserRoleEntity ur on u.id = ur.userId and (ur.roleId IN :roleIds) " +
            " WHERE (:searchText IS NULL OR ilike(u.email, CONCAT('%', :searchText, '%')) = true " +
            " OR ilike(u.firstName, CONCAT('%', :searchText, '%')) = true" +
            " OR ilike(u.lastName, CONCAT('%', :searchText, '%')) = true" +
            " OR ilike(u.userName, CONCAT('%', :searchText, '%')) = true)" +
            " AND (:firstName IS NULL OR ilike(u.firstName, CONCAT('%', :firstName, '%')) = true)" +
            " AND (:lastName IS NULL OR ilike(u.lastName, CONCAT('%', :lastName, '%')) = true)" +
            " AND (:email IS NULL OR ilike(u.email, CONCAT('%', :email, '%')) = true)" +
            " AND (:phone IS NULL OR ilike(u.phone, CONCAT('%', :phone, '%')) = true)" +
            " AND (:company IS NULL OR ilike(u.company, CONCAT('%', :company, '%')) = true)" +
            " AND (:department IS NULL OR ilike(u.department, CONCAT('%', :department, '%')) = true)" +
            " AND (:designation IS NULL OR ilike(u.designation, CONCAT('%', :designation, '%')) = true)" +
            " AND (:userType IS NULL OR u.userType = :userType)" +
            " AND (:userProfileType IS NULL OR u.userProfileType = :userProfileType)" +
            " AND (:userName IS NULL OR ilike(u.userName, CONCAT('%', :userName, '%')) = true)" +
            " AND (:serialNumber IS NULL OR u.serialNumber = :serialNumber)" +
            " AND (:userProfileStatus IS NULL OR u.userProfileStatus = :userProfileStatus)" +
            " AND (:startDate IS NULL OR u.createdTime >= :startDate) " +
            " AND (:endDate IS NULL OR u.createdTime <= :endDate) "
    )
    Page<UserEntity> findAllByRolesWithFilter(@Param("searchText") String searchText,
                                              @Param("firstName") String firstName,
                                              @Param("lastName") String lastName,
                                              @Param("email") String email,
                                              @Param("phone") String phone,
                                              @Param("userType") UserType userType,
                                              @Param("userProfileType") UserProfileType userProfileType,
                                              @Param("company") String company,
                                              @Param("department") String department,
                                              @Param("designation") String designation,
                                              @Param("roleIds") Collection<UUID> roleIds,
                                              @Param("userName") String userName,
                                              @Param("serialNumber") Long serialNumber,
                                              @Param("userProfileStatus") UserProfileStatus userProfileStatus,
                                              @Param("startDate") Long startDate,
                                              @Param("endDate") Long endDate,
                                              Pageable pageable);

    UserEntity findBySerialNumber(Integer serialNumber);

    UserEntity findByPhone(String phone);

    @Query("SELECT u FROM UserEntity u WHERE u.id IN :userIds")
    List<UserEntity> findUsersByIds(@Param("userIds") Collection<UUID> userIds);

}
