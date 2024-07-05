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
package org.thingsboard.server.common.data.household;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.common.data.id.HouseHoldLogId;
import org.thingsboard.server.common.data.id.UserId;


@ApiModel
@Data
public class HouseHoldLog extends BaseData<HouseHoldLogId> {

    private HouseHoldEntityId houseHoldEntityId;
    private HouseHoldActionType actionType;
    @ApiModelProperty(position = 10, value = "JsonNode represented action data", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private JsonNode actionData;


    private Long validFrom;
    private Long validTill;
    private Long currentValidTill;
    @JsonIgnore
    private HouseHoldLogId previousId;
    @JsonIgnore
    private UserId updatedBy;
    private String userName;
    @JsonIgnore
    private HouseHold houseHold;
    @JsonIgnore
    private JsonNode actionDataNew;
    @JsonIgnore
    private DeviceProfile deviceProfile;
    @JsonIgnore
    private DeviceProfile currentDeviceProfile;
    @JsonIgnore
    private DeviceProfile newDeviceProfile;


    private HouseHoldStatus fieldStatus;
    private HouseHoldStatus newFieldStatus;

    public Long getHouseHoldId(){
        return houseHold.getHouseHoldId();
    }
    public Long getWId(){
        return houseHold.getWId();
    }
    public Long getTvId(){
        return houseHold.getTvId();
    }


    public String getCurrentFieldStatusName(){
        return houseHold.getFieldStatus().getName();
    }
    public String getNewFieldStatusName(){
        if(getActionDataNew() == null || this.getNewFieldStatus() == null)
            return "-";
        return this.getNewFieldStatus().getName();
    }
    public String getFieldStatusName(){
        if(getFieldStatus() == null)
            return "-";
        return getFieldStatus().getName();
    }


    public String getProfileName() {
        if(deviceProfile == null) return null;
        return deviceProfile.getName();
    }
    public String getCurrentProfileName() {
        if(currentDeviceProfile == null) return "-";
        return currentDeviceProfile.getName();
    }
    public String getNewProfileName() {
        if(actionDataNew == null || newDeviceProfile == null)
            return "-";
        return newDeviceProfile.getName();
    }
}
