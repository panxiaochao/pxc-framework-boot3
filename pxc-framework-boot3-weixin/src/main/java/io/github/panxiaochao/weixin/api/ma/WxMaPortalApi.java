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
package io.github.panxiaochao.weixin.api.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.constants.WxConstant;
import io.github.panxiaochao.weixin.manager.IWxManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 * 验证微信小程序握手签名
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/ma/portal")
@Tag(name = "微信小程序接入方法", description = "微信小程序接入方法")
public class WxMaPortalApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WxMaService wxMaService;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        logger.info("接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMaService().switchover(wxManager.get(WxConstant.MA_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MA_KEY)));
        }
        if (getWxMaService().checkSignature(timestamp, nonce, signature)) {
            // 清理ThreadLocal
            WxMaConfigHolder.remove();
            return echostr;
        }
        // 清理ThreadLocal
        WxMaConfigHolder.remove();
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(HttpServletRequest request,
            @RequestParam(name = "msg_signature", required = false) String msgSignature,
            @RequestParam(name = "encrypt_type", required = false) String encryptType,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) throws IOException {
        byte[] buffer = IOUtils.toByteArray(request.getInputStream());
        logger.info(
                "接收微信请求：[msg_signature=[{}], encrypt_type=[{}], signature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[{}] ",
                msgSignature, encryptType, signature, timestamp, nonce, new String(buffer, StandardCharsets.UTF_8));
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMaService().switchover(wxManager.get(WxConstant.MA_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MA_KEY)));
        }
        final boolean isJson = Objects.equals(getWxMaService().getWxMaConfig().getMsgDataFormat(),
                WxMaConstants.MsgDataFormat.JSON);

        if (StringUtils.isBlank(encryptType)) {
            String bufferString = IOUtils.toString(buffer, StandardCharsets.UTF_8.name());
            // 明文传输的消息
            WxMaMessage inMessage;
            if (isJson) {
                inMessage = WxMaMessage.fromJson(bufferString);
            }
            else {
                // xml
                inMessage = WxMaMessage.fromXml(bufferString);
            }
            this.route(inMessage);
            // 清理ThreadLocal
            WxMaConfigHolder.remove();
            return "success";
        }
        if ("aes".equals(encryptType)) {
            InputStream in = new ByteArrayInputStream(buffer);
            Objects.requireNonNull(in, "微信请求流内容为空！");
            // 是aes加密的消息
            WxMaMessage inMessage;
            if (isJson) {
                inMessage = WxMaMessage.fromEncryptedJson(in, getWxMaService().getWxMaConfig());
            }
            else {
                // xml
                inMessage = WxMaMessage.fromEncryptedXml(in, getWxMaService().getWxMaConfig(), timestamp, nonce,
                        msgSignature);
            }

            this.route(inMessage);
            // 清理ThreadLocal
            WxMaConfigHolder.remove();
            return "success";
        }
        // 清理ThreadLocal
        WxMaConfigHolder.remove();
        throw new RuntimeException("不可识别的加密类型：" + encryptType);
    }

    private void route(WxMaMessage message) {
        try {
            final WxMaMessageRouter wxMaMessageRouter = SpringContextUtil.getBean(WxMaMessageRouter.class);
            wxMaMessageRouter.route(message);
        }
        catch (Exception e) {
            logger.error("路由消息时出现异常！", e);
        }
    }

    private WxMaService getWxMaService() {
        if (this.wxMaService == null) {
            this.wxMaService = SpringContextUtil.getBean(WxMaService.class);
        }
        return this.wxMaService;
    }

}
