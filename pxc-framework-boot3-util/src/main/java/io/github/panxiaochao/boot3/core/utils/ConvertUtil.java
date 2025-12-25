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
package io.github.panxiaochao.boot3.core.utils;

import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 值转换工具
 * </p>
 *
 * @author Lypxc
 * @since 2023-08-09
 */
public class ConvertUtil {

    private static final String NULL_STR = "null";

    public static final Set<String> TRUE_SET = Set.of("y", "yes", "on", "true", "t");

    public static final Set<String> FALSE_SET = Set.of("n", "no", "off", "false", "f");

    /**
     * Convert String value to int value if parameter value is legal. And it automatically
     * defaults to 0 if parameter value is null or blank str.
     * @param val String value which need to be converted to int value.
     * @return Converted int value and its default value is 0.
     */
    public static int toInt(String val) {
        return toInt(val, 0);
    }

    /**
     * Convert String value to int value if parameter value is legal. And return default
     * value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return int value if input value is legal, otherwise default value
     */
    public static int toInt(String val, int defaultValue) {
        if (!StringUtils.hasText(val)) {
            return defaultValue;
        }
        if (val.equalsIgnoreCase(NULL_STR)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    /**
     * Convert Object value to long value if parameter value is legal. And it
     * automatically defaults to 0 if parameter value is null or other object.
     * @param val object value
     * @return Converted long value and its default value is null.
     */
    public static Long toLong(Object val) {
        if (Objects.isNull(val)) {
            return null;
        }
        if (val instanceof Long) {
            return (Long) val;
        }
        return toLong(val.toString());
    }

    /**
     * Convert String value to long value if parameter value is legal. And it
     * automatically defaults to 0 if parameter value is null or blank str.
     * @param val String value which need to be converted to int value.
     * @return Converted long value and its default value is null.
     */
    public static Long toLong(String val) {
        return toLong(val, null);
    }

    /**
     * Convert String value to long value if parameter value is legal. And return default
     * value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return Long value if input value is legal, otherwise default value
     */
    public static Long toLong(String val, Long defaultValue) {
        if (!StringUtils.hasText(val)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    /**
     * Convert Object value to int value if parameter value is legal. And it automatically
     * defaults to 0 if parameter value is null or other object.
     * @param val object value
     * @return Converted int value and its default value is null.
     */
    public static Integer toInteger(Object val) {
        if (Objects.isNull(val)) {
            return null;
        }
        if (val instanceof Integer) {
            return (Integer) val;
        }
        return toInteger(val.toString());
    }

    /**
     * <p>
     * Convert String value to int value if parameter value is legal. And it automatically
     * defaults to 0 if parameter value is null or blank str.
     * </p>
     * @param val String value which need to be converted to int value.
     * @return Converted int value and its default value is null.
     */
    public static Integer toInteger(String val) {
        return toInteger(val, null);
    }

    /**
     * Convert String value to int value if parameter value is legal. And return default
     * value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return Integer value if input value is legal, otherwise default value
     */
    public static Integer toInteger(String val, Integer defaultValue) {
        if (!StringUtils.hasText(val)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    /**
     * Convert String value to boolean value if parameter value is legal. And return
     * default value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return boolean value if input value is legal, otherwise default value
     */
    public static Boolean toBoolean(String val, boolean defaultValue) {
        if (!StringUtils.hasText(val)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(val);
    }

    /**
     * <p>
     * Converts a String to a boolean (optimised for performance).
     * </p>
     *
     * <p>
     * {@code 'true'}, {@code 'on'}, {@code 'y'}, {@code 't'} or {@code 'yes'} (case
     * insensitive) will return {@code true}. Otherwise, {@code false} is returned.
     * </p>
     *
     * <p>
     * This method performs 4 times faster (JDK1.4) than {@code Boolean.valueOf(String)}.
     * However, this method accepts 'on' and 'yes', 't', 'y' as true values.
     *
     * <pre>
     *   ConvertUtil.toBoolean(null)    = false
     *   ConvertUtil.toBoolean("true")  = true
     *   ConvertUtil.toBoolean("TRUE")  = true
     *   ConvertUtil.toBoolean("tRUe")  = true
     *   ConvertUtil.toBoolean("on")    = true
     *   ConvertUtil.toBoolean("yes")   = true
     *   ConvertUtil.toBoolean("false") = false
     *   ConvertUtil.toBoolean("x gti") = false
     *   ConvertUtil.toBooleanObject("y") = true
     *   ConvertUtil.toBooleanObject("n") = false
     *   ConvertUtil.toBooleanObject("t") = true
     *   ConvertUtil.toBooleanObject("f") = false
     * </pre>
     * @param str the String to check
     * @return the boolean value of the string, {@code false} if no match or the String is
     * null
     */
    public static Boolean toBoolean(final String str) {
        return Boolean.TRUE.equals(toBooleanObject(str));
    }

    /**
     * <p>
     * Converts a String to a Boolean.
     * </p>
     *
     * <p>
     * {@code 'true'}, {@code 'on'}, {@code 'y'}, {@code 't'} or {@code 'yes'} (case
     * insensitive) will return {@code true}. {@code 'false'}, {@code 'off'}, {@code 'n'},
     * {@code 'f'} or {@code
     * 'no'} (case insensitive) will return {@code false}. Otherwise, {@code null} is
     * returned.
     * </p>
     *
     * <p>
     * NOTE: This returns null and will throw a NullPointerException if autoboxed to a
     * boolean.
     * </p>
     *
     * <pre>
     *   // N.B. case is not significant
     *   ConvertUtil.toBooleanObject(null)    = null
     *   ConvertUtil.toBooleanObject("true")  = Boolean.TRUE
     *   ConvertUtil.toBooleanObject("T")     = Boolean.TRUE // i.e. T[RUE]
     *   ConvertUtil.toBooleanObject("false") = Boolean.FALSE
     *   ConvertUtil.toBooleanObject("f")     = Boolean.FALSE // i.e. f[alse]
     *   ConvertUtil.toBooleanObject("No")    = Boolean.FALSE
     *   ConvertUtil.toBooleanObject("n")     = Boolean.FALSE // i.e. n[o]
     *   ConvertUtil.toBooleanObject("on")    = Boolean.TRUE
     *   ConvertUtil.toBooleanObject("ON")    = Boolean.TRUE
     *   ConvertUtil.toBooleanObject("off")   = Boolean.FALSE
     *   ConvertUtil.toBooleanObject("oFf")   = Boolean.FALSE
     *   ConvertUtil.toBooleanObject("yes")   = Boolean.TRUE
     *   ConvertUtil.toBooleanObject("Y")     = Boolean.TRUE // i.e. Y[ES]
     *   ConvertUtil.toBooleanObject("blue")  = null
     *   ConvertUtil.toBooleanObject("true ") = null // trailing space (too long)
     *   ConvertUtil.toBooleanObject("ono")   = null // does not match on or no
     * </pre>
     * @param str the String to check; upper and lower case are treated as the same
     * @return the Boolean value of the string, {@code null} if no match or {@code null}
     * input
     */
    @SuppressWarnings("all")
    public static Boolean toBooleanObject(String str) {
        String formatStr = (str == null ? StrUtil.EMPTY : str).toLowerCase();

        if (TRUE_SET.contains(formatStr)) {
            return true;
        }
        else if (FALSE_SET.contains(formatStr)) {
            return false;
        }
        else {
            return null;
        }
    }

}
