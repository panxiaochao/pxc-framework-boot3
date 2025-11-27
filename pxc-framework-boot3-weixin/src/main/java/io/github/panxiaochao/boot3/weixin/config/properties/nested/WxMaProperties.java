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
package io.github.panxiaochao.boot3.weixin.config.properties.nested;

import io.github.panxiaochao.boot3.weixin.core.ma.handler.AbstractMaHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * <p>
 * 微信小程序配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
public class WxMaProperties {

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 指定key前缀.
     */
    private String keyPrefix = "wx:ma";

    /**
     * 多小程序配置
     */
    private List<MaConfig> configs;

    @Getter
    @Setter
    public static class MaConfig {

        /**
         * 设置微信小程序的 appId
         */
        private String appId;

        /**
         * 设置微信小程序的 appSecret
         */
        private String appSecret;

        /**
         * 令牌 token
         */
        private String token;

        /**
         * 消息加解密密钥 EncodingAESKey
         */
        private String aesKey;

        /**
         * 消息格式，XML或者JSON
         */
        private String msgDataFormat;

        /**
         * 是否使用稳定版 Access Token
         */
        private boolean useStableAccessToken = false;

    }

    /**
     * 配置多个消息处理器
     */
    private List<MaHandler> handlers;

    /**
     * 自定义消息处理器
     */
    @Getter
    @Setter
    public static class MaHandler {

        /**
         * 设置消息处理器
         */
        private Class<? extends AbstractMaHandler> handler;

        /**
         * 消息类型，默认 event
         */
        private String content;

    }

}
