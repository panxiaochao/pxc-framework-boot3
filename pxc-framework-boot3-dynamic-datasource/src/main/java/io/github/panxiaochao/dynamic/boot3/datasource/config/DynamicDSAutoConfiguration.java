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
package io.github.panxiaochao.dynamic.boot3.datasource.config;

import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.processor.DsJakartaHeaderProcessor;
import com.baomidou.dynamic.datasource.processor.DsJakartaSessionProcessor;
import com.baomidou.dynamic.datasource.processor.DsProcessor;
import com.baomidou.dynamic.datasource.processor.DsSpelExpressionProcessor;
import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import io.github.panxiaochao.dynamic.boot3.datasource.config.properties.DsProperties;
import io.github.panxiaochao.dynamic.boot3.datasource.filter.ClearDataSourceFilter;
import io.github.panxiaochao.dynamic.boot3.datasource.processor.LastDsProcessor;
import io.github.panxiaochao.dynamic.boot3.datasource.provider.JdbcDataSourceProvider;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.BeanFactoryResolver;

/**
 * <p>
 * 动态数据源 自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-27
 * @version 1.0
 */
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DsProperties.class)
public class DynamicDSAutoConfiguration {

    /**
     * LOGGER DynamicDSAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDSAutoConfiguration.class);

    /**
     * 动态数据源提供者
     * @param defaultDataSourceCreator 默认数据源创建器
     * @param properties 数据源配置属性
     * @return 动态数据源提供者
     */
    @Bean
    public DynamicDataSourceProvider dynamicDataSourceProvider(DefaultDataSourceCreator defaultDataSourceCreator,
            StringEncryptor stringEncryptor, DataSourceProperties dataSourceProperties, DsProperties properties) {
        LOGGER.info("配置[DynamicDataSourceProvider]成功！");
        return new JdbcDataSourceProvider(defaultDataSourceCreator, stringEncryptor, dataSourceProperties, properties);
    }

    /**
     * 获取数据源处理器
     * @return 数据源处理器
     */
    @Bean
    public DsProcessor dsProcessor(BeanFactory beanFactory) {
        DsProcessor lastDsProcessor = new LastDsProcessor();
        DsProcessor headerProcessor = new DsJakartaHeaderProcessor();
        DsProcessor sessionProcessor = new DsJakartaSessionProcessor();
        DsSpelExpressionProcessor dsSpelExpressionProcessor = new DsSpelExpressionProcessor();
        dsSpelExpressionProcessor.setBeanResolver(new BeanFactoryResolver(beanFactory));
        lastDsProcessor.setNextProcessor(headerProcessor);
        headerProcessor.setNextProcessor(sessionProcessor);
        sessionProcessor.setNextProcessor(dsSpelExpressionProcessor);
        return lastDsProcessor;
    }

    /**
     * 清除数据源过滤器
     * @return ClearDataSourceFilter
     */
    @Bean
    public ClearDataSourceFilter clearDataSourceFilter() {
        return new ClearDataSourceFilter();
    }

}
