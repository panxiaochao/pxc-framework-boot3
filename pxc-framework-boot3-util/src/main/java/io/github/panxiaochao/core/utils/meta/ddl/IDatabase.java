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
package io.github.panxiaochao.core.utils.meta.ddl;

import io.github.panxiaochao.core.enums.DatabaseType;
import io.github.panxiaochao.core.utils.meta.db.ColumnMeta;

import java.sql.Connection;
import java.util.List;

/**
 * <p>
 * 数据库 元接口
 * </p>
 *
 * @author lypxc
 * @since 2025-06-13
 * @version 1.0
 */
public interface IDatabase {

    /**
     * 获取数据库类型
     * @return 数据库类型
     */
    DatabaseType getDatabaseType();

    /**
     * 获取指定物理表的DDL语句
     * @param connection JDBC连接
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @return 字段元信息列表
     */
    String getTableDdl(Connection connection, String schemaName, String tableName);

    /**
     * 获取指定视图表的DDL语句
     * @param connection JDBC连接
     * @param schemaName 模式名称
     * @param tableName 表或视图名称
     * @return 字段元信息列表
     */
    String getViewDdl(Connection connection, String schemaName, String tableName);

    /**
     * 生成创建表 DDL
     * @return 创建表 DDL-SQL
     */
    String generateCreateTableSql(String schemaName, String tableName, String tableComment,
            List<ColumnMeta> columnMetas);

    /**
     * 获取数据库的表全名
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @return 表全名
     */
    String getQuotedSchemaTableCombination(String schemaName, String tableName);

    /**
     * 获取表字段注释定义
     * @param schemaName 模式名称
     * @param tableName 表名称
     * @param tableComment 表注释
     * @param columnMetas 字段元信息列表
     * @return 表字段注释定义
     */
    List<String> getTableColumnCommentDefinition(String schemaName, String tableName, String tableComment,
            List<ColumnMeta> columnMetas);

}
