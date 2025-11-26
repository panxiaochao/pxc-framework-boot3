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
package io.github.panxiaochao.boot3.cache.factory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;

import java.time.Duration;

/**
 * <p>
 * 通用 Caffeine 缓存工厂
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-05
 * @version 1.0
 */
public final class CaffeineCacheFactory {

    /**
     * 创建原生静态缓存类
     * @param initialCapacity 初始化空间大小
     * @param maximumSize 最大缓存空间
     * @param duration 过期时间
     * @return Cache
     */
    public static Cache<String, Object> createNativeCaffeineCache(int initialCapacity, long maximumSize,
            final Duration duration) {
        return Caffeine.newBuilder()
            // 设置过期时间
            .expireAfterWrite(duration)
            // 初始化缓存空间大小
            .initialCapacity(initialCapacity)
            // 最大的缓存条数
            .maximumSize(maximumSize)
            .build();
    }

    /**
     * Constructs a new {@code Caffeine} instance with the settings specified in
     * {@code spec}.
     * @param spec a String in the format specified by {@link CaffeineSpec}
     * @return Cache
     */
    public static Cache<String, Object> createNativeCaffeineCache(String spec) {
        Caffeine<Object, Object> cacheBuilder = Caffeine.from(spec);
        return cacheBuilder.build();
    }

}
