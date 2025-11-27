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
package io.github.panxiaochao.boot3.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.panxiaochao.boot3.sensitive.serializer.jackson.SensitiveJackJsonSerializer;
import io.github.panxiaochao.boot3.sensitive.strategy.IHandler;
import io.github.panxiaochao.boot3.sensitive.strategy.sensitive.SensitiveStrategy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 脱敏注解
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-31
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJackJsonSerializer.class)
@Documented
public @interface Sensitive {

    /**
     * 处理策略，当且仅handler是默认处理{@link IHandler}情况下生效
     */
    SensitiveStrategy strategy() default SensitiveStrategy.DEFAULT;

    /**
     * 自定义处理方法
     */
    Class<? extends IHandler> handler() default IHandler.class;

}
