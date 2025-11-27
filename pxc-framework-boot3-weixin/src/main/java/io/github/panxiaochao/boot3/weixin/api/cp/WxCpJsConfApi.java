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
package io.github.panxiaochao.boot3.weixin.api.cp;

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.core.cp.service.WxCpMultiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 企业号/企业微信 JS Conf
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 * @version 1.0
 */
@RestController
@RequestMapping("/wx/cp/js/{corpId}/{agentId}")
@Tag(name = "企业微信JSConf", description = "企业微信JSConf")
public class WxCpJsConfApi {

    @PostMapping("/getJsConf")
    @Operation(summary = "创建调用jsapi签名", description = "创建调用jsapi签名", method = "POST")
    @Parameter(name = "corpId", description = "企业微信ID")
    @Parameter(name = "agentId", description = "企业微信AgentId")
    @Parameter(name = "uri", description = "url")
    public Map<String, Object> getJsConf(@PathVariable String corpId, @PathVariable Integer agentId, String uri)
            throws WxErrorException {
        final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
        Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
        WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(
                    String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
        }
        WxJsapiSignature wxJsapiSignature = wxCpService.createJsapiSignature(uri);
        String signature = wxJsapiSignature.getSignature();
        String nonceStr = wxJsapiSignature.getNonceStr();
        long timestamp = wxJsapiSignature.getTimestamp();

        Map<String, Object> res = new HashMap<>();
        // 必填，企业微信的corpID
        res.put("appId", corpId);
        // 必填，生成签名的时间戳
        res.put("timestamp", timestamp);
        // 必填，生成签名的随机串
        res.put("nonceStr", nonceStr);
        // 必填，签名，见 附录-JS-SDK使用权限签名算法
        res.put("signature", signature);
        return res;
    }

}
