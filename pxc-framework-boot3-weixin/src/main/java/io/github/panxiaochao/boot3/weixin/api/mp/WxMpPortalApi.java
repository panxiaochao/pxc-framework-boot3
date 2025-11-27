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
package io.github.panxiaochao.boot3.weixin.api.mp;

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.constants.WxConstant;
import io.github.panxiaochao.boot3.weixin.manager.IWxManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
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

/**
 * 验证消息 微信服务器
 *
 * @author Lypxc
 * @since 2024-12-11
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/mp/portal")
@Tag(name = "微信公众号接入方法", description = "微信公众号接入方法")
public class WxMpPortalApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String ENC_TYPE_AES = "aes";

    private WxMpService wxMpService;

    @GetMapping(produces = "text/plain;charset=utf-8")
    @Operation(summary = "微信服务器的认证消息接口", description = "公众号接入开发模式时腾讯调用此接口握手", method = "GET")
    @Parameter(name = "signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    @Parameter(name = "echostr", description = "随机字符串")
    public String authGet(@RequestParam(name = "signature") String signature,
            @RequestParam(name = "timestamp") String timestamp, @RequestParam(name = "nonce") String nonce,
            @RequestParam(name = "echostr") String echostr) {
        logger.info("接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMpService().switchover(wxManager.get(WxConstant.MP_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MP_KEY)));
        }
        if (getWxMpService().checkSignature(timestamp, nonce, signature)) {
            WxMpConfigStorageHolder.remove();
            return echostr;
        }
        WxMpConfigStorageHolder.remove();
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    @Operation(summary = "微信各类消息", description = "公众号接入开发模式后才有效", method = "POST")
    @Parameter(name = "signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    @Parameter(name = "openid", description = "微信粉丝用户ID")
    public String post(HttpServletRequest request, @RequestParam("signature") String signature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
            @RequestParam("openid") String openid,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) throws IOException {
        byte[] buffer = IOUtils.toByteArray(request.getInputStream());
        logger.info(
                "接收微信请求：openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ",
                openid, signature, encType, msgSignature, timestamp, nonce, new String(buffer, StandardCharsets.UTF_8));
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        if (!getWxMpService().switchover(wxManager.get(WxConstant.MP_KEY))) {
            throw new IllegalArgumentException(
                    String.format("未找到对应appid=[%s]的配置，请核实！", wxManager.get(WxConstant.MP_KEY)));
        }
        if (!getWxMpService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        String out = null;
        // 明文传输的消息
        if (encType == null) {
            InputStream in = new ByteArrayInputStream(buffer);
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(in);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                WxMpConfigStorageHolder.remove();
                return "";
            }
            out = outMessage.toXml();
        }
        // aes加密的消息
        else if (ENC_TYPE_AES.equalsIgnoreCase(encType)) {
            InputStream in = new ByteArrayInputStream(buffer);
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(in, getWxMpService().getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            logger.info("消息解密后内容为：{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                WxMpConfigStorageHolder.remove();
                return "";
            }
            out = outMessage.toEncryptedXml(getWxMpService().getWxMpConfigStorage());
        }
        WxMpConfigStorageHolder.remove();
        logger.info("组装回复信息：{}", out);
        return out;
    }

    /**
     * 路由消息
     * @param message message
     * @return WxMpXmlOutMessage
     */
    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            final WxMpMessageRouter messageRouter = SpringContextUtil.getBean(WxMpMessageRouter.class);
            return messageRouter.route(message);
        }
        catch (Exception e) {
            logger.error("路由消息时出现异常！", e);
        }
        return null;
    }

    private WxMpService getWxMpService() {
        if (this.wxMpService == null) {
            this.wxMpService = SpringContextUtil.getBean(WxMpService.class);
        }
        return this.wxMpService;
    }

}
