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
package io.github.panxiaochao.boot3.holiday.config.properties;

import io.github.panxiaochao.boot3.core.utils.JacksonUtil;
import io.github.panxiaochao.boot3.core.utils.ResourceUtil;
import io.github.panxiaochao.boot3.core.utils.Singleton;
import io.github.panxiaochao.boot3.holiday.constants.HolidayConstant;
import io.github.panxiaochao.boot3.holiday.entity.Holiday;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * <p>
 * Holiday 属性文件
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-03
 * @version 1.0
 */
@ConfigurationProperties(prefix = "spring.pxc-framework-boot3.holiday", ignoreInvalidFields = true)
public class HolidayProperties implements InitializingBean {

    /**
     * LOGGER HolidayProperties.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayProperties.class);

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * <p>
     * 自定义扩展json存放路径
     * </p>
     * <pre>
     * 1.加载类路径中: classpath*: /data/**&#47;*.json
     * 2.文件系统路径: file: C:/some/path/**&#47;*.json
     * </pre>
     */
    @Getter
    @Setter
    private String[] jsonLocations;

    /**
     * 是否覆盖已有年份数据, 默认false
     */
    @Getter
    @Setter
    private boolean overwrite;

    public Resource[] resolveDefaultJsonLocation() {
        return Stream.of(HolidayConstant.DEFAULT_JSON_LOCATION)
            .flatMap(location -> Stream.of(getResources(location)))
            .toArray(Resource[]::new);
    }

    public Resource[] getResources(String location) {
        try {
            return this.resourcePatternResolver.getResources(location);
        }
        catch (IOException e) {
            return new Resource[0];
        }
    }

    /**
     * 初始化工作
     */
    @Override
    public void afterPropertiesSet() {
        Resource[] resources = resolveDefaultJsonLocation();
        for (Resource resource : resources) {
            Assert.isTrue(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your holiday json configuration)");
            try (InputStream inputStream = resource.getInputStream()) {
                String json = ResourceUtil.read(inputStream);
                Holiday holiday = JacksonUtil.toBean(json, Holiday.class);
                Objects.requireNonNull(holiday, "holiday cannot be null");
                Singleton.INST.single(HolidayConstant.KEY_PREFIX + holiday.getYear(), holiday);
            }
            catch (IOException e) {
                throw new IllegalStateException("Error resolveResource", e);
            }
        }
        // 加载自定义路径
        if (Optional.ofNullable(jsonLocations).isPresent()) {
            Arrays.stream(jsonLocations).forEach(location -> {
                Resource[] locationResources = getResources(location);
                for (Resource resource : locationResources) {
                    Assert.isTrue(resource.exists(), "Cannot find config location: " + resource
                            + " (please add config file or check your holiday json configuration)");
                    try (InputStream inputStream = resource.getInputStream()) {
                        String json = ResourceUtil.read(inputStream);
                        Holiday holiday = JacksonUtil.toBean(json, Holiday.class);
                        if (holiday != null && holiday.getYear() != null) {
                            // 覆盖 或 年份数据为空
                            if (overwrite
                                    || null == Singleton.INST.get(HolidayConstant.KEY_PREFIX + holiday.getYear())) {
                                Singleton.INST.single(HolidayConstant.KEY_PREFIX + holiday.getYear(), holiday);
                            }
                            else {
                                LOGGER.warn("年份{}已存在，不覆盖！", holiday.getYear());
                            }
                        }
                        else {
                            LOGGER.error("自定义Holiday文件[{}]数据格式有误，请检查！", resource.getFile().getName());
                        }
                    }
                    catch (IOException e) {
                        throw new IllegalStateException("Error resolveResource", e);
                    }
                }
            });
        }
        else {
            LOGGER.warn("自定义Holiday location路径为空，不加载！");
        }
    }

}
