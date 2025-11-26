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
package io.github.panxiaochao.sensitive.strategy.sensitive;

import io.github.panxiaochao.sensitive.strategy.IStrategy;
import io.github.panxiaochao.sensitive.utils.DesensitizeUtil;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * <p>
 * 脱敏策略
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-31
 */
@AllArgsConstructor
public enum SensitiveStrategy implements IStrategy<String> {

    /**
     * 身份证脱敏
     */
    ID_CARD(s -> DesensitizeUtil.idCardNum(s, 3, 4)),
    /**
     * 姓名
     */
    FULL_NAME(DesensitizeUtil::chineseName),
    /**
     * 手机号脱敏
     */
    PHONE(DesensitizeUtil::mobilePhone),
    /**
     * 电话号码
     */
    MOBILE(DesensitizeUtil::fixedPhone),
    /**
     * 地址脱敏
     */
    ADDRESS(s -> DesensitizeUtil.address(s, 8)),
    /**
     * 邮箱脱敏
     */
    EMAIL(DesensitizeUtil::email),
    /**
     * 银行卡
     */
    BANK_CARD(DesensitizeUtil::bankCard),
    /**
     * 密码
     */
    PASSWORD(DesensitizeUtil::password),
    /**
     * 车牌
     */
    CAR_NUMBER(DesensitizeUtil::carLicense),

    /**
     * 中文名
     */
    CHINESE_NAME(DesensitizeUtil::chineseName),

    /**
     * 固定电话
     */
    FIXED_PHONE(DesensitizeUtil::fixedPhone),

    /**
     * 用户ID
     */
    USER_ID(s -> String.valueOf(DesensitizeUtil.userId())),
    /**
     * ipv4
     */
    IPV4(DesensitizeUtil::ipv4),

    /**
     * ipv6
     */
    IPV6(DesensitizeUtil::ipv6),

    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE(DesensitizeUtil::carLicense),

    /**
     * 只显示第一个字符
     */
    FIRST_MASK(DesensitizeUtil::firstMask),

    /**
     * 清空为null
     */
    CLEAR(s -> DesensitizeUtil.clear()),

    /**
     * 清空为""
     */
    CLEAR_TO_NULL(s -> DesensitizeUtil.clearToNull()),
    /**
     * 默认, 原值返回
     */
    DEFAULT(s -> s);

    private final Function<String, String> desensitize;

    @Override
    public Function<String, String> use() {
        return this.desensitize;
    }

}
