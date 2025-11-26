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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 布尔值工具类
 * </p>
 *
 * @author Lypxc
 * @since 2024-06-05
 * @version 1.0
 */
public class BooleanUtil {

    /**
     * 【真】数组
     */
    private static final List<String> TRUE = Collections
        .unmodifiableList(Arrays.asList("true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√"));

    /**
     * 【假】数组
     */
    private static final List<String> FALSE = Collections
        .unmodifiableList(Arrays.asList("false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×"));

    /**
     * boolean 数组取与.
     *
     * <pre>
     *   BooleanUtil.and(true, true)         = true
     *   BooleanUtil.and(false, false)       = false
     *   BooleanUtil.and(true, false)        = false
     *   BooleanUtil.and(true, true, false)  = false
     *   BooleanUtil.and(true, true, true)   = true
     * </pre>
     * @param array an array of {@code boolean}s
     * @return the result of the logical 'and' operation. That is {@code false} if any of
     * the parameters is {@code false} and {@code true} otherwise.
     * @throws NullPointerException if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is empty.
     */
    public static boolean and(final boolean... array) {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException("The Array must not be empty !");
        }
        for (final boolean element : array) {
            if (!element) {
                return false;
            }
        }
        return true;
    }

    /**
     * Boolean 数组取与.
     *
     * <pre>
     *   BooleanUtil.and(Boolean.TRUE, Boolean.TRUE)                 = Boolean.TRUE
     *   BooleanUtil.and(Boolean.FALSE, Boolean.FALSE)               = Boolean.FALSE
     *   BooleanUtil.and(Boolean.TRUE, Boolean.FALSE)                = Boolean.FALSE
     *   BooleanUtil.and(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE)   = Boolean.TRUE
     *   BooleanUtil.and(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE) = Boolean.FALSE
     *   BooleanUtil.and(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)  = Boolean.FALSE
     *   BooleanUtil.and(null, null)                                 = Boolean.FALSE
     * </pre>
     * <p>
     * Null array elements map to false, like {@code Boolean.parseBoolean(null)} and its
     * callers return false.
     * </p>
     * @param array an array of {@link Boolean}s
     * @return the result of the logical 'and' operation. That is {@code false} if any of
     * the parameters is {@code false} and {@code true} otherwise.
     * @throws NullPointerException if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is empty.
     */
    public static Boolean and(final Boolean... array) {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException("The Array must not be empty !");
        }

        for (final Boolean b : array) {
            if (isFalse(b)) {
                return false;
            }
        }
        return true;
    }

    /**
     * boolean 数组取或.
     *
     * <pre>
     *   BooleanUtil.or(true, true)          = true
     *   BooleanUtil.or(false, false)        = false
     *   BooleanUtil.or(true, false)         = true
     *   BooleanUtil.or(true, true, false)   = true
     *   BooleanUtil.or(true, true, true)    = true
     *   BooleanUtil.or(false, false, false) = false
     * </pre>
     * @param array an array of {@code boolean}s
     * @return {@code true} if any of the arguments is {@code true}, and it returns
     * {@code false} otherwise.
     * @throws NullPointerException if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is empty.
     */
    public static boolean or(final boolean... array) {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException("The Array must not be empty !");
        }
        for (final boolean element : array) {
            if (element) {
                return true;
            }
        }
        return false;
    }

    /**
     * Boolean 数组取与.
     *
     * <pre>
     *   BooleanUtil.or(Boolean.TRUE, Boolean.TRUE)                  = Boolean.TRUE
     *   BooleanUtil.or(Boolean.FALSE, Boolean.FALSE)                = Boolean.FALSE
     *   BooleanUtil.or(Boolean.TRUE, Boolean.FALSE)                 = Boolean.TRUE
     *   BooleanUtil.or(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE)    = Boolean.TRUE
     *   BooleanUtil.or(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE)  = Boolean.TRUE
     *   BooleanUtil.or(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE)   = Boolean.TRUE
     *   BooleanUtil.or(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE) = Boolean.FALSE
     *   BooleanUtil.or(Boolean.TRUE, null)                          = Boolean.TRUE
     *   BooleanUtil.or(Boolean.FALSE, null)                         = Boolean.FALSE
     * </pre>
     * <p>
     * Null array elements map to false, like {@code Boolean.parseBoolean(null)} and its
     * callers return false.
     * </p>
     * @param array an array of {@link Boolean}s
     * @return {@code true} if any of the arguments is {@code true}, and it returns
     * {@code false} otherwise.
     * @throws NullPointerException if {@code array} is {@code null}
     * @throws IllegalArgumentException if {@code array} is empty.
     */
    public static Boolean or(final Boolean... array) {
        if (ArrayUtil.isEmpty(array)) {
            throw new IllegalArgumentException("The Array must not be empty !");
        }
        for (final Boolean b : array) {
            if (isTrue(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查 {@link Boolean} 值为 {@code false}, 当值为 {@code null} 返回 {@code false}.
     *
     * <pre>
     *   BooleanUtil.isFalse(Boolean.TRUE)  = false
     *   BooleanUtil.isFalse(Boolean.FALSE) = true
     *   BooleanUtil.isFalse(null)          = false
     * </pre>
     * @param bool the boolean to check, null returns {@code false}
     * @return {@code true} only if the input is non-{@code null} and {@code false}
     */
    public static boolean isFalse(final Boolean bool) {
        return Boolean.FALSE.equals(bool);
    }

    /**
     * 检查 {@link Boolean} 值<i>不</i>为 {@code false}, 当值为 {@code null} 返回 {@code true}.
     *
     * <pre>
     *   BooleanUtil.isNotFalse(Boolean.TRUE)  = true
     *   BooleanUtil.isNotFalse(Boolean.FALSE) = false
     *   BooleanUtil.isNotFalse(null)          = true
     * </pre>
     * @param bool the boolean to check, null returns {@code true}
     * @return {@code true} if the input is {@code null} or {@code true}
     */
    public static boolean isNotFalse(final Boolean bool) {
        return !isFalse(bool);
    }

    /**
     * 检查 {@link Boolean} 值<i>不</i>为 {@code true}, 当值为 {@code null} 返回 {@code true}.
     *
     * <pre>
     *   BooleanUtil.isNotTrue(Boolean.TRUE)  = false
     *   BooleanUtil.isNotTrue(Boolean.FALSE) = true
     *   BooleanUtil.isNotTrue(null)          = true
     * </pre>
     * @param bool the boolean to check, null returns {@code true}
     * @return {@code true} if the input is null or false
     */
    public static boolean isNotTrue(final Boolean bool) {
        return !isTrue(bool);
    }

    /**
     * 检查 {@link Boolean} 值为 {@code true}, 当值为 {@code null} 返回 {@code false}.
     *
     * <pre>
     *   BooleanUtil.isTrue(Boolean.TRUE)  = true
     *   BooleanUtil.isTrue(Boolean.FALSE) = false
     *   BooleanUtil.isTrue(null)          = false
     * </pre>
     * @param bool the boolean to check, {@code null} returns {@code false}
     * @return {@code true} only if the input is non-null and true
     * @since 2.1
     */
    public static boolean isTrue(final Boolean bool) {
        return Boolean.TRUE.equals(bool);
    }

    /**
     * 取boolean相反值, 如果值为 {@code null} , 返回 {@code null}.
     *
     * <pre>
     *   BooleanUtil.negate(Boolean.TRUE)  = Boolean.FALSE;
     *   BooleanUtil.negate(Boolean.FALSE) = Boolean.TRUE;
     *   BooleanUtil.negate(null)          = null;
     * </pre>
     * @param bool the Boolean to negate, may be null
     * @return the negated Boolean, or {@code null} if {@code null} input
     */
    public static Boolean negate(final Boolean bool) {
        if (bool == null) {
            return null;
        }
        return bool.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * 转换字符串为boolean值
     *
     * <p>
     * ["true", "yes", "y", "t", "ok", "1", "on", "是", "对", "真", "對", "√"], 返回
     * {@code true}
     * </p>
     * <p>
     * ["false", "no", "n", "f", "0", "off", "否", "错", "假", "錯", "×"], 返回 {@code false}
     * </p>
     * <p>
     * 其他情况返回 {@code null}
     * </p>
     * @param booleanStr 字符串
     * @return boolean
     */
    public static Boolean toBoolean(String booleanStr) {
        if (StrUtil.isNotBlank(booleanStr)) {
            booleanStr = booleanStr.trim().toLowerCase();
            if (TRUE.contains(booleanStr)) {
                return true;
            }
            else if (FALSE.contains(booleanStr)) {
                return false;
            }
        }
        return null;
    }

    /**
     * 将boolean转换为字符串
     *
     * <pre>
     *   BooleanUtil.toString(true)   = "true"
     *   BooleanUtil.toString(false)  = "false"
     *   BooleanUtil.toString(null)  = null
     * </pre>
     * @param bool Boolean值
     * @return 结果值
     */
    public static String toString(Boolean bool) {
        if (ObjectUtil.isEmpty(bool)) {
            return null;
        }
        return bool ? "true" : "false";
    }

    /**
     * Converts a boolean to a String returning one of the input Strings.
     *
     * <pre>
     *   BooleanUtils.toString(true, "true", "false")   = "true"
     *   BooleanUtils.toString(false, "true", "false")  = "false"
     * </pre>
     * @param bool the Boolean to check
     * @param trueString the String to return if {@code true}, may be {@code null}
     * @param falseString the String to return if {@code false}, may be {@code null}
     * @return one of the two input Strings
     */
    public static String toString(final boolean bool, final String trueString, final String falseString) {
        return bool ? trueString : falseString;
    }

    /**
     * Converts a Boolean to a String returning one of the input Strings.
     *
     * <pre>
     *   BooleanUtils.toString(Boolean.TRUE, "true", "false", null)   = "true"
     *   BooleanUtils.toString(Boolean.FALSE, "true", "false", null)  = "false"
     *   BooleanUtils.toString(null, "true", "false", null)           = null;
     * </pre>
     * @param bool the Boolean to check
     * @param trueString the String to return if {@code true}, may be {@code null}
     * @param falseString the String to return if {@code false}, may be {@code null}
     * @param nullString the String to return if {@code null}, may be {@code null}
     * @return one of the three input Strings
     */
    public static String toString(final Boolean bool, final String trueString, final String falseString,
            final String nullString) {
        if (bool == null) {
            return nullString;
        }
        return bool.booleanValue() ? trueString : falseString;
    }

    /**
     * Converts a boolean to an int using the convention that {@code true} is {@code 1}
     * and {@code false} is {@code 0}.
     *
     * <pre>
     *   BooleanUtils.toInteger(true)  = 1
     *   BooleanUtils.toInteger(false) = 0
     * </pre>
     * @param bool the boolean to convert
     * @return one if {@code true}, zero if {@code false}
     */
    public static int toInteger(final boolean bool) {
        return bool ? 1 : 0;
    }

    /**
     * Converts a boolean to an int specifying the conversion values.
     *
     * <pre>
     *   BooleanUtils.toInteger(true, 1, 0)  = 1
     *   BooleanUtils.toInteger(false, 1, 0) = 0
     * </pre>
     * @param bool the to convert
     * @param trueValue the value to return if {@code true}
     * @param falseValue the value to return if {@code false}
     * @return the appropriate value
     */
    public static int toInteger(final boolean bool, final int trueValue, final int falseValue) {
        return bool ? trueValue : falseValue;
    }

    /**
     * Converts a Boolean to an int specifying the conversion values.
     *
     * <pre>
     *   BooleanUtils.toInteger(Boolean.TRUE, 1, 0, 2)  = 1
     *   BooleanUtils.toInteger(Boolean.FALSE, 1, 0, 2) = 0
     *   BooleanUtils.toInteger(null, 1, 0, 2)          = 2
     * </pre>
     * @param bool the Boolean to convert
     * @param trueValue the value to return if {@code true}
     * @param falseValue the value to return if {@code false}
     * @param nullValue the value to return if {@code null}
     * @return the appropriate value
     */
    public static int toInteger(final Boolean bool, final int trueValue, final int falseValue, final int nullValue) {
        if (bool == null) {
            return nullValue;
        }
        return bool.booleanValue() ? trueValue : falseValue;
    }

    /**
     * 给定类是否为Boolean或者boolean
     * @param clazz 类
     * @return 是否为Boolean或者boolean
     */
    public static boolean isBoolean(Class<?> clazz) {
        return (clazz == Boolean.class || clazz == boolean.class);
    }

}
