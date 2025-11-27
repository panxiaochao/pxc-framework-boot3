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
package io.github.panxiaochao.boot3.weixin.api.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.github.panxiaochao.boot3.core.response.R;
import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.constants.WxConstant;
import io.github.panxiaochao.boot3.weixin.manager.IWxManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信小程序用户方法
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/ma/user")
@Tag(name = "微信小程序获取用户信息", description = "微信小程序获取用户信息")
public class WxMaUserApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WxMaService wxMaService;

    /**
     * 登陆接口
     */
    @GetMapping("/login")
    public R<Object> login(String code) {
        if (StringUtils.isBlank(code)) {
            return R.fail("empty jscode");
        }
        try {
            final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
            if (!getWxMaService().switchover(wxManager.get(WxConstant.MA_KEY))) {
                throw new IllegalArgumentException(
                        String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MA_KEY)));
            }
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            logger.info("session:{}", session);
            return R.ok(session);
        }
        catch (WxErrorException e) {
            logger.error("微信小程序登录接口出错", e);
            return R.fail("微信小程序登录接口出错");
        }
        finally {
            // 清理ThreadLocal
            WxMaConfigHolder.remove();
        }
    }

    /**
     * <p>
     * 获取用户信息接口
     */
    @GetMapping("/info")
    public R<Object> info(String sessionKey, String signature, String rawData, String encryptedData, String iv) {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMaService().switchover(wxManager.get(WxConstant.MA_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MA_KEY)));
        }
        // 用户信息校验
        if (!checkUserInfo(getWxMaService(), sessionKey, rawData, signature)) {
            return R.fail("user check failed");
        }
        // 解密用户信息
        WxMaUserInfo userInfo = getWxMaService().getUserService().getUserInfo(sessionKey, encryptedData, iv);
        // 清理ThreadLocal
        WxMaConfigHolder.remove();
        return R.ok(userInfo);
    }

    /**
     * <p>
     * 获取用户绑定手机号信息
     */
    @GetMapping("/phone")
    public R<WxMaPhoneNumberInfo> phone(String code) throws WxErrorException {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMaService().switchover(wxManager.get(WxConstant.MA_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MA_KEY)));
        }
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = getWxMaService().getUserService().getPhoneNoInfo(code);
        // 清理ThreadLocal
        WxMaConfigHolder.remove();
        return R.ok(phoneNoInfo);
    }

    /**
     * <p>
     * 用户信息校验
     */
    private boolean checkUserInfo(WxMaService wxMaService, String sessionKey, String signature, String rawData) {
        if (!getWxMaService().getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            // 清理ThreadLocal
            WxMaConfigHolder.remove();
            return false;
        }
        return true;
    }

    private WxMaService getWxMaService() {
        if (this.wxMaService == null) {
            this.wxMaService = SpringContextUtil.getBean(WxMaService.class);
        }
        return this.wxMaService;
    }

}
