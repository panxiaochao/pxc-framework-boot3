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
package io.github.panxiaochao.core.utils.meta.ddl.impl;

import io.github.panxiaochao.core.enums.DatabaseType;
import io.github.panxiaochao.core.utils.StrUtil;
import io.github.panxiaochao.core.utils.meta.db.ColumnMeta;
import io.github.panxiaochao.core.utils.meta.ddl.AbstractDatabase;
import io.github.panxiaochao.core.utils.meta.ddl.IDatabase;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 达梦 数据库实现类
 * </p>
 *
 * @author lypxc
 * @since 2025-06-13
 * @version 1.0
 */
public class DatabaseDMImpl extends AbstractDatabase implements IDatabase {

    private static final String SHOW_CREATE_TABLE_SQL = "SELECT DBMS_METADATA.GET_DDL('TABLE','%s','%s') FROM DUAL";

    private static final String SHOW_CREATE_VIEW_SQL = "SELECT DBMS_METADATA.GET_DDL('VIEW','%s','%s') FROM DUAL";

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.DM;
    }

    /**
     * 获取指定物理表的DDL语句
     * @param connection JDBC连接
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @return 字段元信息列表
     */
    @Override
    public String getTableDdl(Connection connection, String schemaName, String tableName) {
        String sql = String.format(SHOW_CREATE_TABLE_SQL, tableName, schemaName);
        try (Statement st = connection.createStatement()) {
            if (st.execute(sql)) {
                try (ResultSet rs = st.getResultSet()) {
                    if (rs != null && rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 获取指定视图表的DDL语句
     * @param connection JDBC连接
     * @param schemaName 模式名称
     * @param tableName 表或视图名称
     * @return 字段元信息列表
     */
    @Override
    public String getViewDdl(Connection connection, String schemaName, String tableName) {
        String sql = String.format(SHOW_CREATE_VIEW_SQL, tableName, schemaName);
        try (Statement st = connection.createStatement()) {
            if (st.execute(sql)) {
                try (ResultSet rs = st.getResultSet()) {
                    if (rs != null && rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * 生成创建表 DDL
     * @return 创建表 DDL-SQL
     */
    @Override
    public String generateCreateTableSql(String schemaName, String tableName, String tableComment,
            List<ColumnMeta> columnMetas) {
        if (null == columnMetas || columnMetas.isEmpty()) {
            return "";
        }
        List<String> ddlCommands = new ArrayList<>();
        ddlCommands.add("CREATE TABLE");
        ddlCommands.add(getQuotedSchemaTableCombination(schemaName, tableName));
        ddlCommands.add("(");
        for (int i = 0; i < columnMetas.size(); i++) {
            if (i > 0) {
                ddlCommands.add(",");
            }
            else {
                ddlCommands.add("");
            }
            ColumnMeta v = columnMetas.get(i);
            ddlCommands.add(reflectionFieldSqlFromColumnMeta(v));
        }
        // 获取主键数组
        // List<String> pks = columnMetas.stream()
        // .filter(ColumnMeta::isPrimaryKey)
        // .map(ColumnMeta::getColumnName)
        // .collect(Collectors.toList());
        // ddlCommands.add(appendPrimaryKeyForCreateTableSql(pks));
        ddlCommands.add(");");
        // 添加注释
        ddlCommands.addAll(getTableColumnCommentDefinition(schemaName, tableName, tableComment, columnMetas));
        return String.join(" ", ddlCommands);
    }

    public String reflectionFieldSqlFromColumnMeta(ColumnMeta column) {
        int type = column.getJdbcType();
        List<String> columnDdl = new ArrayList<>();
        columnDdl.add("\"" + column.getColumnName() + "\"");
        // jdbcType 转换
        columnDdl.add(super.getFieldDefineSqlFormJdbcType(column, type));
        if (!column.isNullable()) {
            columnDdl.add("NOT NULL");
        }
        // 主键 && 自增
        if (column.isPrimaryKey()) {
            columnDdl.add("PRIMARY KEY");
            if (column.isAutoIncrement()) {
                columnDdl.add("AUTO_INCREMENT");
            }
        }
        // 默认值
        if (StrUtil.isNotBlank(column.getColumnDefault()) && !"null".equals(column.getColumnDefault())
                && !"NULL".equals(column.getColumnDefault())) {
            if (type != Types.TIMESTAMP && type != Types.TIME && type != Types.DATE) {
                if (column.getColumnDefault().startsWith("'")) {
                    columnDdl.add("DEFAULT " + column.getColumnDefault());
                }
                else {
                    columnDdl.add("DEFAULT '" + column.getColumnDefault() + "'");
                }
            }
            else {
                columnDdl.add("DEFAULT CURRENT_TIMESTAMP");
            }
        }
        return String.join(" ", columnDdl);
    }

    public String appendPrimaryKeyForCreateTableSql(List<String> pks) {
        // 检查输入列表是否为 null 或为空
        if (pks == null || pks.isEmpty()) {
            return "";
        }
        String joinedPkColumns = "`" + StringUtils.join(pks, "` , `") + "`";
        StringBuilder sqlBuilder = new StringBuilder();
        // 构建主键列定义部分
        sqlBuilder.append(", PRIMARY KEY (").append(joinedPkColumns).append(")");
        // 多主键时使用 BTREE 索引
        if (pks.size() > 1) {
            sqlBuilder.append(" USING BTREE");
        }
        return sqlBuilder.toString();
    }

    /**
     * 获取数据库的表全名
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @return 表全名
     */
    @Override
    public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
        return String.format("%s.\"%s\"", schemaName, tableName);
    }

    @Override
    public List<String> getTableColumnCommentDefinition(String schemaName, String tableName, String tableComment,
            List<ColumnMeta> columnMetas) {
        List<String> results = new ArrayList<>();
        if (StrUtil.isNotBlank(tableComment)) {
            results.add(String.format("COMMENT ON TABLE \"%s\".\"%s\" IS '%s';", schemaName, tableName,
                    tableComment.replace("\"", "\\\"")));
        }
        for (ColumnMeta column : columnMetas) {
            if (StrUtil.isNotBlank(column.getColumnComment())) {
                results.add(String.format("COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s';", schemaName, tableName,
                        column.getColumnName(), column.getColumnComment().replace("\"", "\\\"")));
            }
        }
        return results;
    }

}
