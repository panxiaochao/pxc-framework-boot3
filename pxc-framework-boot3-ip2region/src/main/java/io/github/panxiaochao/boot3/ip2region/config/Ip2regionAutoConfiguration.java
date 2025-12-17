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
package io.github.panxiaochao.boot3.ip2region.config;

import io.github.panxiaochao.boot3.ip2region.config.properties.Ip2regionProperties;
import io.github.panxiaochao.boot3.ip2region.core.Ip2regionClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * IP转换地址模块自动配置类
 * </p>
 *
 * @author lypxc
 * @since 2025-10-09
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(Ip2regionProperties.class)
public class Ip2regionAutoConfiguration {

    /**
     * LOGGER Ip2regionAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Ip2regionAutoConfiguration.class);

    /**
     * 创建 Ip2regionClient 对象
     * @param ip2regionProperties ip2regionProperties
     * @return Ip2regionClient
     */
    @Bean
    public Ip2regionClient ip2regionClient(Ip2regionProperties ip2regionProperties) {
        LOGGER.info("配置[Ip2regionClient]成功");
        return new Ip2regionClient(ip2regionProperties);
    }

}
