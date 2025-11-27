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
package io.github.panxiaochao.boot3.core.utils;

import java.util.regex.Pattern;

/**
 * <p>
 * 正则表达式模式合集.
 * </p>
 *
 * @author Lypxc
 * @since 2024-10-11
 * @version 1.0
 */
public interface PatternPools {

    /**
     * 正则表达式：数字
     */
    Pattern NUMBERS = Pattern.compile(RegexPools.NUMBERS);

    /**
     * 字母
     */
    Pattern CHARS = Pattern.compile(RegexPools.CHARS);

    /**
     * QQ号码
     */
    Pattern QQ_NUMBER = Pattern.compile(RegexPools.QQ_NUMBER);

    /**
     * 邮政编码，兼容港澳台
     */
    Pattern POSTAL_CODE = Pattern.compile(RegexPools.POSTAL_CODE);

    /**
     * 18位身份证号码
     */
    Pattern CITIZEN_ID = Pattern.compile(RegexPools.CITIZEN_ID);

    /**
     * 注册账号, 格式：大小写字母、数字、下划线、4-15位
     */
    Pattern ACCOUNT = Pattern.compile(RegexPools.ACCOUNT);

    /**
     * 密码：包含至少8个字符，包括大写字母、小写字母、数字和特殊字符
     */
    Pattern PASSWORD = Pattern.compile(RegexPools.PASSWORD);

    /**
     * 统一社会信用代码
     *
     * <pre>
     * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
     * 第二部分：机构类别代码1位 (数字或大写英文字母)
     * 第三部分：登记管理机关行政区划码6位 (数字)
     * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
     * 第五部分：校验码1位 (数字或大写英文字母)
     * </pre>
     */
    Pattern CREDIT_CODE = Pattern.compile(RegexPools.CREDIT_CODE);

    /**
     * 中国车牌号码（兼容新能源车牌）
     */
    Pattern PLATE_NUMBER = Pattern.compile(RegexPools.PLATE_NUMBER);

    /**
     * Http URL（来自：<a href="http://urlregex.com/">urlregex</a>）<br>
     * 此正则同时支持FTP、File等协议的URL
     */
    Pattern URL_HTTP = Pattern.compile(RegexPools.URL_HTTP);

    /**
     * 邮件，符合RFC 5322规范，正则来自：<a href="http://emailregex.com/">emailregex</a>
     */
    Pattern EMAIL = Pattern.compile(RegexPools.EMAIL);

}
