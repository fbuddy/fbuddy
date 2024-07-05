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
package org.thingsboard.server.dao.role;

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.role.ModuleDisplayResponse;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;

public interface ModuleService extends EntityDaoService {

    List<Module> findAll();

    List<Module> findAllExceptSysAdmin();
    List<ModuleDisplayResponse> prepareModuleListingResponse();

    EntityType getEntityType();

    Module getModuleById(ModuleId moduleId);

    void clearModuleMap();

    List<Module> findBySysAdminAutority();
}
