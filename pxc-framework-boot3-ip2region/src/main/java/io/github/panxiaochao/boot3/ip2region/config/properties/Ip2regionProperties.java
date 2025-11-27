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
package io.github.panxiaochao.boot3.ip2region.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 * IP转换地址模块配置属性类
 * </p>
 *
 * @author lypxc
 * @since 2025-10-09
 * @version 1.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.pxc-framework-boot3.ip2region", ignoreInvalidFields = true)
public class Ip2regionProperties {

    /**
     * ip2region_v4.xdb 文件路径
     */
    private String v4dbLocation;

    /**
     * ip2region_v6.xdb 文件路径
     */
    private String v6dbLocation;

}
