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

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.Role;
import org.thingsboard.server.common.data.role.RoleFilter;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.RoleEntity;
import org.thingsboard.server.dao.role.RoleDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.*;

/**
 * Created by Prashant Gupta on 3/6/2024.
 */
@Component
@SqlDao
public class JpaRoleDao extends JpaAbstractDao<RoleEntity, Role> implements RoleDao {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected Class<RoleEntity> getEntityClass() {
        return RoleEntity.class;
    }

    @Override
    protected JpaRepository<RoleEntity, UUID> getRepository() {
        return roleRepository;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ROLE;
    }

    @Override
    public List<Role> findAll() {

        List<RoleEntity> entities = Lists.newArrayList(roleRepository.findAllWithRoleName());
        return DaoUtil.convertDataList(entities);
    }
    @Override
    public PageData<Role> findAll(PageLink pageLink) {

        return DaoUtil.toPageData(roleRepository.findAllRoleWithFilter(pageLink.getTextSearch(),
                null,
                null,
                DaoUtil.toPageable(pageLink)
        ));
    }
    @Override
    public PageData<Role> findAllRoleByFilter(PageLink pageLink, RoleFilter roleFilter) {
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("updatedByUserName","u.userName");
        columnMap.put("active","isActive");
        if(roleFilter.getId() == null && roleFilter.getUpdatedBy() == null) {
            return DaoUtil.toPageData(roleRepository.findAllRoleWithFilter(
                    pageLink.getTextSearch(),
                    roleFilter.getStartDate(),
                    roleFilter.getEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)
            ));

        } else if(roleFilter.getId() != null && roleFilter.getUpdatedBy() == null){
            return DaoUtil.toPageData(roleRepository.findAllRoleWithFilterByRoleId(
                    pageLink.getTextSearch(),
                    roleFilter.getId(),
                    roleFilter.getStartDate(),
                    roleFilter.getEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)
            ));
        } else if(roleFilter.getId() == null && roleFilter.getUpdatedBy() != null){
            return DaoUtil.toPageData(roleRepository.findAllRoleWithFilterUpdatedBy(
                    pageLink.getTextSearch(),
                    roleFilter.getUpdatedBy(),
                    roleFilter.getStartDate(),
                    roleFilter.getEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)
            ));
        } else
        {
            return DaoUtil.toPageData(roleRepository.findAllRoleWithFilterByRoleIdAndUpdatedBy(
                    pageLink.getTextSearch(),
                    roleFilter.getId(),
                    roleFilter.getUpdatedBy(),
                    roleFilter.getStartDate(),
                    roleFilter.getEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)
            ));
        }


        //return DaoUtil.toPageData(roleRepository.findAll(DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Role findFirstByName(String name) {
        return roleRepository.findFirstByName(name);
    }
}
