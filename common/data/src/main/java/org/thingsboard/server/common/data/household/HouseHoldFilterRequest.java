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
package org.thingsboard.server.common.data.household;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.HasSerialNumberRange;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.UUID;

@Data
@ApiModel
@Slf4j
public class HouseHoldFilterRequest implements HasSerialNumberRange {

    @NoXss
    @Length(fieldName = "houseHoldId")
    @ApiModelProperty(position = 1, value = "Household Id of the Household")
    private Long houseHoldId;

    @NoXss
    @Length(fieldName = "fieldStatus")
    @ApiModelProperty(position = 2)
    private HouseHoldStatus fieldStatus;



    @NoXss
    @Length(fieldName = "listId")
    @ApiModelProperty(position = 5)
    private UUID listId;

    @NoXss
    @Length(fieldName = "updatedStartDate")
    @ApiModelProperty(position = 6, value = "Start Date to apply filter on update date")
    protected Long updatedStartDate;

    @NoXss
    @Length(fieldName = "updatedEndDate")
    @ApiModelProperty(position = 7, value = "End Date to apply filter on update date")
    protected Long updatedEndDate;

    @NoXss
    @Length(fieldName = "audiSessionStartDate")
    @ApiModelProperty(position = 8, value = "Start Date to apply filter on audience session date")
    protected Long audiSessionStartDate;

    @NoXss
    @Length(fieldName = "audiSessionEndDate")
    @ApiModelProperty(position = 9, value = "End Date to apply filter on audience session date")
    protected Long audiSessionEndDate;

    @NoXss
    @Length(fieldName = "showLatestDate")
    @ApiModelProperty(position = 10)
    protected Boolean showLatestData = false;



    @NoXss
    @Length(fieldName = "profileId")
    @ApiModelProperty(position = 11)
    private UUID profileId;

    @Length(fieldName = "serialNumberRangeHouseHold")
    String serialNumberHouseHoldRange = null;




}
