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
package org.thingsboard.server.dao.household;


import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldFilterRequest;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.Dao;

import java.util.List;

public interface HouseHoldLogDao extends Dao<HouseHoldLog> {


    List<HouseHoldLog> findAll();
    PageData<HouseHoldLog> findAll(PageLink pageLink);

    HouseHoldLog findLatestLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType);
    void deleteFutureLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType);

    PageData<HouseHoldLog> findHouseHoldLogsByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink);

    HouseHoldLog findCurrentApplicableLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType);
}
