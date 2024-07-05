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
package org.thingsboard.server.common.data.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class Module extends BaseData<ModuleId> implements HasName {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 3, value = "Name of the Module", example = "Module 1")
    private String name;

    private Long orderNumber = 0L;

    @JsonIgnore
    private boolean isActive;

    @JsonIgnore
    private ModuleId parent;
    @ApiModelProperty(position = 5, value = "Authority of the Module", example = "true")
    private String authority;

    public Module() {
        super();
    }

    public Module(ModuleId moduleId) {
        super(moduleId);
    }

    public Module(Module module) {
        super(module);
        this.name = module.getName();
        this.isActive = module.isActive();
        this.parent = module.getParent();
        this.authority = module.getAuthority();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the module Id. " +
            "Specify this field to update the module. " +
            "Referencing non-existing module Id will cause error. " +
            "Omit this field to create new module.")
    @Override
    public ModuleId getId() {
        return super.getId();
    }

    @JsonIgnore
    @ApiModelProperty(position = 2, value = "Timestamp of the module creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @Override
    public String getName() {
        return name;
    }




}
