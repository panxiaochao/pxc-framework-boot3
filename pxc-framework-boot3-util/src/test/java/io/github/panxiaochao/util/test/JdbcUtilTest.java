package io.github.panxiaochao.util.test;

import io.github.panxiaochao.boot3.core.enums.DatabaseType;
import io.github.panxiaochao.boot3.core.utils.DbMetaUtil;
import io.github.panxiaochao.boot3.core.utils.JacksonUtil;
import io.github.panxiaochao.boot3.core.utils.JdbcUtil;
import io.github.panxiaochao.boot3.core.utils.meta.db.ColumnMeta;
import io.github.panxiaochao.boot3.core.utils.meta.ddl.AbstractDatabase;
import io.github.panxiaochao.boot3.core.utils.meta.ddl.DatabaseFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-30
 * @version 1.0
 */
public class JdbcUtilTest {

    @Test
    void test() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // System.out.println(JdbcUtil.getDatabaseType(dataSource));
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3308/pxc-system?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "123456";
            conn = JdbcUtil.getConnection(driver, url, username, password, hikariConfig -> {
                // 设置可以获取tables remarks信息
                hikariConfig.addDataSourceProperty("remarks", "true");
                hikariConfig.addDataSourceProperty("useInformationSchema", "true");
            });
            System.out.println(JdbcUtil.getDataBaseVersion(conn));
            System.out.println(conn.getMetaData().getDatabaseProductName());
            ps = conn.prepareStatement("select * from test");
            rs = ps.executeQuery();
            // JdbcUtil.printResultSet(rs, true, ",");
            JdbcUtil.printResultSetColumnsInfo(rs);
            // System.out.println(JdbcUtil.getResultSetValue(rs, 1));
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        finally {
            JdbcUtil.close(conn);
            JdbcUtil.close(ps);
            JdbcUtil.close(rs);
        }
    }

    @Test
    void getTables() {
        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3308/pxc-system?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
            String username = "root";
            String password = "123456";
            DataSource dataSource = JdbcUtil.getDataSource(driver, url, username, password, hikariConfig -> {
                // 设置可以获取tables remarks信息
                hikariConfig.addDataSourceProperty("remarks", "true");
                hikariConfig.addDataSourceProperty("useInformationSchema", "true");
            });
            // List<String> tables = DbMetaUtil.getTables(dataSource);
            // System.out.println(tables);
            // List<String> columns = DbMetaUtil.getColumnNames(dataSource,
            // "oauth2_authorization_consent");
            // System.out.println(columns);

            // List<TableMeta> tableMetas = DbMetaUtil.getTableMeta(dataSource, null,
            // null, null);
            // System.out.println(JacksonUtil.toString(tableMetas));

            List<ColumnMeta> columnMetas = DbMetaUtil.getColumnMeta(dataSource, null, null, "database_field_tag");
            System.out.println(JacksonUtil.toString(columnMetas));

            // List<String> columnNames = DbMetaUtil.getColumnNames(dataSource,
            // "oauth2_authorization");
            // System.out.println(JacksonUtil.toString(columnNames));

            AbstractDatabase database = DatabaseFactory.getDatabaseInstance(DatabaseType.MYSQL);
            String createTableDDL = database.generateCreateTableSql(null, "test1", "测试", columnMetas);
            System.out.println(createTableDDL);
            // System.out.println(database.getTableDdl(dataSource.getConnection(), null,
            // "test1"));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getDmTables() {
        try {
            String driver = "dm.jdbc.driver.DmDriver";
            // String url = "jdbc:dm://134.98.6.38:5237/HZ_SPT_TEST";
            // String username = "HZ_SPT_TEST";
            // String password = "hesc@00728";
            String url = "jdbc:dm://134.98.6.38:5237/SRT_CLOUD_TEST";
            String username = "SRT_CLOUD_TEST";
            String password = "SRT_CLOUD_TEST@2024";
            DataSource dataSource = JdbcUtil.getDataSource(driver, url, username, password);
            // List<String> tables = DbMetaUtil.getTables(dataSource);
            // System.out.println(tables);
            // List<String> columns = DbMetaUtil.getColumnNames(dataSource,
            // "oauth2_authorization_consent");
            // System.out.println(columns);

            // List<TableMeta> tableMetas = DbMetaUtil.getTableMeta(dataSource, null,
            // null, null);
            // System.out.println(JacksonUtil.toString(tableMetas));
            //
            // System.out.println(dataSource.getConnection().getMetaData().getDatabaseProductName());
            List<ColumnMeta> columnMetas = DbMetaUtil.getColumnMeta(dataSource, null, "SRT_CLOUD_TEST", "sys_user");
            System.out.println(JacksonUtil.toString(columnMetas));

            AbstractDatabase database = DatabaseFactory.getDatabaseInstance(DatabaseType.DM);
            String createTableDDL = database.generateCreateTableSql("SRT_CLOUD_TEST", "test1", "测试", columnMetas);
            System.out.println(createTableDDL);
            // System.out.println(database.getTableDdl(dataSource.getConnection(),
            // "SRT_CLOUD_TEST", "sys_user"));

            // List<String> columnNames = DbMetaUtil.getColumnNames(dataSource,
            // "urp_user");
            // System.out.println(JacksonUtil.toString(columnNames));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConnection() {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/oauth2?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "root1234561";
        System.out.println(JdbcUtil.testConnection(url, username, password));
    }

}
