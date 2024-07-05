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
import kotlin.jvm.Transient;
import lombok.Data;
import org.thingsboard.server.common.data.BaseData;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.HasName;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.validation.NoXss;


import java.util.List;
import java.util.UUID;


@ApiModel
@Data
public class HouseHold extends BaseData<HouseHoldEntityId> implements HasName {

    private Long houseHoldId;
    private Long wId;
    private Long tvId;

    @ApiModelProperty(value = "JSON array with member info.")
    @NoXss
    private List<HouseHoldMember> memberInfo;

    private HouseHoldStatus fieldStatus = HouseHoldStatus.ACCEPTED;

    private UUID profileId = EntityId.NULL_UUID;

    private HouseHoldLogId activeFieldStatusLogId;

    private HouseHoldLogId activeProfileLogId;

    @JsonIgnore
    private DeviceProfile deviceProfile;
    @JsonIgnore
    private HouseHoldLog houseHoldLog;



    public HouseHold() {
        super();
    }

    public HouseHold(HouseHoldEntityId id) {
        super(id);
    }
    public HouseHold(HouseHold houseHold){
        super(houseHold);
        this.setHouseHoldId(houseHold.getHouseHoldId());
        this.setWId(houseHold.getWId());
        this.setTvId(houseHold.getTvId());
        this.setMemberInfo(houseHold.getMemberInfo());
        this.setFieldStatus(houseHold.getFieldStatus());
        this.setProfileId(houseHold.getProfileId());
    }

    @Override
    public String getName() {
        return this.houseHoldId.toString();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Household creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    public Long getUpdatedTime(){
        if(houseHoldLog != null)
            return houseHoldLog.getCreatedTime();
        else
            return null;
    }
    public String getUpdatedBy(){
        if(houseHoldLog != null)
            return houseHoldLog.getUserName();
        else
            return null;
    }
    public Long getValidFrom(){
        if(houseHoldLog != null)
            return houseHoldLog.getValidFrom();
        else
            return null;
    }
    public Long getValidTill(){
        if(houseHoldLog != null)
            return houseHoldLog.getValidTill();
        else
            return null;
    }
    public String getProfileName(){
        if(deviceProfile != null)
            return deviceProfile.getName();
        else
            return null;
    }
    public String getFieldStatusName(){
        return fieldStatus.getName();
    }

    @JsonIgnore
    public String getMemberInfoString(){
        String s = "";
        for(HouseHoldMember houseHoldMember : this.getMemberInfo())
        {
            s += houseHoldMember.getId()+" : "+houseHoldMember.getName()+", ";
        }
        return s;
    }
}
