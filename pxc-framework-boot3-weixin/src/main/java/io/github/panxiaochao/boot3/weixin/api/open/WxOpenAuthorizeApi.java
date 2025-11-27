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
package io.github.panxiaochao.boot3.weixin.api.open;

import io.github.panxiaochao.boot3.core.response.R;
import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.api.mp.request.WxH5OAuthForm;
import io.github.panxiaochao.boot3.weixin.config.properties.WxProperties;
import io.github.panxiaochao.boot3.weixin.entity.WxUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenMpService;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerListResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 微信开发平台/微信公众号开发者授权
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-16
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/open/authorize")
public class WxOpenAuthorizeApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WxProperties wxProperties;

    private WxOpenService wxOpenService;

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
        final WxOpenMpService wxOpenMpService = getWxOpenMpService();
        String authorizationUrl = wxOpenMpService.getOAuth2Service().buildAuthorizationUrl(redirectUri, scope, state);
        return R.ok(authorizationUrl);
    }

    /**
     * 使用微信授权code换取openid
     * @param response response
     * @param form form
     * @return R
     */
    @PostMapping("/codeToOpenid")
    @CrossOrigin
    @Operation(summary = "网页登录-code换取openid", description = "scope为snsapi_base", method = "POST")
    public R<String> codeToOpenid(HttpServletResponse response, @RequestBody WxH5OAuthForm form) {
        try {
            final WxOpenMpService wxOpenMpService = getWxOpenMpService();
            WxOAuth2AccessToken token = wxOpenMpService.getOAuth2Service().getAccessToken(form.getCode());
            String openid = token.getOpenId();
            return R.ok(openid);
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
            final WxOpenMpService wxOpenMpService = getWxOpenMpService();
            WxOAuth2AccessToken token = wxOpenMpService.getOAuth2Service().getAccessToken(form.getCode());
            WxOAuth2UserInfo userInfo = wxOpenMpService.getOAuth2Service().getUserInfo(token, "zh_CN");
            return R.ok(new WxUser(userInfo, wxProperties.getOpen().getMpAppId()));
        }
        catch (WxErrorException e) {
            logger.error("code换取用户信息失败", e);
            return R.fail(e.getError().getErrorMsg());
        }
    }

    @GetMapping("/goto_auth_url_show")
    public String gotoPreAuthUrlShow() {
        return "<a href='goto_auth_url'>点击前往微信开放平台授权</a>";
    }

    /**
     * <p>
     * 获取微信公众号授权链接
     * </p>
     */
    @GetMapping("/goto_auth_url")
    public void gotoPreAuthUrl(HttpServletRequest request, HttpServletResponse response) {
        String host = request.getHeader("host");
        try {
            String url = wxProperties.getOpen().getAuthorizeUrl() + "/wx/open/authorize/jump";
            url = wxOpenService.getWxOpenComponentService().getPreAuthUrl(url);
            // 添加来源，解决302跳转来源丢失的问题
            response.addHeader("Referer", "http://" + host);
            response.sendRedirect(url);
        }
        catch (WxErrorException | IOException e) {
            logger.error("gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <h2>公众号授权回调</h2>
     * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/api/api_get_authorizer_info.html#%E5%85%AC%E4%BC%97%E5%8F%B7%E8%AE%A4%E8%AF%81%E7%B1%BB%E5%9E%8B
     */
    @GetMapping("/jump")
    public String jump(@RequestParam("auth_code") String authorizationCode) {
        try {
            WxOpenQueryAuthResult queryAuthResult = wxOpenService.getWxOpenComponentService()
                .getQueryAuth(authorizationCode);
            logger.info("getQueryAuth, {}", queryAuthResult);
            // 这里要进行缓存才行的吧
            String appId = queryAuthResult.getAuthorizationInfo().getAuthorizerAppid();
            String reFreshToken = queryAuthResult.getAuthorizationInfo().getAuthorizerRefreshToken();
            wxOpenService.getWxOpenConfigStorage().setAuthorizerRefreshToken(appId, reFreshToken);
            return "已成功授权，请返回管理台查看。";
        }
        catch (WxErrorException e) {
            logger.error("gotoPreAuthUrl", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get_authorizer_info")
    public WxOpenAuthorizerInfoResult getAuthorizerInfo(@RequestParam String appId) {
        try {
            return wxOpenService.getWxOpenComponentService().getAuthorizerInfo(appId);
        }
        catch (WxErrorException e) {
            logger.error("getAuthorizerInfo", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化RefreshToken吗，存在以下情况：
     * <p>
     * 在迁系统或者重启Redis缓存失效的情况下，初始化RefreshToken丢失，报错：{"errcode":61023,"errmsg":"refresh_token
     * is invalid rid: 66277be0-02270739-5ff9c0e1"}
     * </p>
     * <p>
     * 接口参考：https://developers.weixin.qq.com/doc/oplatform/openApi/OpenApiDoc/authorization-management/getAuthorizerList.html
     * </p>
     */
    @GetMapping("/initRefreshToken")
    public R<String> initRefreshToken() {
        try {
            WxOpenAuthorizerListResult wxOpenAuthorizerListResult = wxOpenService.getWxOpenComponentService()
                .getAuthorizerList(0, 10);
            if (null != wxOpenAuthorizerListResult) {
                if (wxOpenAuthorizerListResult.getTotalCount() != 0) {
                    String authorizerAppid = wxOpenAuthorizerListResult.getList()
                        .stream()
                        .map(m -> m.get("authorizer_appid"))
                        .collect(Collectors.joining(","));
                    return R.ok("已授权账号的appid已刷新RefreshToken", authorizerAppid);
                }
                return R.fail("授权的账号总数为 0");
            }
            return R.fail("wxOpenAuthorizerListResult is null");
        }
        catch (WxErrorException e) {
            logger.error("initRefreshToken", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtain the WxOpenMpService
     * @return WxOpenMpService
     */
    private WxOpenMpService getWxOpenMpService() {
        if (this.wxOpenService == null) {
            this.wxOpenService = SpringContextUtil.getBean(WxOpenService.class);
        }
        Objects.requireNonNull(this.wxOpenService, () -> "请正确配置[WxOpenService]相关配置");
        return this.wxOpenService.getWxOpenComponentService()
            .getWxMpServiceByAppid(wxProperties.getOpen().getMpAppId());
    }

}
