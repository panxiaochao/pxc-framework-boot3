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

import io.github.panxiaochao.boot3.core.utils.StringPools;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * 数据库-字段元数据
 * </p>
 *
 * @author Lypxc
 * @since 2024-05-07
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class ColumnMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库 名
     */
    private String schema;

    /**
     * 数据库 表名
     */
    private String tableName;

    /**
     * 字段 名称
     */
    private String columnName;

    /**
     * 是否 主键
     */
    private boolean primaryKey;

    /**
     * 是否 自增
     */
    private boolean autoIncrement;

    /**
     * 表中的列的索引（从 1 开始）
     */
    private int ordinalPosition;

    /**
     * 字段 默认值
     */
    private String columnDefault;

    /**
     * 是否 可空
     */
    private boolean nullable;

    /**
     * jdbc类型，对应java.sql.Types中的类型
     */
    private int jdbcType;

    /**
     * jdbc类型名
     */
    private String jdbcTypeName;

    /**
     * 字段 长度，或者精度
     * <p>
     * <pre>
     * 1.对于数值数据，这是最大精度。
     * 2.对于字符数据，这是字符长度。
     * 3.对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。
     * 4.对于二进制数据，这是字节长度。
     * </pre>
     * </p>
     */
    private int columnLength;

    /**
     * 小数点位数
     */
    private int scale;

    /**
     * 字段 注释
     */
    private String columnComment;

    /**
     * 构建数据库字段元数据
     */
    public static ColumnMeta build(ResultSet rs) {
        ColumnMeta columnMeta = new ColumnMeta();
        try {
            populateBasicInfo(columnMeta, rs);
            populateOptionalFields(columnMeta, rs);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return columnMeta;
    }

    private static void populateBasicInfo(ColumnMeta columnMeta, ResultSet rs) throws SQLException {
        columnMeta.setSchema(rs.getString("TABLE_SCHEM"));
        columnMeta.setTableName(rs.getString("TABLE_NAME"));
        columnMeta.setColumnName(rs.getString("COLUMN_NAME"));
        columnMeta.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
        columnMeta.setColumnDefault(rs.getString("COLUMN_DEF"));
        columnMeta.setNullable(rs.getBoolean("NULLABLE"));
        columnMeta.setJdbcType(rs.getInt("DATA_TYPE"));
        columnMeta.setJdbcTypeName(rs.getString("TYPE_NAME"));
        columnMeta.setColumnLength(rs.getInt("COLUMN_SIZE"));
        columnMeta.setColumnComment(formatComment(rs.getString("REMARKS")));
    }

    private static void populateOptionalFields(ColumnMeta columnMeta, ResultSet rs) {
        populateScale(columnMeta, rs);
        populateAutoIncrement(columnMeta, rs);
    }

    private static void populateScale(ColumnMeta columnMeta, ResultSet rs) {
        try {
            int digit = rs.getInt("DECIMAL_DIGITS");
            columnMeta.setScale(digit);
        }
        catch (SQLException ignore) {
            // 某些驱动可能不支持，跳过
        }
    }

    private static void populateAutoIncrement(ColumnMeta columnMeta, ResultSet rs) {
        try {
            String auto = rs.getString("IS_AUTOINCREMENT");
            columnMeta.setAutoIncrement("YES".equalsIgnoreCase(auto));
        }
        catch (SQLException ignore) {
            // 某些驱动可能不支持，跳过
        }
    }

    /**
     * 格式化内容
     * @param comment 注释
     * @return 格式化内容
     */
    private static String formatComment(String comment) {
        return StringUtils.hasText(comment) ? comment.replaceAll("\r\n", "\t") : StringPools.EMPTY;
    }

}
