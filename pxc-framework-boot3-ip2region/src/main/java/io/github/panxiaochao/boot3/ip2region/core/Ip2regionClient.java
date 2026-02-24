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

import io.github.panxiaochao.boot3.ip2region.constants.Ip2regionConstant;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
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
public class Ip2regionClient implements InitializingBean, DisposableBean {

    /**
     * LOGGER HolidayProperties.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Ip2regionClient.class);

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private static Ip2Region IP_SEARCHER;

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
     * 关闭Ip2Region服务
     */
    public void close() {
        if (IP_SEARCHER == null) {
            return;
        }
        try {
            IP_SEARCHER.close(10000);
        }
        catch (Exception e) {
            LOGGER.error("Ip2Region服务关闭异常", e);
        }
    }

    /**
     * 关闭Ip2Region服务
     * @param timeout 关闭超时时间
     */
    public void close(final Duration timeout) {
        if (IP_SEARCHER == null) {
            return;
        }
        if (timeout == null) {
            close();
            return;
        }
        try {
            IP_SEARCHER.close(timeout.toMillis());
        }
        catch (Exception e) {
            LOGGER.error("Ip2Region服务关闭异常", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        IP_SEARCHER.close();
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
            throw new RuntimeException("构建 Ip2region Config 失败", e);
        }
    }

    /**
     * 获取资源
     * @param location 路径
     * @return Resource[]
     */
    private Resource[] getResources(String location) {
        try {
            return this.resourcePatternResolver.getResources(location);
        }
        catch (IOException e) {
            return new Resource[0];
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Config v4Config = null;
        // 加载位置在classpath下的ip2region文件夹下的 ip2region_v4.db 数据库
        // 建议读取项目中的自带数据库，方便对视更新Xdb数据库
        // 本次版本已去除自带的 ip2region_v4.db 数据库，减少Jar打包体积
        Resource[] v4Resources = getResources(Ip2regionConstant.IP2REGION_V4_DB_LOCATION);
        if (v4Resources.length > 0) {
            for (Resource resource : v4Resources) {
                try (InputStream v4InputStream = resource.getInputStream()) {
                    v4Config = buildConfig(v4InputStream, false);
                    LOGGER.info("配置自定义[ip2region_v4]成功！");
                }
                catch (IOException e) {
                    throw new RuntimeException("初始化IPV4数据库文件失败，检查数据库路径是否正确放置 resources/ip2region 任意目录下", e);
                }
            }
        }
        else {
            LOGGER.warn("未检测到 ip2region_v4.xdb 数据库文件，如需使用Ipv4检索功能，请将 ip2region_v4.xdb 放置到 resources/ip2region 任意目录下");
        }

        // 自定义 IPV6 数据库
        Config v6Config = null;
        Resource[] v6Resources = getResources(Ip2regionConstant.IP2REGION_V6_DB_LOCATION);
        if (v6Resources.length > 0) {
            for (Resource resource : v6Resources) {
                try (InputStream v6InputStream = resource.getInputStream()) {
                    v6Config = buildConfig(v6InputStream, true);
                    LOGGER.info("配置自定义[ip2region_v6]成功！");
                }
                catch (IOException e) {
                    throw new RuntimeException("初始化IPV6数据库文件失败，检查数据库路径是否正确放置 resources/ip2region 任意目录下", e);
                }
            }
        }
        else {
            LOGGER.warn("未检测到 ip2region_v6.xdb 数据库文件，如需使用Ipv6检索功能，请将 ip2region_v6.xdb 放置到 resources/ip2region 任意目录下");
        }

        // 通过上述配置创建 Ip2Region 查询服务
        IP_SEARCHER = Ip2Region.create(v4Config, v6Config);
    }

}
