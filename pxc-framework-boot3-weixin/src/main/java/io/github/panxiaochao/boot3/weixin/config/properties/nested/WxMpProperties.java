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

import io.github.panxiaochao.boot3.weixin.core.mp.handler.AbstractMpHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * <p>
 * 微信公众号配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
public class WxMpProperties {

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 指定key前缀.
     */
    private String keyPrefix = "wx:mp";

    /**
     * 多个公众号配置信息 <pre>
     *     configs:
     *       - appId: 1111 # 第一个公众号的appid
     *         secret: 1111
     *         token: 111
     *         aesKey: 111
     *       - appId: 2222 # 第二个公众号的appid，以下同上
     *         secret: 2222
     *         token: 2222
     *         aesKey: 2222
     * </pre>
     */
    private List<MpConfig> configs;

    /**
     * 基础配置信息
     */
    @Getter
    @Setter
    public static class MpConfig {

        /**
         * 设置微信公众号的 appId
         */
        private String appId;

        /**
         * 设置微信公众号的 appSecret
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
         * 是否使用稳定版 Access Token, 默认true
         */
        private boolean useStableAccessToken = false;

    }

    /**
     * 配置多个消息处理器
     */
    private List<MpHandler> handlers;

    /**
     * 自定义消息处理器
     */
    @Getter
    @Setter
    public static class MpHandler {

        /**
         * 设置消息处理器
         */
        private Class<? extends AbstractMpHandler> handler;

        /**
         * 消息类型，默认 event
         */
        private String msgType;

        /**
         * 事件类型：比如有订阅、关注等等，具体值参考官方Demo示例
         */
        private String event;

    }

}
