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
package io.github.panxiaochao.boot3.cache.config;

import io.github.panxiaochao.boot3.cache.config.properties.CacheManagerProperties;
import io.github.panxiaochao.boot3.cache.constants.CacheManagerType;
import io.github.panxiaochao.boot3.cache.core.PlusCaffeineCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 缓存自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-01
 */
@EnableConfigurationProperties({ CacheManagerProperties.class })
@AutoConfiguration(before = CacheAutoConfiguration.class)
public class CacheManagerAutoConfiguration {

    /**
     * LOGGER CacheManagerAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheManagerAutoConfiguration.class);

    /**
     * 自定义 CacheManager 缓存管理器
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(final CacheManagerProperties cacheManagerProperties) {
        if (CacheManagerType.CAFFEINE.equals(cacheManagerProperties.getCacheType())) {
            // 使用自定义 PlusCaffeineCacheManager 缓存管理器
            PlusCaffeineCacheManager caffeineCacheManager = new PlusCaffeineCacheManager();
            String specification = cacheManagerProperties.getCaffeine().getSpec();
            if (StringUtils.hasText(specification)) {
                caffeineCacheManager.setCacheSpecification(specification);
            }
            LOGGER.info("配置[Cache -> Caffeine]成功！");
            return caffeineCacheManager;
        }
        else if (CacheManagerType.REDIS.equals(cacheManagerProperties.getCacheType())) {
            Class<?> cacheManagerClass = loadClass("io.github.panxiaochao.boot3.redis.cache.PlusRedissonCacheManager");
            if (cacheManagerClass != null) {
                try {
                    LOGGER.info("配置[Cache -> Redis]成功！");
                    return (CacheManager) cacheManagerClass.getDeclaredConstructor().newInstance();
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                LOGGER.error("[pxc-framework-redis] is not dependency, will use simple cache!");
            }
        }
        LOGGER.info("配置[Cache -> Simple]成功！");
        return new ConcurrentMapCacheManager();
    }

    public Class<?> loadClass(String className) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader != null) {
                // 尝试找到已加载的类
                return classLoader.loadClass(className);
            }
            // 如果上下文类加载器为空，则使用系统类加载器
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            // 类没有找到，认为类没有加载
            return null;
        }
    }

}
