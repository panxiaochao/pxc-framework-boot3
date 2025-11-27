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
package io.github.panxiaochao.boot3.weixin.core.cp;

import io.github.panxiaochao.boot3.core.utils.SpringContextUtil;
import io.github.panxiaochao.boot3.weixin.config.properties.WxProperties;
import io.github.panxiaochao.boot3.weixin.config.properties.nested.WxCpProperties;
import io.github.panxiaochao.boot3.weixin.constants.WxConstant;
import io.github.panxiaochao.boot3.weixin.core.cp.handler.AbstractCpHandler;
import io.github.panxiaochao.boot3.weixin.core.cp.handler.LogHandler;
import io.github.panxiaochao.boot3.weixin.core.cp.handler.NullHandler;
import io.github.panxiaochao.boot3.weixin.core.cp.service.WxCpMultiService;
import io.github.panxiaochao.boot3.weixin.core.cp.service.WxCpMultiServiceImpl;
import io.github.panxiaochao.boot3.weixin.enums.HttpClientType;
import io.github.panxiaochao.boot3.weixin.enums.StorageType;
import io.github.panxiaochao.boot3.weixin.manager.IWxManager;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceApacheHttpClientImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceJoddHttpImpl;
import me.chanjar.weixin.cp.api.impl.WxCpServiceOkHttpImpl;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpRedisTemplateConfigImpl;
import me.chanjar.weixin.cp.config.impl.WxCpRedissonConfigImpl;
import me.chanjar.weixin.cp.message.WxCpMessageRouter;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * WxCpService初始化 重点！！！
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-17
 */
@RequiredArgsConstructor
public class PlusWxCpService {

    private final WxProperties wxProperties;

    /**
     * 初始化 WxCpService
     */
    public WxCpMultiService build() {
        WxCpMultiServiceImpl wxCpMultiService = new WxCpMultiServiceImpl();
        if (wxProperties.getCp().getEnabled()) {
            final List<WxCpProperties.WxCpConfig> cpPropertiesList = wxProperties.getCp().getConfig();
            if (CollectionUtils.isEmpty(cpPropertiesList)) {
                throw new RuntimeException("请配置企业号/企业微信相关参数！");
            }
            // 存储方式
            StorageType storageType = wxProperties.getStorageType();
            for (WxCpProperties.WxCpConfig wxCpConfig : cpPropertiesList) {
                WxCpDefaultConfigImpl configStorage;
                switch (storageType) {
                    case Redisson:
                        configStorage = redissonConfigStorage();
                        break;
                    case RedisTemplate:
                        configStorage = redisTemplateConfigStorage();
                        break;
                    default:
                        configStorage = new WxCpDefaultConfigImpl();
                        break;
                }
                configStorage.setCorpId(wxCpConfig.getCorpId());
                configStorage.setAgentId(wxCpConfig.getAgentId());
                configStorage.setCorpSecret(wxCpConfig.getCorpSecret());
                configStorage.setToken(wxCpConfig.getToken());
                configStorage.setAesKey(wxCpConfig.getAesKey());
                // 企业微信，私钥，会话存档路径
                if (StringUtils.hasText(wxCpConfig.getMsgAuditPriKey())) {
                    configStorage.setMsgAuditPriKey(wxCpConfig.getMsgAuditPriKey());
                }
                if (StringUtils.hasText(wxCpConfig.getMsgAuditLibPath())) {
                    configStorage.setMsgAuditLibPath(wxCpConfig.getMsgAuditLibPath());
                }
                WxCpService wxCpService = getWxCpService(configStorage);
                wxCpMultiService.setWxCpService(wxCpConfig.getCorpId() + wxCpConfig.getAgentId(), wxCpService);
                wxCpMultiService.setWxCpMessageRouter(wxCpConfig.getCorpId() + wxCpConfig.getAgentId(),
                        getWxCpMessageRouter(wxCpService));
            }
            // 设置默认CorpId，启动默认设置第一个
            setDefaultCorpId(cpPropertiesList);
        }
        return wxCpMultiService;
    }

    private void setDefaultCorpId(List<WxCpProperties.WxCpConfig> cpPropertiesList) {
        final IWxManager wxManager = SpringContextUtil.getBean(IWxManager.class);
        Objects.requireNonNull(wxManager, "请正确配置IWxManager相关配置！");
        wxManager.set(WxConstant.CP_KEY, cpPropertiesList.get(0).getCorpId() + cpPropertiesList.get(0).getAgentId());
    }

    private WxCpService getWxCpService(WxCpConfigStorage configStorages) {
        HttpClientType httpClientType = wxProperties.getHttpClientType();
        WxCpService wxCpService;
        switch (httpClientType) {
            case OkHttp:
                wxCpService = new WxCpServiceOkHttpImpl();
                break;
            case JoddHttp:
                wxCpService = new WxCpServiceJoddHttpImpl();
                break;
            case HttpClient:
                wxCpService = new WxCpServiceApacheHttpClientImpl();
                break;
            default:
                wxCpService = new WxCpServiceImpl();
                break;
        }
        wxCpService.setWxCpConfigStorage(configStorages);
        wxCpService.setMaxRetryTimes(3);
        wxCpService.setRetrySleepMillis(1000);
        return wxCpService;
    }

    public WxCpMessageRouter getWxCpMessageRouter(WxCpService wxCpService) {
        final WxCpMessageRouter newRouter = new WxCpMessageRouter(wxCpService);
        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(new LogHandler()).next();
        // 点击菜单连接事件
        newRouter.rule()
            .async(false)
            .msgType(WxConsts.XmlMsgType.EVENT)
            .event(WxConsts.EventType.VIEW)
            .handler(new NullHandler())
            .end();
        // 加载自定义处理器
        if (!CollectionUtils.isEmpty(wxProperties.getMp().getHandlers())) {
            List<AbstractCpHandler> messageHandlers = new ArrayList<>();
            for (WxCpProperties.CpHandler cpHandler : wxProperties.getCp().getHandlers()) {
                try {
                    AbstractCpHandler handler = SpringContextUtil.getBean(cpHandler.getHandler());
                    Objects.requireNonNull(handler, () -> "请在自定义处理器中加入@Component");
                    // 目前没有msgType和event的情况下，是通用消息类型处理，默认放最后一个
                    if (!StringUtils.hasText(cpHandler.getMsgType()) && !StringUtils.hasText(cpHandler.getEvent())) {
                        messageHandlers.add(handler);
                    }
                    else {
                        newRouter.rule().async(false).msgType(cpHandler.getMsgType()).event(cpHandler.getEvent()).end();
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException("加载企业号/企业微信自定义处理器错误！", e);
                }
            }
            // 接入最后的通用消息处理器
            if (!CollectionUtils.isEmpty(messageHandlers)) {
                messageHandlers.forEach(s -> newRouter.rule().async(false).handler(s).end());
            }
        }
        return newRouter;
    }

    /**
     * Redisson 存储方案
     */
    private WxCpDefaultConfigImpl redissonConfigStorage() {
        RedissonClient redissonClient = SpringContextUtil.getBean(RedissonClient.class);
        if (Objects.isNull(redissonClient)) {
            redissonClient = SpringContextUtil.getBean("redissonClient");
        }
        Objects.requireNonNull(redissonClient, "请正确配置Redisson相关配置！");
        return new WxCpRedissonConfigImpl(redissonClient, wxProperties.getCp().getKeyPrefix());
    }

    /**
     * RedisTemplate 存储方案
     */
    private WxCpDefaultConfigImpl redisTemplateConfigStorage() {
        StringRedisTemplate redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("stringRedisTemplate");
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean("redisTemplate");
        }
        Objects.requireNonNull(redisTemplate, "请正确配置RedisTemplate相关配置！");
        return new WxCpRedisTemplateConfigImpl(redisTemplate, wxProperties.getCp().getKeyPrefix());
    }

}
