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
package io.github.panxiaochao.weixin.api.open;

import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.util.WxMpConfigStorageHolder;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 验证消息 微信开放平台服务器
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-16
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/open/portal")
@Tag(name = "微信开放平台接入方法", description = "微信开放平台接入方法")
public class WxOpenPortalApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String ENC_TYPE_AES = "aes";

    @RequestMapping(value = "/receive_ticket", produces = "text/plain;charset=utf-8")
    public Object receiveTicket(@RequestBody(required = false) String requestBody,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce,
            @RequestParam("signature") String signature,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        logger.info(
                "接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);
        final WxOpenService wxOpenService = SpringContextUtil.getBean(WxOpenService.class);
        if (!StringUtils.equalsIgnoreCase(ENC_TYPE_AES, encType)
                || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
                wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        logger.info("消息解密后内容为：{} ", inMessage.toString());
        try {
            wxOpenService.getWxOpenComponentService().route(inMessage);
            if ("unauthorized".equals(inMessage.getInfoType())) {
                logger.info("{}, 取消授权", inMessage.getAuthorizerAppid());
            }
            return "success";
        }
        catch (WxErrorException e) {
            logger.error("receive_ticket", e);
            return "";
        }
    }

    @RequestMapping(value = "/{appId}/callback", produces = "text/plain;charset=utf-8")
    public Object callback(@RequestBody(required = false) String requestBody, @PathVariable("appId") String appId,
            @RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce, @RequestParam("openid") String openid,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        logger.info(
                "接收微信请求：[appId=[{}], openid=[{}], signature=[{}], encType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                appId, openid, signature, encType, msgSignature, timestamp, nonce, requestBody);
        final WxOpenService wxOpenService = SpringContextUtil.getBean(WxOpenService.class);
        if (!StringUtils.equalsIgnoreCase(ENC_TYPE_AES, encType)
                || !wxOpenService.getWxOpenComponentService().checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        String out = "";
        // aes加密的消息
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody,
                wxOpenService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        logger.info("消息解密后内容为：{} ", inMessage.toString());
        // 全网发布测试用例
        CharSequence[] testAppId = {
                // 第一组
                "wxd101a85aa106f53e", "wx570bc396a51b8ff8",
                // 第二组
                "wx9252c5e0bb1836fc", "wxc39235c15087f6f3",
                // 第三组
                "wx8e1097c5bc82cde9", "wx7720d01d4b2a4500",
                // 第四组
                "wx14550af28c71a144", "wx05d483572dcd5d8b",
                // 第五组
                "wxa35b9c23cfe664eb", "wx5910277cae6fd970" };
        if (StringUtils.equalsAnyIgnoreCase(appId, testAppId)) {
            try {
                if (StringUtils.equals(inMessage.getMsgType(), "text")) {
                    if (StringUtils.equals(inMessage.getContent(), "TESTCOMPONENT_MSG_TYPE_TEXT")) {
                        WxMpXmlOutTextMessage wxMpXmlOutTextMessage = WxMpXmlOutMessage.TEXT()
                            .content("TESTCOMPONENT_MSG_TYPE_TEXT_callback")
                            .fromUser(inMessage.getToUser())
                            .toUser(inMessage.getFromUser())
                            .build();
                        out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(wxMpXmlOutTextMessage,
                                wxOpenService.getWxOpenConfigStorage());
                    }
                    else if (StringUtils.startsWith(inMessage.getContent(), "QUERY_AUTH_CODE:")) {
                        String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:", "") + "_from_api";
                        WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT()
                            .content(msg)
                            .toUser(inMessage.getFromUser())
                            .build();
                        wxOpenService.getWxOpenComponentService()
                            .getWxMpServiceByAppid(appId)
                            .getKefuService()
                            .sendKefuMessage(kefuMessage);
                    }
                }
                else if (StringUtils.equals(inMessage.getMsgType(), "event")) {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT()
                        .content(inMessage.getEvent() + "from_callback")
                        .toUser(inMessage.getFromUser())
                        .build();
                    wxOpenService.getWxOpenComponentService()
                        .getWxMpServiceByAppid(appId)
                        .getKefuService()
                        .sendKefuMessage(kefuMessage);
                }
            }
            catch (WxErrorException e) {
                logger.error("callback", e);
            }
        }
        else {
            WxMpXmlOutMessage outMessage = route(inMessage, appId);
            if (outMessage != null) {
                out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(outMessage,
                        wxOpenService.getWxOpenConfigStorage());
            }
        }
        logger.info("处理组装回复信息：{}", out);
        WxMpConfigStorageHolder.remove();
        return out;
    }

    /**
     * 路由消息
     * @param message message
     * @return WxMpXmlOutMessage
     */
    private WxMpXmlOutMessage route(WxMpXmlMessage message, String appId) {
        try {
            final WxOpenMessageRouter messageRouter = SpringContextUtil.getBean(WxOpenMessageRouter.class);
            return messageRouter.route(message, appId);
        }
        catch (Exception e) {
            logger.error("路由消息时出现异常！", e);
        }
        return null;
    }

}
