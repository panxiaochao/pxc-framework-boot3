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
package io.github.panxiaochao.weixin.api.cp;

import io.github.panxiaochao.core.response.R;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.core.cp.service.WxCpMultiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpOauth2UserInfo;
import me.chanjar.weixin.cp.bean.WxCpUserDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>
 * 企业微信网页授权相关
 * </p>
 *
 * @author Lypxc
 * @since 2025-04-14
 * @version 1.0
 */
@RestController
@RequestMapping("/wx/cp/auth/{corpId}/{agentId}")
@RequiredArgsConstructor
@Tag(name = "企业微信网页授权", description = "企业微信网页授权")
public class WxCpAuthApi {

    /**
     * 获取授权登陆的URL
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @param scope 静默:snsapi_base, 带信息授权:snsapi_privateinfo
     * @param state state
     */
    @GetMapping("/authorizationUrl")
    @Operation(summary = "构造oauth2授权的url连接", description = "scope为snsapi_base，snsapi_privateinfo", method = "GET")
    public R<String> authorizationUrl(@PathVariable String corpId, @PathVariable Integer agentId, String redirectUri,
            String scope, String state) {
        final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
        Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
        WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(
                    String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
        }
        String authorizationUrl = wxCpService.getOauth2Service().buildAuthorizationUrl(redirectUri, state, scope);
        return R.ok(authorizationUrl);
    }

    /**
     * 静默授权第二步，回调拿到code，根据业务处理，拿到用户登录信息
     */
    @GetMapping("/codeToUserInfo")
    @ResponseBody
    public R<WxCpOauth2UserInfo> codeToUserInfo(@PathVariable String corpId, @PathVariable Integer agentId,
            @RequestParam("code") String code) {
        WxCpOauth2UserInfo cpOauth2UserInfo;
        try {
            final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
            Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
            WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
            if (wxCpService == null) {
                throw new IllegalArgumentException(
                        String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
            }
            cpOauth2UserInfo = wxCpService.getOauth2Service().getAuthUserInfo(code);
        }
        catch (WxErrorException e) {
            return R.fail(e.getError().getErrorCode(), e.getError().getErrorMsg());
        }
        return R.ok(cpOauth2UserInfo);
    }

    /**
     * 静默授权第二步，回调拿到code，根据业务处理，拿到用户敏感信息
     */
    @GetMapping("/codeToUserDetail")
    @ResponseBody
    public R<WxCpUserDetail> codeToUserDetail(@PathVariable String corpId, @PathVariable Integer agentId,
            @RequestParam("code") String code) {
        WxCpUserDetail userDetail;
        try {
            final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
            Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
            WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
            if (wxCpService == null) {
                throw new IllegalArgumentException(
                        String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
            }
            WxCpOauth2UserInfo cpOauth2UserInfo = wxCpService.getOauth2Service().getAuthUserInfo(code);
            String userTicket = cpOauth2UserInfo.getUserTicket();
            // 获取访问用户敏感信息
            userDetail = wxCpService.getOauth2Service().getUserDetail(userTicket);
        }
        catch (WxErrorException e) {
            return R.fail(e.getError().getErrorCode(), e.getError().getErrorMsg());
        }
        return R.ok(userDetail);
    }

}
