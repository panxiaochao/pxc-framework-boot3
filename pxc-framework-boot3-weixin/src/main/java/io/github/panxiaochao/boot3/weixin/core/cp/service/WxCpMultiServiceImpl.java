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
package io.github.panxiaochao.boot3.weixin.core.cp.service;

import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 多企业微信实例 {@link WxCpMultiService} 默认实现
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 * @version 1.0
 */
public class WxCpMultiServiceImpl implements WxCpMultiService {

    private static final ConcurrentHashMap<String, WxCpService> CONCURRENT_CP_MAP = new ConcurrentHashMap<>(1);

    private static final ConcurrentHashMap<String, WxCpMessageRouter> CONCURRENT_ROUTER_MAP = new ConcurrentHashMap<>(
            1);

    /**
     * 通过key, 获取WxCpService
     * @param key key
     * @return WxCpService
     */
    @Override
    public WxCpService getWxCpService(String key) {
        return CONCURRENT_CP_MAP.get(key);
    }

    /**
     * 通过key, 获取WxCpMessageRouter
     * @param key key
     * @return WxCpMessageRouter
     */
    @Override
    public WxCpMessageRouter getWxCpMessageRouter(String key) {
        return CONCURRENT_ROUTER_MAP.get(key);
    }

    /**
     * 设置WxCpService，通过key
     * @param key key
     * @param wxCpService wxCpService
     */
    public void setWxCpService(String key, WxCpService wxCpService) {
        CONCURRENT_CP_MAP.put(key, wxCpService);
    }

    /**
     * 设置WxCpMessageRouter，通过key
     * @param key key
     * @param wxCpMessageRouter wxCpMessageRouter
     */
    public void setWxCpMessageRouter(String key, WxCpMessageRouter wxCpMessageRouter) {
        CONCURRENT_ROUTER_MAP.put(key, wxCpMessageRouter);
    }

    /**
     * 通过key，从列表中移除一个 WxCpService 实例
     * @param key key
     */
    @Override
    public void removeWxCpService(String key) {
        CONCURRENT_CP_MAP.remove(key);
        CONCURRENT_ROUTER_MAP.remove(key);
    }

}
