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
package io.github.panxiaochao.weixin.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import com.github.binarywang.wxpay.service.WxPayService;
import io.github.panxiaochao.weixin.config.properties.WxProperties;
import io.github.panxiaochao.weixin.core.channel.PlusWxChannelService;
import io.github.panxiaochao.weixin.core.channel.service.WxChannelMultiService;
import io.github.panxiaochao.weixin.core.cp.PlusWxCpService;
import io.github.panxiaochao.weixin.core.cp.service.WxCpMultiService;
import io.github.panxiaochao.weixin.core.ma.PlusWxMaMessageRouter;
import io.github.panxiaochao.weixin.core.ma.PlusWxMaService;
import io.github.panxiaochao.weixin.core.mp.PlusWxMpMessageRouter;
import io.github.panxiaochao.weixin.core.mp.PlusWxMpService;
import io.github.panxiaochao.weixin.core.open.PlusWxOpenMessageRouter;
import io.github.panxiaochao.weixin.core.open.PlusWxOpenService;
import io.github.panxiaochao.weixin.core.pay.PlusWxPayService;
import io.github.panxiaochao.weixin.enums.StorageType;
import io.github.panxiaochao.weixin.manager.IWxManager;
import io.github.panxiaochao.weixin.manager.WxMemoryManager;
import io.github.panxiaochao.weixin.manager.WxRedisTemplateManager;
import io.github.panxiaochao.weixin.manager.WxRedissonManager;
import me.chanjar.weixin.channel.message.WxChannelMessageRouter;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.open.api.WxOpenService;
import me.chanjar.weixin.open.api.impl.WxOpenMessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 微信相关组件自动配置类
 * </p>
 *
 * @author Lypxc
 * @since 2024-12-10
 * @version 1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(WxProperties.class)
public class WxAutoConfiguration {

    /**
     * LOGGER WxAutoConfiguration.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WxAutoConfiguration.class);

    /**
     * 微信状态管理多元化管理
     * @param wxProperties the wxProperties
     * @return IWxAppIdManager
     */
    @Bean
    public IWxManager wxManager(final WxProperties wxProperties) {
        final StorageType storageType = wxProperties.getStorageType();
        IWxManager wxManager;
        switch (storageType) {
            case Redisson:
                wxManager = new WxRedissonManager();
                break;
            case RedisTemplate:
                wxManager = new WxRedisTemplateManager();
                break;
            default:
                wxManager = new WxMemoryManager();
                break;
        }
        return wxManager;
    }

    /**
     * 微信公众号自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.mp.enabled", havingValue = "true")
    static class WxMpConfiguration {

        /**
         * 微信公众号初始化
         * @param wxProperties 属性配置
         * @return WxMpService
         */
        @Bean
        public WxMpService wxMpService(ObjectProvider<WxProperties> wxProperties) {
            LOGGER.info("配置微信公众号[WxMpService]成功！");
            return new PlusWxMpService(wxProperties.getIfAvailable()).build();
        }

        /**
         * 消息路由处理器
         * @param wxMpService wxMpService
         * @return WxMpMessageRouter
         */
        @Bean
        public WxMpMessageRouter wxMpMessageRouter(ObjectProvider<WxProperties> wxProperties,
                ObjectProvider<WxMpService> wxMpService) {
            return new PlusWxMpMessageRouter(wxProperties.getIfAvailable(), wxMpService.getIfAvailable()).build();
        }

    }

    /**
     * 微信小程序自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.ma.enabled", havingValue = "true")
    static class WxMaConfiguration {

        /**
         * 微信小程序初始化
         * @param wxProperties 属性配置
         * @return WxMaService
         */
        @Bean
        public WxMaService wxMaService(WxProperties wxProperties) {
            LOGGER.info("配置微信小程序[WxMaService]成功！");
            return new PlusWxMaService(wxProperties).build();
        }

        /**
         * 消息路由处理器
         * @param wxMaService wxMaService
         * @return WxMaMessageRouter
         */
        @Bean
        public WxMaMessageRouter wxMaMessageRouter(WxProperties wxProperties, WxMaService wxMaService) {
            return new PlusWxMaMessageRouter(wxProperties, wxMaService).build();
        }

    }

    /**
     * 微信开放平台自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.open.enabled", havingValue = "true")
    static class WxOpenConfiguration {

        /**
         * 微信开放平台 初始化
         * @param wxProperties 属性配置
         * @return WxOpenService
         */
        @Bean
        public WxOpenService wxOpenService(WxProperties wxProperties) {
            LOGGER.info("配置微信开放平台[WxOpenService]成功！");
            return new PlusWxOpenService(wxProperties).build();
        }

        /**
         * 微信开放平台 消息路由处理器
         * @return WxOpenMessageRouter
         */
        @Bean
        public WxOpenMessageRouter wxOpenMessageRouter(WxProperties wxProperties, WxOpenService wxOpenService) {
            return new PlusWxOpenMessageRouter(wxProperties, wxOpenService).build();
        }

    }

    /**
     * 企业号/企业微信自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.cp.enabled", havingValue = "true")
    static class WxCpConfiguration {

        /**
         * 企业号/企业微信初始化
         * @param wxProperties 属性配置
         * @return WxCpMultiService
         */
        @Bean
        public WxCpMultiService wxCpMultiService(WxProperties wxProperties) {
            LOGGER.info("配置企业号/企业微信[WxCpMultiService]成功！");
            return new PlusWxCpService(wxProperties).build();
        }

    }

    /**
     * 微信支付自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.pay.enabled", havingValue = "true")
    static class WxPayConfiguration {

        /**
         * 微信支付初始化
         * @param wxProperties 属性配置
         * @return WxPayService
         */
        @Bean
        public WxPayService wxPayService(ObjectProvider<WxProperties> wxProperties) {
            LOGGER.info("配置微信支付[WxPayService]成功！");
            return new PlusWxPayService(wxProperties.getIfAvailable()).build();
        }

    }

    /**
     * 微信视频号自动配置类
     */
    @Configuration
    @ConditionalOnProperty(name = "spring.pxc-framework-boot3.wx.channel.enabled", havingValue = "true")
    static class WxChannelConfiguration {

        /**
         * 微信视频号初始化
         * @param wxProperties 属性配置
         * @return WxChannelMultiService
         */
        @Bean
        public WxChannelMultiService wxChannelMultiService(WxProperties wxProperties) {
            LOGGER.info("配置微信视频号[WxChannelMultiService]成功！");
            return new PlusWxChannelService(wxProperties).build();
        }

        /**
         * 微信视频号 消息路由器
         * @return WxChannelMessageRouter
         */
        @Bean
        public WxChannelMessageRouter wxChannelMessageRouter() {
            return new WxChannelMessageRouter();
        }

    }

}
