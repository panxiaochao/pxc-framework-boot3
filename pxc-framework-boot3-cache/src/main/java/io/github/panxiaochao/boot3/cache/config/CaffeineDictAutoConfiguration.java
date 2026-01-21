package io.github.panxiaochao.boot3.cache.config;

import io.github.panxiaochao.boot3.cache.config.properties.CacheManagerProperties;
import io.github.panxiaochao.boot3.cache.dict.CaffeineDictResolver;
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
 * 缓存自动配置类，用于配置 Caffeine 缓存管理器
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({ CacheManagerProperties.class })
@ConditionalOnProperty(name = "spring.pxc-framework-boot3.cache.type", havingValue = "CAFFEINE")
public class CaffeineDictAutoConfiguration {

    /**
     * LOGGER CaffeineDictAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaffeineDictAutoConfiguration.class);

    @Bean
    public IDictResolver caffeineDictResolver() {
        CaffeineDictResolver caffeineDictResolver = new CaffeineDictResolver();
        // 注册 CaffeineDictResolver 到 DictResolverProvider
        DictResolverProvider.setDictResolver(caffeineDictResolver);
        LOGGER.info("配置[Dict -> CaffeineDict]成功！");
        return caffeineDictResolver;
    }

}
