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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.UUID;

@Data
@ApiModel
@Slf4j
public class RoleFilter {

    private static final long serialVersionUID = 3021989561267192281L;

    public static final ObjectMapper mapper = new ObjectMapper();

    @NoXss
    @Length(fieldName = "id")
    @ApiModelProperty(position = 1, value = "Id of the Role")
    private UUID id;

    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 2, value = "Name of the Role", example = "Role 1")
    private String name;

    @NoXss
    @Length(fieldName = "updatedBy")
    @ApiModelProperty(position = 3, value = "Id of user who is updating the Role")
    private UUID updatedBy;

    @NoXss
    @Length(fieldName = "startDate")
    @ApiModelProperty(position = 4, value = "Start Date to apply filter on created date")
    protected Long startDate;

    @NoXss
    @Length(fieldName = "endDate")
    @ApiModelProperty(position = 5, value = "End Date to apply filter on created date")
    protected Long endDate;







}
