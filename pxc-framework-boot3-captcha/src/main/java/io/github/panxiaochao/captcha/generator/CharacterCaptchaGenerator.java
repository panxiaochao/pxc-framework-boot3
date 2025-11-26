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
package io.github.panxiaochao.captcha.generator;

import io.github.panxiaochao.captcha.enums.CaptchaType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * 字符验证码生成器.
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-07
 * @version 1.0
 */
public class CharacterCaptchaGenerator implements ICaptchaGenerator {

    private static final int DEFAULT_LENGTH = 4;

    private final int length;

    private final char[] source;

    public CharacterCaptchaGenerator() {
        this(DEFAULT_LENGTH, CaptchaType.CHAR_NUMBER);
    }

    public CharacterCaptchaGenerator(CaptchaType captchaType) {
        this(DEFAULT_LENGTH, captchaType);
    }

    public CharacterCaptchaGenerator(int length, CaptchaType captchaType) {
        this.length = length;
        this.source = captchaType.getSource();
    }

    private static ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    /**
     * 生成验证码.
     */
    @Override
    public String generateCode() {
        int size = this.source.length;
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < this.length; j++) {
            sb.append(this.source[random().nextInt(size)]);
        }
        return sb.toString();
    }

    /**
     * 验证用户输入的字符串是否与生成的验证码匹配
     * @param code 生成的随机验证码
     * @param inputCode 用户输入的验证码
     * @return 是否验证通过 true or false
     */
    @Override
    public boolean verify(String code, String inputCode) {
        return false;
    }

}
