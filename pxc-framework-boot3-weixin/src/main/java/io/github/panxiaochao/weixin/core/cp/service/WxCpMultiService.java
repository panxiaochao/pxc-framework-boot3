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
package io.github.panxiaochao.weixin.core.cp.service;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

/**
 * <p>
 * 多企业微信 {@link WxCpService} 所有实例存放类
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 * @version 1.0
 */
public interface WxCpMultiService {

    /**
     * 通过key, 获取WxCpService
     * @param key key
     * @return WxCpService
     */
    WxCpService getWxCpService(String key);

    /**
     * 通过key, 获取WxCpMessageRouter
     * @param key key
     * @return WxCpMessageRouter
     */
    WxCpMessageRouter getWxCpMessageRouter(String key);

    /**
     * 通过key，从列表中移除一个 WxCpService 实例
     * @param key key
     */
    void removeWxCpService(String key);

}
