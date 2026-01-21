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
package io.github.panxiaochao.boot3.mybatis.plus.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.panxiaochao.boot3.mybatis.plus.config.properties.MpProperties;
import io.github.panxiaochao.boot3.mybatis.plus.handler.IMetaObjectHandler;
import io.github.panxiaochao.boot3.mybatis.plus.handler.MetaObjectHandlerCustomizer;
import io.github.panxiaochao.boot3.mybatis.plus.interceptor.SqlLogInterceptor;
import io.github.panxiaochao.boot3.utils.BooleanUtil;
import io.github.panxiaochao.boot3.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * <p>
 * MyBatis plus 自动配置类
 * </p>
 *
 * <p>
 * 理论上, mybatisPlus 连表插件(MPJBaseMapper) 与自定义SQL注入器(MySqlInjector, OracleInjector) 不应该冲突,
 * 但是在实际使用中, 发现 MPJBaseMapper 会覆盖自定义SQL注入器的方法, 导致自定义SQL注入器失效. 参考
 * <a href="https://blog.csdn.net/ygxyvip/article/details/120524157">论mybatisPlus
 * 连表插件(MPJBaseMapper) 与自定义SQL注入器冲突</a>
 * </p>
 *
 * @author Lypxc
 * @since 2023-07-17
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(MpProperties.class)
public class MybatisPlusCustomizerAutoConfiguration {

    private final MpProperties mpProperties;

    /**
     * 配置 mybatis plus 插件
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限
        // interceptor.addInnerInterceptor(new DataScopeInnerInterceptor());
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    /**
     * 分页插件
     */
    private CustomizerPaginationInnerInterceptor paginationInnerInterceptor() {
        CustomizerPaginationInnerInterceptor paginationInnerInterceptor = new CustomizerPaginationInnerInterceptor();
        // 设置数据库类型
        paginationInnerInterceptor.setDbType(mpProperties.getDbType());
        paginationInnerInterceptor.setOptimizeJoin(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        // 分页合理化
        paginationInnerInterceptor.setOverflow(true);
        return paginationInnerInterceptor;
    }

    /**
     * 自定义分页插件, 解决当 size 小于 0 时, 直接设置为 0, 防止错误查询全表
     */
    static class CustomizerPaginationInnerInterceptor extends PaginationInnerInterceptor {

        @Override
        public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
                ResultHandler resultHandler, BoundSql boundSql) {
            IPage<?> page = ParameterUtils.findPage(parameter).orElse(null);
            if (null == page) {
                return;
            }
            // size 小于 0 直接设置为 0
            if (page.getSize() < 0) {
                page.setSize(0);
            }
            super.beforeQuery(executor, ms, page, rowBounds, resultHandler, boundSql);
        }

    }

    /**
     * 自定义 Mybatis Plus 插件配置：
     *
     * <ul>
     * <li>1.配置 sql 日志拦截器</li>
     * </ul>
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            if (BooleanUtil.isTrue(BooleanUtil.toBoolean(mpProperties.getSqlLogTrace()))) {
                // 添加sql日志拦截器
                configuration.addInterceptor(new SqlLogInterceptor());
            }
        };
    }

    /**
     * Mybatis Plus 自动填充配置
     * @return MetaObjectHandler
     */
    @Bean
    public MetaObjectHandler metaObjectHandler(IMetaObjectHandler metaObjectHandler) {
        return new MetaObjectHandlerCustomizer(metaObjectHandler);
    }

    /**
     * 使用网卡信息绑定雪花生成器, 防止集群雪花ID重复
     */
    @Bean
    public IdentifierGenerator idGenerator() {
        long workerId = IpUtil.ipv4ToLong(IpUtil.getLocalhostStr()) & 31;
        long dataCenterId = workerId > 30 ? 0 : workerId + 1;
        return new DefaultIdentifierGenerator(workerId, dataCenterId);
    }

    /**
     * 仅 MySQL 注入器
     * @return 注入器
     */
    // @Bean
    // @ConditionalOnProperty(name = "mybatis-plus.db-type", havingValue = "mysql")
    // public MySqlInjector mySqlInjector() {
    // return new MySqlInjector();
    // }

    /**
     * 仅 Oracle 注入器
     * @return 注入器
     */
    // @Bean
    // @ConditionalOnProperty(name = "mybatis-plus.db-type", havingValue = "oracle")
    // public OracleInjector oracleInjector() {
    // return new OracleInjector();
    // }

    /**
     * 自定义元对象字段填充默认实现类
     */
    @Configuration(proxyBeanMethods = false)
    static class MetaObjectHandlerCustomizerConfiguration {

        @Bean
        @ConditionalOnMissingBean(IMetaObjectHandler.class)
        public DefaultMetaObjectHandlerCustomizer defaultMetaObjectHandlerCustomizer() {
            return new DefaultMetaObjectHandlerCustomizer();
        }

        static final class DefaultMetaObjectHandlerCustomizer implements IMetaObjectHandler, Ordered {

            @Override
            public void insertFillCustomize(MetaObject metaObject) {
                // default no something
            }

            @Override
            public void updateFillCustomize(MetaObject metaObject) {
                // default no something
            }

            @Override
            public int getOrder() {
                return 0;
            }

        }

    }

}
