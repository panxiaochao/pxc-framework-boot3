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
package io.github.panxiaochao.boot3.web.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 自定属性配置
 * </p>
 *
 * @author Lypxc
 * @since 2023-07-17
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.pxc-framework", ignoreInvalidFields = true)
public class WebProperties {

    /**
     * Xss 配置
     */
    private Xss xss = new Xss();

    /**
     * Cors 配置
     */
    private Cors cors = new Cors();

    /**
     * OkHttp 配置
     */
    private OkHttp okHttp = new OkHttp();

    /**
     * RestTemplate 增强配置
     */
    private RestTemplate restTemplate = new RestTemplate();

    @Getter
    @Setter
    public static class Cors {

        /**
         * 是否开启 跨域Cors
         */
        private Boolean enabled;

    }

    @Getter
    @Setter
    public static class Xss {

        /**
         * 是否开启 XSS
         */
        private Boolean enabled;

        /**
         * 排除的URL列表
         */
        private List<String> excludeUrls = Collections.emptyList();

    }

    @Getter
    @Setter
    public static class OkHttp {

        /**
         * 连接超时，默认 10 秒，0 表示没有超时限制
         */
        private Integer connectTimeout = 10;

        /**
         * 响应超时，默认 10 秒，0 表示没有超时限制
         */
        private Integer readTimeout = 10;

        /**
         * 写超时，默认 10 秒，0 表示没有超时限制
         */
        private Integer writeTimeout = 10;

        /**
         * 连接池中整体的空闲连接的最大数量，默认 5 个连接数
         */
        private Integer maxIdleConnections = 10;

        /**
         * 连接空闲时间最大时间，单位秒，默认 300 秒
         */
        private Long keepAliveDuration = 300L;

    }

    @Getter
    @Setter
    public static class RestTemplate {

        /**
         * 是否开启增强 RestTemplate
         */
        private Boolean enabled;

        /**
         * 连接超时，默认 10 秒，0 表示没有超时限制
         */
        private long connectTimeout = 10;

        /**
         * 响应超时，默认 10 秒，0 表示没有超时限制
         */
        private long readTimeout = 10;

        /**
         * 连接空闲时间最大时间，单位秒，默认 300 秒
         */
        private long timeToLive = 300;

        /**
         * 重试次数，默认 3 次重试
         */
        private Integer maxRetries = 3;

        /**
         * 重试间隔时间，默认 3 秒
         */
        private long retryInterval = 3;

        /**
         * 连接池中整体的空闲连接的最大数量，默认 200 个连接数
         */
        private Integer maxConnTotal = 200;

        /**
         * 连接池中每个路由的最大连接数，默认 50 个连接数
         */
        private Integer maxConnPerRoute = 50;

    }

}
