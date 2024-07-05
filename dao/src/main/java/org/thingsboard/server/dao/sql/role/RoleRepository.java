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
package org.thingsboard.server.dao.sql.role;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.role.Role;
import org.thingsboard.server.dao.model.sql.RoleEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Prashant on 3/6/2024.
 */
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    Role findFirstByName(String role);
    @Query("SELECT new RoleEntity(r, u.userName) FROM RoleEntity r LEFT JOIN UserEntity u ON u.id = r.updatedBy " +
            " WHERE r.isActive = true")
    List<RoleEntity> findAllWithRoleName();

    @Query("SELECT new RoleEntity(r, u.userName) FROM RoleEntity r " +
            "LEFT JOIN UserEntity u ON u.id = r.updatedBy " +
            "WHERE r.isActive = true AND r.name NOT IN ('System Admin', 'Tenant Admin') " +
            "AND (:searchText IS NULL OR ilike(r.name, CONCAT('%', :searchText, '%')) = true) " +
            "AND (:startDate IS NULL OR r.createdTime >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdTime <= :endDate) ")
    Page<RoleEntity> findAllRoleWithFilter(
            @Param("searchText") String searchText,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate,
            Pageable pageable);

    @Query("SELECT new RoleEntity(r, u.userName) FROM RoleEntity r " +
            "LEFT JOIN UserEntity u ON u.id = r.updatedBy " +
            "WHERE r.isActive = true AND r.name NOT IN ('System Admin', 'Tenant Admin') " +
            "AND (:searchText IS NULL OR ilike(r.name, CONCAT('%', :searchText, '%')) = true) " +
            "AND (r.id = :id) " +
            "AND (:startDate IS NULL OR r.createdTime >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdTime <= :endDate) ")
    Page<RoleEntity> findAllRoleWithFilterByRoleId(
            @Param("searchText") String searchText,
            @Param("id") UUID id,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate,
            Pageable pageable);

    @Query("SELECT new RoleEntity(r, u.userName) FROM RoleEntity r " +
            "LEFT JOIN UserEntity u ON u.id = r.updatedBy " +
            "WHERE r.isActive = true AND r.name NOT IN ('System Admin', 'Tenant Admin') " +
            "AND (r.updatedBy = :updatedBy) " +
            "AND (:searchText IS NULL OR ilike(r.name, CONCAT('%', :searchText, '%')) = true) " +
            "AND (:startDate IS NULL OR r.createdTime >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdTime <= :endDate) ")
    Page<RoleEntity> findAllRoleWithFilterUpdatedBy(
            @Param("searchText") String searchText,
            @Param("updatedBy") UUID updatedBy,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate,
            Pageable pageable);

    @Query("SELECT new RoleEntity(r, u.userName) FROM RoleEntity r " +
            "LEFT JOIN UserEntity u ON u.id = r.updatedBy " +
            "WHERE r.isActive = true AND r.name NOT IN ('System Admin', 'Tenant Admin') " +
            "AND (r.updatedBy = :updatedBy) " +
            "AND (:searchText IS NULL OR ilike(r.name, CONCAT('%', :searchText, '%')) = true) " +
            "AND (r.id = :id) " +
            "AND (:startDate IS NULL OR r.createdTime >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdTime <= :endDate) ")
    Page<RoleEntity> findAllRoleWithFilterByRoleIdAndUpdatedBy(
            @Param("searchText") String searchText,
            @Param("id") UUID id,
            @Param("updatedBy") UUID updatedBy,
            @Param("startDate") Long startDate,
            @Param("endDate") Long endDate,
            Pageable pageable);
}
