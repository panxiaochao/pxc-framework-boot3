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
package io.github.panxiaochao.dynamic.datasource.provider;

import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import io.github.panxiaochao.dynamic.datasource.config.properties.DsProperties;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-27
 * @version 1.0
 */
public class JdbcDataSourceProvider extends AbstractJdbcDataSourceProvider {

    /**
     * LOGGER JdbcDataSourceProvider.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcDataSourceProvider.class);

    private final DsProperties properties;

    private final DataSourceProperties dataSourceProperties;

    private final StringEncryptor stringEncryptor;

    /**
     * 通过默认数据源创建器创建数据源
     * @param defaultDataSourceCreator 默认数据源创建器
     * @param stringEncryptor 加密配置文件
     * @param dataSourceProperties 基础数据源属性配置文件
     * @param properties 属性配置文件
     */
    public JdbcDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator, StringEncryptor stringEncryptor,
            DataSourceProperties dataSourceProperties, DsProperties properties) {
        super(defaultDataSourceCreator, dataSourceProperties.getUrl(), dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword());
        this.properties = properties;
        this.dataSourceProperties = dataSourceProperties;
        this.stringEncryptor = stringEncryptor;
    }

    /**
     * 执行语句获得数据源参数
     * @param statement 语句
     * @return 数据源参数
     * @throws SQLException sql异常
     */
    @Override
    protected Map<String, DataSourceProperty> executeStmt(Statement statement) throws SQLException {
        Map<String, DataSourceProperty> map = new HashMap<>(8);
        if (StringUtils.hasText(properties.getQueryDsSql())) {
            ResultSet rs = statement.executeQuery(properties.getQueryDsSql());
            while (rs.next()) {
                String name = rs.getString("name");
                String url = rs.getString("url");
                String username = rs.getString("username");
                String password = rs.getString("password");
                DataSourceProperty property = new DataSourceProperty();
                property.setUsername(username);
                property.setLazy(true);
                property.setPassword(password);
                property.setUrl(url);
                map.put(name, property);
            }
        }
        else {
            LOGGER.error("请配置动态数据库查询语句参数[queryDsSql]");
        }

        // 添加默认主数据源
        DataSourceProperty property = new DataSourceProperty();
        property.setUsername(dataSourceProperties.getUsername());
        property.setPassword(dataSourceProperties.getPassword());
        property.setUrl(dataSourceProperties.getUrl());
        property.setLazy(true);
        map.put("master", property);
        return map;
    }

}
