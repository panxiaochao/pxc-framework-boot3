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

import io.github.panxiaochao.boot3.captcha.utils.CaptchaSymbolPool;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * <p>
 * 验证码内容类型.
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-07
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {

    /**
     * 字母数字混合
     */
    CHAR_NUMBER(1, CaptchaSymbolPool.CHAR_NUMBER_MIX),

    /**
     * 纯数字
     */
    NUMBER(2, CaptchaSymbolPool.NUMBER),

    /**
     * 纯大写字母
     */
    CHAR_UPPER(3, CaptchaSymbolPool.CHAR_UPPER),

    /**
     * 纯小写字母
     */
    CHAR_LOWER(4, CaptchaSymbolPool.CHAR_LOWER),

    /**
     * 中文
     */
    MODERN_CHINESE(5, CaptchaSymbolPool.MODERN_CHINESE);

    private final int index;

    private final char[] source;

    public char[] of(CaptchaType captchaType) {
        if (Optional.ofNullable(captchaType).isPresent()) {
            for (CaptchaType captchaTypeObj : values()) {
                if (captchaTypeObj.equals(captchaType)) {
                    return captchaTypeObj.getSource();
                }
            }
        }
        throw new NullPointerException("CaptchaType is null!");
    }

}
