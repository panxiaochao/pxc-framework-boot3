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
package io.github.panxiaochao.boot3.mybatis.plus.interceptor;

import io.github.panxiaochao.boot3.core.utils.ObjectUtil;
import io.github.panxiaochao.boot3.core.utils.date.LocalDateTimeUtil;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * <p>
 * Mybatis-Plus Sql日志拦截器
 * </p>
 *
 * @author lypxc
 * @since 2025-06-05
 * @version 1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
        @Signature(type = Executor.class, method = "query",
                args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class,
                        BoundSql.class }),
        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class SqlLogInterceptor implements Interceptor {

    /**
     * LOGGER SqlLogInterceptor.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlLogInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Map<String, String> resultMap = new HashMap<>();
        try {
            resultMap = generateSql(invocation);
        }
        catch (Exception exception) {
            LOGGER.error("获取sql异常", exception);
        }
        // 性能追踪
        Object proceed;
        try (PerfTracker.TimerContext ignored = PerfTracker.start(resultMap)) {
            proceed = invocation.proceed();
        }
        return proceed;
    }

    private static Map<String, String> generateSql(Invocation invocation) {
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        Configuration configuration = statement.getConfiguration();
        BoundSql boundSql = statement.getBoundSql(parameter);
        // 获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 获取参数映射
        List<ParameterMapping> params = boundSql.getParameterMappings();
        // 获取到执行的SQL
        String sql = boundSql.getSql();
        // SQL中多个空格使用一个空格代替
        sql = sql.replaceAll("[\\s]+", " ");
        if (!ObjectUtil.isEmpty(params) && !ObjectUtil.isEmpty(parameterObject)) {
            // TypeHandlerRegistry 是 MyBatis 用来管理 TypeHandler 的注册器 TypeHandler 用于在 Java
            // 类型和 JDBC 类型之间进行转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果参数对象的类型有对应的 TypeHandler，则使用 TypeHandler 进行处理
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            }
            else {
                // 否则，逐个处理参数映射
                for (ParameterMapping param : params) {
                    // 获取参数的属性名
                    String propertyName = param.getProperty();
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    // 检查对象中是否存在该属性的 getter 方法，如果存在就取出来进行替换
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                        // 检查 BoundSql 对象中是否存在附加参数
                    }
                    else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    }
                    else {
                        // SQL匹配不上，带上“缺失”方便找问题
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        // 返回结果
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("sql", sql);
        resultMap.put("id", statement.getId());
        resultMap.put("type", statement.getSqlCommandType().name());
        return resultMap;
    }

    private static String getParameterValue(Object object) {
        String value = "";
        if (object instanceof String) {
            value = "'" + object + "'";
        }
        else if (object instanceof Date) {
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + format.format((Date) object) + "'";
        }
        else if (object instanceof LocalDate) {
            value = "'" + LocalDateTimeUtil.localDateToString((LocalDate) object) + "'";
        }
        else if (object instanceof LocalDateTime) {
            value = "'" + LocalDateTimeUtil.localDateTimeToString((LocalDateTime) object) + "'";
        }
        else if (!ObjectUtil.isEmpty(object)) {
            value = object.toString();
        }
        return value;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

}
