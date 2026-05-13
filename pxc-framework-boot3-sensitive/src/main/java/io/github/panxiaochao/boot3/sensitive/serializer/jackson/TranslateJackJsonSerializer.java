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
package io.github.panxiaochao.boot3.sensitive.serializer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.panxiaochao.boot3.common.constants.CommonResponseEnum;
import io.github.panxiaochao.boot3.common.exception.ServerRuntimeException;
import io.github.panxiaochao.boot3.sensitive.annotation.Translate;
import io.github.panxiaochao.boot3.sensitive.strategy.IHandler;
import io.github.panxiaochao.boot3.sensitive.strategy.IStrategy;
import io.github.panxiaochao.boot3.sensitive.utils.InvokeMethodUtil;
import io.github.panxiaochao.boot3.utils.ObjectUtil;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * <p>
 * Jackson 翻译序列化
 * </p>
 *
 * @author Lypxc
 * @since 2024-06-11
 * @version 1.0
 */
public class TranslateJackJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 策略
     */
    private final IStrategy<Object> strategy;

    /**
     * 自定义策略 className
     */
    private final String strategyClassName;

    public TranslateJackJsonSerializer() {
        this(null, null);
    }

    public TranslateJackJsonSerializer(IStrategy<Object> strategy, String strategyClassName) {
        this.strategy = strategy;
        this.strategyClassName = strategyClassName;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) {
        try {
            Object invokeValue;
            // 默认方法
            if (strategyClassName.equals(IHandler.class.getName())) {
                invokeValue = strategy.use().apply(value);
            }
            else {
                invokeValue = InvokeMethodUtil.invoke(strategyClassName, value);
            }
            // 根据目标字段类型进行相应的序列化输出
            writeValueByType(invokeValue, gen);
        }
        catch (Exception e) {
            throw new ServerRuntimeException(CommonResponseEnum.INTERNAL_SERVER_ERROR,
                    "The field [" + gen.getOutputContext().getCurrentName() + "] serialize is error! ");
        }
    }

    /**
     * 根据目标字段类型写入相应格式的值
     */
    private void writeValueByType(Object value, JsonGenerator gen) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        Class<?> targetClass = value.getClass();

        // Boolean类型
        if (Boolean.class.isAssignableFrom(targetClass)) {
            gen.writeBoolean(Boolean.parseBoolean(value.toString()));
            return;
        }

        // 数字类型处理
        if (Number.class.isAssignableFrom(targetClass)) {
            if (targetClass == Integer.class) {
                gen.writeNumber((Integer) value);
            }
            else if (targetClass == Long.class) {
                gen.writeNumber((Long) value);
            }
            else if (targetClass == Double.class) {
                gen.writeNumber((Double) value);
            }
            else if (targetClass == Float.class) {
                gen.writeNumber((Float) value);
            }
            else if (targetClass == BigDecimal.class) {
                gen.writeNumber((BigDecimal) value);
            }
            else if (targetClass == Short.class) {
                gen.writeNumber((Short) value);
            }
            else if (targetClass == Byte.class) {
                gen.writeNumber((Byte) value);
            }
            else {
                gen.writeString(value.toString());
            }
            return;
        }

        // String类型 或 其他的
        gen.writeString(value.toString());
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (ObjectUtil.isNotEmpty(property)) {
            Translate translate = property.getAnnotation(Translate.class);
            if (null == translate) {
                translate = property.getContextAnnotation(Translate.class);
            }
            if (null != translate) {
                Assert.notNull(translate.strategy(), "The strategy must not be null");
                Assert.notNull(translate.handler(), "The handler must not be null");
                return new TranslateJackJsonSerializer(translate.strategy(), translate.handler().getName());
            }
        }
        return new TranslateJackJsonSerializer();
    }

}
