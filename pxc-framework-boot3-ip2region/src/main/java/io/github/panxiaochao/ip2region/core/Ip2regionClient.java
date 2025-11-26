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
package io.github.panxiaochao.ip2region.core;

import io.github.panxiaochao.ip2region.config.properties.Ip2regionProperties;
import io.github.panxiaochao.ip2region.constants.Ip2regionConstant;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.function.Function;

/**
 * <p>
 * IP转换地址模块客户端类
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

    private static Searcher SEARCHER_V4;

    private static Searcher SEARCHER_V6;

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
            String[] ipV4Part = IpInfo.getIpv4Part(ip);
            if (ipV4Part.length == 4) {
                IpInfo ipInfo = IpInfo.toIpInfo(SEARCHER_V4.search(ip));
                ipInfo.setIp(ip);
                return ipInfo;
            }
            else if (ip.contains(":")) {
                if (SEARCHER_V6 == null) {
                    LOGGER.warn("IPV6 未初始化，请检查配置");
                }
                else {
                    IpInfo ipInfo = IpInfo.toIpInfo(SEARCHER_V6.search(ip));
                    ipInfo.setIp(ip);
                    return ipInfo;
                }
            }
            else {
                // 3.不合法 IP
                LOGGER.error("invalid ip address {}", ip);
            }
            return null;
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

    /**
     * 从内存加载DB数据
     * @param filePath 路径
     * @return byte[]
     */
    private LongByteArray loadContentFromFile(String filePath) {
        Resource[] resources = getResources(filePath);
        for (Resource resource : resources) {
            Assert.isTrue(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your ip2region db configuration)");
            // try {
            // File file = resource.getFile();
            // validateDbFromPath(file);
            // }
            // catch (IOException e) {
            // throw new RuntimeException(e);
            // }
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                final LongByteArray byteArray = new LongByteArray();
                byteArray.append(bytes);
                return byteArray;
            }
            catch (IOException e) {
                throw new RuntimeException("load ip2region file db is error", e);
            }
        }
        return null;
    }

    /**
     * 验证xdb文件是否适配当前Searcher客户端
     * @param dbFile 路径
     */
    private void validateDbFromPath(File dbFile) {
        try {
            // mode: r 只读模式打开文件
            final RandomAccessFile handle = new RandomAccessFile(dbFile, "r");
            Searcher.verify(handle);
            handle.close();
        }
        catch (Exception e) {
            // 适用性验证失败！！！
            // 当前查询客户端实现不适用于 dbPath 指定的 xdb 文件的查询.
            // 应该停止启动服务，使用合适的 xdb 文件或者升级到适合 dbPath 的 Searcher 实现。
            LOGGER.error("当前查询客户端实现不适用于 dbPath 指定的 xdb 文件的查询. 路径：{}", dbFile.getPath());
            throw new RuntimeException("当前查询客户端实现不适用于 dbPath 指定的 xdb 文件的查询. 路径：" + dbFile.getPath());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String v4dbLocation = ip2regionProperties.getV4dbLocation();
        if (StringUtils.hasText(v4dbLocation)) {
            LongByteArray byteArray = loadContentFromFile(v4dbLocation);
            SEARCHER_V4 = Searcher.newWithBuffer(Version.IPv4, byteArray);
            LOGGER.info("配置自定义[ip2region_v4]成功！");
        }
        else {
            // 默认加载自带的 ip2region_v4.db 数据库
            LongByteArray byteArray = loadContentFromFile(Ip2regionConstant.IP2REGION_V4_DB_LOCATION);
            SEARCHER_V4 = Searcher.newWithBuffer(Version.IPv4, byteArray);
            LOGGER.info("配置默认[ip2region_v4]成功！");
        }
        // 自定义 IPV6 数据库
        String v6dbLocation = ip2regionProperties.getV6dbLocation();
        if (StringUtils.hasText(v6dbLocation)) {
            LongByteArray byteArray = loadContentFromFile(v6dbLocation);
            SEARCHER_V6 = Searcher.newWithBuffer(Version.IPv6, byteArray);
            LOGGER.info("配置自定义[ip2region_v6]成功！");
        }
    }

}
