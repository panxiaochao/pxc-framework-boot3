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
package io.github.panxiaochao.weixin.core.channel.service;

import me.chanjar.weixin.channel.api.WxChannelService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 视频号 {@link WxChannelMultiService} 实现
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-18
 * @version 1.0
 */
public class WxChannelMultiServiceImpl implements WxChannelMultiService {

    private static final ConcurrentHashMap<String, WxChannelService> CONCURRENT_CHANNEL_MAP = new ConcurrentHashMap<>(
            1);

    // private static final ConcurrentHashMap<String, WxChannelMessageRouter>
    // CONCURRENT_ROUTER_MAP = new ConcurrentHashMap<>(
    // 1);

    /**
     * 通过key 获取 WxChannelService
     * @param key key
     * @return WxChannelService
     */
    @Override
    public WxChannelService getWxChannelService(String key) {
        return CONCURRENT_CHANNEL_MAP.get(key);
    }

    // /**
    // * 通过key, 获取WxChannelMessageRouter
    // *
    // * @param key key
    // * @return WxChannelMessageRouter
    // */
    // @Override
    // public WxChannelMessageRouter getWxChannelMessageRouter(String key) {
    // return CONCURRENT_ROUTER_MAP.get(key);
    // }

    /**
     * 设置WxChannelService，通过key
     * @param key key
     * @param wxChannelService wxChannelService
     */
    public void setWxChannelService(String key, WxChannelService wxChannelService) {
        CONCURRENT_CHANNEL_MAP.put(key, wxChannelService);
    }

    // /**
    // * 设置WxChannelMessageRouter，通过key
    // * @param key key
    // * @param wxChannelMessageRouter wxChannelMessageRouter
    // */
    // public void setWxChannelMessageRouter(String key, WxChannelMessageRouter
    // wxChannelMessageRouter) {
    // CONCURRENT_ROUTER_MAP.put(key, wxChannelMessageRouter);
    // }

    /**
     * 根据key，从列表中移除一个 WxChannelService 实例
     * @param key key
     */
    @Override
    public void removeWxChannelService(String key) {
        CONCURRENT_CHANNEL_MAP.remove(key);
        // CONCURRENT_ROUTER_MAP.remove(key);
    }

}
