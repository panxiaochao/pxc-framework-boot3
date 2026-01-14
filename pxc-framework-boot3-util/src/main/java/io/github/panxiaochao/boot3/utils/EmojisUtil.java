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

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 轻量级表情符号工具类
 * </p>
 *
 * @author Lypxc
 * @since 2024-09-30
 * @version 1.0
 */
public class EmojisUtil {

    /**
     * 是否为表情符号.
     * @param str 被测试的字符串
     * @return 是否为Emoji表情的Unicode符
     */
    public static boolean isEmoji(String str) {
        return EmojiManager.isEmoji(str);
    }

    /**
     * 是否包含表情符号.
     * @param str 被测试的字符串
     * @return 是否包含Emoji表情的Unicode符
     */
    public static boolean containsEmoji(String str) {
        return EmojiManager.containsEmoji(str);
    }

    /**
     * 通过tag方式获取对应的所有Emoji表情
     * @param tag tag标签，例如“happy”
     * @return Emoji表情集合，如果找不到返回null
     */
    public static Set<Emoji> getByTag(String tag) {
        return EmojiManager.getForTag(tag);
    }

    /**
     * 通过别名获取Emoji
     * @param alias 别名，例如“smile”
     * @return Emoji对象，如果找不到返回null
     */
    public static Emoji get(String alias) {
        return EmojiManager.getForAlias(alias);
    }

    /**
     * 将子串中的Emoji别名（两个":"包围的格式）和其HTML表示形式替换为为Unicode Emoji符号.
     *
     * <pre>
     *     <code>:smile:</code> 替换为 <code>😄</code> <code>&amp;#128516;</code>
     * 替换为<code>😄</code> <code>:boy|type_6:</code> 替换为 <code>👦🏿</code> </pre>
     * @param str 包含Emoji别名或者HTML表现形式的字符串
     * @return 替换后的字符串
     */
    public static String toUnicode(String str) {
        return EmojiParser.parseToUnicode(str);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为别名表现形式（两个":"包围的格式）
     * <p>
     * 例如： <code>😄</code> 转换为 <code>:smile:</code>
     *
     * <p>
     * {@code EmojiParser.FitzpatrickAction}参数被设置为PARSE，则别名后会增加"|"并追加fitzpatrick类型
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy|type_6:</code>
     *
     * <p>
     * {@code EmojiParser.FitzpatrickAction}参数被设置为REMOVE，则别名后的"|"和类型将被去除
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:</code>
     *
     * <p>
     * {@code EmojiParser.FitzpatrickAction}参数被设置为IGNORE，则别名后的类型将被忽略
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy:🏿</code>
     * @param str 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toAlias(String str) {
        return toAlias(str, EmojiParser.FitzpatrickAction.PARSE);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为别名表现形式（两个":"包围的格式），别名后会增加"|"并追加fitzpatrick类型
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>:boy|type_6:</code>
     * @param str 包含Emoji Unicode字符的字符串
     * @param fitzpatrickAction {@code EmojiParser.FitzpatrickAction}
     * @return 替换后的字符串
     */
    public static String toAlias(String str, EmojiParser.FitzpatrickAction fitzpatrickAction) {
        return EmojiParser.parseToAliases(str, fitzpatrickAction);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML 16进制表现形式
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>&amp;#x1f466;</code>
     * @param str 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toHtmlHex(String str) {
        return toHtml(str, true);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML表现形式（Hex方式）
     * <p>
     * 例如：<code>👦🏿</code> 转换为 <code>&amp;#128102;</code>
     * @param str 包含Emoji Unicode字符的字符串
     * @return 替换后的字符串
     */
    public static String toHtml(String str) {
        return toHtml(str, false);
    }

    /**
     * 将字符串中的Unicode Emoji字符转换为HTML表现形式，例如： <pre>
     * 如果为hex形式，<code>👦🏿</code> 转换为 <code>&amp;#x1f466;</code> 否则，<code>👦🏿</code> 转换为
     * <code>&amp;#128102;</code> </pre>
     * @param str 包含Emoji Unicode字符的字符串
     * @param isHex 是否hex形式
     * @return 替换后的字符串
     * @since 5.7.21
     */
    public static String toHtml(String str, boolean isHex) {
        return isHex ? EmojiParser.parseToHtmlHexadecimal(str) : EmojiParser.parseToHtmlDecimal(str);
    }

    /**
     * 去除字符串中所有的Emoji Unicode字符
     * @param str 包含Emoji字符的字符串
     * @return 替换后的字符串
     */
    public static String removeAllEmojis(String str) {
        return EmojiParser.removeAllEmojis(str);
    }

    /**
     * 提取字符串中所有的Emoji Unicode
     * @param str 包含Emoji字符的字符串
     * @return Emoji字符列表
     */
    public static List<String> extractEmojis(String str) {
        return EmojiParser.extractEmojis(str);
    }

}
