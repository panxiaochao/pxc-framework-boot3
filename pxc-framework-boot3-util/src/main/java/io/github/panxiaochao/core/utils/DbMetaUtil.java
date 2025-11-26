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
package io.github.panxiaochao.core.utils;

import io.github.panxiaochao.core.utils.meta.db.ColumnMeta;
import io.github.panxiaochao.core.utils.meta.db.IndexMeta;
import io.github.panxiaochao.core.utils.meta.db.TableMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 *
 * <p>
 * 数据库 元数据工具
 * </p>
 * <pre>
 *  注意：数据库连接中需要增加一下配置才能获取注释信息
 *  remarks = true
 *  useInformationSchema = true
 * </pre>
 *
 * @author Lypxc
 * @since 2024-05-07
 * @version 1.0
 */
public class DbMetaUtil {

    /**
     * LOGGER DbMetaUtil.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMetaUtil.class);

    /**
     * 获取所有表名
     * @param dataSource 数据源
     * @return 表名列表
     */
    public static List<String> getTables(DataSource dataSource) {
        return getTables(dataSource, TableType.TABLE);
    }

    /**
     * 获取所有表名
     * @param dataSource 数据源
     * @param types 表类型
     * @return 表名列表
     */
    public static List<String> getTables(DataSource dataSource, TableType... types) {
        if (null == types) {
            types = new TableType[] { TableType.TABLE };
        }
        return getTables(dataSource, null, null, types);
    }

    /**
     * 获取所有表名
     * @param dataSource 数据源
     * @param schema 表数据库名，对于Oracle为用户名
     * @param tableName 表名
     * @param types 表类型
     * @return 表名列表
     */
    public static List<String> getTables(DataSource dataSource, String schema, String tableName, TableType... types) {
        final List<String> tables = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            final String catalog = getCatalog(conn);
            if (!StringUtils.hasText(schema)) {
                schema = getSchema(conn);
            }
            final DatabaseMetaData metaData = conn.getMetaData();
            String[] tableTypes = Arrays.stream(types).map(TableType::getName).toArray(String[]::new);
            try (final ResultSet rs = metaData.getTables(catalog, schema, tableName, tableTypes)) {
                if (null != rs) {
                    String table;
                    while (rs.next()) {
                        table = rs.getString("TABLE_NAME");
                        if (StrUtil.isNotBlank(table)) {
                            tables.add(table);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("获取数据库表元数据失败", e);
        }
        finally {
            JdbcUtil.close(conn);
        }
        return tables;
    }

    /**
     * 获取表的元数据信息数组
     * @param dataSource 数据源
     * @param tableName 表名, 若为空查所有
     * @param catalog catalog name
     * @param schema schema name
     * @return Table数组
     */
    public static List<TableMeta> getTableMeta(DataSource dataSource, String catalog, String schema, String tableName) {
        return getTableMeta(dataSource, catalog, schema, tableName, TableType.TABLE);
    }

    /**
     * 获取表的元数据信息数组
     * @param dataSource 数据源
     * @param tableName 表名, 若为空查所有
     * @param catalog catalog name
     * @param schema schema name
     * @param types 表类型
     * @return Table数组
     */
    public static List<TableMeta> getTableMeta(DataSource dataSource, String catalog, String schema, String tableName,
            TableType... types) {
        final List<TableMeta> tableMetas = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if (null == catalog) {
                catalog = getCatalog(conn);
            }
            if (null == schema) {
                schema = getSchema(conn);
            }
            final DatabaseMetaData metaData = conn.getMetaData();
            String[] tableTypes = Arrays.stream(types).map(TableType::getName).toArray(String[]::new);
            // 获得表元数据（表注释）
            try (final ResultSet rs = metaData.getTables(catalog, schema, tableName, tableTypes)) {
                if (null != rs) {
                    while (rs.next()) {
                        TableMeta table = TableMeta.build(rs);
                        tableMetas.add(table);
                    }
                }
            }

            for (TableMeta tableMeta : tableMetas) {
                // 获取主键
                final Set<String> pks = getPrimaryKeysSet(metaData, tableMeta.getCatalog(), tableMeta.getSchema(),
                        tableMeta.getTableName());
                tableMeta.setPkNames(pks);

                // 获取字段信息
                try (final ResultSet rs = metaData.getColumns(tableMeta.getCatalog(), tableMeta.getSchema(),
                        tableMeta.getTableName(), null)) {
                    if (null != rs) {
                        Map<String, ColumnMeta> columnMetaMap = new LinkedHashMap<>();
                        while (rs.next()) {
                            ColumnMeta columnMeta = ColumnMeta.build(rs);
                            // 是否是主键
                            columnMeta.setPrimaryKey(tableMeta.isPrimaryKey(columnMeta.getColumnName()));
                            columnMetaMap.put(columnMeta.getColumnName(), columnMeta);
                        }
                        tableMeta.setColumns(columnMetaMap);
                    }
                }

                // 获得索引信息
                try (final ResultSet rs = metaData.getIndexInfo(tableMeta.getCatalog(), tableMeta.getSchema(),
                        tableMeta.getTableName(), false, false)) {
                    final Map<String, IndexMeta> indexInfoMap = new LinkedHashMap<>();
                    if (null != rs) {
                        while (rs.next()) {
                            // 排除tableIndexStatistic类型索引
                            if (0 == rs.getShort("TYPE")) {
                                continue;
                            }
                            IndexMeta indexMeta = new IndexMeta();
                            indexMeta.setTableName(tableMeta.getTableName());
                            indexMeta.setNonUnique(rs.getBoolean("NON_UNIQUE"));
                            indexMeta.setIndexName(rs.getString("INDEX_NAME"));
                            indexMeta.setColumnName(rs.getString("COLUMN_NAME"));
                            String key = String.join("#", indexMeta.getTableName(), indexMeta.getIndexName());
                            if (null != indexInfoMap.get(key)) {
                                String preColumnName = indexInfoMap.get(key).getColumnName();
                                String newColumnName = String.join(StringPools.COMMA, preColumnName,
                                        indexMeta.getColumnName());
                                indexMeta.setColumnName(newColumnName);
                            }
                            indexInfoMap.put(key, indexMeta);
                        }
                        tableMeta.setIndexInfoList(CollectionUtil.toList(indexInfoMap.values()));
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("获取数据库表元数据信息失败", e);
        }
        finally {
            JdbcUtil.close(conn);
        }
        return tableMetas;
    }

    /**
     * 获取表的简化元数据信息数组
     * @param dataSource 数据源
     * @param tableName 表名, 若为空查所有
     * @param catalog catalog name
     * @param schema schema name
     * @return Table数组
     */
    public static List<TableMeta> getSimplifyTableMeta(DataSource dataSource, String catalog, String schema,
            String tableName) {
        return getSimplifyTableMeta(dataSource, catalog, schema, tableName, TableType.TABLE);
    }

    /**
     * 获取表的简化元数据信息数组
     * @param dataSource 数据源
     * @param tableName 表名, 若为空查所有
     * @param catalog catalog name
     * @param schema schema name
     * @param types 表类型
     * @return Table数组
     */
    public static List<TableMeta> getSimplifyTableMeta(DataSource dataSource, String catalog, String schema,
            String tableName, TableType... types) {
        final List<TableMeta> tableMetas = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            if (null == catalog) {
                catalog = getCatalog(conn);
            }
            if (null == schema) {
                schema = getSchema(conn);
            }
            final DatabaseMetaData metaData = conn.getMetaData();
            String[] tableTypes = Arrays.stream(types).map(TableType::getName).toArray(String[]::new);
            // 获得表元数据（表注释）
            try (final ResultSet rs = metaData.getTables(catalog, schema, tableName, tableTypes)) {
                if (null != rs) {
                    while (rs.next()) {
                        TableMeta table = TableMeta.build(rs);
                        tableMetas.add(table);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("获取数据库表元数据信息失败", e);
        }
        finally {
            JdbcUtil.close(conn);
        }
        return tableMetas;
    }

    /**
     * 获取某个表的所有字段名
     * @param dataSource 数据源
     * @param tableName 表名
     * @return 字段数组
     */
    public static List<String> getColumnNames(DataSource dataSource, String tableName) {
        final List<ColumnMeta> columnNames = getColumnMeta(dataSource, null, null, tableName);
        return columnNames.stream().map(ColumnMeta::getColumnName).collect(Collectors.toList());
    }

    /**
     * 获取某张表的字段元数据信息
     * @param dataSource 数据源
     * @param tableName 表名, 必填
     * @param catalog catalog name
     * @param schema schema name
     * @return Table数组
     */
    public static List<ColumnMeta> getColumnMeta(DataSource dataSource, String catalog, String schema,
            String tableName) {
        final Map<String, ColumnMeta> columnMetaMap = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(tableName)) {
            Connection conn = null;
            try {
                conn = dataSource.getConnection();
                if (null == catalog) {
                    catalog = getCatalog(conn);
                }
                if (null == schema) {
                    schema = getSchema(conn);
                }
                final DatabaseMetaData metaData = conn.getMetaData();
                // 获取主键
                final Set<String> pks = getPrimaryKeysSet(metaData, catalog, schema, tableName);
                // 获取字段信息
                try (final ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
                    if (null != rs) {
                        while (rs.next()) {
                            ColumnMeta columnMeta = ColumnMeta.build(rs);
                            // 是否是主键
                            columnMeta.setPrimaryKey(pks.contains(columnMeta.getColumnName()));
                            columnMetaMap.put(columnMeta.getColumnName(), columnMeta);
                        }
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException("获取数据库表-字段元数据信息失败", e);
            }
            finally {
                JdbcUtil.close(conn);
            }
        }
        return new ArrayList<>(columnMetaMap.values());
    }

    /**
     * 获取表主键
     * @param metaData 数据库元数据对象
     * @param tableName 表名, 必填
     * @param catalog catalog name
     * @param schema schema name
     * @return 返回主键数组
     */
    private static Set<String> getPrimaryKeysSet(DatabaseMetaData metaData, String catalog, String schema,
            String tableName) {
        final Set<String> pks = new HashSet<>();
        if (StrUtil.isNotBlank(tableName)) {
            try {
                try (final ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                    if (null != rs) {
                        while (rs.next()) {
                            pks.add(rs.getString("COLUMN_NAME"));
                        }
                        if (pks.size() > 1) {
                            LOGGER.warn("当前表: {}, 存在多主键: [{}]", tableName, String.join(",", pks));
                        }
                    }
                }
            }
            catch (SQLException e) {
                throw new RuntimeException("获取数据库表主键失败!", e);
            }
        }
        return pks;
    }

    /**
     * 获取catalog，获取失败获取{@code null}
     * @param conn {@link Connection} 数据库连接，{@code null}时获取null
     * @return catalog 获取失败获取{@code null}
     */
    private static String getCatalog(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getCatalog();
        }
        catch (SQLException e) {
            // ignore
        }
        return null;
    }

    /**
     * 获取schema，获取失败获取{@code null}
     * @param conn {@link Connection} 数据库连接，{@code null}时获取null
     * @return schema 获取失败获取{@code null}
     */
    private static String getSchema(Connection conn) {
        if (null == conn) {
            return null;
        }
        try {
            return conn.getSchema();
        }
        catch (SQLException e) {
            // ignore
        }
        return null;
    }

    @Getter
    @AllArgsConstructor
    public enum TableType {

        TABLE("TABLE"),

        VIEW("VIEW"),

        SYSTEM_TABLE("SYSTEM TABLE"),

        GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),

        LOCAL_TEMPORARY("LOCAL TEMPORARY"),

        ALIAS("ALIAS"),

        SYSTEM_VIEW("SYSTEM VIEW"),

        UNKNOWN("UNKNOWN"),

        SYNONYM("SYNONYM");

        private final String name;

    }

}
