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
package io.github.panxiaochao.boot3.captcha.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 标识符
 *
 * @author L.cm
 */
@Getter
@RequiredArgsConstructor
public enum MathSymbol {

    /**
     * 加法
     */
    ADD("+", false),

    /**
     * 减发
     */
    SUB("-", false),

    /**
     * 乘法
     */
    MUL("x", true);

    /**
     * 算数符号
     */
    private final String symbol;

    /**
     * 是否优先计算
     */
    private final boolean priority;

    public static MathSymbol of(String c) {
        for (MathSymbol value : values()) {
            if (value.symbol.equals(c)) {
                return value;
            }
        }
        throw new IllegalArgumentException("不支持的标识符，仅仅支持(+、-、×、÷)");
    }

}
