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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.role.ModuleDisplayResponse;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;


@Service("ModuleDaoService")
@Slf4j
@RequiredArgsConstructor
public class ModuleServiceImpl  extends AbstractEntityService implements ModuleService {



    private final ModuleDao moduleDao;

    private static Map<ModuleId, Module> moduleMap = new HashMap<>();

    //private final ApplicationEventPublisher eventPublisher;
    //private final EntityCountService countService;

    @Override
    public List<Module> findAll() {
        if(moduleMap.isEmpty())
            moduleMap = moduleDao.findAll().stream().collect(Collectors.toMap(Module::getId, m -> m));

        return moduleMap.values().stream().filter(m -> Authority.SYS_ADMIN.equals(m.getAuthority())).collect(Collectors.toList());
    }

    @Override
    public List<Module> findAllExceptSysAdmin() {
        if(moduleMap.isEmpty())
            moduleMap = moduleDao.findAll().stream().collect(Collectors.toMap(Module::getId, m -> m));

        return moduleMap.values().stream().collect(Collectors.toList());
    }

    public void clearModuleMap()
    {
        this.moduleMap = new HashMap<>();
    }

    @Override
    public List<Module> findBySysAdminAutority() {
        return moduleDao.findAll()
                .stream()
                .filter(module -> Authority.SYS_ADMIN.name().equals(module.getAuthority()))
                .collect(Collectors.toList());
    }


    @Override
    public List<ModuleDisplayResponse> prepareModuleListingResponse() {
        List<Module> modules =  findAllExceptSysAdmin();
        List<ModuleDisplayResponse> response = new ArrayList<>();
        Collections.sort(modules, comparing(Module::getOrderNumber));

        for(Module module:modules)
        {
            if(module.getParent() == null) {
                ModuleDisplayResponse moduleDisplayResponse = new ModuleDisplayResponse();
                moduleDisplayResponse.setName(module.getName());
                moduleDisplayResponse.setModuleId(module.getId());
                moduleDisplayResponse.setOrderNumber(module.getOrderNumber());
                response.add(moduleDisplayResponse);
            }
        }
        for(Module module:modules)
        {
            response = response.stream()
                    .map(m -> {
                        if(m.getModuleId().equals(module.getParent()))
                        {
                            ModuleDisplayResponse.SubModuleDisplayResponse subModuleDisplayResponse = new ModuleDisplayResponse.SubModuleDisplayResponse();
                            subModuleDisplayResponse.setName(module.getName());
                            subModuleDisplayResponse.setModuleId(module.getId());
                            subModuleDisplayResponse.setOrderNumber(module.getOrderNumber());
                            m.getModules().add(subModuleDisplayResponse);
                        }
                        return m;
                    })
                    .collect(Collectors.toList());
        }
        for(Module module:modules)
        {
            response = response.stream()
                    .map(m -> {
                        for(ModuleDisplayResponse.SubModuleDisplayResponse sm:m.getModules()) {
                            if (sm.getModuleId().equals(module.getParent())) {
                                sm.getSubModules().add(module);
                            }
                        }

                        return m;
                    })
                    .collect(Collectors.toList());
        }
        return response.stream().filter(m ->!m.getModules().isEmpty()).collect(Collectors.toList());
    }

    @Override
    public Module getModuleById(ModuleId moduleId){
        if(moduleMap.isEmpty())
            findAll();

        return moduleMap.get(moduleId);
    }

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(getModuleById(new ModuleId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return ModuleService.super.countByTenantId(tenantId);
    }

    @Override
    public void deleteEntity(TenantId tenantId, EntityId id) {
        ModuleService.super.deleteEntity(tenantId, id);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.MODULE;
    }


}
