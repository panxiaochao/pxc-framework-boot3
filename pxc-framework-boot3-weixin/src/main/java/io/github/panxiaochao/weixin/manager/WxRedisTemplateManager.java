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

import io.github.panxiaochao.core.utils.SpringContextUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * <p>
 * 多微信AppId管理管理 - RedisTemplate管理
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-13
 */
public class WxRedisTemplateManager implements IWxManager {

    private final StringRedisTemplate stringRedisTemplate;

    public WxRedisTemplateManager() {
        StringRedisTemplate redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("redisTemplate");
        }
        Objects.requireNonNull(redisTemplate, "请正确配置RedisTemplate相关配置！");
        this.stringRedisTemplate = redisTemplate;
    }

    /**
     * obtain the v
     * @param key key
     * @return value
     */
    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * Set the value
     * @param key key
     * @param value object value
     */
    @Override
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

}
