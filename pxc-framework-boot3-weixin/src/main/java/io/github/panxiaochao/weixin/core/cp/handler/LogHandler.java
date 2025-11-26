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
package io.github.panxiaochao.weixin.core.cp.handler;

import io.github.panxiaochao.core.utils.JacksonUtil;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;

import java.util.Map;

/**
 *
 * <p>
 * 日志消息拦截
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
public class LogHandler extends AbstractCpHandler {

    @Override
    public WxCpXmlOutMessage handle(WxCpXmlMessage wxCpMessage, Map<String, Object> map, WxCpService wxCpService,
            WxSessionManager wxSessionManager) {
        try {
            logger.info("接收到请求消息，内容：{}", JacksonUtil.toString(wxCpMessage));
        }
        catch (Exception e) {
            logger.error("记录消息异常", e);
        }
        return null;
    }

}
