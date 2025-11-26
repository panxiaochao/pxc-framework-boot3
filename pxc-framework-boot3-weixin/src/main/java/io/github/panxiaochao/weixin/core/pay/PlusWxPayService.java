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
package io.github.panxiaochao.weixin.core.pay;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * WxPayService 初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
public class PlusWxPayService {

    private final WxProperties wxProperties;

    /**
     * 初始化WxPayService
     * @return WxPayService
     */
    public WxPayService build() {
        if (!wxProperties.getPay().getEnabled()) {
            return new WxPayServiceImpl();
        }
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(StringUtils.trimToNull(wxProperties.getPay().getConfig().getAppId()));
        payConfig.setMchId(StringUtils.trimToNull(wxProperties.getPay().getConfig().getMchId()));
        payConfig.setMchKey(StringUtils.trimToNull(wxProperties.getPay().getConfig().getMchKey()));
        payConfig.setSubAppId(StringUtils.trimToNull(wxProperties.getPay().getConfig().getSubAppId()));
        payConfig.setSubMchId(StringUtils.trimToNull(wxProperties.getPay().getConfig().getSubMchId()));
        payConfig.setKeyPath(StringUtils.trimToNull(wxProperties.getPay().getConfig().getKeyPath()));
        // 以下是Api v3以及支付分相关
        payConfig.setServiceId(StringUtils.trimToNull(wxProperties.getPay().getConfig().getServiceId()));
        payConfig
            .setPayScoreNotifyUrl(StringUtils.trimToNull(wxProperties.getPay().getConfig().getPayScoreNotifyUrl()));
        payConfig.setPrivateKeyPath(StringUtils.trimToNull(wxProperties.getPay().getConfig().getPrivateKeyPath()));
        payConfig.setPrivateCertPath(StringUtils.trimToNull(wxProperties.getPay().getConfig().getPrivateCertPath()));
        payConfig.setCertSerialNo(StringUtils.trimToNull(wxProperties.getPay().getConfig().getCertSerialNo()));
        payConfig.setApiV3Key(StringUtils.trimToNull(wxProperties.getPay().getConfig().getApiv3Key()));
        // 可以指定是否使用沙箱环境
        payConfig.setUseSandboxEnv(false);
        WxPayService wxPayService = new WxPayServiceImpl();
        wxPayService.setConfig(payConfig);
        return wxPayService;
    }

}
