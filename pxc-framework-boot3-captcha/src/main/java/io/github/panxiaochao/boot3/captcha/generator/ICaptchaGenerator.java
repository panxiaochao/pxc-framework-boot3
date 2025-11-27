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
package io.github.panxiaochao.boot3.captcha.generator;

/**
 * <p>
 * 验证码生成器.
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-07
 * @version 1.0
 */
public interface ICaptchaGenerator {

    /**
     * 生成验证码.
     */
    String generateCode();

    /**
     * 验证用户输入的字符串是否与生成的验证码匹配
     * @param code 生成的随机验证码
     * @param inputCode 用户输入的验证码
     * @return 是否验证通过 true or false
     */
    boolean verify(String code, String inputCode);

}
