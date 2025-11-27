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

import org.apache.ibatis.reflection.MetaObject;

import java.util.Objects;

/**
 * <p>
 * 自定义元对象插入实现类
 * </p>
 *
 * @author Lypxc
 * @since 2024-06-28
 * @version 1.0
 */
public interface IMetaObjectHandler {

    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     * @param metaObject 元对象
     */
    void insertFillCustomize(MetaObject metaObject);

    /**
     * 通用填充判断
     * @param fieldName java bean property name
     * @param fieldVal java bean property value
     * @param metaObject meta object parameter
     */
    default boolean fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject) {
        return Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName);
    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     * @param metaObject 元对象
     */
    void updateFillCustomize(MetaObject metaObject);

}
