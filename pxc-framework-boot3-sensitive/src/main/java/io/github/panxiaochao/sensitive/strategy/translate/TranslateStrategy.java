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
package io.github.panxiaochao.sensitive.strategy.translate;

import io.github.panxiaochao.sensitive.strategy.IStrategy;
import io.github.panxiaochao.sensitive.utils.TranslateUtil;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * <p>
 * 翻译策略
 * </p>
 *
 * @author Lypxc
 * @since 2024-06-11
 * @version 1.0
 */
@AllArgsConstructor
public enum TranslateStrategy implements IStrategy<Object> {

    /**
     * 布尔值翻译
     */
    BOOLEAN(TranslateUtil::toBoolean),
    /**
     * 默认, 原值返回
     */
    DEFAULT(s -> s);

    private final Function<String, Object> translate;

    @Override
    public Function<String, Object> use() {
        return this.translate;
    }

}
