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
package io.github.panxiaochao.holiday.core;

import io.github.panxiaochao.core.utils.Singleton;
import io.github.panxiaochao.core.utils.StringPools;
import io.github.panxiaochao.holiday.constants.HolidayConstant;
import io.github.panxiaochao.holiday.entity.Holiday;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * <p>
 * 节假日客户端
 * </p>
 * <p>
 * 《全国年节及纪念日放假办法》（国务院令第270号）规定：
 * </p>
 *
 * <ul>
 * <li>1、每一周的法定工作日为：5天(原则上：自每周一至周五，周六和周日为工休日——完全适用国有机关和事业单位，企业除外：企业单位可根据实际情况灵活安排周休息日)
 * <li>2、全年的法定工休日为：104天(12个月所有的周六和周日)；
 * <li>3、全年的法定节假日为：11天(元旦1天、春节3天、清明节1天、五一劳动节1天、端午节1天、中秋节1天、国庆节3天)；
 * <li>4、一年的法定工作日为：365天/年-104天/年法定工休日-11天/年法定节假日=250天；
 * <li>5、每一个季度的法定工作日为：250天/年法定工作日÷4个季度=62.50天；
 * <li>6、每一个月法定工作日为：250天/年法定工作日÷12个月=20.83天。
 * </ul>
 *
 * @author Lypxc
 * @since 2024-04-02
 * @version 1.0
 */
@RequiredArgsConstructor
public class HolidayClient {

    /**
     * LOGGER HolidayClient.class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HolidayClient.class);

    /**
     * 是否是工作日
     * @param day 日期
     * @return true Or false
     */
    public boolean isWeekday(String day) {
        return !isPublicHoliday(day);
    }

    /**
     * 是否是工休日（周六和周日）
     * @param day 日期
     * @return true Or false
     */
    public boolean isPublicHoliday(String day) {
        LocalDate localDate = parseAndValidateDate(day);
        int week = localDate.getDayOfWeek().getValue();
        return week == 6 || week == 7;
    }

    /**
     * 是否是节假日
     * @param day 日期
     * @return true Or false
     */
    public boolean isHoliday(String day) {
        LocalDate localDate = parseAndValidateDate(day);
        Holiday holiday = getHolidayOfYear(String.valueOf(localDate.getYear()));
        return holiday != null && holiday.getDays().stream().anyMatch(f -> f.getDate().equals(day));
    }

    /**
     * 是否需要补班
     * @param day 日期
     * @return true Or false
     */
    public boolean isWorkDay(String day) {
        LocalDate localDate = parseAndValidateDate(day);
        Holiday holiday = getHolidayOfYear(String.valueOf(localDate.getYear()));
        return holiday != null && holiday.getWorkdays().stream().anyMatch(f -> f.getDate().equals(day));
    }

    /**
     * 获取某一年假期的天数
     * @param year 年份
     * @return 假期天数
     */
    public int holidaysCount(String year) {
        Holiday holiday = getHolidayOfYear(year);
        return holiday != null ? holiday.getDays().size() : 0;
    }

    /**
     * 获取某一天的假期信息
     * @param day 日期
     * @return 假期信息
     */
    public Holiday.Days getHoliday(String day) {
        LocalDate localDate = parseAndValidateDate(day);
        Holiday holiday = getHolidayOfYear(String.valueOf(localDate.getYear()));
        if (holiday == null) {
            return null;
        }
        return holiday.getDays().stream().filter(f -> f.getDate().equals(day)).findFirst().orElse(null);
    }

    /**
     * 解析日期为LocalDate 和 验证日期格式
     * @param day 日期
     * @return LocalDate
     */
    private LocalDate parseAndValidateDate(String day) {
        if (!StringUtils.hasText(day) || day.split(StringPools.DASH).length != 3) {
            throw new IllegalArgumentException("Check Invalid day: " + day + ", make sure date format 'yyyy-MM-dd'");
        }
        String[] days = StringUtils.split(day, StringPools.SPACE);
        // 兼容带空格
        if (days != null && days.length > 0) {
            day = days[0];
        }
        try {
            // 1. 使用 uuuu 代替 yyyy，u 代表的是带符号年份（Signed Year），它不需要指定时代（默认就是公元后）。
            // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            // .withResolverStyle(ResolverStyle.STRICT);
            // 2.兼容 1900 年之前, 手动设置 1，表示公元 0 表示公元前
            // DateTimeFormatter formatter = new
            // DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
            // .parseDefaulting(ChronoField.ERA, 1)
            // .toFormatter()
            // .withResolverStyle(ResolverStyle.STRICT);
            // 3.直接使用 LocalDate.parse() 方法，自带严格模式
            return LocalDate.parse(day);
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Parse Invalid day: " + day + ", errMsg: " + e.getMessage());
        }
    }

    /**
     * 获取指定年份的 Holiday 数据
     * @param year 年份
     * @return Holiday 对象
     */
    private Holiday getHolidayOfYear(String year) {
        Holiday holiday = Singleton.INST.get(HolidayConstant.KEY_PREFIX + year);
        if (holiday == null) {
            LOGGER.error("没有对应年[{}]的数据，请升级或者自行维护数据！", year);
        }
        return holiday;
    }

}
