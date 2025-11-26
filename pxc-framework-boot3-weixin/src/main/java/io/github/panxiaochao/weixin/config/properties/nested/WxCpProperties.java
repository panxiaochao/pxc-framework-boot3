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
package io.github.panxiaochao.weixin.config.properties.nested;

import io.github.panxiaochao.weixin.core.cp.handler.AbstractCpHandler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * <p>
 * 企业号/企业微信配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
public class WxCpProperties {

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 指定key前缀.
     */
    private String keyPrefix = "wx:cp";

    /**
     * 企业号/企业微信
     */
    private List<WxCpConfig> config;

    @Getter
    @Setter
    public static class WxCpConfig {

        /**
         * 企业微信的 corpId
         */
        private String corpId;

        /**
         * 企业微信应用的 Secret
         */
        private String corpSecret;

        /**
         * 企业微信应用的 AgentId
         */
        private Integer agentId;

        /**
         * 企业微信应用的 token
         */
        private String token;

        /**
         * 企业微信应用的 EncodingAESKey
         */
        private String aesKey;

        /**
         * 微信企业号应用 会话存档私钥
         */
        private String msgAuditPriKey;

        /**
         * 微信企业号应用 会话存档类库路径
         */
        private String msgAuditLibPath;

    }

    /**
     * 配置多个消息处理器
     */
    private List<CpHandler> handlers;

    /**
     * 自定义消息处理器
     */
    @Getter
    @Setter
    public static class CpHandler {

        /**
         * 设置消息处理器
         */
        private Class<? extends AbstractCpHandler> handler;

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
