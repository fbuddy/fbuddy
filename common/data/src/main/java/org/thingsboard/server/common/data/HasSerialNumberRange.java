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
package org.thingsboard.server.common.data;

import java.util.ArrayList;
import java.util.List;

public interface HasSerialNumberRange {





    default List<Long> getNumberList(String rangeOrListStr){
        List<Long> serialNumberList = new ArrayList<>();
        if(rangeOrListStr != null && !rangeOrListStr.contains("-"))
        {
            for(String s: rangeOrListStr.split(","))
            {
                serialNumberList.add(Long.parseLong(s.trim()));
            }
        }
        else{
            Long start = getStartNumber(rangeOrListStr);
            Long end = getEndNumber(rangeOrListStr);
            if(start != null && end != null) {
                for (long l = start; l <= end; l++)
                {
                    serialNumberList.add(l);
                }
            }
            return serialNumberList;
        }
        return serialNumberList;
    }
    default Long getStartNumber(String rangeOrListStr){
        if(rangeOrListStr != null && rangeOrListStr.contains("-"))
        {
            return Long.parseLong(rangeOrListStr.split("-")[0].trim());

        }
        return null;
    }
    default Long getEndNumber(String rangeOrListStr){
        if(rangeOrListStr != null && rangeOrListStr.contains("-"))
        {
            return Long.parseLong(rangeOrListStr.split("-")[1].trim());

        }
        return null;
    }

}
