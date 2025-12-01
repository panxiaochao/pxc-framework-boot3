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
package io.github.panxiaochao.boot3.core.utils.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import io.github.panxiaochao.boot3.core.utils.date.DatePattern;
import io.github.panxiaochao.boot3.core.utils.jackson.jsonserializer.BigNumberSerializer;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;

/**
 * <p>
 * 自定义 Jackson TimeModule.
 * </p>
 *
 * @author Lypxc
 * @since 2023-06-06
 */
public class CustomizeJavaTimeModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 默认日期时间格式
     */
    private static final String LOCAL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    private static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    private static final String DATE_TIME_FORMAT = "HH:mm:ss";

    public CustomizeJavaTimeModule() {
        super(PackageVersion.VERSION);
        // ====== Serialize ======
        // yyyy
        this.addSerializer(Year.class, new YearSerializer(DatePattern.NORMAL_YEAR_FORMATTER));
        // yyyy-MM
        this.addSerializer(YearMonth.class, new YearMonthSerializer(DatePattern.NORMAL_YEAR_MONTH_FORMATTER));
        // yyyy-MM-dd HH:mm:ss
        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DatePattern.NORMAL_DATE_TIME_FORMATTER));
        // yyyy-MM-dd
        this.addSerializer(LocalDate.class, new LocalDateSerializer(DatePattern.NORMAL_DATE_FORMATTER));
        // HH:mm:ss
        this.addSerializer(LocalTime.class, new LocalTimeSerializer(DatePattern.NORMAL_TIME_FORMATTER));
        // Instant 时间戳
        this.addSerializer(Instant.class, InstantSerializer.INSTANCE);
        // Duration 类型
        this.addSerializer(Duration.class, DurationSerializer.INSTANCE);
        // 数值型
        this.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        this.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        this.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        this.addSerializer(BigDecimal.class, BigNumberSerializer.INSTANCE);

        // ====== Deserialize ======
        // yyyy
        this.addDeserializer(Year.class, new YearDeserializer(DatePattern.NORMAL_YEAR_FORMATTER));
        // yyyy-MM
        this.addDeserializer(YearMonth.class, new YearMonthDeserializer(DatePattern.NORMAL_YEAR_MONTH_FORMATTER));
        // yyyy-MM-dd HH:mm:ss
        this.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DatePattern.NORMAL_DATE_TIME_FORMATTER));
        // yyyy-MM-dd
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DatePattern.NORMAL_DATE_FORMATTER));
        // HH:mm:ss
        this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DatePattern.NORMAL_TIME_FORMATTER));
        // Instant 时间戳
        this.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        // Duration 类型
        this.addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
    }

}
