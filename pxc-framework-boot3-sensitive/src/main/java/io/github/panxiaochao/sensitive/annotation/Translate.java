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
package io.github.panxiaochao.sensitive.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.panxiaochao.sensitive.serializer.jackson.TranslateJackJsonSerializer;
import io.github.panxiaochao.sensitive.strategy.IHandler;
import io.github.panxiaochao.sensitive.strategy.translate.TranslateStrategy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 翻译注解
 * </p>
 *
 * @author Lypxc
 * @since 2024-06-05
 * @version 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = TranslateJackJsonSerializer.class)
@Documented
public @interface Translate {

    /**
     * 处理策略，当且仅handler是默认处理{@link IHandler}情况下生效
     */
    TranslateStrategy strategy() default TranslateStrategy.DEFAULT;

    /**
     * 自定义处理方法
     */
    Class<? extends IHandler> handler() default IHandler.class;

}
