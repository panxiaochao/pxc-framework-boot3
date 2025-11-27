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

import lombok.Getter;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * JDBC 字段类型枚举，{@link java.sql.Types}
 * </p>
 *
 * @author Lypxc、Clinton Begin
 * @since 2024-05-07
 * @version 1.0
 */
@Getter
public enum JdbcTypes {

    ARRAY(Types.ARRAY),

    BIT(Types.BIT),

    TINYINT(Types.TINYINT),

    SMALLINT(Types.SMALLINT),

    INTEGER(Types.INTEGER),

    BIGINT(Types.BIGINT),

    FLOAT(Types.FLOAT),

    REAL(Types.REAL),

    DOUBLE(Types.DOUBLE),

    NUMERIC(Types.NUMERIC),

    DECIMAL(Types.DECIMAL),

    CHAR(Types.CHAR),

    VARCHAR(Types.VARCHAR),

    LONGVARCHAR(Types.LONGVARCHAR),

    DATE(Types.DATE),

    TIME(Types.TIME),

    TIMESTAMP(Types.TIMESTAMP),

    BINARY(Types.BINARY),

    VARBINARY(Types.VARBINARY),

    LONGVARBINARY(Types.LONGVARBINARY),

    NULL(Types.NULL),

    OTHER(Types.OTHER),

    BLOB(Types.BLOB),

    CLOB(Types.CLOB),

    BOOLEAN(Types.BOOLEAN),
    // Oracle
    CURSOR(-10),

    UNDEFINED(Integer.MIN_VALUE + 1000),
    // JDK6
    NVARCHAR(Types.NVARCHAR),
    // JDK6
    NCHAR(Types.NCHAR),
    // JDK6
    NCLOB(Types.NCLOB),

    STRUCT(Types.STRUCT),

    JAVA_OBJECT(Types.JAVA_OBJECT),

    DISTINCT(Types.DISTINCT),

    REF(Types.REF),

    DATALINK(Types.DATALINK),
    // JDK6
    ROWID(Types.ROWID),

    LONGNVARCHAR(Types.LONGNVARCHAR),
    // JDK6
    SQLXML(Types.SQLXML),
    // SQL Server 2008
    DATETIMEOFFSET(-155),
    // JDBC 4.2 JDK8
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    // JDBC 4.2 JDK8
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);

    public final int typeCode;

    private static final Map<Integer, JdbcTypes> CODE_LOOKUP = new HashMap<>();

    static {
        for (JdbcTypes type : JdbcTypes.values()) {
            CODE_LOOKUP.put(type.typeCode, type);
        }
    }

    JdbcTypes(int code) {
        this.typeCode = code;
    }

    /**
     * 通过{@link java.sql.Types}中对应int值找到enum值
     * @param code Jdbc type值
     * @return {@code JdbcTypes}
     */
    public static JdbcTypes ofCode(int code) {
        return CODE_LOOKUP.get(code);
    }

}
