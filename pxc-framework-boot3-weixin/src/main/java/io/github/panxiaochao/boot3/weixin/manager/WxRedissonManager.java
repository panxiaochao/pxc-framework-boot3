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

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * <p>
 * 多微信AppId管理管理 - Redisson管理
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-13
 */
public class WxRedissonManager implements IWxManager {

    private final RedissonClient redissonClient;

    public WxRedissonManager() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        this.redissonClient = redissonClient;
    }

    /**
     * obtain the v
     * @param key key
     * @return value
     */
    @Override
    public String get(String key) {
        RBucket<String> rBucket = getRbucket(key);
        return StringUtils.hasText(rBucket.get()) ? rBucket.get() : null;
    }

    /**
     * Set the value
     * @param key key
     * @param value object value
     */
    @Override
    public void set(String key, String value) {
        getRbucket(key).set(value);
    }

    /**
     * obtain the bucket
     * @param bucketName bucket name
     * @return RBucket
     */
    private <T> RBucket<T> getRbucket(String bucketName) {
        return this.redissonClient.getBucket(bucketName, new StringCodec(StandardCharsets.UTF_8));
    }

}
