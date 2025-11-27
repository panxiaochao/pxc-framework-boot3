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
package io.github.panxiaochao.boot3.weixin.core.cp.builder;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.message.WxCpXmlMessage;
import me.chanjar.weixin.cp.bean.message.WxCpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * <p>
 * 消息抽象
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
public abstract class AbstractCpBuilder {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param content 内容
     * @param wxMessage 消息
     * @param cpService cpService
     * @return WxMpXmlOutMessage
     */
    public abstract WxCpXmlOutMessage build(String content, WxCpXmlMessage wxMessage, WxCpService cpService);

}
