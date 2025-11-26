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
package io.github.panxiaochao.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * <p>
 * 集合类工具
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-18
 * @version 1.0
 */
public class CollectionUtil {

    /**
     * Don't allow instances.
     */
    private CollectionUtil() {
    }

    @SuppressWarnings("rawtypes")
    private static final Collection EMPTY_COLLECTION = Collections.emptyList();

    @SuppressWarnings("rawtypes")
    public static final List EMPTY_LIST = Collections.emptyList();

    /**
     * 空集合
     * @return 空集合
     * @param <T> 类型
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> emptyCollection() {
        return EMPTY_COLLECTION;
    }

    /**
     * 空集合
     * @return 空集合
     * @param <T> 类型
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }

    /**
     * 空集合判断，如果NULL返回空集合，否则原集合
     * @param <T> 元素类型
     * @param list 集合
     * @return 返回集合，若为null返回空集合
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> emptyIfNull(List<T> list) {
        return isEmpty(list) ? (List<T>) emptyCollection() : list;
    }

    /**
     * 空集合判断，如果NULL返回空集合，否则原集合
     * @param <T> 元素类型
     * @param list 集合
     * @return 返回集合，若为null返回空集合
     */
    public static <T> Collection<T> emptyIfNull(Collection<T> list) {
        return isEmpty(list) ? emptyCollection() : list;
    }

    /**
     * 判断集合是否为NULL
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * 判断集合是否为非NULL
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Iterable是非空
     * @param iterable Iterable对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterable<?> iterable) {
        return null != iterable && isNotEmpty(iterable.iterator());
    }

    /**
     * 判断Iterator是非空
     * @param iterator Iterator对象
     * @return 是否为空
     */
    public static boolean isNotEmpty(Iterator<?> iterator) {
        return null != iterator && iterator.hasNext();
    }

    /**
     * 判断指定集合是否包含指定值
     * @param collection 集合
     * @param value 需要查找的值
     * @return true or false
     */
    public static boolean contains(Collection<?> collection, Object value) {
        try {
            return isNotEmpty(collection) && collection.contains(value);
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * 自定义函数判断集合是否包含某类值
     * @param collection 集合
     * @param predicate 自定义判断函数
     * @param <T> 值类型
     * @return 是否包含自定义规则的值
     */
    public static <T> boolean contains(Collection<T> collection, Predicate<? super T> predicate) {
        if (isEmpty(collection) || null == predicate) {
            return false;
        }
        for (T t : collection) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否至少有一个符合判断条件
     * @param <T> 集合元素类型
     * @param collection 集合
     * @param predicate 自定义判断函数
     * @return 至少一个匹配
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection) || null == predicate) {
            return false;
        }
        return collection.stream().anyMatch(predicate);
    }

    /**
     * 判断是否全部匹配判断条件
     * @param <T> 集合元素类型
     * @param collection 集合
     * @param predicate 自定义判断函数
     * @return 是否全部匹配 布尔值
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection) || null == predicate) {
            return false;
        }
        return collection.stream().allMatch(predicate);
    }

    /**
     * 转换HashSet
     * @param <T> 集合元素类型
     * @param collection 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> toHashSet(Collection<T> collection) {
        return isEmpty(collection) ? new HashSet<>() : new HashSet<>(collection);
    }

    /**
     * 转换HashSet
     * @param <T> 集合元素类型
     * @param iterator 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> toHashSet(Iterator<T> iterator) {
        final HashSet<T> set = new HashSet<>();
        if (null != iterator) {
            while (iterator.hasNext()) {
                set.add(iterator.next());
            }
        }
        return set;
    }

    /**
     * 转换HashSet
     * @param <T> 集合元素类型
     * @param iterable 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> toHashSet(Iterable<T> iterable) {
        HashSet<T> set = new HashSet<>();
        if (null != iterable) {
            set = toHashSet(iterable.iterator());
        }
        return set;
    }

    /**
     * 转换HashSet
     * @param <T> 集合元素类型
     * @param enumeration 集合
     * @return HashSet对象
     */
    public static <T> HashSet<T> toHashSet(Enumeration<T> enumeration) {
        final HashSet<T> set = new HashSet<>();
        if (null != enumeration) {
            while (enumeration.hasMoreElements()) {
                set.add(enumeration.nextElement());
            }
        }
        return set;
    }

    /**
     * 转换List
     * @param <T> 集合元素类型
     * @param collection 集合
     * @return List对象
     */
    public static <T> List<T> toList(Collection<T> collection) {
        if (isNotEmpty(collection)) {
            return new ArrayList<>(collection);
        }
        return Collections.emptyList();
    }

    /**
     * 转换List
     * @param <T> 集合元素类型
     * @param iterator 集合
     * @return List对象
     */
    public static <T> List<T> toList(Iterator<T> iterator) {
        final List<T> list = new ArrayList<>();
        if (null != iterator) {
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        return list;
    }

    /**
     * 转换List
     * @param <T> 集合元素类型
     * @param iterable 集合
     * @return List对象
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        if (null != iterable) {
            list = toList(iterable.iterator());
        }
        return list;
    }

    /**
     * 转换List
     * @param <T> 集合元素类型
     * @param enumeration 集合
     * @return List对象
     */
    public static <T> List<T> toList(Enumeration<T> enumeration) {
        List<T> list = new ArrayList<>();
        if (null != enumeration) {
            while (enumeration.hasMoreElements()) {
                list.add(enumeration.nextElement());
            }
        }
        return list;
    }

    /**
     * 转换List
     * @param <T> 集合元素类型
     * @param values 集合
     * @return List对象
     */
    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        if (ArrayUtil.isNotEmpty(values)) {
            final List<T> list = new ArrayList<>(values.length);
            Collections.addAll(list, values);
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * 转换不可修改List
     * @param <T> 集合元素类型
     * @param list 集合
     * @return List对象
     */
    public static <T> List<T> toUnmodifiableList(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }
        return Collections.unmodifiableList(list);
    }

}
