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
package io.github.panxiaochao.boot3.weixin.core.open;

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.config.properties.WxProperties;
import io.github.panxiaochao.boot3.weixin.config.properties.nested.WxMpProperties;
import io.github.panxiaochao.boot3.weixin.core.mp.handler.AbstractMpHandler;
import io.github.panxiaochao.boot3.weixin.core.mp.handler.LogHandler;
import io.github.panxiaochao.boot3.weixin.core.mp.handler.NullHandler;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * <p>
 * 微信开放平台消息路由
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-16
 */
@RequiredArgsConstructor
public class PlusWxOpenMessageRouter {

    private final WxProperties wxProperties;

    private final WxOpenService wxOpenService;

    public WxOpenMessageRouter build() {
        final WxOpenMessageRouter newRouter = new WxOpenMessageRouter(wxOpenService);
        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(new LogHandler()).next();
        // 点击菜单连接事件
        newRouter.rule()
            .async(false)
            .msgType(WxConsts.XmlMsgType.EVENT)
            .event(WxConsts.EventType.VIEW)
            .handler(new NullHandler())
            .end();
        // 加载自定义处理器
        if (!CollectionUtils.isEmpty(wxProperties.getOpen().getHandlers())) {
            List<AbstractMpHandler> messageHandlers = new ArrayList<>();
            for (WxMpProperties.MpHandler mpHandler : wxProperties.getOpen().getHandlers()) {
                try {
                    AbstractMpHandler handler = SpringContextUtil.getBean(mpHandler.getHandler());
                    Objects.requireNonNull(handler, () -> "请在自定义处理器中加入@Component");
                    // 目前没有msgType和event的情况下，是通用消息类型处理，默认放最后一个
                    if (!StringUtils.hasText(mpHandler.getMsgType()) && !StringUtils.hasText(mpHandler.getEvent())) {
                        messageHandlers.add(handler);
                    }
                    else {
                        newRouter.rule().async(false).msgType(mpHandler.getMsgType()).event(mpHandler.getEvent()).end();
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException("加载微信开放平台自定义处理器错误！", e);
                }
            }
            // 接入最后的通用消息处理器
            if (!CollectionUtils.isEmpty(messageHandlers)) {
                messageHandlers.forEach(s -> newRouter.rule().async(false).handler(s).end());
            }
        }
        return newRouter;
    }

}
