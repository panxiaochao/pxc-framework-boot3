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

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 正则表达式工具类
 * </p>
 *
 * @author Lypxc
 * @since 2024-10-11
 * @version 1.0
 */
public class RegexUtil {

    /**
     * The Pattern POOLS.
     */
    private static final Map<String, Pattern> POOLS = new ConcurrentHashMap<>();

    /**
     * 给定内容是否匹配正则.
     * @param regex 正则表达式
     * @param content 被匹配内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (StrUtil.isAnyBlank(regex, content)) {
            return false;
        }
        final Pattern pattern = POOLS.computeIfAbsent(regex, (key) -> Pattern.compile(regex, Pattern.DOTALL));
        return isMatch(pattern, content);
    }

    /**
     * 给定内容是否匹配正则.
     * @param pattern 正则模式
     * @param content 被匹配内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (ObjectUtil.anyNull(pattern, content)) {
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @param regex 正则表达式
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) {
        if (StrUtil.isAnyBlank(regex, content)) {
            return null;
        }
        final Pattern pattern = POOLS.computeIfAbsent(regex, (key) -> Pattern.compile(regex, Pattern.DOTALL));
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (ObjectUtil.anyNull(pattern, content)) {
            return null;
        }
        final Set<String> result = new HashSet<>();
        get(pattern, content, matcher -> result.add(matcher.group(groupIndex)));
        return result.stream().findFirst().orElse(null);
    }

    /**
     * 获得匹配的字符串.
     * @param regex 正则表达式
     * @param content 被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, String groupName) {
        if (StrUtil.isAnyBlank(regex, content)) {
            return null;
        }
        final Pattern pattern = POOLS.computeIfAbsent(regex, (key) -> Pattern.compile(regex, Pattern.DOTALL));
        return get(pattern, content, groupName);
    }

    /**
     * 获得匹配的字符串.
     * @param pattern 匹配的正则
     * @param content 被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, String groupName) {
        if (ObjectUtil.anyNull(pattern, content, groupName)) {
            return null;
        }
        final Set<String> result = new HashSet<>();
        get(pattern, content, matcher -> result.add(matcher.group(groupName)));
        return result.stream().findFirst().orElse(null);
    }

    /**
     * 在给定字符串中查找给定规则的字符, 结果使用使用{@link Consumer}处理，如果内容中有多个匹配项，则只处理找到的第一个结果.
     * @param pattern 匹配的正则
     * @param content 被匹配的内容
     * @param consumer 匹配到的内容处理器
     */
    public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (ObjectUtil.anyNull(pattern, content, consumer)) {
            return;
        }
        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            consumer.accept(m);
        }
    }

    /**
     * 获得匹配的字符串匹配到的所有分组.
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content) {
        return getAllGroups(pattern, content, true);
    }

    /**
     * 获得匹配的字符串匹配到的所有分组.
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
        return getAllGroups(pattern, content, withGroup0, false);
    }

    /**
     * 获得匹配的字符串匹配到的所有分组.
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @param findAll 是否查找所有匹配到的内容，{@code false}表示只读取第一个匹配到的内容
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0,
            boolean findAll) {
        if (ObjectUtil.anyNull(pattern, content)) {
            return null;
        }
        ArrayList<String> result = new ArrayList<>();
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            final int startGroup = withGroup0 ? 0 : 1;
            final int groupCount = matcher.groupCount();
            for (int i = startGroup; i <= groupCount; i++) {
                result.add(matcher.group(i));
            }
            if (!findAll) {
                break;
            }
        }
        return result;
    }

    /**
     * 根据给定正则查找字符串中的匹配项，返回所有匹配的分组名对应分组值.
     *
     * <pre>
     * pattern: (?&lt;year&gt;\\d+)-(?&lt;month&gt;\\d+)-(?&lt;day&gt;\\d+)
     * content: 2021-10-11
     * result : year: 2021, month: 10, day: 11
     * </pre>
     * @param regex 正则表达式
     * @param content 被匹配的内容
     * @return 命名捕获组，key为分组名，value为对应值
     */
    public static Map<String, String> getAllGroupNames(String regex, CharSequence content) {
        if (StrUtil.isAnyBlank(regex, content)) {
            return null;
        }
        final Pattern pattern = POOLS.computeIfAbsent(regex, (key) -> Pattern.compile(regex, Pattern.DOTALL));
        return getAllGroupNames(pattern, content);
    }

    /**
     * 根据给定正则查找字符串中的匹配项，返回所有匹配的分组名对应分组值.
     *
     * <pre>
     * pattern: (?&lt;year&gt;\\d+)-(?&lt;month&gt;\\d+)-(?&lt;day&gt;\\d+)
     * content: 2021-10-11
     * result : year: 2021, month: 10, day: 11
     * </pre>
     * @param pattern 匹配的正则
     * @param content 被匹配的内容
     * @return 命名捕获组，key为分组名，value为对应值
     */
    public static Map<String, String> getAllGroupNames(Pattern pattern, CharSequence content) {
        if (ObjectUtil.anyNull(pattern, content)) {
            return null;
        }
        final Matcher m = pattern.matcher(content);
        final Map<String, String> result = MapUtil.newHashMap(m.groupCount());
        if (m.find()) {
            // 通过反射获取 namedGroups 方法
            Method handlerMethod = ReflectionUtils.findMethod(pattern.getClass(), "namedGroups");
            if (handlerMethod != null) {
                ReflectionUtils.makeAccessible(handlerMethod);
                Map<String, Integer> map = (Map<String, Integer>) ReflectionUtils.invokeMethod(handlerMethod, pattern);
                if (map != null) {
                    map.forEach((key, value) -> result.put(key, m.group(value)));
                }
            }
        }
        return result;
    }

}
