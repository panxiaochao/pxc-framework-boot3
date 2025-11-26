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
package io.github.panxiaochao.holiday.config;

import io.github.panxiaochao.holiday.config.properties.HolidayProperties;
import io.github.panxiaochao.holiday.core.HolidayClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * Holiday 自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-02
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(HolidayProperties.class)
public class HolidayAutoConfiguration {

    /**
     * LOGGER HolidayAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayAutoConfiguration.class);

    /**
     * Holiday client
     * @return HolidayClient
     */
    @Bean
    public HolidayClient holidayClient() {
        LOGGER.info("配置[HolidayClient]成功！");
        return new HolidayClient();
    }

}
