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
package io.github.panxiaochao.core.config;

import io.github.panxiaochao.core.utils.JdkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * <p>
 * 虚拟线程配置，仅JDK21及以上版本生效
 * </p>
 *
 * @author lypxc
 * @since 2025-08-12
 * @version 1.0
 */
@AutoConfiguration
public class VirtualThreadConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualThreadConfig.class);

    private static final String NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR = "newVirtualThreadPerTaskExecutor";

    /**
     * 虚拟线程配置，仅JDK21及以上版本生效
     * @return TomcatProtocolHandlerCustomizer
     */
    @Bean
    @Conditional(Jdk21OrHigherCondition.class)
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
        return protocolHandler -> {
            try {
                // 使用反射避免直接引用JDK21+方法
                Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
                Method newVirtualThreadMethod = executorsClass.getMethod(NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR);
                Executor virtualThreadExecutor = (Executor) newVirtualThreadMethod.invoke(null);
                protocolHandler.setExecutor(virtualThreadExecutor);
                LOGGER.info("[启用虚拟线程支持成功]");
            }
            catch (Exception e) {
                LOGGER.error("启用虚拟线程失败，将使用默认线程池", e);
            }
        };
    }

    /**
     * 虚拟线程执行器，name = virtualThreadExecutor <br/>
     * <pre>
     *     使用方法: &#64;Async("virtualThreadExecutor")
     * </pre>
     * @return Executor
     */
    @Bean(name = "virtualThreadExecutor")
    @Conditional(Jdk21OrHigherCondition.class)
    public Executor virtualThreadExecutor() {
        try {
            // 使用反射避免直接引用JDK21+方法
            Class<?> executorsClass = Class.forName("java.util.concurrent.Executors");
            Method newVirtualThreadMethod = executorsClass.getMethod(NEW_VIRTUAL_THREAD_PER_TASK_EXECUTOR);
            LOGGER.info("[启用虚拟线程执行器成功]");
            return (Executor) newVirtualThreadMethod.invoke(null);
        }
        catch (Exception e) {
            LOGGER.error("启用虚拟线程执行器失败，将使用默认线程池", e);
            throw new RuntimeException("启用虚拟线程执行器失败", e);
        }
    }

    /**
     * JDK版本条件判断：仅JDK21及以上版本生效
     */
    static class Jdk21OrHigherCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            try {
                return JdkUtil.IS_GTE_JDK21;
            }
            catch (Exception e) {
                return false;
            }
        }

    }

}
