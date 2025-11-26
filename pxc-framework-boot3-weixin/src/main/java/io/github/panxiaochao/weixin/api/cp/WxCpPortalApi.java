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

import io.github.panxiaochao.core.utils.JacksonUtil;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.core.cp.service.WxCpMultiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import me.chanjar.weixin.cp.util.crypto.WxCpCryptUtil;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 * 验证企业号/企业微信握手签名
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/wx/cp/portal/{corpId}/{agentId}")
@Tag(name = "企业微信接入方法", description = "企业微信接入方法")
public class WxCpPortalApi {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping(produces = "text/plain;charset=utf-8")
    @Operation(summary = "微信服务器的认证消息接口", description = "公众号接入开发模式时腾讯调用此接口握手", method = "GET")
    @Parameter(name = "msg_signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    @Parameter(name = "echostr", description = "随机字符串")
    public String authGet(@PathVariable String corpId, @PathVariable Integer agentId,
            @RequestParam(name = "msg_signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        this.logger.info(
                "接收到来自微信服务器的认证消息：corpId = [{}], agentId = [{}],signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                corpId, agentId, signature, timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
        Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
        WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(
                    String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
        }
        if (wxCpService.checkSignature(signature, timestamp, nonce, echostr)) {
            return new WxCpCryptUtil(wxCpService.getWxCpConfigStorage()).decrypt(echostr);
        }
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    @Operation(summary = "微信各类消息", description = "企业号接入开发模式后才有效", method = "POST")
    @Parameter(name = "msg_signature", description = "微信加密签名")
    @Parameter(name = "timestamp", description = "时间戳")
    @Parameter(name = "nonce", description = "随机数")
    public String post(@PathVariable String corpId, @PathVariable Integer agentId, HttpServletRequest request,
            @RequestParam("msg_signature") String signature, @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce) throws IOException {
        byte[] buffer = IOUtils.toByteArray(request.getInputStream());
        this.logger.info(
                "接收微信请求：[corpId = [{}],agentId = [{}],signature=[{}], timestamp=[{}], nonce=[{}], requestBody=[{}] ",
                corpId, agentId, signature, timestamp, nonce, new String(buffer, StandardCharsets.UTF_8));
        InputStream in = new ByteArrayInputStream(buffer);
        Objects.requireNonNull(in, "微信请求流内容为空！");
        final WxCpMultiService wxCpMultiService = SpringContextUtil.getBean(WxCpMultiService.class);
        Objects.requireNonNull(wxCpMultiService, "请配置WxCpMultiService类！");
        WxCpService wxCpService = wxCpMultiService.getWxCpService(corpId + agentId);
        if (wxCpService == null) {
            throw new IllegalArgumentException(
                    String.format("未找到对应corpId=[%s], agentId=[%d]的配置，请核实！", corpId, agentId));
        }
        WxCpXmlMessage inMessage = WxCpXmlMessage.fromEncryptedXml(in, wxCpService.getWxCpConfigStorage(), timestamp,
                nonce, signature);
        this.logger.debug("消息解密后内容为：{} ", JacksonUtil.toString(inMessage));
        // 路由消息
        WxCpMessageRouter messageRouter = wxCpMultiService.getWxCpMessageRouter(corpId + agentId);
        WxCpXmlOutMessage outMessage = messageRouter.route(inMessage);
        if (outMessage == null) {
            return "";
        }
        String out = outMessage.toEncryptedXml(wxCpService.getWxCpConfigStorage());
        this.logger.debug("组装回复信息：{}", out);
        return out;
    }

}
