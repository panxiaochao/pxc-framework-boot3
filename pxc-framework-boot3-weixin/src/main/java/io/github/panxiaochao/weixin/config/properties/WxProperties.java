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
package io.github.panxiaochao.weixin.config.properties;

import io.github.panxiaochao.weixin.config.properties.nested.WxChannelProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxCpProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxMaProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxMpProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxOpenProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxPayProperties;
import io.github.panxiaochao.weixin.enums.HttpClientType;
import io.github.panxiaochao.weixin.enums.StorageType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 *
 * <p>
 * 微信全局配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.pxc-framework-boot3.wx", ignoreInvalidFields = true)
public class WxProperties {

    /**
     * 存储类型.
     */
    private StorageType storageType = StorageType.Memory;

    /**
     * http客户端类型.
     */
    private HttpClientType httpClientType = HttpClientType.HttpClient;

    /**
     * 微信小程序
     **/
    @NestedConfigurationProperty
    private WxMaProperties ma = new WxMaProperties();

    /**
     * 微信公众号
     **/
    @NestedConfigurationProperty
    private WxMpProperties mp = new WxMpProperties();

    /**
     * 企业号/企业微信
     **/
    @NestedConfigurationProperty
    private WxCpProperties cp = new WxCpProperties();

    /**
     * 微信支付
     **/
    @NestedConfigurationProperty
    private WxPayProperties pay = new WxPayProperties();

    /**
     * 微信开放平台
     **/
    @NestedConfigurationProperty
    private WxOpenProperties open = new WxOpenProperties();

    /**
     * 微信视频号
     **/
    @NestedConfigurationProperty
    private WxChannelProperties channel = new WxChannelProperties();

}
