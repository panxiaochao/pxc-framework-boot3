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

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * <p>
 * This class tries to handle {@code null} input gracefully
 * </p>
 *
 * @author Lypxc
 * @since 2023-01-28
 */
public class ObjectUtil {

    /**
     * Checks if all values in the array are not {@code nulls}.
     *
     * <p>
     * If any value is {@code null} or the array is {@code null} then {@code false} is
     * returned. If all elements in array are not {@code null} or the array is empty
     * (contains no elements) {@code true} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtil.allNotNull(*)             = true
     * ObjectUtil.allNotNull(*, *)          = true
     * ObjectUtil.allNotNull(null)          = false
     * ObjectUtil.allNotNull(null, null)    = false
     * ObjectUtil.allNotNull(null, *)       = false
     * ObjectUtil.allNotNull(*, null)       = false
     * ObjectUtil.allNotNull(*, *, null, *) = false
     * </pre>
     * @param values the values to test, may be {@code null} or empty
     * @return {@code false} if there is at least one {@code null} value in the array or
     * the array is {@code null}, {@code true} if all values in the array are not
     * {@code null}s or array contains no elements.
     */
    public static boolean allNotNull(final Object... values) {
        return values != null && Arrays.stream(values).noneMatch(Objects::isNull);
    }

    /**
     * Checks if all values in the given array are {@code null}.
     *
     * <p>
     * If all the values are {@code null} or the array is {@code null} or empty, then
     * {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtil.allNull(*)                = false
     * ObjectUtil.allNull(*, null)          = false
     * ObjectUtil.allNull(null, *)          = false
     * ObjectUtil.allNull(null, null, *, *) = false
     * ObjectUtil.allNull(null)             = true
     * ObjectUtil.allNull(null, null)       = true
     * </pre>
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if all values in the array are {@code null}s, {@code false} if
     * there is at least one non-null value in the array.
     */
    public static boolean allNull(final Object... values) {
        return !anyNotNull(values);
    }

    /**
     * Checks if any value in the given array is not {@code null}.
     *
     * <p>
     * If all the values are {@code null} or the array is {@code null} or empty then
     * {@code false} is returned. Otherwise {@code true} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtil.anyNotNull(*)                = true
     * ObjectUtil.anyNotNull(*, null)          = true
     * ObjectUtil.anyNotNull(null, *)          = true
     * ObjectUtil.anyNotNull(null, null, *, *) = true
     * ObjectUtil.anyNotNull(null)             = false
     * ObjectUtil.anyNotNull(null, null)       = false
     * </pre>
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if there is at least one non-null value in the array,
     * {@code false} if all values in the array are {@code null}s. If the array is
     * {@code null} or empty {@code false} is also returned.
     */
    public static boolean anyNotNull(final Object... values) {
        return firstNonNull(values) != null;
    }

    /**
     * Checks if any value in the given array is {@code null}.
     *
     * <p>
     * If any of the values are {@code null} or the array is {@code null}, then
     * {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtil.anyNull(*)             = false
     * ObjectUtil.anyNull(*, *)          = false
     * ObjectUtil.anyNull(null)          = true
     * ObjectUtil.anyNull(null, null)    = true
     * ObjectUtil.anyNull(null, *)       = true
     * ObjectUtil.anyNull(*, null)       = true
     * ObjectUtil.anyNull(*, *, null, *) = true
     * </pre>
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if there is at least one {@code null} value in the array,
     * {@code false} if all the values are non-null. If the array is {@code null} or
     * empty, {@code true} is also returned.
     */
    public static boolean anyNull(final Object... values) {
        return !allNotNull(values);
    }

    /**
     * <p>
     * Returns the first value in the array which is not {@code null}. If all the values
     * are {@code null} or the array is {@code null} or empty then {@code null} is
     * returned.
     * </p>
     *
     * <pre>
     * ObjectUtil.firstNonNull(null, null)      = null
     * ObjectUtil.firstNonNull(null, "")        = ""
     * ObjectUtil.firstNonNull(null, null, "")  = ""
     * ObjectUtil.firstNonNull(null, "zz")      = "zz"
     * ObjectUtil.firstNonNull("abc", *)        = "abc"
     * ObjectUtil.firstNonNull(null, "xyz", *)  = "xyz"
     * ObjectUtil.firstNonNull(Boolean.TRUE, *) = Boolean.TRUE
     * ObjectUtil.firstNonNull()                = null
     * </pre>
     * @param <T> the component type of the array
     * @param values the values to test, may be {@code null} or empty
     * @return the first value from {@code values} which is not {@code null}, or
     * {@code null} if there are no non-null values
     */
    @SafeVarargs
    public static <T> T firstNonNull(final T... values) {
        return Arrays.stream(values).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * <p>
     * Checks if an Object is empty or null.
     * </p>
     * <p>
     * The following types are supported:
     * <ul>
     * <li>{@link CharSequence}: Considered empty if its length is zero.</li>
     * <li>{@code Array}: Considered empty if its length is zero.</li>
     * <li>{@link Collection}: Considered empty if it has zero elements.</li>
     * <li>{@link Map}: Considered empty if it has zero key-value mappings.</li>
     * </ul>
     *
     * <pre>
     * ObjectUtil.isEmpty(null)             = true
     * ObjectUtil.isEmpty("")               = true
     * ObjectUtil.isEmpty("ab")             = false
     * ObjectUtil.isEmpty(new int[]{})      = true
     * ObjectUtil.isEmpty(new int[]{1,2,3}) = false
     * ObjectUtil.isEmpty(1234)             = false
     * </pre>
     * @param object the {@code Object} to test, may be {@code null}
     * @return {@code true} if the object has a supported type and is empty or null,
     * {@code false} otherwise
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Optional) {
            return !((Optional<?>) object).isPresent();
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Iterable) {
            return !((Iterable<?>) object).iterator().hasNext();
        }
        if (object instanceof Iterator) {
            return !((Iterator<?>) object).hasNext();
        }
        return false;
    }

    /**
     * Determine whether the given array is empty: i.e. {@code null} or of zero length.
     * @param array the array to check
     * @see #isEmpty(Object)
     */
    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * <p>
     * Checks if an Object is not empty and not null.
     * </p>
     * <p>
     * The following types are supported:
     * <ul>
     * <li>{@link CharSequence}: Considered empty if its length is zero.</li>
     * <li>{@code Array}: Considered empty if its length is zero.</li>
     * <li>{@link Collection}: Considered empty if it has zero elements.</li>
     * <li>{@link Map}: Considered empty if it has zero key-value mappings.</li>
     * </ul>
     *
     * <pre>
     * ObjectUtil.isNotEmpty(null)             = false
     * ObjectUtil.isNotEmpty("")               = false
     * ObjectUtil.isNotEmpty("ab")             = true
     * ObjectUtil.isNotEmpty(new int[]{})      = false
     * ObjectUtil.isNotEmpty(new int[]{1,2,3}) = true
     * ObjectUtil.isNotEmpty(1234)             = true
     * </pre>
     * @param object the {@code Object} to test, may be {@code null}
     * @return {@code true} if the object has an unsupported type or is not empty and not
     * null, {@code false} otherwise
     */
    public static boolean isNotEmpty(final Object object) {
        return !isEmpty(object);
    }

    /**
     * <p>
     * Returns a default value if the object passed is {@code null}.
     * </p>
     *
     * <pre>
     * ObjectUtil.defaultIfNull(null, null)      = null
     * ObjectUtil.defaultIfNull(null, "")        = ""
     * ObjectUtil.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtil.defaultIfNull("abc", *)        = "abc"
     * ObjectUtil.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     * @param <T> the type of the object
     * @param object the {@code Object} to test, may be {@code null}
     * @param defaultValue the default value to return, may be {@code null}
     * @return {@code object} if it is not {@code null}, defaultValue otherwise
     */
    public static <T> T getIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * <p>
     * Returns the given {@code object} is it is non-null, otherwise returns the
     * Supplier's {@link Supplier#get()} value.
     * </p>
     *
     * <p>
     * The caller responsible for thread-safety and exception handling of default value
     * supplier.
     * </p>
     *
     * <pre>
     * ObjectUtil.getIfNull(null, () -&gt; null)     = null
     * ObjectUtil.getIfNull(null, null)              = null
     * ObjectUtil.getIfNull(null, () -&gt; "")       = ""
     * ObjectUtil.getIfNull(null, () -&gt; "zz")     = "zz"
     * ObjectUtil.getIfNull("abc", *)                = "abc"
     * ObjectUtil.getIfNull(Boolean.TRUE, *)         = Boolean.TRUE
     * </pre>
     * @param <T> the type of the object
     * @param object the {@code Object} to test, may be {@code null}
     * @param defaultSupplier the default value to return, may be {@code null}
     * @return {@code object} if it is not {@code null},
     * {@code defaultValueSupplier.get()} otherwise
     */
    public static <T> T getIfNull(final T object, final Supplier<T> defaultSupplier) {
        return object != null ? object : defaultSupplier == null ? null : defaultSupplier.get();
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the
     * input array contents plus the given object.
     * @param array the array to append to (can be {@code null})
     * @param obj the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
        Class<?> compType = Object.class;
        if (array != null) {
            compType = array.getClass().getComponentType();
        }
        else if (obj != null) {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1 : 1);
        @SuppressWarnings("unchecked")
        A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
        if (array != null) {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * <p>
     * Clone an object.
     * </p>
     * @param <T> the type of the object
     * @param obj the object to clone, null returns null
     * @return the clone if the object implements {@link Cloneable} otherwise {@code null}
     */
    public static <T> T clone(final T obj) {
        if (obj instanceof Cloneable) {
            final Object result;
            if (obj.getClass().isArray()) {
                final Class<?> componentType = obj.getClass().getComponentType();
                if (componentType.isPrimitive()) {
                    int length = Array.getLength(obj);
                    result = Array.newInstance(componentType, length);
                    while (length-- > 0) {
                        Array.set(result, length, Array.get(obj, length));
                    }
                }
                else {
                    result = ((Object[]) obj).clone();
                }
            }
            else {
                try {
                    final Method clone = obj.getClass().getMethod("clone");
                    result = clone.invoke(obj);
                }
                catch (final NoSuchMethodException e) {
                    throw new RuntimeException("Cloneable type " + obj.getClass().getName() + " has no clone method",
                            e);
                }
                catch (final IllegalAccessException e) {
                    throw new RuntimeException("Cannot clone Cloneable type " + obj.getClass().getName(), e);
                }
                catch (final InvocationTargetException e) {
                    throw new RuntimeException("Exception cloning Cloneable type " + obj.getClass().getName(),
                            e.getCause());
                }
            }
            @SuppressWarnings("unchecked") // OK because input is of type T
            final T checked = (T) result;
            return checked;
        }
        else {
            if (obj instanceof Serializable) {
                return SerializationUtil.clone(obj);
            }
        }
        return null;
    }

    /**
     * 比较两个对象是否相等，此方法是 {@link #equal(Object, Object)}的别名方法。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see #equal(Object, Object)
     */
    public static boolean equals(Object obj1, Object obj2) {
        return equal(obj1, obj2);
    }

    /**
     * 比较两个对象是否相等。<br>
     * 相同的条件有两个，满足其一即可：<br>
     * <ol>
     * <li>obj1 == null &amp;&amp; obj2 == null</li>
     * <li>obj1.equals(obj2)</li>
     * <li>如果是BigDecimal比较，0 == obj1.compareTo(obj2)</li>
     * </ol>
     * @param obj1 对象1
     * @param obj2 对象2
     * @return 是否相等
     * @see Objects#equals(Object, Object)
     */
    public static boolean equal(Object obj1, Object obj2) {
        if (obj1 instanceof Number && obj2 instanceof Number) {
            if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
                // BigDecimal使用compareTo方式判断，因为使用equals方法也判断小数位数，如2.0和2.00就不相等
                return equals((BigDecimal) obj1, (BigDecimal) obj2);
            }
        }
        return Objects.equals(obj1, obj2);
    }

    /**
     * 比较大小，值相等 返回true<br>
     * 此方法通过调用{@link BigDecimal#compareTo(BigDecimal)}方法来判断是否相等<br>
     * 此方法判断值相等时忽略精度的，即0.00 == 0
     * @param bigNum1 数字1
     * @param bigNum2 数字2
     * @return 是否相等
     */
    public static boolean equals(BigDecimal bigNum1, BigDecimal bigNum2) {
        // noinspection NumberEquality
        if (bigNum1.equals(bigNum2)) {
            // 如果用户传入同一对象，省略compareTo以提高性能。
            return true;
        }
        if (bigNum1 == null || bigNum2 == null) {
            return false;
        }
        return 0 == bigNum1.compareTo(bigNum2);
    }

    /**
     * 用字符串形式返回整个对象标识.
     * @param obj the object (may be {@code null})
     * @return 字符串标识
     */
    public static String identityToString(@Nullable Object obj) {
        if (obj == null) {
            return StringPools.EMPTY;
        }
        return obj.getClass().getName() + StringPools.AT + getIdentityHexString(obj);
    }

    /**
     * 用字符串形式返回对象标识哈希码的十六进制.
     * @param obj the object
     * @return the object's identity code in hex notation
     */
    public static String getIdentityHexString(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }

    /**
     * 返回基于内容的字符串形式
     * @param obj the object to build a display String for
     * @return a display String representation of {@code obj}
     * @see #nullSafeToString(Object)
     */
    public static String getDisplayString(@Nullable Object obj) {
        if (obj == null) {
            return StringPools.EMPTY;
        }
        return nullSafeToString(obj);
    }

    /**
     * 返回指定对象的字符串表示形式.
     * @param obj the object to build a String representation for
     * @return a String representation of {@code obj}
     */
    public static String nullSafeToString(@Nullable Object obj) {
        if (obj == null) {
            return StringPools.NULL;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Object[]) {
            return nullSafeToString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return nullSafeToString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return nullSafeToString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return nullSafeToString((char[]) obj);
        }
        if (obj instanceof double[]) {
            return nullSafeToString((double[]) obj);
        }
        if (obj instanceof float[]) {
            return nullSafeToString((float[]) obj);
        }
        if (obj instanceof int[]) {
            return nullSafeToString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return nullSafeToString((long[]) obj);
        }
        if (obj instanceof short[]) {
            return nullSafeToString((short[]) obj);
        }
        String str = obj.toString();
        return (str != null ? str : StringPools.EMPTY);
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable Object[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (Object o : array) {
            stringJoiner.add(String.valueOf(o));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable boolean[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (boolean b : array) {
            stringJoiner.add(String.valueOf(b));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable byte[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (byte b : array) {
            stringJoiner.add(String.valueOf(b));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable char[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (char c : array) {
            stringJoiner.add('\'' + String.valueOf(c) + '\'');
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable double[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (double d : array) {
            stringJoiner.add(String.valueOf(d));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable float[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (float f : array) {
            stringJoiner.add(String.valueOf(f));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable int[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (int i : array) {
            stringJoiner.add(String.valueOf(i));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable long[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (long l : array) {
            stringJoiner.add(String.valueOf(l));
        }
        return stringJoiner.toString();
    }

    /**
     * 返回指定数组内容的字符串表示形式.
     * @param array the array to build a String representation for
     * @return a String representation of {@code array}
     */
    public static String nullSafeToString(@Nullable short[] array) {
        if (array == null) {
            return StringPools.NULL;
        }
        int length = array.length;
        if (length == 0) {
            return StringPools.LEFT_RIGHT_BRACE;
        }
        StringJoiner stringJoiner = new StringJoiner(", ", StringPools.LEFT_BRACE, StringPools.RIGHT_BRACE);
        for (short s : array) {
            stringJoiner.add(String.valueOf(s));
        }
        return stringJoiner.toString();
    }

}
