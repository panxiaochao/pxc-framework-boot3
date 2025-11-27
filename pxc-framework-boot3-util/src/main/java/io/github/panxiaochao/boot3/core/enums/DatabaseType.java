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
package io.github.panxiaochao.boot3.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 数据库类型枚举.
 * </p>
 *
 * @author Lypxc
 * @since 2023-11-14
 */
@Getter
@ToString
@AllArgsConstructor
public enum DatabaseType {

    /**
     * MYSQL
     */
    MYSQL("mysql", "com.mysql.cj.jdbc.Driver", "MySql数据库"),
    /**
     * MARIADB
     */
    MARIADB("mariadb", "org.mariadb.jdbc.Driver", "MariaDB数据库"),
    /**
     * ORACLE
     */
    ORACLE("oracle", "oracle.jdbc.driver.OracleDriver", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)"),
    /**
     * oracle12c new pagination
     */
    ORACLE_12C("oracle12c", "", "Oracle12c+数据库"),
    /**
     * DB2
     */
    DB2("db2", "com.ibm.db2.jcc.DB2Driver", "DB2数据库"),
    /**
     * H2
     */
    H2("h2", "org.h2.Driver", "H2数据库"),
    /**
     * HSQL
     */
    HSQL("hsql", "", "HSQL数据库"),
    /**
     * SQLITE
     */
    SQLITE("sqlite", "org.sqlite.JDBC", "SQLite数据库"),
    /**
     * POSTGRE
     */
    POSTGRE_SQL("postgresql", "org.postgresql.Driver", "Postgre数据库"),
    /**
     * SQLSERVER2005
     */
    SQL_SERVER2005("sqlserver2005", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SQLServer2005数据库"),
    /**
     * SQLSERVER
     */
    SQL_SERVER("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "SQLServer数据库"),
    /**
     * DM
     */
    DM("dm", "dm.jdbc.driver.DmDriver", "达梦数据库"),
    /**
     * xugu
     */
    XU_GU("xugu", "com.xugu.cloudjdbc.Driver", "虚谷数据库"),
    /**
     * Kingbase
     */
    KINGBASE_ES("kingbasees", "com.kingbase8.Driver", "人大金仓数据库"),
    /**
     * Phoenix
     */
    PHOENIX("phoenix", "org.apache.phoenix.jdbc.PhoenixDriver", "Phoenix HBase数据库"),
    /**
     * Gauss
     */
    GAUSS("zenith", "", "Gauss 数据库"),
    /**
     * ClickHouse
     */
    CLICK_HOUSE("clickhouse", "com.clickhouse.jdbc.ClickHouseDriver", "clickhouse 数据库"),
    /**
     * GBase
     */
    GBASE("gbase", "com.gbase.jdbc.Driver", "南大通用(华库)数据库"),
    /**
     * GBase-8s
     */
    GBASE_8S("gbase-8s", "com.gbase.jdbc.Driver", "南大通用数据库 GBase 8s"),
    /**
     * Sinodb
     */
    SINODB("sinodb", "", "星瑞格数据库"),
    /**
     * Oscar
     */
    OSCAR("oscar", "", "神通数据库"),
    /**
     * Sybase
     */
    SYBASE("sybase", "", "Sybase ASE 数据库"),
    /**
     * OceanBase
     */
    OCEAN_BASE("oceanbase", "com.mysql.cj.jdbc.Driver", "OceanBase 数据库"),
    /**
     * Firebird
     */
    FIREBIRD("Firebird", "", "Firebird 数据库"),
    /**
     * HighGo
     */
    HIGH_GO("highgo", "", "瀚高数据库"),
    /**
     * CUBRID
     */
    CUBRID("cubrid", "", "CUBRID数据库"),
    /**
     * GOLDILOCKS
     */
    GOLDILOCKS("goldilocks", "", "GOLDILOCKS数据库"),
    /**
     * CSIIDB
     */
    CSIIDB("csiidb", "", "CSIIDB数据库"),
    /**
     * Hana
     */
    SAP_HANA("hana", "", "SAP_HANA数据库"),
    /**
     * Impala
     */
    IMPALA("impala", "", "impala数据库"),
    /**
     * Vertica
     */
    VERTICA("vertica", "", "vertica数据库"),
    /**
     * xcloud
     */
    XCloud("xcloud", "", "行云数据库"),
    /**
     * redshift
     */
    REDSHIFT("redshift", "", "亚马逊redshift数据库"),
    /**
     * openGauss
     */
    OPENGAUSS("openGauss", "", "华为 opengauss 数据库"),
    /**
     * TDengine
     */
    TDENGINE("TDengine", "", "TDengine数据库"),
    /**
     * Informix
     */
    INFORMIX("informix", "", "Informix数据库"),
    /**
     * uxdb
     */
    UXDB("uxdb", "", "优炫数据库"),
    /**
     * lealone
     */
    LEALONE("lealone", "", "Lealone数据库"),
    /**
     * UNKNOWN DB
     */
    OTHER("other", "", "其他数据库");

    /**
     * 数据库名称
     */
    private final String dbType;

    /**
     * 驱动名称
     */
    private final String driverClassName;

    /**
     * 描述
     */
    private final String remark;

    /**
     * 获取数据库类型
     * @param dbType 数据库类型字符串
     */
    public static DatabaseType getDatabaseType(String dbType) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.dbType.equalsIgnoreCase(dbType)) {
                return type;
            }
        }
        return OTHER;
    }

}
