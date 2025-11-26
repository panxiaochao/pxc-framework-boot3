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
package io.github.panxiaochao.weixin.api.channel;

import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.core.channel.service.WxChannelMultiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.channel.api.WxChannelService;
import me.chanjar.weixin.channel.message.WxChannelMessage;
import me.chanjar.weixin.channel.message.WxChannelMessageRouter;
import me.chanjar.weixin.channel.util.JsonUtils;
import me.chanjar.weixin.channel.util.WxChCryptUtils;
import me.chanjar.weixin.channel.util.XmlUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 * 验证微信视频握手签名
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-18
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/cp/channel/{appId}")
@Tag(name = "微信视频号接入方法", description = "微信视频号接入方法")
public class WxChannelPortalApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(produces = "text/plain;charset=utf-8")
    @Operation(summary = "微信服务器的认证消息接口", description = "视频号接入开发模式时腾讯调用此接口握手", method = "GET")
    @Parameter(name = "msg_signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    @Parameter(name = "echostr", description = "随机字符串")
    public String authGet(@PathVariable String appId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        this.logger.info(
                "接收到来自微信服务器的认证消息：appId = [{}], signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", appId,
                signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        final WxChannelMultiService wxChannelMultiService = SpringContextUtil.getBean(WxChannelMultiService.class);
        Objects.requireNonNull(wxChannelMultiService, "请配置WxChannelMultiService类！");
        WxChannelService wxChannelService = wxChannelMultiService.getWxChannelService(appId);
        if (wxChannelService == null) {
            throw new IllegalArgumentException(String.format("appId=[%s]的配置，请核实！", appId));
        }
        if (wxChannelService.checkSignature(timestamp, nonce, signature)) {
            return new WxChCryptUtils(wxChannelService.getConfig()).decrypt(echostr);
        }
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    @Operation(summary = "微信各类消息", description = "视频号接入开发模式后才有效", method = "POST")
    @Parameter(name = "msg_signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    public String post(@PathVariable String appId, HttpServletRequest request,
            @RequestParam(name = "msg_signature", required = false) String msgSignature,
            @RequestParam(name = "encrypt_type", required = false) String encryptType,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) throws IOException {
        byte[] buffer = IOUtils.toByteArray(request.getInputStream());
        this.logger.info(
                "接收微信请求：[appId=[{}],msgSignature=[{}],encryptType=[{}],signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ",
                appId, msgSignature, encryptType, signature, timestamp, nonce,
                new String(buffer, StandardCharsets.UTF_8));
        final WxChannelMultiService wxChannelMultiService = SpringContextUtil.getBean(WxChannelMultiService.class);
        Objects.requireNonNull(wxChannelMultiService, "请配置WxChannelMultiService类！");
        WxChannelService wxChannelService = wxChannelMultiService.getWxChannelService(appId);
        if (wxChannelService == null) {
            throw new IllegalArgumentException(String.format("appId=[%s]的配置，请核实！", appId));
        }
        final boolean isJson = Objects.equals(wxChannelService.getConfig().getMsgDataFormat(),
                WxMaConstants.MsgDataFormat.JSON);
        if (StringUtils.isBlank(encryptType)) {
            String bufferString = IOUtils.toString(buffer, StandardCharsets.UTF_8.name());
            // 明文传输的消息
            WxChannelMessage inMessage;
            if (isJson) {
                inMessage = JsonUtils.decode(bufferString, WxChannelMessage.class);
            }
            else {
                inMessage = XmlUtils.decode(bufferString, WxChannelMessage.class);
            }
            final WxChannelMessageRouter messageRouter = SpringContextUtil.getBean(WxChannelMessageRouter.class);
            messageRouter.route(inMessage, bufferString, appId, wxChannelService);
            return "success";
        }
        if ("aes".equals(encryptType)) {
            String bufferString = IOUtils.toString(buffer, StandardCharsets.UTF_8.name());
            // 是aes加密的消息
            WxChannelMessage inMessage;
            if (isJson) {
                inMessage = JsonUtils.decode(bufferString, WxChannelMessage.class);
            }
            else {
                inMessage = XmlUtils.decode(bufferString, WxChannelMessage.class);
            }
            String encryptContent = inMessage.getEncrypt();
            // aes加密的消息
            String plainText = this.decodeMessage(wxChannelService, encryptContent, timestamp, nonce, msgSignature);
            final WxChannelMessageRouter messageRouter = SpringContextUtil.getBean(WxChannelMessageRouter.class);
            messageRouter.route(inMessage, plainText, appId, wxChannelService);
            return "success";
        }
        throw new RuntimeException("不可识别的加密类型：" + encryptType);
    }

    /**
     * 解密消息
     * @param requestBody 消息体
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param msgSignature 签名串
     * @return 解密后的消息体
     */
    protected String decodeMessage(WxChannelService wxChannelService, String requestBody, String timestamp,
            String nonce, String msgSignature) {
        String plainText = null;
        try {
            WxChCryptUtils cryptUtil = new WxChCryptUtils(wxChannelService.getConfig());
            plainText = cryptUtil.decryptContent(msgSignature, timestamp, nonce, requestBody);
            logger.info("消息体:{}", plainText);
        }
        catch (Throwable e) {
            logger.error("解密异常", e);
        }
        return plainText;
    }

}
