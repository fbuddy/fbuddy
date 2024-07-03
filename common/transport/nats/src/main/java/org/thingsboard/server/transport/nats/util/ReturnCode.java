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
package org.thingsboard.server.transport.nats.util;

public enum ReturnCode {
    SUCCESS((byte) 0x00),
    SERVER_UNAVAILABLE((byte) 0x03),
    BAD_USER_NAME_OR_PASSWORD((byte) 0x04),
    NOT_AUTHORIZED((byte) 0x05);
    private static final ReturnCode[] VALUES;

    static {
        ReturnCode[] values = values();
        VALUES = new ReturnCode[163];
        for (ReturnCode code : values) {
            final int unsignedByte = code.byteValue & 0xFF;
            // Suppress a warning about out of bounds access since the enum contains only correct values
            VALUES[unsignedByte] = code;    // lgtm [java/index-out-of-bounds]
        }
    }

    private final byte byteValue;

    ReturnCode(byte byteValue) {
        this.byteValue = byteValue;
    }

    public byte byteValue() {
        return byteValue;
    }

    public short shortValue(){return byteValue;}

    public static ReturnCode valueOf(byte b) {
        final int unsignedByte = b & 0xFF;
        ReturnCode mqttConnectReturnCode = null;
        try {
            mqttConnectReturnCode = VALUES[unsignedByte];
        } catch (ArrayIndexOutOfBoundsException ignored) {
            // no op
        }
        if (mqttConnectReturnCode == null) {
            throw new IllegalArgumentException("unknown connect return code: " + unsignedByte);
        }
        return mqttConnectReturnCode;
    }
}