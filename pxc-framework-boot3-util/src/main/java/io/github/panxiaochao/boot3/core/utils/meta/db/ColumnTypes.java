/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
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
package io.github.panxiaochao.boot3.core.utils.meta.db;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 数据库字段类型
 * </p>
 *
 * @author lypxc
 * @since 2025-06-16
 * @version 1.0
 */
public class ColumnTypes {

    /**
     * Value type indicating that the value has no type set
     */
    public static final int TYPE_NONE = 0;

    /**
     * Value type indicating that the value contains a floating point double precision
     * number.
     */
    public static final int TYPE_NUMBER = 1;

    /**
     * Value type indicating that the value contains a text String.
     */
    public static final int TYPE_STRING = 2;

    /**
     * Value type indicating that the value contains a Date.
     */
    public static final int TYPE_DATE = 3;

    /**
     * Value type indicating that the value contains a boolean.
     */
    public static final int TYPE_BOOLEAN = 4;

    /**
     * Value type indicating that the value contains a long integer.
     */
    public static final int TYPE_INTEGER = 5;

    /**
     * Value type indicating that the value contains a floating point precision number
     * with arbitrary precision.
     */
    public static final int TYPE_BIGNUMBER = 6;

    /**
     * Value type indicating that the value contains an Object.
     */
    public static final int TYPE_SERIALIZABLE = 7;

    /**
     * Value type indicating that the value contains binary data: BLOB, CLOB, ...
     */
    public static final int TYPE_BINARY = 8;

    /**
     * Value type indicating that the value contains a date-time with nanosecond precision
     */
    public static final int TYPE_TIMESTAMP = 9;

    /**
     * Value type indicating that the value contains a time
     */
    public static final int TYPE_TIME = 10;

    /**
     * Value type indicating that the value contains a Internet address
     */
    public static final int TYPE_INET = 11;

    private static final Map<Integer, Integer> JDBC_TYPE_MAP = new HashMap<>();

    static {
        // 字符串类型映射
        int[] stringTypes = { java.sql.Types.CHAR, java.sql.Types.NCHAR, java.sql.Types.VARCHAR,
                java.sql.Types.NVARCHAR, java.sql.Types.LONGVARCHAR, java.sql.Types.LONGNVARCHAR, java.sql.Types.CLOB,
                java.sql.Types.NCLOB, java.sql.Types.SQLXML, java.sql.Types.ROWID };
        for (int type : stringTypes) {
            JDBC_TYPE_MAP.put(type, TYPE_STRING);
        }

        // 整数类型映射
        int[] integerTypes = { java.sql.Types.INTEGER, java.sql.Types.TINYINT, java.sql.Types.SMALLINT };
        for (int type : integerTypes) {
            JDBC_TYPE_MAP.put(type, TYPE_INTEGER);
        }

        // 数字类型映射
        int[] numberTypes = { java.sql.Types.DECIMAL, java.sql.Types.DOUBLE, java.sql.Types.FLOAT, java.sql.Types.REAL,
                java.sql.Types.NUMERIC };
        for (int type : numberTypes) {
            JDBC_TYPE_MAP.put(type, TYPE_NUMBER);
        }

        // 时间类型映射
        JDBC_TYPE_MAP.put(java.sql.Types.TIMESTAMP, TYPE_TIMESTAMP);
        JDBC_TYPE_MAP.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE, TYPE_TIMESTAMP);
        JDBC_TYPE_MAP.put(java.sql.Types.DATE, TYPE_DATE);

        // 时间类型映射
        JDBC_TYPE_MAP.put(java.sql.Types.TIME, TYPE_TIME);
        JDBC_TYPE_MAP.put(java.sql.Types.TIME_WITH_TIMEZONE, TYPE_TIME);

        // 布尔类型映射
        JDBC_TYPE_MAP.put(java.sql.Types.BOOLEAN, TYPE_BOOLEAN);
        JDBC_TYPE_MAP.put(java.sql.Types.BIT, TYPE_BOOLEAN);

        // 二进制类型映射
        int[] binaryTypes = { java.sql.Types.BINARY, java.sql.Types.BLOB, java.sql.Types.VARBINARY,
                java.sql.Types.LONGVARBINARY };
        for (int type : binaryTypes) {
            JDBC_TYPE_MAP.put(type, TYPE_BINARY);
        }

        // BIGINT 类型映射
        JDBC_TYPE_MAP.put(java.sql.Types.BIGINT, TYPE_BIGNUMBER);
    }

    /**
     * 根据 JDBC 类型转换为内部类型
     * @param jdbcType JDBC 类型 {@link java.sql.Types}
     * @return 对应的内部类型常量
     */
    public static int transformJdbcType(int jdbcType) {
        return JDBC_TYPE_MAP.getOrDefault(jdbcType, TYPE_STRING);
    }

}