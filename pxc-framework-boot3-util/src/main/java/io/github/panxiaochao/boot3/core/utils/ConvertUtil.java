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

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

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

    public static final Set<String> TRUE_SET = Set.of("y", "yes", "on", "true", "t", "1");

    public static final Set<String> FALSE_SET = Set.of("n", "no", "off", "false", "f", "0");

    /**
     * Converts a {@link String} to a {@code float}, returning {@code 0.0f} if the
     * conversion fails.
     *
     * <p>
     * If the string {@code str} is {@code null}, {@code 0.0f} is returned.
     * </p>
     *
     * <pre>
     *   ConvertUtil.toFloat(null)   = 0.0f
     *   ConvertUtil.toFloat("")     = 0.0f
     *   ConvertUtil.toFloat("1.5")  = 1.5f
     * </pre>
     * @param str the string to convert, may be {@code null}.
     * @return the float represented by the string, or {@code 0.0f} if conversion fails.
     * @since 2.1
     */
    public static float toFloat(final String str) {
        return toFloat(str, 0.0f);
    }

    /**
     * Converts a {@link String} to a {@code float}, returning a default value if the
     * conversion fails.
     *
     * <p>
     * If the string {@code str} is {@code null}, the default value is returned.
     * </p>
     *
     * <pre>
     *   ConvertUtil.toFloat(null, 1.1f)   = 1.1f
     *   ConvertUtil.toFloat("", 1.1f)     = 1.1f
     *   ConvertUtil.toFloat("1.5", 0.0f)  = 1.5f
     * </pre>
     * @param str the string to convert, may be {@code null}.
     * @param defaultValue the default value.
     * @return the float represented by the string, or defaultValue if conversion fails.
     * @since 2.1
     */
    public static float toFloat(final String str, final float defaultValue) {
        if (StrUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(str);
        }
        catch (final RuntimeException e) {
            return defaultValue;
        }
    }

    /**
     * Converts a {@link String} to a {@code double}, returning {@code 0.0d} if the
     * conversion fails.
     *
     * <p>
     * If the string {@code str} is {@code null}, {@code 0.0d} is returned.
     * </p>
     *
     * <pre>
     *   NumberUtils.toDouble(null)   = 0.0d
     *   NumberUtils.toDouble("")     = 0.0d
     *   NumberUtils.toDouble("1.5")  = 1.5d
     * </pre>
     * @param str the string to convert, may be {@code null}.
     * @return the double represented by the string, or {@code 0.0d} if conversion fails.
     * @since 2.1
     */
    public static double toDouble(final String str) {
        return toDouble(str, 0.0d);
    }

    /**
     * Converts a {@link String} to a {@code double}, returning a default value if the
     * conversion fails.
     *
     * <p>
     * If the string {@code str} is {@code null}, the default value is returned.
     * </p>
     *
     * <pre>
     *   NumberUtils.toDouble(null, 1.1d)   = 1.1d
     *   NumberUtils.toDouble("", 1.1d)     = 1.1d
     *   NumberUtils.toDouble("1.5", 0.0d)  = 1.5d
     * </pre>
     * @param str the string to convert, may be {@code null}
     * @param defaultValue the default value.
     * @return the double represented by the string, or defaultValue if conversion fails.
     * @since 2.1
     */
    public static double toDouble(final String str, final double defaultValue) {
        if (StrUtil.isBlank(str)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        }
        catch (final RuntimeException e) {
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
        if (StrUtil.isBlank(val)) {
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
        if (StrUtil.isBlank(val)) {
            return defaultValue;
        }
        if (val.equalsIgnoreCase(NULL_STR)) {
            return defaultValue;
        }
        try {
            // 去除前后空格以处理 " 123 " 这类情况
            val = val.trim();
            return Integer.parseInt(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
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
            return Boolean.TRUE;
        }
        else if (FALSE_SET.contains(formatStr)) {
            return Boolean.FALSE;
        }
        else {
            return null;
        }
    }

    /**
     * Converts an Integer to a Boolean using the convention that {@code zero} is
     * {@code false}, every other numeric value is {@code true}.
     *
     * <p>
     * {@code null} will be converted to {@code null}.
     * </p>
     *
     * <p>
     * NOTE: This method may return {@code null} and may throw a
     * {@link NullPointerException} if unboxed to a {@code boolean}.
     * </p>
     *
     * <pre>
     *   BooleanUtils.toBooleanObject(Integer.valueOf(0))    = Boolean.FALSE
     *   BooleanUtils.toBooleanObject(Integer.valueOf(1))    = Boolean.TRUE
     *   BooleanUtils.toBooleanObject(Integer.valueOf(null)) = null
     * </pre>
     * @param value the Integer to convert
     * @return Boolean.TRUE if non-zero, Boolean.FALSE if zero, {@code null} if
     * {@code null} input
     */
    public static Boolean toBooleanObject(final Integer value) {
        if (value == null) {
            return null;
        }
        return value.longValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Convert Object value to short value if parameter value is legal.
     * @param val object value
     * @return Converted short value and its default value is null.
     */
    public static Short toShort(Object val) {
        if (Objects.isNull(val)) {
            return null;
        }
        if (val instanceof Short) {
            return (Short) val;
        }
        return toShort(val.toString());
    }

    /**
     * Convert String value to short value if parameter value is legal.
     * @param val String value which need to be converted to short value.
     * @return Converted short value and its default value is null.
     */
    public static Short toShort(String val) {
        return toShort(val, null);
    }

    /**
     * Convert String value to short value if parameter value is legal. And return default
     * value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return Short value if input value is legal, otherwise default value
     */
    public static Short toShort(String val, Short defaultValue) {
        if (StrUtil.isBlank(val)) {
            return defaultValue;
        }
        try {
            return Short.parseShort(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    /**
     * Convert Object value to byte value if parameter value is legal.
     * @param val object value
     * @return Converted byte value and its default value is null.
     */
    public static Byte toByte(Object val) {
        if (Objects.isNull(val)) {
            return null;
        }
        if (val instanceof Byte) {
            return (Byte) val;
        }
        return toByte(val.toString());
    }

    /**
     * Convert String value to byte value if parameter value is legal.
     * @param val String value which need to be converted to byte value.
     * @return Converted byte value and its default value is null.
     */
    public static Byte toByte(String val) {
        return toByte(val, null);
    }

    /**
     * Convert String value to byte value if parameter value is legal. And return default
     * value if parameter value is null or blank str.
     * @param val value
     * @param defaultValue default value
     * @return Byte value if input value is legal, otherwise default value
     */
    public static Byte toByte(String val, Byte defaultValue) {
        if (StrUtil.isBlank(val)) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(val);
        }
        catch (NumberFormatException exception) {
            return defaultValue;
        }
    }

    /**
     * Convert Object to String. Returns empty string if object is null.
     * @param obj Object to convert
     * @return String representation of object
     */
    public static String toString(Object obj) {
        return toString(obj, StrUtil.EMPTY);
    }

    /**
     * Convert Object to String with default value.
     * @param obj Object to convert
     * @param defaultValue Default value if object is null
     * @return String representation of object or default value
     */
    public static String toString(Object obj, String defaultValue) {
        if (Objects.isNull(obj)) {
            return defaultValue;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return Arrays.deepToString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        return obj.toString();
    }

    /**
     * Convert int to String.
     * @param value int value to convert
     * @return String representation of int value
     */
    public static String toString(int value) {
        return Integer.toString(value);
    }

    /**
     * Convert long to String.
     * @param value long value to convert
     * @return String representation of long value
     */
    public static String toString(long value) {
        return Long.toString(value);
    }

    /**
     * Convert double to String.
     * @param value double value to convert
     * @return String representation of double value
     */
    public static String toString(double value) {
        return Double.toString(value);
    }

    /**
     * Convert float to String.
     * @param value float value to convert
     * @return String representation of float value
     */
    public static String toString(float value) {
        return Float.toString(value);
    }

    /**
     * Convert boolean to String.
     * @param value boolean value to convert
     * @return String representation of boolean value
     */
    public static String toString(boolean value) {
        return Boolean.toString(value);
    }

    /**
     * Convert byte to String.
     * @param value byte value to convert
     * @return String representation of byte value
     */
    public static String toString(byte value) {
        return Byte.toString(value);
    }

    /**
     * Convert short to String.
     * @param value short value to convert
     * @return String representation of short value
     */
    public static String toString(short value) {
        return Short.toString(value);
    }

    /**
     * Convert char to String.
     * @param value char value to convert
     * @return String representation of char value
     */
    public static String toString(char value) {
        return Character.toString(value);
    }

    /**
     * Convert Object to specified type with default value.
     * @param obj Object to convert
     * @param defaultValue default value if conversion fails
     * @param parser function to parse object to target type
     * @param <T> target type
     * @return converted value or default value
     */
    public static <T> T convert(Object obj, T defaultValue, Function<Object, T> parser) {
        if (ObjectUtil.isEmpty(obj)) {
            return defaultValue;
        }
        try {
            return parser.apply(obj);
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

}
