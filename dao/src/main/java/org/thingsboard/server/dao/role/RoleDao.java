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


import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.Role;
import org.thingsboard.server.common.data.role.RoleFilter;
import org.thingsboard.server.dao.Dao;

import java.util.List;

public interface RoleDao extends Dao<Role> {


    List<Role> findAll();
    PageData<Role> findAll(PageLink pageLink);
    PageData<Role> findAllRoleByFilter(PageLink pageLink, RoleFilter roleFilter);
    Role findFirstByName(String name);
    //Optional<Module> findByModuleId(UUID moduleUuid);


}
