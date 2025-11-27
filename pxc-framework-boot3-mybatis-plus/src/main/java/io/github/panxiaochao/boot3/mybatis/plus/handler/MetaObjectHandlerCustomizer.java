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
package io.github.panxiaochao.boot3.mybatis.plus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.github.panxiaochao.boot3.core.utils.ObjectUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * Mybatis Plus 自动填充配置
 * </p>
 *
 * @author Lypxc
 * @since 2023-07-17
 */
public class MetaObjectHandlerCustomizer implements MetaObjectHandler {

    /**
     * 实体类中表示创建时间的字段名，通常为时间类型
     */
    private static final String FIELD_CREATE_TIME = "createTime";

    /**
     * 实体类中表示创建日期的字段名，通常为日期类型
     */
    private static final String FIELD_CREATE_AT = "createAt";

    /**
     * 实体类中表示更新时间的字段名，通常为时间类型
     */
    private static final String FIELD_UPDATE_TIME = "updateTime";

    /**
     * 实体类中表示更新日期的字段名，通常为日期类型
     */
    private static final String FIELD_UPDATE_AT = "updateAt";

    /**
     * 存储时间类型与其对应值生成器的映射。 不同的时间类型（如 Long、LocalDateTime 等）对应不同的值生成逻辑。
     */
    private static final Map<Class<?>, ValueSupplier> TIME_VALUE_SUPPLIERS = new HashMap<>();

    static {
        // 为 Long 类型的时间字段提供当前时间戳
        TIME_VALUE_SUPPLIERS.put(Long.class, System::currentTimeMillis);
        // 为 LocalDateTime 类型的时间字段提供当前日期时间
        TIME_VALUE_SUPPLIERS.put(LocalDateTime.class, LocalDateTime::now);
        // 为 LocalDate 类型的时间字段提供当前日期
        TIME_VALUE_SUPPLIERS.put(LocalDate.class, LocalDate::now);
        // 为 Date 类型的时间字段提供当前日期时间
        TIME_VALUE_SUPPLIERS.put(Date.class, Date::new);
    }

    /**
     * 函数式接口，定义一个值供应方法，用于生成不同类型的时间值。
     */
    @FunctionalInterface
    private interface ValueSupplier {

        /**
         * 获取生成的值。
         * @return 生成的时间值
         */
        Object get();

    }

    /**
     * 自定义元对象处理器，用于处理额外的插入和更新填充逻辑。
     */
    private final IMetaObjectHandler metaObjectHandler;

    /**
     * 构造函数，初始化自定义元对象处理器。
     * @param metaObjectHandler 自定义元对象处理器实例
     */
    public MetaObjectHandlerCustomizer(IMetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    /**
     * 插入操作时的字段填充方法。 在实体对象插入数据库前，自动填充创建时间和更新时间字段，并调用自定义插入逻辑。
     * @param metaObject 包含实体对象属性信息的元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 调用填充时间字段的方法，传入是否为更新操作的标识（false 表示插入操作）
        fillTimeFields(metaObject, false);
        // 调用自定义实现的插入填充逻辑
        metaObjectHandler.insertFillCustomize(metaObject);
    }

    /**
     * 更新操作时的字段填充方法。 在实体对象更新数据库前，自动填充更新时间字段，并调用自定义更新逻辑。
     * @param metaObject 包含实体对象属性信息的元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 调用填充时间字段的方法，传入是否为更新操作的标识（true 表示更新操作）
        fillTimeFields(metaObject, true);
        // 调用自定义实现的更新填充逻辑
        metaObjectHandler.updateFillCustomize(metaObject);
    }

    /**
     * 填充时间字段
     * @param metaObject 元数据对象
     * @param updateFill 是否是更新操作
     */
    private void fillTimeFields(MetaObject metaObject, boolean updateFill) {
        String[] fields = updateFill ? new String[] { FIELD_UPDATE_TIME, FIELD_UPDATE_AT }
                : new String[] { FIELD_CREATE_TIME, FIELD_CREATE_AT, FIELD_UPDATE_TIME, FIELD_UPDATE_AT };
        for (String field : fields) {
            for (Map.Entry<Class<?>, ValueSupplier> entry : TIME_VALUE_SUPPLIERS.entrySet()) {
                Class<?> fieldType = entry.getKey();
                Object fieldVal = entry.getValue().get();
                strictFillValByName(metaObject, field, fieldVal, fieldType, updateFill);
            }
        }
    }

    /**
     * 填充值，判断是是否是insert还是update，例如：job必须手动设置, 多线程必须手动设置
     * @param metaObject 元数据对象
     * @param fieldName 属性名
     * @param fieldVal 属性值
     * @param updateFill 是否更新
     */
    private static void strictFillValByName(MetaObject metaObject, String fieldName, Object fieldVal,
            Class<?> fieldType, boolean updateFill) {
        // 0. 如果填充值为空
        if (fieldVal == null) {
            return;
        }
        // 1. 没有 get 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 2. 当是insert和值为null的时候才会置值
        if (!updateFill) {
            Object setValueObj = metaObject.getValue(fieldName);
            String setValueStr = ObjectUtil.isEmpty(setValueObj) ? "" : String.valueOf(setValueObj);
            if (StringUtils.hasText(setValueStr)) {
                return;
            }
        }
        // 3. 判断 fieldType 和 getterType 是否相同
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (Objects.equals(getterType, fieldType) && ClassUtils.isAssignableValue(getterType, fieldVal)) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }

}
