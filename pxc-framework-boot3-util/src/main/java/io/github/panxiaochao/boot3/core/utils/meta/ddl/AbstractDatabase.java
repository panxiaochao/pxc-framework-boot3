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
package io.github.panxiaochao.boot3.core.utils.meta.ddl;

import io.github.panxiaochao.boot3.core.utils.meta.db.ColumnMeta;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author lypxc
 * @since 2025-06-17
 * @version 1.0
 */
public abstract class AbstractDatabase implements IDatabase {

    /**
     * 获取数据库的表全名
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @return 表全名
     */
    @Override
    public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
        return String.format(" \"%s\".\"%s\" ", schemaName, tableName);
    }

    /**
     * 获取字段定义SQL (根据JDBC类型)
     * @param column 字段信息
     * @return 字段定义SQL
     */
    public String getFieldDefineSqlFormJdbcType(ColumnMeta column, int jdbcType) {
        List<String> columnDdl = new ArrayList<>();
        switch (jdbcType) {
            // 数值类型
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
                if (jdbcType == Types.TINYINT && column.getColumnLength() == 1) {
                    columnDdl.add("TINYINT(1)");
                }
                else {
                    columnDdl.add(column.getJdbcTypeName());
                }
                break;
            // 浮点类型
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
            case Types.NUMERIC:
            case Types.DECIMAL:
                if (column.getColumnLength() > 0 && column.getScale() > 0) {
                    if (column.getColumnLength() >= column.getScale()) {
                        columnDdl.add(column.getJdbcTypeName() + "(" + column.getColumnLength() + ","
                                + column.getScale() + ")");
                    }
                    else {
                        throw new RuntimeException(column.getColumnName() + " 字段长度不能小于精度");
                    }
                }
                else if (column.getColumnLength() > 0) {
                    columnDdl.add(column.getJdbcTypeName());
                }
                break;
            // 日期类型
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                columnDdl.add(column.getJdbcTypeName());
                break;
            // 字符串类型
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
                if ("ENUM".equalsIgnoreCase(column.getJdbcTypeName())
                        || "SET".equalsIgnoreCase(column.getJdbcTypeName())) {
                    columnDdl.add(column.getJdbcTypeName() + "('" + column.getColumnLength() + "')");
                }
                else if ("TINYTEXT".equalsIgnoreCase(column.getJdbcTypeName())) {
                    columnDdl.add(column.getJdbcTypeName());
                }
                else {
                    columnDdl.add(column.getJdbcTypeName() + "(" + column.getColumnLength() + ")");
                }
                break;
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.NCLOB:
            case Types.CLOB:
            case Types.BLOB:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.SQLXML:
            case Types.ROWID:
            case Types.BINARY:
                columnDdl.add(column.getJdbcTypeName());
                break;
            // 布尔类型
            case Types.BIT:
            case Types.BOOLEAN:
                if (column.getColumnLength() == 1) {
                    columnDdl.add("TINYINT(1)");
                }
                else {
                    columnDdl.add(column.getJdbcTypeName() + "(" + column.getColumnLength() + ")");
                }
                break;
            default:
                columnDdl.add(column.getJdbcTypeName() + "(" + column.getColumnLength() + ")");
        }
        return String.join(" ", columnDdl);
    }

}
