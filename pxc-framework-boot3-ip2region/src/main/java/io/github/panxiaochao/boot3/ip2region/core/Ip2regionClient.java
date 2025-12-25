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
package io.github.panxiaochao.boot3.ip2region.core;

import io.github.panxiaochao.boot3.ip2region.config.properties.Ip2regionProperties;
import io.github.panxiaochao.boot3.ip2region.constants.Ip2regionConstant;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * <p>
 * IP 转换地址模块客户端类
 * </p>
 *
 * @author lypxc
 * @since 2025-10-09
 * @version 1.0
 */
public class Ip2regionClient implements InitializingBean {

    /**
     * LOGGER HolidayProperties.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Ip2regionClient.class);

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private final Ip2regionProperties ip2regionProperties;

    private static Ip2Region IP_SEARCHER;

    public Ip2regionClient(Ip2regionProperties ip2regionProperties) {
        this.ip2regionProperties = ip2regionProperties;
    }

    /**
     * IP解析, 返回{@link IpInfo}对象
     * @param ip 解析的ip
     * @return IpInfo
     */
    public IpInfo memorySearch(String ip) {
        try {
            IpInfo ipInfo = IpInfo.toIpInfo(IP_SEARCHER.search(ip));
            ipInfo.setIp(ip);
            return ipInfo;
        }
        catch (Exception e) {
            LOGGER.error("memorySearch ip {} parse is error", ip, e);
            throw new RuntimeException("memorySearch ip " + ip + " parse is error: " + e.getMessage());
        }
    }

    /**
     * 读取 {@link IpInfo} 中的信息
     * @param ip ip
     * @param function Function
     * @return 地址
     */
    public String getInfo(String ip, Function<IpInfo, String> function) {
        return IpInfo.readInfo(memorySearch(ip), function);
    }

    /**
     * 构建 Config 对象
     * @param inputStream xdb 输入流
     * @param isV6 是否为 IPv6
     * @return Config
     */
    private Config buildConfig(InputStream inputStream, boolean isV6) {
        try {
            final ConfigBuilder configBuilder = Config.custom()
                // 当指定 xdbInputStream 时，CachePolicy 必须为 BufferCache
                .setCachePolicy(Config.BufferCache)
                .setSearchers(15)
                .setXdbInputStream(inputStream);
            if (isV6) {
                return configBuilder.asV6();
            }
            return configBuilder.asV4();
        }
        catch (Exception e) {
            throw new RuntimeException("构建 Config 失败", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String v4dbLocation = ip2regionProperties.getV4dbLocation();
        Config v4Config;
        if (StringUtils.hasText(v4dbLocation)) {
            try {
                Resource v4Resource = this.resourcePatternResolver.getResource(v4dbLocation);
                v4Config = buildConfig(v4Resource.getInputStream(), false);
                LOGGER.info("配置自定义[ip2region_v4]成功！");
            }
            catch (IOException e) {
                throw new RuntimeException("未找到自定义IPV4数据库文件：" + v4dbLocation, e);
            }
        }
        else {
            // 默认加载自带的 ip2region_v4.db 数据库
            Resource v4Resource = this.resourcePatternResolver.getResource(Ip2regionConstant.IP2REGION_V4_DB_LOCATION);
            v4Config = buildConfig(v4Resource.getInputStream(), false);
            LOGGER.info("配置默认[ip2region_v4]成功！");
        }

        // 自定义 IPV6 数据库
        String v6dbLocation = ip2regionProperties.getV6dbLocation();
        Config v6Config = null;
        if (StringUtils.hasText(v6dbLocation)) {
            try {
                Resource v6Resource = this.resourcePatternResolver.getResource(v6dbLocation);
                v6Config = buildConfig(v6Resource.getInputStream(), true);
                LOGGER.info("配置自定义[ip2region_v6]成功！");
            }
            catch (IOException e) {
                throw new RuntimeException("未找到自定义IPV6数据库文件：" + v6dbLocation, e);
            }
        }

        // 通过上述配置创建 Ip2Region 查询服务
        IP_SEARCHER = org.lionsoul.ip2region.service.Ip2Region.create(v4Config, v6Config);
    }

}
