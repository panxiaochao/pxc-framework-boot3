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
package io.github.panxiaochao.boot3.core.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.panxiaochao.boot3.core.enums.DatabaseType;
import io.github.panxiaochao.boot3.crypto.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.NumberUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 * Jdbc 工具类
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-28
 * @version 1.0
 */
public class JdbcUtil {

    /**
     * LOGGER JdbcUtil.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUtil.class);

    /**
     * 连接超时设置
     */
    private static final int CONNECTION_TIMEOUTS_SECONDS = 5;

    private static final List<String> JAVA8_TIME = Arrays.asList("LocalDate", "LocalTime", "LocalDateTime");

    /**
     * Open a new database connection with the given settings.
     * @param driver the driver class name
     * @param url the database URL
     * @param username the username
     * @param password the password
     * @return the database connection
     */
    public static Connection getConnection(String driver, String url, String username, String password) {
        Consumer<HikariConfig> consumer = hikariConfig -> {
            hikariConfig.setMinimumIdle(5);
            hikariConfig.setMaximumPoolSize(10);
        };
        return getConnection(driver, url, username, password, consumer);
    }

    /**
     * Open a new database connection with the given settings.
     * @param driver the driver class name
     * @param url the database URL
     * @param username the username
     * @param password the password
     * @param consumer the HikariConfig consumer
     * @return the database connection
     */
    public static Connection getConnection(String driver, String url, String username, String password,
            Consumer<HikariConfig> consumer) {
        try {
            DataSource dataSource = getDataSource(driver, url, username, password, consumer);
            return dataSource.getConnection();
        }
        catch (SQLException e) {
            LOGGER.error("获取Connection失败", e);
            return null;
        }
    }

    /**
     * Obtain a new database DataSource with the given settings.
     * @param driver the driver class name
     * @param url the database URL
     * @param username the username
     * @param password the password
     * @return the database connection
     */
    public static DataSource getDataSource(String driver, String url, String username, String password) {
        return getDataSource(driver, url, username, password, null);
    }

    /**
     * Obtain a new database DataSource with the given settings.
     * @param driver the driver class name
     * @param url the database URL
     * @param username the username
     * @param password the password
     * @param consumer the HikariConfig consumer, customer properties
     * @return the database connection
     */
    public static DataSource getDataSource(String driver, String url, String username, String password,
            Consumer<HikariConfig> consumer) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        if (null != consumer) {
            consumer.accept(hikariConfig);
        }
        return new HikariDataSource(hikariConfig);
    }

    /**
     * 获取数据库版本号
     * @param con Connection
     * @return 数据库版本号
     */
    public static String getDataBaseVersion(Connection con) {
        if (con != null) {
            try {
                DatabaseMetaData databaseMetaData = con.getMetaData();
                // System.out.println(databaseMetaData.getDatabaseMajorVersion());
                // System.out.println(databaseMetaData.getDatabaseMinorVersion());
                // System.out.println(databaseMetaData.getDatabaseProductVersion());
                return databaseMetaData.getDatabaseProductVersion();
            }
            catch (SQLException e) {
                LOGGER.error("获取数据库版本失败", e);
            }
        }
        return "";
    }

    public static Object getResultSetValue(ResultSet rs, int index, Class<?> requiredType) throws SQLException {
        if (requiredType == null) {
            return getResultSetValue(rs, index);
        }

        Object value = null;
        // Explicitly extract typed value, as far as possible.
        if (String.class == requiredType) {
            return rs.getString(index);
        }
        else if (boolean.class == requiredType || Boolean.class == requiredType) {
            value = rs.getBoolean(index);
        }
        else if (byte.class == requiredType || Byte.class == requiredType) {
            value = rs.getByte(index);
        }
        else if (short.class == requiredType || Short.class == requiredType) {
            value = rs.getShort(index);
        }
        else if (int.class == requiredType || Integer.class == requiredType) {
            value = rs.getInt(index);
        }
        else if (long.class == requiredType || Long.class == requiredType) {
            value = rs.getLong(index);
        }
        else if (float.class == requiredType || Float.class == requiredType) {
            value = rs.getFloat(index);
        }
        else if (double.class == requiredType || Double.class == requiredType || Number.class == requiredType) {
            value = rs.getDouble(index);
        }
        else if (BigDecimal.class == requiredType) {
            return rs.getBigDecimal(index);
        }
        else if (Date.class == requiredType) {
            return rs.getDate(index);
        }
        else if (Time.class == requiredType) {
            return rs.getTime(index);
        }
        else if (Timestamp.class == requiredType || java.util.Date.class == requiredType) {
            return rs.getTimestamp(index);
        }
        else if (byte[].class == requiredType) {
            return rs.getBytes(index);
        }
        else if (Blob.class == requiredType) {
            return rs.getBlob(index);
        }
        else if (Clob.class == requiredType) {
            return rs.getClob(index);
        }
        else if (requiredType.isEnum()) {
            // Enums can either be represented through a String or an enum index value:
            // leave enum type conversion up to the caller (e.g. a ConversionService)
            // but make sure that we return nothing other than a String or an Integer.
            Object obj = rs.getObject(index);
            if (obj instanceof String) {
                return obj;
            }
            else if (obj instanceof Number) {
                // Defensively convert any Number to an Integer (as needed by our
                // ConversionService's IntegerToEnumConverterFactory) for use as index
                return NumberUtils.convertNumberToTargetClass((Number) obj, Integer.class);
            }
            else {
                // e.g. on Postgres: getObject returns a PGObject, but we need a String
                return rs.getString(index);
            }
        }
        else if (JAVA8_TIME.contains(requiredType.getSimpleName())) {
            // Corresponding SQL types for JSR-310 / Joda-Time types, left up
            // to the caller to convert them (e.g. through a ConversionService).
            String typeName = requiredType.getSimpleName();
            switch (typeName) {
                case "LocalDate":
                    return rs.getDate(index);
                case "LocalTime":
                    return rs.getTime(index);
                case "LocalDateTime":
                    return rs.getTimestamp(index);
            }
        }
        else {
            // Some unknown type desired -> rely on getObject.
            try {
                return rs.getObject(index, requiredType);
            }
            catch (SQLFeatureNotSupportedException | AbstractMethodError ex) {
                LOGGER.debug("JDBC driver does not support JDBC 4.1 'getObject(int, Class)' method", ex);
            }
            catch (SQLException ex) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("JDBC driver has limited support for 'getObject(int, Class)' with column type: "
                            + requiredType.getName(), ex);
                }
            }
            // Fall back to getObject without type specification, again
            // left up to the caller to convert the value if necessary.
            return getResultSetValue(rs, index);
        }

        // Perform was-null check if necessary (for results that the JDBC driver returns
        // as primitives).
        return (rs.wasNull() ? null : value);
    }

    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        }
        else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        }
        else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getTimestamp(index);
        }
        else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            }
            else {
                obj = rs.getDate(index);
            }
        }
        else if (obj instanceof Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }

    public static void printResultSet(ResultSet rs, boolean printHeader, String seperator) throws SQLException {
        PrintStream out = System.out;
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (printHeader) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }
                out.print(metadata.getColumnName(columnIndex));
            }
        }

        out.println();

        while (rs.next()) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }

                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnIndex));
                }
                else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(date.toString());
                    }
                }
                else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Boolean.toString(value));
                    }
                }
                else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Boolean.toString(value));
                    }
                }
                else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Byte.toString(value));
                    }
                }
                else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Short.toString(value));
                    }
                }
                else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Integer.toString(value));
                    }
                }
                else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(Long.toString(value));
                    }
                }
                else if (type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE) {
                    out.print(String.valueOf(rs.getTimestamp(columnIndex)));
                }
                else if (type == Types.DECIMAL) {
                    out.print(String.valueOf(rs.getBigDecimal(columnIndex)));
                }
                else if (type == Types.CLOB) {
                    out.print(String.valueOf(rs.getString(columnIndex)));
                }
                else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(String.valueOf(object));
                    }
                }
                else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        out.print(String.valueOf(object));
                    }
                }
                else if (type == Types.NULL) {
                    out.print("null");
                }
                else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    }
                    else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexUtil.encode(bytes);
                            out.print(text);
                        }
                        else {
                            out.print(String.valueOf(object));
                        }
                    }
                }
            }
            out.println();
        }
    }

    public static void printResultSetColumnsInfo(ResultSet rs) {
        try {
            PrintStream out = System.out;
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                out.println("CatalogName: " + metadata.getCatalogName(i));
                out.println("SchemaName: " + metadata.getSchemaName(i));
                out.println("TableName: " + metadata.getTableName(i));
                out.println("ColumnName: " + metadata.getColumnName(i));
                out.println("ColumnLabel: " + metadata.getColumnLabel(i));
                out.println("ColumnClassName: " + metadata.getColumnClassName(i));
                out.println("ColumnTypeName: " + metadata.getColumnTypeName(i));
                out.println("ColumnType: " + metadata.getColumnType(i));
                out.println("Precision: " + metadata.getPrecision(i));
                out.println("Scale: " + metadata.getScale(i));
                out.println("ColumnDisplaySize: " + metadata.getColumnDisplaySize(i));
                out.println("AutoIncrement: " + metadata.isAutoIncrement(i));
                out.println("CaseSensitive: " + metadata.isCaseSensitive(i));
                out.println("Currency: " + metadata.isCurrency(i));
                out.println("DefinitelyWritable: " + metadata.isDefinitelyWritable(i));
                out.println("Nullable: " + metadata.isNullable(i));
                out.println("ReadOnly: " + metadata.isReadOnly(i));
                out.println("Searchable: " + metadata.isSearchable(i));
                out.println("Signed: " + metadata.isSigned(i));
                out.println("Writable: " + metadata.isWritable(i));
                out.println("-------------------------------------------------------------\n");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取数据库类型
     * @param conn Connection
     * @return 类型枚举值，如果没找到，则返回 null
     */
    public static DatabaseType getDatabaseType(Connection conn) {
        try {
            return getDatabaseType(conn.getMetaData().getURL().toLowerCase());
        }
        catch (SQLException ex) {
            LOGGER.error("获取数据库类型失败", ex);
            return null;
        }
    }

    /**
     * 获取数据库类型
     * @param dataSource dataSource
     * @return 类型枚举值，如果没找到，则返回 null
     */
    public static DatabaseType getDatabaseType(DataSource dataSource) {
        try {
            Connection conn = dataSource.getConnection();
            return getDatabaseType(conn.getMetaData().getURL().toLowerCase());
        }
        catch (SQLException ex) {
            LOGGER.error("获取数据库类型失败", ex);
            return null;
        }
    }

    /**
     * 获取数据库类型
     * @param jdbcUrl jdbcUrl
     * @return 类型枚举值，如果没找到，则返回 null
     */
    public static DatabaseType getDatabaseType(String jdbcUrl) {
        // 统一变小写
        jdbcUrl = jdbcUrl.toLowerCase();
        if (jdbcUrl.contains(":mysql:") || jdbcUrl.contains(":cobar:")) {
            return DatabaseType.MYSQL;
        }
        else if (jdbcUrl.contains(":oracle:")) {
            return DatabaseType.ORACLE;
        }
        else if (jdbcUrl.contains(":postgresql:")) {
            return DatabaseType.POSTGRE_SQL;
        }
        else if (jdbcUrl.contains(":sqlserver:")) {
            return DatabaseType.SQL_SERVER;
        }
        else if (jdbcUrl.contains(":db2:")) {
            return DatabaseType.DB2;
        }
        else if (jdbcUrl.contains(":mariadb:")) {
            return DatabaseType.MARIADB;
        }
        else if (jdbcUrl.contains(":sqlite:")) {
            return DatabaseType.SQLITE;
        }
        else if (jdbcUrl.contains(":h2:")) {
            return DatabaseType.H2;
        }
        else if (jdbcUrl.contains(":lealone:")) {
            return DatabaseType.LEALONE;
        }
        else if (jdbcUrl.contains(":kingbase:") || jdbcUrl.contains(":kingbase8:")) {
            return DatabaseType.KINGBASE_ES;
        }
        else if (jdbcUrl.contains(":dm:")) {
            return DatabaseType.DM;
        }
        else if (jdbcUrl.contains(":zenith:")) {
            return DatabaseType.GAUSS;
        }
        else if (jdbcUrl.contains(":oscar:")) {
            return DatabaseType.OSCAR;
        }
        else if (jdbcUrl.contains(":firebird:")) {
            return DatabaseType.FIREBIRD;
        }
        else if (jdbcUrl.contains(":xugu:")) {
            return DatabaseType.XU_GU;
        }
        else if (jdbcUrl.contains(":clickhouse:")) {
            return DatabaseType.CLICK_HOUSE;
        }
        else if (jdbcUrl.contains(":sybase:")) {
            return DatabaseType.SYBASE;
        }
        else {
            return DatabaseType.OTHER;
        }
    }

    /**
     * Close the given JDBC Connection
     * @param x the JDBC Connection to close (maybe {@code null})
     */
    public static void close(Connection x) {
        if (x != null) {
            try {
                if (x.isClosed()) {
                    return;
                }
                x.close();
            }
            catch (Exception e) {
                LOGGER.error("Close Connection error", e);
            }
        }
    }

    /**
     * Close the given JDBC Statement
     * @param x the JDBC Statement to close (maybe {@code null})
     */
    public static void close(Statement x) {
        if (x != null) {
            try {
                x.close();
            }
            catch (Exception e) {
                LOGGER.error("Close Statement error", e);
            }
        }
    }

    /**
     * Close the given JDBC ResultSet
     * @param x the JDBC ResultSet to close (maybe {@code null})
     */
    public static void close(ResultSet x) {
        if (x != null) {
            try {
                x.close();
            }
            catch (Exception e) {
                LOGGER.error("Close ResultSet error", e);
            }
        }
    }

    /**
     * Close the given IO stream Closeable
     * @param x the IO stream Closeable to close (maybe {@code null})
     */
    public static void close(Closeable x) {
        if (x != null) {
            try {
                x.close();
            }
            catch (Exception e) {
                LOGGER.error("Close Closeable", e);
            }
        }
    }

    /**
     * Close the given JDBC SQL Blob
     * @param x the JDBC SQL Blob to close (maybe {@code null})
     */
    public static void close(Blob x) {
        if (x != null) {
            try {
                x.free();
            }
            catch (Exception e) {
                LOGGER.error("close error", e);
            }
        }
    }

    /**
     * Close the given JDBC SQL Clob
     * @param x the JDBC SQL Clob to close (maybe {@code null})
     */
    public static void close(Clob x) {
        if (x != null) {
            try {
                x.free();
            }
            catch (Exception e) {
                LOGGER.error("close error", e);
            }
        }
    }

    /**
     * 测试连接数据库是否成功
     * @param url 连接url
     * @param username 用户名
     * @param password 密码
     * @return true or false
     */
    public static boolean testConnection(String url, String username, String password) {
        DriverManager.setLoginTimeout(CONNECTION_TIMEOUTS_SECONDS);
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            LOGGER.info("Connection {} is success!", connection.getMetaData().getDatabaseProductName());
            return true;
        }
        catch (SQLException e) {
            LOGGER.error("Connection jdbc is error", e);
            return false;
        }
    }

}
