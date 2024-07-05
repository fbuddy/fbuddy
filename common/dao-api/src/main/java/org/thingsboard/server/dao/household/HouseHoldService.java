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

import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldFilterRequest;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.id.RoleId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.role.Module;
import org.thingsboard.server.common.data.role.*;
import org.thingsboard.server.dao.entity.EntityDaoService;

import java.util.List;
import java.util.Set;

public interface HouseHoldService{

    List<HouseHold> findAll();

    PageData<HouseHold> findAll(PageLink pageLink);


    HouseHold findByHouseHoldIdAndTvId(Long houseHoldId, Long tvId);
    HouseHold save(HouseHold houseHold, User user) throws ThingsboardException;
    HouseHoldLog saveChangeLog(HouseHoldLog houseHoldUpdateRequest, HouseHold houseHold, HouseHoldActionType houseHoldActionType, User user) throws ThingsboardException;
    PageData<HouseHold> getHouseHoldByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink) throws ThingsboardException;
    PageData<HouseHoldLog> getHouseHoldLogsByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink);

    HouseHold updateFieldStatus(HouseHoldLog houseHold, User user) throws ThingsboardException;

    HouseHold updateProfile(HouseHoldLog houseHoldUpdateRequest, User user) throws ThingsboardException;
}
