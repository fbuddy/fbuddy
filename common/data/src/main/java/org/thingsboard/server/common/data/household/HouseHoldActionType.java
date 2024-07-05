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

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;


@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum HouseHoldActionType {

    MEMBER_INFO_CHANGE("MEMBER_INFO_CHANGE"),
    PROFILE_CHANGE("PROFILE_CHANGE"), // log entity
    STATUS_CHANGE("STATUS_CHANGE");


    private String id;
    private String name;

    HouseHoldActionType(String displayText)
    {
        this.name = displayText;
        this.id = this.name();
    }

}
