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
package io.github.panxiaochao.boot3.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Map工具类
 * </p>
 *
 * @author Lypxc
 * @since 2022-11-28
 */
public class MapUtil {

    /**
     * 默认初始大小
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 默认增长因子，当Map的size达到 容量*增长因子时，开始扩充Map
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 新建一个HashMap
     * @param <K> Key类型
     * @param <V> Value类型
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 新建一个HashMap
     * @param <K> Key类型
     * @param <V> Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(int size) {
        return newHashMap(size, false);
    }

    /**
     * 新建一个HashMap
     * @param <K> Key类型
     * @param <V> Value类型
     * @param isLinked Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(boolean isLinked) {
        return newHashMap(DEFAULT_INITIAL_CAPACITY, isLinked);
    }

    /**
     * 新建一个HashMap
     * @param <K> Key类型
     * @param <V> Value类型
     * @param size 初始大小，由于默认负载因子0.75，传入的size会实际初始大小为size / 0.75 + 1
     * @param isLinked Map的Key是否有序，有序返回 {@link LinkedHashMap}，否则返回 {@link HashMap}
     * @return HashMap对象
     */
    public static <K, V> HashMap<K, V> newHashMap(int size, boolean isLinked) {
        final int initialCapacity = (int) (size / DEFAULT_LOAD_FACTOR) + 1;
        return isLinked ? new LinkedHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
    }

    /**
     * Null-safe check if the specified Dictionary is empty.
     *
     * <p>
     * Null returns true.
     * </p>
     * @param map the collection to check, may be null
     * @return true if empty or null
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (null == map || map.isEmpty());
    }

    /**
     * Null-safe check if the specified Dictionary is not empty.
     *
     * <p>
     * Null returns false.
     * </p>
     * @param map the collection to check, may be null
     * @return true if non-null and non-empty
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 如果提供的集合为{@code null}，返回一个不可变的默认空集合，否则返回原集合<br>
     * 空集合使用{@link Collections#emptyMap()}
     * @param <K> 键类型
     * @param <V> 值类型
     * @param set 提供的集合，可能为null
     * @return 原集合，若为null返回空集合
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> set) {
        return (null == set) ? Collections.emptyMap() : set;
    }

    /**
     * 如果给定Map为空，返回默认Map
     * @param <T> 集合类型
     * @param <K> 键类型
     * @param <V> 值类型
     * @param map Map
     * @param defaultMap 默认Map
     * @return 非空（empty）的原Map或默认Map
     */
    public static <T extends Map<K, V>, K, V> T defaultIfEmpty(T map, T defaultMap) {
        return isEmpty(map) ? defaultMap : map;
    }

    /**
     * 去掉Map中指定key的键值对，修改原Map
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map Map
     * @param keys 键列表
     */
    @SafeVarargs
    public static <K, V> void removeAny(Map<K, V> map, final K... keys) {
        if (isEmpty(map)) {
            return;
        }

        if (ArrayUtil.isEmpty(keys)) {
            return;
        }

        List<K> keysArray = Arrays.asList(keys);
        final Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
        Map.Entry<K, V> entry;
        while (iter.hasNext()) {
            entry = iter.next();
            if (keysArray.contains(entry.getKey())) {
                iter.remove();
            }
        }
    }

    /**
     * 按照值排序，可选是否倒序
     * @param map 需要对值排序的map
     * @param <K> 键类型
     * @param <V> 值类型
     * @param isDesc 是否倒序
     * @return 排序后新的Map
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> comparingByValue(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        Comparator<Map.Entry<K, V>> entryComparator = Map.Entry.comparingByValue();
        if (isDesc) {
            entryComparator = entryComparator.reversed();
        }
        map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    /**
     * 按照键值排序，可选是否倒序
     * @param map 需要对值排序的map
     * @param <K> 键类型
     * @param <V> 值类型
     * @param isDesc 是否倒序
     * @return 排序后新的Map
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> comparingByKey(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        Comparator<Map.Entry<K, V>> entryComparator = Map.Entry.comparingByKey();
        if (isDesc) {
            entryComparator = entryComparator.reversed();
        }
        map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

}
