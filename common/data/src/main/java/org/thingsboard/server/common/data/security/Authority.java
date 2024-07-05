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
package org.thingsboard.server.common.data.security;

import java.util.Arrays;
import java.util.List;

public enum Authority {

    SYS_ADMIN(0),
    TENANT_ADMIN(1),
    CUSTOMER_USER(2),
    REFRESH_TOKEN(10),
    PRE_VERIFICATION_TOKEN(11),

    ;

    private int code;
    private Authority parent;
    private boolean canView;
    private boolean canEdit;

    Authority(int code) {
        this.code = code;
    }
    Authority(int code, Authority parent, boolean canView, boolean canEdit) {
        this(code);
        this.parent = parent;
        this.canView = canView;
        this.canEdit = canEdit;
    }

    public int getCode() {
        return code;
    }

    public static Authority parse(String value) {
        Authority authority = null;
        if (value != null && value.length() != 0) {
            for (Authority current : Authority.values()) {
                if (current.name().equalsIgnoreCase(value)) {
                    authority = current;
                    break;
                }
            }
        }
        return authority;
    }
    public static List<Authority> getAuthorityByParam(Authority authority, boolean canView, boolean canEdit)
    {
        for (Authority current : Authority.values()) {
            if (current.parent.equals(authority) && current.canView == canView && current.canEdit == canEdit) {
                return Arrays.asList(current);
            }
        }
        return null;

    }
}
