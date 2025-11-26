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
package io.github.panxiaochao.sensitive.serializer.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.github.panxiaochao.core.enums.CommonResponseEnum;
import io.github.panxiaochao.core.exception.ServerRuntimeException;
import io.github.panxiaochao.core.utils.ObjectUtil;
import io.github.panxiaochao.sensitive.annotation.Translate;
import io.github.panxiaochao.sensitive.strategy.IHandler;
import io.github.panxiaochao.sensitive.strategy.IStrategy;
import io.github.panxiaochao.sensitive.utils.InvokeMethodUtil;
import org.springframework.util.Assert;

import java.util.Objects;

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
            // 默认方法
            if (strategyClassName.equals(IHandler.class.getName())) {
                Object objectVal = strategy.use().apply(value);
                if (Objects.equals(objectVal.getClass(), Boolean.class)) {
                    gen.writeBoolean(Boolean.parseBoolean(objectVal.toString()));
                }
                else {
                    gen.writeString(objectVal.toString());
                }
            }
            else {
                Object invokeValue = InvokeMethodUtil.invoke(strategyClassName, value);
                if (Objects.equals(invokeValue.getClass(), Boolean.class)) {
                    gen.writeBoolean(Boolean.parseBoolean(invokeValue.toString()));
                }
                else {
                    gen.writeString(invokeValue.toString());
                }
            }
        }
        catch (Exception e) {
            throw new ServerRuntimeException(CommonResponseEnum.INTERNAL_SERVER_ERROR,
                    "The field [" + gen.getOutputContext().getCurrentName() + "] serialize is error! ");
        }

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
