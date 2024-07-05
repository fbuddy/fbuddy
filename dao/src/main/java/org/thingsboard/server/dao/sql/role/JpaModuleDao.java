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
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.ModuleEntity;
import org.thingsboard.server.dao.role.ModuleDao;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.List;
import java.util.UUID;

/**
 * Created by Prashant Gupta on 3/6/2024.
 */
@Component
@SqlDao
public class JpaModuleDao extends JpaAbstractDao<ModuleEntity, Module> implements ModuleDao {

    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    protected Class<ModuleEntity> getEntityClass() {
        return ModuleEntity.class;
    }

    @Override
    protected JpaRepository<ModuleEntity, UUID> getRepository() {
        return moduleRepository;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.MODULE;
    }

    @Override
    public List<Module> findAll() {
        List<ModuleEntity> entities = Lists.newArrayList(getRepository().findAll());
        return DaoUtil.convertDataList(entities);
    }
}
