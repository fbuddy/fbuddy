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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.household.HouseHoldMember;
import org.thingsboard.server.common.data.household.HouseHoldStatus;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.common.data.id.HouseHoldLogId;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = ModelConstants.HOUSE_HOLD_TABLE_NAME)
public class HouseHoldEntity extends BaseSqlEntity<HouseHold>{

    @Column(name = HOUSE_HOLD_HOUSE_HOLD_ID_PROPERTY)
    private Long houseHoldId;
    @Column(name = HOUSE_HOLD_TV_ID_PROPERTY)
    private Long tvId;
    @Column(name = HOUSE_HOLD_W_ID_PROPERTY)
    private Long wId;

    @Type(type = "jsonb")
    @Column(name = HOUSE_HOLD_MEMBER_INFO_PROPERTY, columnDefinition = "jsonb")
    private JsonNode memberInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = HOUSE_HOLD_FIELD_STATUS_PROPERTY)
    private HouseHoldStatus fieldStatus;

    @Column(name = HOUSE_HOLD_PROFILE_ID_PROPERTY)
    private UUID profileId;

    @Column(name = "active_field_status_log_id")
    private UUID activeFieldStatusLogId;

    @Column(name = "active_profile_log_id")
    private UUID activeProfileLogId;

    @Transient
    private HouseHoldLog houseHoldLog;

    public HouseHoldEntity() {
        super();
    }

    public HouseHoldEntity(HouseHold houseHold) {
        if (houseHold.getId() != null) {
            this.setUuid(houseHold.getId().getId());
        }
        this.setCreatedTime(houseHold.getCreatedTime());
        this.houseHoldId = houseHold.getHouseHoldId();
        this.wId = houseHold.getWId();
        this.tvId = houseHold.getTvId();
        this.memberInfo = JacksonUtil.toJsonNode(JacksonUtil.valueToTree(houseHold.getMemberInfo()).toString());
        this.fieldStatus = houseHold.getFieldStatus();
        this.profileId = houseHold.getProfileId();
        if(houseHold.getActiveFieldStatusLogId() != null)
            this.activeFieldStatusLogId = houseHold.getActiveFieldStatusLogId().getId();
        if(houseHold.getActiveProfileLogId() != null)
            this.activeProfileLogId = houseHold.getActiveProfileLogId().getId();
    }
    public HouseHoldEntity(HouseHoldEntity houseHold, HouseHoldLogEntity houseHoldLogEntity) {
        if (houseHold.getId() != null) {
            this.setUuid(houseHold.getId());
        }
        this.setCreatedTime(houseHold.getCreatedTime());
        this.houseHoldId = houseHold.getHouseHoldId();
        this.wId = houseHold.getWId();
        this.tvId = houseHold.getTvId();
        this.memberInfo = JacksonUtil.toJsonNode(JacksonUtil.valueToTree(houseHold.getMemberInfo()).toString());
        this.fieldStatus = houseHold.getFieldStatus();
        this.profileId = houseHold.getProfileId();
        this.activeFieldStatusLogId = houseHold.getActiveFieldStatusLogId();
        this.activeProfileLogId = houseHold.getActiveProfileLogId();

        this.houseHoldLog = DaoUtil.getData(houseHoldLogEntity);
    }

    @Override
    public HouseHold toData() {
        HouseHold houseHold = new HouseHold();
        houseHold.setId(new HouseHoldEntityId(this.id));
        houseHold.setHouseHoldId(this.houseHoldId);
        houseHold.setWId(this.wId);
        houseHold.setTvId(this.tvId);
        houseHold.setMemberInfo(JacksonUtil.convertValue(this.memberInfo, new TypeReference<List<HouseHoldMember>>() {
        }));
        houseHold.setFieldStatus(this.fieldStatus);
        houseHold.setProfileId(this.profileId);
        houseHold.setCreatedTime(this.createdTime);
        houseHold.setHouseHoldLog(this.houseHoldLog);
        houseHold.setActiveProfileLogId(new HouseHoldLogId(this.activeProfileLogId));
        houseHold.setActiveFieldStatusLogId(new HouseHoldLogId(this.activeFieldStatusLogId));
        return houseHold;
    }
}
