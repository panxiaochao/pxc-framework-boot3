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
package io.github.panxiaochao.weixin.manager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 多微信AppId管理管理 - 内存管理
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-13
 */
public class WxMemoryManager implements IWxManager {

    private static final ConcurrentHashMap<String, String> CONCURRENT_APPID_MAP = new ConcurrentHashMap<>(1);

    /**
     * obtain the v
     * @param key key
     * @return value
     */
    @Override
    public String get(String key) {
        return CONCURRENT_APPID_MAP.get(key);
    }

    /**
     * set the value
     * @param key key
     * @param value object value
     */
    @Override
    public void set(String key, String value) {
        CONCURRENT_APPID_MAP.put(key, value);
    }

}
