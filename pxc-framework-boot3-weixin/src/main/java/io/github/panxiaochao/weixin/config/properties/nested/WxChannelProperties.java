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

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * <p>
 * 微信视频号 配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
public class WxChannelProperties {

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 指定key前缀.
     */
    private String keyPrefix = "wx:channel";

    /**
     * 视频号
     */
    private List<WxChannelConfig> config;

    @Getter
    @Setter
    public static class WxChannelConfig {

        /**
         * 设置微信视频号的 appid.
         */
        private String appId;

        /**
         * 设置微信视频号的 secret.
         */
        private String secret;

        /**
         * 设置微信视频号的 token.
         */
        private String token;

        /**
         * 设置微信视频号的 EncodingAESKey.
         */
        private String aesKey;

        /**
         * 是否使用稳定版 Access Token
         */
        private boolean useStableAccessToken = false;

    }

}
