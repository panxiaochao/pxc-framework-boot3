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
package io.github.panxiaochao.weixin.core.mp;

import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxMpProperties;
import io.github.panxiaochao.weixin.constants.WxConstant;
import io.github.panxiaochao.weixin.enums.HttpClientType;
import io.github.panxiaochao.weixin.enums.StorageType;
import io.github.panxiaochao.weixin.manager.IWxManager;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceHttpClientImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceJoddHttpImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedissonConfigImpl;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * WxMpService初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-11
 */
@RequiredArgsConstructor
public class PlusWxMpService {

    private final WxProperties wxProperties;

    /**
     * 初始化 WxMpService
     * @return WxMpService
     */
    public WxMpService build() {
        if (!wxProperties.getMp().getEnabled()) {
            return new WxMpServiceImpl();
        }
        final List<WxMpProperties.MpConfig> mpPropertiesList = wxProperties.getMp().getConfigs();
        if (CollectionUtils.isEmpty(mpPropertiesList)) {
            throw new RuntimeException("请配置微信公众号appId和appSecret相关参数！");
        }
        // 存储方式
        StorageType storageType = wxProperties.getStorageType();
        Map<String, WxMpConfigStorage> configStorages = mpPropertiesList.stream().map(mpProperties -> {
            WxMpDefaultConfigImpl configStorage;
            switch (storageType) {
                case Redisson:
                    configStorage = redissonConfigStorage();
                    break;
                case RedisTemplate:
                    configStorage = redisTemplateConfigStorage();
                    break;
                default:
                    configStorage = new WxMpDefaultConfigImpl();
                    break;
            }
            configStorage.setAppId(mpProperties.getAppId());
            configStorage.setSecret(mpProperties.getAppSecret());
            configStorage.setToken(mpProperties.getToken());
            configStorage.setAesKey(mpProperties.getAesKey());
            configStorage.setUseStableAccessToken(mpProperties.isUseStableAccessToken());
            return configStorage;
        }).collect(Collectors.toMap(WxMpDefaultConfigImpl::getAppId, a -> a, (o, n) -> o));
        // 设置默认AppId，启动默认设置第一个
        setDefaultAppId(mpPropertiesList);
        return getWxMpService(configStorages);
    }

    private void setDefaultAppId(List<WxMpProperties.MpConfig> mpPropertiesList) {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        Objects.requireNonNull(wxManager, "请正确配置IWxManager相关配置！");
        wxManager.set(WxConstant.MP_KEY, mpPropertiesList.get(0).getAppId());
    }

    @NotNull
    private WxMpService getWxMpService(Map<String, WxMpConfigStorage> configStorages) {
        HttpClientType httpClientType = wxProperties.getHttpClientType();
        WxMpService wxMpService;
        switch (httpClientType) {
            case OkHttp:
                wxMpService = new WxMpServiceOkHttpImpl();
                break;
            case JoddHttp:
                wxMpService = new WxMpServiceJoddHttpImpl();
                break;
            case HttpClient:
                wxMpService = new WxMpServiceHttpClientImpl();
                break;
            default:
                wxMpService = new WxMpServiceImpl();
                break;
        }
        wxMpService.setMultiConfigStorages(configStorages);
        wxMpService.setMaxRetryTimes(3);
        wxMpService.setRetrySleepMillis(1000);
        return wxMpService;
    }

    /**
     * Redisson 存储方案
     */
    private WxMpDefaultConfigImpl redissonConfigStorage() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        return new WxMpRedissonConfigImpl(redissonClient, wxProperties.getMp().getKeyPrefix());
    }

    /**
     * RedisTemplate 存储方案
     */
    private WxMpDefaultConfigImpl redisTemplateConfigStorage() {
        StringRedisTemplate redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("redisTemplate");
        }
        Objects.requireNonNull(redisTemplate, "请正确配置RedisTemplate相关配置！");
        WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
        return new WxMpRedisConfigImpl(redisOps, wxProperties.getMp().getKeyPrefix());
    }

}
