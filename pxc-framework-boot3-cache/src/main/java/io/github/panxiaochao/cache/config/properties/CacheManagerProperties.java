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
package io.github.panxiaochao.cache.config.properties;

import io.github.panxiaochao.cache.constants.CacheManagerType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * Cache 自定义属性
 * </p>
 *
 * @author Lypxc
 * @since 2023-06-27
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.pxc-framework.cache", ignoreInvalidFields = true)
public class CacheManagerProperties {

    /**
     * 缓存类型: caffeine（默认）、REDIS、SIMPLE
     */
    private CacheManagerType cacheType = CacheManagerType.CAFFEINE;

    /**
     *
     */
    private final Caffeine caffeine = new Caffeine();

    @Getter
    @Setter
    public static class Caffeine {

        /**
         * The spec to use to create caches. See CaffeineSpec for more details on the spec
         * format.
         */
        private String spec;

    }

}
