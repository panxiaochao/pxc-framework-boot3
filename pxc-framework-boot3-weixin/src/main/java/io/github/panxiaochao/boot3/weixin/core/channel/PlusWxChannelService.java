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
package io.github.panxiaochao.boot3.weixin.core.channel;

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.config.properties.WxProperties;
import io.github.panxiaochao.boot3.weixin.config.properties.nested.WxChannelProperties;
import io.github.panxiaochao.boot3.weixin.constants.WxConstant;
import io.github.panxiaochao.boot3.weixin.core.channel.service.WxChannelMultiService;
import io.github.panxiaochao.boot3.weixin.core.channel.service.WxChannelMultiServiceImpl;
import io.github.panxiaochao.boot3.weixin.enums.HttpClientType;
import io.github.panxiaochao.boot3.weixin.enums.StorageType;
import io.github.panxiaochao.boot3.weixin.manager.IWxManager;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.channel.api.WxChannelService;
import me.chanjar.weixin.channel.api.impl.WxChannelServiceHttpClientImpl;
import me.chanjar.weixin.channel.api.impl.WxChannelServiceImpl;
import me.chanjar.weixin.channel.api.impl.WxChannelServiceOkHttpImpl;
import me.chanjar.weixin.channel.config.WxChannelConfig;
import me.chanjar.weixin.channel.config.impl.WxChannelDefaultConfigImpl;
import me.chanjar.weixin.channel.config.impl.WxChannelRedisConfigImpl;
import me.chanjar.weixin.channel.config.impl.WxChannelRedissonConfigImpl;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.common.redis.WxRedisOps;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * WxChannelService初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-18
 * @version 1.0
 */
@RequiredArgsConstructor
public class PlusWxChannelService {

    private final WxProperties wxProperties;

    /**
     * 初始化 WxChannelService
     */
    public WxChannelMultiService build() {
        WxChannelMultiServiceImpl wxChannelMultiService = new WxChannelMultiServiceImpl();
        if (wxProperties.getChannel().getEnabled()) {
            final List<WxChannelProperties.WxChannelConfig> cpPropertiesList = wxProperties.getChannel().getConfig();
            if (CollectionUtils.isEmpty(cpPropertiesList)) {
                throw new RuntimeException("请配置微信视频号相关参数！");
            }
            // 存储方式
            StorageType storageType = wxProperties.getStorageType();
            for (WxChannelProperties.WxChannelConfig wxChannelConfig : cpPropertiesList) {
                WxChannelDefaultConfigImpl configStorage;
                switch (storageType) {
                    case Redisson:
                        configStorage = redissonConfigStorage();
                        break;
                    case RedisTemplate:
                        configStorage = redisTemplateConfigStorage();
                        break;
                    default:
                        configStorage = new WxChannelDefaultConfigImpl();
                        break;
                }
                configStorage.setAppid(wxChannelConfig.getAppId());
                configStorage.setSecret(wxChannelConfig.getSecret());
                if (StringUtils.hasText(wxChannelConfig.getToken())) {
                    configStorage.setToken(wxChannelConfig.getToken());
                }
                if (StringUtils.hasText(wxChannelConfig.getAesKey())) {
                    configStorage.setAesKey(wxChannelConfig.getAesKey());
                }
                configStorage.setStableAccessToken(wxChannelConfig.isUseStableAccessToken());
                WxChannelService wxChannelService = getWxChannelService(configStorage);
                wxChannelMultiService.setWxChannelService(wxChannelConfig.getAppId(), wxChannelService);
            }
            // 设置默认AppId，启动默认设置第一个
            setDefaultAppId(cpPropertiesList);
        }
        return wxChannelMultiService;
    }

    private void setDefaultAppId(List<WxChannelProperties.WxChannelConfig> cpPropertiesList) {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        Objects.requireNonNull(wxManager, "请正确配置IWxManager相关配置！");
        wxManager.set(WxConstant.CHANNEL_KEY, cpPropertiesList.get(0).getAppId());
    }

    private WxChannelService getWxChannelService(WxChannelConfig configStorages) {
        HttpClientType httpClientType = wxProperties.getHttpClientType();
        WxChannelService wxChannelService;
        switch (httpClientType) {
            case OkHttp:
            case JoddHttp:
                wxChannelService = new WxChannelServiceOkHttpImpl();
                break;
            case HttpClient:
                wxChannelService = new WxChannelServiceHttpClientImpl();
                break;
            default:
                wxChannelService = new WxChannelServiceImpl();
                break;
        }
        wxChannelService.setConfig(configStorages);
        wxChannelService.setMaxRetryTimes(3);
        wxChannelService.setRetrySleepMillis(1000);
        return wxChannelService;
    }

    /**
     * Redisson 存储方案
     */
    private WxChannelDefaultConfigImpl redissonConfigStorage() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        return new WxChannelRedissonConfigImpl(redissonClient, wxProperties.getCp().getKeyPrefix());
    }

    /**
     * RedisTemplate 存储方案
     */
    private WxChannelDefaultConfigImpl redisTemplateConfigStorage() {
        StringRedisTemplate redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("redisTemplate");
        }
        Objects.requireNonNull(redisTemplate, "请正确配置RedisTemplate相关配置！");
        WxRedisOps redisOps = new RedisTemplateWxRedisOps(redisTemplate);
        return new WxChannelRedisConfigImpl(redisOps, wxProperties.getChannel().getKeyPrefix());
    }

}
