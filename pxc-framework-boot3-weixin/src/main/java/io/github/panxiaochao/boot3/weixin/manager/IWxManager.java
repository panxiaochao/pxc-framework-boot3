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
package io.github.panxiaochao.boot3.weixin.manager;

/**
 * <p>
 * 微信管理
 * </p>
 *
 * <ul>
 * <li>多AppId管理</li>
 * </ul>
 *
 * @author Lypxc
 * @since 2024-12-13
 */
public interface IWxManager {

    /**
     * obtain the v
     * @param key key
     * @return value
     */
    String get(String key);

    /**
     * set k-v
     * @param key key
     * @param value value
     */
    void set(String key, String value);

}
