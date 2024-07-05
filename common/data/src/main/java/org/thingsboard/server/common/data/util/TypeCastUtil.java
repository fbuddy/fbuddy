/**
 * Copyright © 2016-2024 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.util;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.thingsboard.server.common.data.kv.DataType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TypeCastUtil {

    private TypeCastUtil() {
    }

    public static Pair<DataType, Object> castValue(String value) {
        if (isNumber(value)) {
            String formattedValue = value.replace(',', '.');
            try {
                BigDecimal bd = new BigDecimal(formattedValue);
                if (bd.stripTrailingZeros().scale() > 0 || isSimpleDouble(formattedValue)) {
                    if (bd.scale() <= 16) {
                        return Pair.of(DataType.DOUBLE, bd.doubleValue());
                    }
                } else {
                    return Pair.of(DataType.LONG, bd.longValueExact());
                }
            } catch (RuntimeException ignored) {
            }
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Pair.of(DataType.BOOLEAN, Boolean.parseBoolean(value));
        }
        return Pair.of(DataType.STRING, value);
    }

    public static Pair<DataType, Number> castToNumber(String value) {
        if (isNumber(value)) {
            String formattedValue = value.replace(',', '.');
            BigDecimal bd = new BigDecimal(formattedValue);
            if (bd.stripTrailingZeros().scale() > 0 || isSimpleDouble(formattedValue)) {
                if (bd.scale() <= 16) {
                    return Pair.of(DataType.DOUBLE, bd.doubleValue());
                } else {
                    return Pair.of(DataType.DOUBLE, bd);
                }
            } else {
                return Pair.of(DataType.LONG, bd.longValueExact());
            }
        } else {
            throw new IllegalArgumentException("'" + value + "' can't be parsed as number");
        }
    }

    private static boolean isNumber(String value) {
        return NumberUtils.isNumber(value.replace(',', '.'));
    }

    private static boolean isSimpleDouble(String valueAsString) {
        return valueAsString.contains(".") && !valueAsString.contains("E") && !valueAsString.contains("e");
    }

    public static List<Long> getSerialNumberData(String serialNumber) {
        List<Long> serialNumbers = null;

        if (serialNumber != null) {
            serialNumbers = new ArrayList<>();
            if (serialNumber.contains("-")) {
                serialNumbers = Arrays.stream(serialNumber.split("-"))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                serialNumbers.add(0L);
            } else if (serialNumber.contains(","))
                serialNumbers = Arrays.stream(serialNumber.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            else
                serialNumbers.add(Long.valueOf(serialNumber));
        }
        return serialNumbers;
    }

}
