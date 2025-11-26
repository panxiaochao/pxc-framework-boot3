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
package io.github.panxiaochao.weixin.api.mp;

import io.github.panxiaochao.core.response.R;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.api.mp.request.WxH5OAuthForm;
import io.github.panxiaochao.weixin.constants.WxConstant;
import io.github.panxiaochao.weixin.entity.WxUser;
import io.github.panxiaochao.weixin.manager.IWxManager;
import io.github.panxiaochao.weixin.utils.SHA1Util;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 微信网页授权相关
 *
 * @author Lypxc
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/wx/mp/auth")
@RequiredArgsConstructor
@Tag(name = "微信网页授权", description = "微信网页授权")
public class WxMpAuthApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WxMpService wxMpService;

    /**
     * 获取授权登陆的URL
     * @param redirectUri 用户授权完成后的重定向链接，无需urlencode, 方法内会进行encode
     * @param scope 静默:snsapi_base, 带信息授权:snsapi_userinfo
     * @param state state
     * @return 构造oauth2授权的url连接
     */
    @GetMapping("/authorizationUrl")
    @Operation(summary = "构造oauth2授权的url连接", description = "scope为snsapi_base，snsapi_userinfo", method = "GET")
    public R<String> authorizationUrl(String redirectUri, String scope, String state) {
        final WxMpService wxMpService = SpringContextUtil.getBean(WxMpService.class);
        Objects.requireNonNull(wxMpService, "请配置wxMpService类！");
        String authorizationUrl = wxMpService.getOAuth2Service().buildAuthorizationUrl(redirectUri, scope, state);
        return R.ok(authorizationUrl);
    }

    /**
     * 使用微信授权code换取openid
     * @param form form
     * @return R
     */
    @PostMapping("/codeToOpenid")
    @CrossOrigin
    @Operation(summary = "网页登录-code换取openid", description = "scope为snsapi_base", method = "POST")
    public R<String> codeToOpenid(@RequestBody WxH5OAuthForm form) {
        try {
            final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
            if (!getWxMpService().switchover(wxManager.get(WxConstant.MP_KEY))) {
                throw new IllegalArgumentException(
                        String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MP_KEY)));
            }
            WxOAuth2AccessToken token = getWxMpService().getOAuth2Service().getAccessToken(form.getCode());
            return R.ok(token.getOpenId());
        }
        catch (WxErrorException e) {
            logger.error("code换取openid失败", e);
            return R.fail(e.getError().getErrorMsg());
        }
    }

    /**
     * 使用微信授权code换取用户信息(需scope为 snsapi_userinfo)
     * @param response response
     * @param form form
     * @return R
     */
    @PostMapping("/codeToUserInfo")
    @CrossOrigin
    @Operation(summary = "网页登录-code换取用户信息", description = "需scope为 snsapi_userinfo", method = "POST")
    public R<WxUser> codeToUserInfo(HttpServletResponse response, @RequestBody WxH5OAuthForm form) {
        try {
            final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
            if (!getWxMpService().switchover(wxManager.get(WxConstant.MP_KEY))) {
                throw new IllegalArgumentException(
                        String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MP_KEY)));
            }
            WxOAuth2AccessToken token = getWxMpService().getOAuth2Service().getAccessToken(form.getCode());
            WxOAuth2UserInfo userInfo = getWxMpService().getOAuth2Service().getUserInfo(token, "zh_CN");
            WxMpConfigStorageHolder.remove();
            return R.ok(new WxUser(userInfo, wxManager.get(WxConstant.MP_KEY)));
        }
        catch (WxErrorException e) {
            logger.error("code换取用户信息失败", e);
            return R.fail(e.getError().getErrorMsg());
        }
    }

    /**
     * 获取微信分享的签名配置 允许跨域（只有微信公众号添加了js安全域名的网站才能加载微信分享，故这里不对域名进行校验）
     * @param response response
     * @param request request
     * @return R
     */
    @GetMapping("/getShareSignature")
    @Operation(summary = "获取微信分享的签名配置", description = "微信公众号添加了js安全域名的网站才能加载微信分享", method = "GET")
    public R<Map<String, String>> getShareSignature(HttpServletRequest request, HttpServletResponse response)
            throws WxErrorException {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMpService().switchover(wxManager.get(WxConstant.MP_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MP_KEY)));
        }
        // 1.拼接url（当前网页的URL，不包含#及其后面部分）
        String wxShareUrl = request.getHeader("wx-client-href");
        if (!StringUtils.hasText(wxShareUrl)) {
            WxMpConfigStorageHolder.remove();
            return R.fail("header中缺少wx-client-href参数，微信分享加载失败");
        }
        wxShareUrl = wxShareUrl.split("#")[0];
        Map<String, String> wxMap = new TreeMap<>();
        String wxNoncestr = UUID.randomUUID().toString();
        String wxTimestamp = (System.currentTimeMillis() / 1000) + "";
        wxMap.put("noncestr", wxNoncestr);
        wxMap.put("timestamp", wxTimestamp);
        wxMap.put("jsapi_ticket", getWxMpService().getJsapiTicket());
        wxMap.put("url", wxShareUrl);

        // 加密获取signature
        StringBuilder wxBaseString = new StringBuilder();
        wxMap.forEach((key, value) -> wxBaseString.append(key).append("=").append(value).append("&"));
        String wxSignString = wxBaseString.substring(0, wxBaseString.length() - 1);
        // signature
        String wxSignature = SHA1Util.sha1(wxSignString);
        Map<String, String> resMap = new TreeMap<>();
        resMap.put("appId", WxMpConfigStorageHolder.get());
        resMap.put("wxTimestamp", wxTimestamp);
        resMap.put("wxNoncestr", wxNoncestr);
        resMap.put("wxSignature", wxSignature);
        WxMpConfigStorageHolder.remove();
        return R.ok(resMap);
    }

    private WxMpService getWxMpService() {
        if (this.wxMpService == null) {
            this.wxMpService = SpringContextUtil.getBean(WxMpService.class);
        }
        return this.wxMpService;
    }

}
