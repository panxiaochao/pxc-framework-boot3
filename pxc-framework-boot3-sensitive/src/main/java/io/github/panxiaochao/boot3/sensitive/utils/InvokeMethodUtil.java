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
package io.github.panxiaochao.boot3.sensitive.utils;

import io.github.panxiaochao.boot3.core.enums.CommonResponseEnum;
import io.github.panxiaochao.boot3.core.exception.ServerRuntimeException;
import io.github.panxiaochao.boot3.core.utils.Singleton;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * <p>
 * 执行自定义方法反射方法
 * </p>
 *
 * @author Lypxc
 * @since 2023-09-01
 */
public class InvokeMethodUtil {

    /**
     * 映射自定义方法
     * @param className class名
     * @param value json value
     * @return 脱敏后的值
     */
    public static Object invoke(String className, Object value) {
        // 不同class，使用自定义策略
        try {
            // 防止反射内存泄漏，每次都new一个对象
            Object obj;
            if (null != Singleton.INST.get(className)) {
                obj = Singleton.INST.get(className);
            }
            else {
                Class<?> cls = Class.forName(className);
                obj = cls.newInstance();
                Singleton.INST.single(className, obj);
            }
            Method handlerMethod = ReflectionUtils.findMethod(obj.getClass(), "handler", String.class);
            if (Objects.isNull(handlerMethod)) {
                throw new ServerRuntimeException(CommonResponseEnum.INTERNAL_SERVER_ERROR,
                        "The class [" + className + "] is not implements IHandler! ");
            }
            else {
                ReflectionUtils.makeAccessible(handlerMethod);
                return ReflectionUtils.invokeMethod(handlerMethod, obj, value);
            }
        }
        catch (Exception e) {
            // 使用默认值
            return value.toString();
        }
    }

}
