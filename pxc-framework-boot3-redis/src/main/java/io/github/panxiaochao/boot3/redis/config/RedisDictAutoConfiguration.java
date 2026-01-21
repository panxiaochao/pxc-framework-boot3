package io.github.panxiaochao.boot3.redis.config;

import io.github.panxiaochao.boot3.cache.config.properties.CacheManagerProperties;
import io.github.panxiaochao.boot3.redis.dict.RedisDictResolver;
import io.github.panxiaochao.boot3.utils.dict.DictResolverProvider;
import io.github.panxiaochao.boot3.utils.dict.IDictResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * RedisDict 字典解析器自动配置类
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({ CacheManagerProperties.class })
@ConditionalOnProperty(name = "spring.pxc-framework-boot3.cache.type", havingValue = "REDIS")
public class RedisDictAutoConfiguration {

    /**
     * LOGGER RedisDictAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisDictAutoConfiguration.class);

    @Bean
    public IDictResolver redisDictResolver() {
        RedisDictResolver redisDictResolver = new RedisDictResolver();
        // 注册 RedisDictResolver 到 DictResolverProvider
        DictResolverProvider.setDictResolver(redisDictResolver);
        LOGGER.info("配置[Dict -> RedisDict]成功！");
        return redisDictResolver;
    }

}
