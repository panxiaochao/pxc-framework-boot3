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
package io.github.panxiaochao.weixin.core.ma;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceHttpClientImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceJoddHttpImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceOkHttpImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedissonConfigImpl;
import io.github.panxiaochao.core.utils.SpringContextUtil;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import io.github.panxiaochao.weixin.config.properties.nested.WxMaProperties;
import io.github.panxiaochao.weixin.constants.WxConstant;
import io.github.panxiaochao.weixin.enums.HttpClientType;
import io.github.panxiaochao.weixin.enums.StorageType;
import io.github.panxiaochao.weixin.manager.IWxManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RedissonClient;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * <p>
 * WxMaService初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
public class PlusWxMaService {

    private final WxProperties wxProperties;

    /**
     * 初始化 WxMaService
     * @return WxMaService
     */
    public WxMaService build() {
        if (!wxProperties.getMa().getEnabled()) {
            return new WxMaServiceImpl();
        }
        final List<WxMaProperties.MaConfig> maPropertiesList = wxProperties.getMa().getConfigs();
        if (CollectionUtils.isEmpty(maPropertiesList)) {
            throw new RuntimeException("请配置微信小程序相关参数！");
        }
        // 存储方式
        StorageType storageType = wxProperties.getStorageType();
        Map<String, WxMaConfig> configStorages = maPropertiesList.stream().map(maConfig -> {
            WxMaDefaultConfigImpl configStorage;
            switch (storageType) {
                case Redisson:
                case RedisTemplate:
                    configStorage = redissonConfigStorage();
                    break;
                default:
                    configStorage = new WxMaDefaultConfigImpl();
                    break;
            }
            configStorage.setAppid(maConfig.getAppId());
            configStorage.setSecret(maConfig.getAppSecret());
            configStorage.setToken(maConfig.getToken());
            configStorage.setAesKey(maConfig.getAesKey());
            configStorage.setMsgDataFormat(maConfig.getMsgDataFormat());
            configStorage.useStableAccessToken(maConfig.isUseStableAccessToken());
            return configStorage;
        }).collect(Collectors.toMap(WxMaDefaultConfigImpl::getAppid, a -> a, (o, n) -> o));
        // 设置默认AppId，启动默认设置第一个
        setDefaultAppId(maPropertiesList);
        return getWxMaService(configStorages);
    }

    private void setDefaultAppId(List<WxMaProperties.MaConfig> maPropertiesList) {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        Objects.requireNonNull(wxManager, "请正确配置IWxManager相关配置！");
        wxManager.set(WxConstant.MA_KEY, maPropertiesList.get(0).getAppId());
    }

    @NotNull
    private WxMaService getWxMaService(Map<String, WxMaConfig> configStorages) {
        HttpClientType httpClientType = wxProperties.getHttpClientType();
        WxMaService waMpService;
        switch (httpClientType) {
            case OkHttp:
                waMpService = new WxMaServiceOkHttpImpl();
                break;
            case JoddHttp:
                waMpService = new WxMaServiceJoddHttpImpl();
                break;
            case HttpClient:
                waMpService = new WxMaServiceHttpClientImpl();
                break;
            default:
                waMpService = new WxMaServiceImpl();
                break;
        }
        waMpService.setMultiConfigs(configStorages);
        waMpService.setMaxRetryTimes(3);
        waMpService.setRetrySleepMillis(1000);
        return waMpService;
    }

    /**
     * Redisson 存储方案
     */
    private WxMaDefaultConfigImpl redissonConfigStorage() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        return new WxMaRedissonConfigImpl(redissonClient, wxProperties.getMa().getKeyPrefix());
    }

}
