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

/**
 *
 * <p>
 * 微信支付配置属性
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 */
@Getter
@Setter
public class WxPayProperties {

    /**
     * 是否开启
     */
    private Boolean enabled;

    /**
     * 微信支付配置，单个实例
     */
    private WxPayConfig config;

    @Getter
    @Setter
    public static class WxPayConfig {

        /**
         * 设置微信公众号或者小程序等的 appid
         */
        private String appId;

        /**
         * 微信支付商户号
         */
        private String mchId;

        /**
         * 微信支付商户密钥
         */
        private String mchKey;

        /**
         * 服务商模式下的子商户公众账号ID，普通模式请不要配置，请在配置文件中将对应项删除
         */
        private String subAppId;

        /**
         * 服务商模式下的子商户号，普通模式请不要配置，最好是请在配置文件中将对应项删除
         */
        private String subMchId;

        /**
         * apiclient_cert.p12 文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
         */
        private String keyPath;

        /**
         * 微信支付分serviceId
         */
        private String serviceId;

        /**
         * 证书序列号
         */
        private String certSerialNo;

        /**
         * apiV3秘钥
         */
        private String apiv3Key;

        /**
         * 微信支付分回调地址
         */
        private String payScoreNotifyUrl;

        /**
         * apiv3 商户apiclient_key.pem 文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
         */
        private String privateKeyPath;

        /**
         * apiv3 商户apiclient_cert.pem 文件的绝对路径，或者如果放在项目中，请以classpath:开头指定
         */
        private String privateCertPath;

    }

}
