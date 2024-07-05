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

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.common.data.id.HouseHoldLogId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.util.mapping.JsonBinaryType;

import javax.persistence.*;
import java.util.UUID;

import static org.thingsboard.server.dao.model.ModelConstants.*;

@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity
@Table(name = HOUSE_HOLD_LOG_TABLE_NAME)
public class HouseHoldLogEntity extends BaseSqlEntity<HouseHoldLog> {

    @Column(name = HOUSE_HOLD_LOG_HOUSE_HOLD_ENTITY_ID_PROPERTY)
    private UUID houseHoldEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = AUDIT_LOG_ACTION_TYPE_PROPERTY)
    private HouseHoldActionType actionType; // FIELD_STATUS_UPDATE, PROFILE_UPDATE

    @Type(type = "jsonb")
    @Column(name = AUDIT_LOG_ACTION_DATA_PROPERTY, columnDefinition = "jsonb")
    private JsonNode actionData;

    @Column(name = VALID_FROM_TIME_PROPERTY)
    private Long validFrom;

    @Column(name = VALID_TO_TIME_PROPERTY)
    private Long validTill;

    @Column(name = HOUSE_HOLD_LOG_PREVIOUS_ID)
    private UUID previousId;

    @Column(name = AUDIT_LOG_USER_ID_PROPERTY)
    private UUID userId;

    @Column(name = AUDIT_LOG_USER_NAME_PROPERTY)
    private String userName;

    @Transient
    private HouseHold houseHold;
    @Transient
    private JsonNode actionDataNew;

    public HouseHoldLogEntity() {
        super();
    }

    public HouseHoldLogEntity(HouseHoldLogEntity houseHoldLog, HouseHoldEntity houseHoldEntity, HouseHoldLogEntity houseHoldLogNew) {
        if (houseHoldLog.getId() != null) {
            this.setUuid(houseHoldLog.getId());
        }
        this.setCreatedTime(houseHoldLog.getCreatedTime());
        this.setHouseHoldEntityId(houseHoldLog.getHouseHoldEntityId());
        this.setActionType(houseHoldLog.getActionType());
        this.setActionData(houseHoldLog.getActionData());
        this.setValidFrom(houseHoldLog.getValidFrom());
        this.setValidTill(houseHoldLog.getValidTill());
        this.setPreviousId(houseHoldLog.getPreviousId());
        this.setUserId(houseHoldLog.getUserId());
        this.setUserName(houseHoldLog.getUserName());

        this.setHouseHold(DaoUtil.getData(houseHoldEntity));
        if(houseHoldLogNew != null)
            this.setActionDataNew(houseHoldLogNew.getActionData());
    }
    public HouseHoldLogEntity(HouseHoldLog houseHoldLog) {
        if (houseHoldLog.getId() != null) {
            this.setUuid(houseHoldLog.getId().getId());
        }
        this.setCreatedTime(houseHoldLog.getCreatedTime());
        this.setHouseHoldEntityId(houseHoldLog.getHouseHoldEntityId().getId());
        this.setActionType(houseHoldLog.getActionType());
        this.setActionData(houseHoldLog.getActionData());
        this.setValidFrom(houseHoldLog.getValidFrom());
        this.setValidTill(houseHoldLog.getValidTill());
        this.setPreviousId(houseHoldLog.getPreviousId().getId());
        if(houseHoldLog.getUpdatedBy()!=null)
            this.setUserId(houseHoldLog.getUpdatedBy().getId());
        this.setUserName(houseHoldLog.getUserName());
    }

    @Override
    public HouseHoldLog toData() {
        HouseHoldLog houseHoldLog = new HouseHoldLog();
        houseHoldLog.setId(new HouseHoldLogId(this.id));
        houseHoldLog.setHouseHoldEntityId(new HouseHoldEntityId(this.getHouseHoldEntityId()));
        houseHoldLog.setActionType(this.actionType);
        houseHoldLog.setActionData(this.actionData);
        houseHoldLog.setCreatedTime(this.createdTime);
        houseHoldLog.setValidFrom(this.validFrom);
        houseHoldLog.setValidTill(this.validTill);
        houseHoldLog.setPreviousId(new HouseHoldLogId(this.previousId));
        houseHoldLog.setUpdatedBy(new UserId(this.userId));
        houseHoldLog.setUserName(this.userName);
        houseHoldLog.setHouseHold(this.houseHold);
        houseHoldLog.setActionDataNew(this.actionDataNew);
        return houseHoldLog;
    }
}
