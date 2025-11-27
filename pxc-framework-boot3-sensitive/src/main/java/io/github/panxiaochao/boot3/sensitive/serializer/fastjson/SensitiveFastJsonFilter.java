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
package io.github.panxiaochao.boot3.sensitive.serializer.fastjson;

import com.alibaba.fastjson.serializer.ValueFilter;
import io.github.panxiaochao.boot3.core.enums.CommonResponseEnum;
import io.github.panxiaochao.boot3.core.exception.ServerRuntimeException;
import io.github.panxiaochao.boot3.sensitive.annotation.Sensitive;
import io.github.panxiaochao.boot3.sensitive.strategy.IHandler;
import io.github.panxiaochao.boot3.sensitive.strategy.sensitive.SensitiveStrategy;
import io.github.panxiaochao.boot3.sensitive.utils.InvokeMethodUtil;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * <p>
 * FastJson 1.X 版本脱敏过滤器
 * </p>
 *
 * @author Lypxc
 * @since 2023-09-01
 */
public class SensitiveFastJsonFilter implements ValueFilter {

    @Override
    public Object process(Object object, String name, Object value) {
        if (Objects.isNull(value) || !(value instanceof String)) {
            return value;
        }
        // 获取字段上注解
        try {
            Field field = ReflectionUtils.findField(object.getClass(), name);
            Sensitive sensitive = field.getAnnotation(Sensitive.class);
            if (Objects.isNull(sensitive) || field.getType() != String.class) {
                return value;
            }
            // 获取属性
            SensitiveStrategy strategy = sensitive.strategy();
            String strategyClassName = sensitive.handler().getName();
            // 相同的class，使用自带策略
            if (strategyClassName.equals(IHandler.class.getName())) {
                return strategy.use().apply(value.toString());
            }
            else {
                return InvokeMethodUtil.invoke(strategyClassName, value);
            }
        }
        catch (Exception e) {
            throw new ServerRuntimeException(CommonResponseEnum.INTERNAL_SERVER_ERROR,
                    "The field [" + name + "] serialize is error! ");
        }
    }

}
