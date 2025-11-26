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
package io.github.panxiaochao.holiday.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 日期类型，工作日-0, 工休日（休息日）-1, 节假日-2
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-02
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public enum HolidayType {

    /**
     * 工作日
     */
    WEEKDAY(0, "工作日"),
    /**
     * 公休日（休息日）
     */
    PUBLIC_HOLIDAY(1, "公休日"),
    /**
     * 节假日
     */
    HOLIDAY(2, "节假日");

    public final int type;

    public final String name;

    /**
     * 根据type返回日期类型
     * @param type 值
     * @return 日期类型
     */
    public HolidayType ofType(int type) {
        for (HolidayType holidayType : values()) {
            if (holidayType.type == type) {
                return holidayType;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }

}
