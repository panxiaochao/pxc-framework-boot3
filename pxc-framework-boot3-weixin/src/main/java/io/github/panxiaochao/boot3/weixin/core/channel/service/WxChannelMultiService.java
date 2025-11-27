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
package io.github.panxiaochao.boot3.weixin.core.channel.service;

import me.chanjar.weixin.channel.api.WxChannelService;

/**
 * <p>
 * 视频号 {@link WxChannelService} 所有实例存放类.
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-18
 * @version 1.0
 */
public interface WxChannelMultiService {

    /**
     * 通过key 获取 WxChannelService
     * @param key key
     * @return WxChannelService
     */
    WxChannelService getWxChannelService(String key);

    // /**
    // * 通过key, 获取WxChannelMessageRouter
    // * @param key key
    // * @return WxChannelMessageRouter
    // */
    // WxChannelMessageRouter getWxChannelMessageRouter(String key);

    /**
     * 根据key，从列表中移除一个 WxChannelService 实例
     * @param key key
     */
    void removeWxChannelService(String key);

}
