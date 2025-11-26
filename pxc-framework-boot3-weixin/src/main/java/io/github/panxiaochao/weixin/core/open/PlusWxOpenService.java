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
package io.github.panxiaochao.weixin.core.open;

import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxOpenProperties;
import io.github.panxiaochao.weixin.enums.StorageType;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.open.api.WxOpenConfigStorage;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedisTemplateConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenInRedissonConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 *
 * <p>
 * WxOpenService 初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-16
 */
@RequiredArgsConstructor
public class PlusWxOpenService {

    private final WxProperties wxProperties;

    /**
     * 初始化 WxOpenService
     * @return WxOpenService
     */
    public WxOpenService build() {
        if (!wxProperties.getOpen().getEnabled()) {
            return new WxOpenServiceImpl();
        }
        final WxOpenProperties openProperties = wxProperties.getOpen();
        // 存储方式
        StorageType storageType = wxProperties.getStorageType();
        WxOpenConfigStorage configStorage;
        switch (storageType) {
            case Redisson:
                configStorage = redissonConfigStorage();
                break;
            case RedisTemplate:
                configStorage = redisTemplateConfigStorage();
                break;
            default:
                configStorage = new WxOpenInMemoryConfigStorage();
                break;
        }
        configStorage.setWxOpenInfo(openProperties.getAppId(), openProperties.getSecret(), openProperties.getToken(),
                openProperties.getAesKey());
        WxOpenService service = new WxOpenServiceImpl();
        service.setWxOpenConfigStorage(configStorage);
        return service;
    }

    /**
     * Redisson 存储方案
     */
    private WxOpenConfigStorage redissonConfigStorage() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        return new WxOpenInRedissonConfigStorage(redissonClient, wxProperties.getOpen().getKeyPrefix());
    }

    /**
     * RedisTemplate 存储方案
     */
    private WxOpenConfigStorage redisTemplateConfigStorage() {
        StringRedisTemplate redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("redisTemplate");
        }
        Objects.requireNonNull(redisTemplate, "请正确配置RedisTemplate相关配置！");
        WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
        return new WxOpenInRedisTemplateConfigStorage(redisOps, wxProperties.getOpen().getKeyPrefix());
    }

}
