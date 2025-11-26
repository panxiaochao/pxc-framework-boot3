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
package io.github.panxiaochao.weixin.core.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxMaProperties;
import io.github.panxiaochao.weixin.core.ma.handler.AbstractMaHandler;
import io.github.panxiaochao.weixin.core.ma.handler.LogHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 微信小程序消息路由
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
public class PlusWxMaMessageRouter {

    private final WxProperties wxProperties;

    private final WxMaService wxMaService;

    public WxMaMessageRouter build() {
        final WxMaMessageRouter newRouter = new WxMaMessageRouter(wxMaService);
        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(new LogHandler()).next();
        // 加载自定义处理器
        final List<WxMaProperties.MaHandler> handlers = wxProperties.getMa().getHandlers();
        if (!CollectionUtils.isEmpty(handlers)) {
            for (WxMaProperties.MaHandler maHandler : handlers) {
                try {
                    AbstractMaHandler handler = SpringContextUtil.getBean(maHandler.getHandler());
                    Objects.requireNonNull(handler, "请在自定义处理器中加入@Component");
                    newRouter.rule().async(false).content(maHandler.getContent()).handler(handler).end();
                }
                catch (Exception e) {
                    throw new RuntimeException("加载微信小程序自定义处理器错误！", e);
                }
            }
        }
        return newRouter;
    }

}
