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
package io.github.panxiaochao.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import io.github.panxiaochao.core.utils.date.DatePattern;
import io.github.panxiaochao.core.utils.jackson.CustomizeJavaTimeModule;
import io.github.panxiaochao.redis.config.properties.Redisson3Properties;
import io.github.panxiaochao.redis.mapper.KeyPrefixNameMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * <p>
 * Redisson 自动配置类 3表示大版本号
 * </p>
 *
 * @author Lypxc
 * @since 2023-06-27
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties({ Redisson3Properties.class })
@ConditionalOnWebApplication
public class Redisson3AutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Redisson3AutoConfiguration.class);

    private final Redisson3Properties redisson3Properties;

    /**
     * 自定义 Redisson 配置
     * @return RedissonAutoConfigurationCustomizer
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizers() {
        return config -> {
            // 序列化模式
            JsonJacksonCodec jsonCodec = new JsonJacksonCodec(objectMapper());
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            config.setCodec(new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec));
            config.setThreads(16);
            config.setNettyThreads(32);
            // 缓存 Lua 脚本 减少网络传输(redisson 大部分的功能都是基于 Lua 脚本实现)
            config.setUseScriptCache(true);
            // 获取方法
            Method singleServerMethod = ReflectionUtils.findMethod(Config.class, "getSingleServerConfig");
            Method sentinelServersMethod = ReflectionUtils.findMethod(Config.class, "getSentinelServersConfig");
            Method clusterServersMethod = ReflectionUtils.findMethod(Config.class, "getClusterServersConfig");
            // 自定义前缀
            KeyPrefixNameMapper keyPrefixNameMapper = new KeyPrefixNameMapper(redisson3Properties.getKeyPrefix());
            // 使用单机模式, 使用自定义前缀
            if (singleServerMethod != null) {
                ReflectionUtils.makeAccessible(singleServerMethod);
                Object singleServerObject = ReflectionUtils.invokeMethod(singleServerMethod, config);
                if (Objects.nonNull(singleServerObject)) {
                    ((SingleServerConfig) singleServerObject).setNameMapper(keyPrefixNameMapper);
                }
            }
            // 哨兵模式
            if (sentinelServersMethod != null) {
                ReflectionUtils.makeAccessible(sentinelServersMethod);
                Object sentinelServersObject = ReflectionUtils.invokeMethod(sentinelServersMethod, config);
                if (Objects.nonNull(sentinelServersObject)) {
                    ((SentinelServersConfig) sentinelServersObject).setNameMapper(keyPrefixNameMapper);
                }
            }
            // 集群配置方式
            // 哨兵模式
            if (clusterServersMethod != null) {
                ReflectionUtils.makeAccessible(clusterServersMethod);
                Object clusterServersObject = ReflectionUtils.invokeMethod(clusterServersMethod, config);
                if (Objects.nonNull(clusterServersObject)) {
                    ((ClusterServersConfig) clusterServersObject).setNameMapper(keyPrefixNameMapper);
                }
            }
            LOGGER.info("配置[Redis -> Redisson]成功！");
        };
    }

    /**
     * 创建并配置RedisTemplate模板 用于Redis操作，支持泛型
     * @param redisConnectionFactory Redis连接工厂，用于创建Redis连接
     * @return 返回RedisTemplate模板实例
     */
    @Bean(name = "redisTemplate")
    public <T> RedisTemplate<String, T> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper(), Object.class);
        // 使用 StringRedisSerializer 来序列化和反序列化redis的key值
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 Jackson2JsonRedisSerializer 序列化VALUE
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // afterPropertiesSet
        template.afterPropertiesSet();
        LOGGER.info("配置[Redis -> RedisTemplate]成功！");
        return template;
    }

    private ObjectMapper objectMapper() {
        // 使用Jackson2JsonRedisSerialize 替换默认序列化(默认采用的是JDK序列化)
        ObjectMapper om = new ObjectMapper();
        om.setLocale(Locale.CHINA);
        om.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 指定要序列化的域, field, get, set, 以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        om.setDateFormat(new SimpleDateFormat(DatePattern.NORMAL_DATE_TIME_PATTERN));
        om.registerModule(new CustomizeJavaTimeModule());
        return om;
    }

}
