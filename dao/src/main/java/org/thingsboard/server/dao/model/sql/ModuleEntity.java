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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_ROLE_MODULE_TABLE_NAME)
public final class ModuleEntity extends BaseSqlEntity<Module> {



    @Column(name = ModelConstants.NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.IS_ACTIVE_PROPERTY)
    private Boolean isActive;

    @Column(name = ModelConstants.USER_AUTHORITY_PROPERTY)
    private String authority;

    @Column(name = "parent")
    private UUID parent;

    @Column(name = "order_number")
    private Long orderNumber = 0L;




    public ModuleEntity() {
        super();
    }

    public ModuleEntity(Module module) {
        if (module.getId() != null) {
            this.setUuid(module.getId().getId());
        }
        this.setCreatedTime(module.getCreatedTime());
        this.name = module.getName();
        this.authority = module.getAuthority();
        this.parent = module.getParent().getId();
        this.isActive = module.isActive();
        this.orderNumber = module.getOrderNumber();
    }

    @Override
    public Module toData() {
        Module module = new Module(new ModuleId(this.getUuid()));
        module.setName(this.name);
        module.setActive(this.isActive);
        module.setAuthority(this.authority);
        module.setOrderNumber(this.orderNumber);
        if(this.parent != null)
            module.setParent(new ModuleId(this.parent));
        return module;
    }

}
