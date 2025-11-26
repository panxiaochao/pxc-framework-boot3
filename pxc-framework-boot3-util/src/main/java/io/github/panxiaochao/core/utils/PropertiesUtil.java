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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * <p>
 * 属性工具，提供对Properties对象的加载、保存、子集提取以及从字符串或文件中创建Properties的功能.
 * </p>
 *
 * @author Lypxc
 * @since 2025-04-25
 * @version 1.0
 */
public class PropertiesUtil {

    // ---------------------------------------------------------------- to/from files

    /**
     * 从指定文件名加载Properties对象。
     * @param fileName 文件名，指向要加载的Properties文件
     * @return 加载后的Properties对象
     * @throws IOException 如果文件读取失败或不存在
     */
    public static Properties createFromFile(final String fileName) throws IOException {
        return createFromFile(new File(fileName));
    }

    /**
     * 从指定文件加载Properties对象。
     * @param file 指向要加载的Properties文件
     * @return 加载后的Properties对象
     * @throws IOException 如果文件读取失败或不存在
     */
    public static Properties createFromFile(final File file) throws IOException {
        final Properties prop = new Properties();
        loadFromFile(prop, file);
        return prop;
    }

    /**
     * 从指定文件名加载Properties内容并追加到现有的Properties对象中。
     * @param p 要填充的Properties对象
     * @param fileName 文件名，指向要加载的Properties文件
     * @throws IOException 如果文件读取失败或不存在
     */
    public static void loadFromFile(final Properties p, final String fileName) throws IOException {
        loadFromFile(p, new File(fileName));
    }

    /**
     * 从指定文件加载Properties内容并追加到现有的Properties对象中。
     * @param p 要填充的Properties对象
     * @param file 指向要加载的Properties文件
     * @throws IOException 如果文件读取失败或不存在
     */
    public static void loadFromFile(final Properties p, final File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File does not exist: " + (file != null ? file.getAbsolutePath() : "null"));
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            p.load(fis);
        }
    }

    /**
     * 将Properties对象写入指定文件名的文件中。
     * @param p 要写入的Properties对象
     * @param fileName 目标文件名
     * @throws IOException 如果文件写入失败
     */
    public static void writeToFile(final Properties p, final String fileName) throws IOException {
        writeToFile(p, new File(fileName), null);
    }

    /**
     * 将Properties对象写入指定文件名的文件中，并可选添加头部信息。
     * @param p 要写入的Properties对象
     * @param fileName 目标文件名
     * @param header 可选的头部信息
     * @throws IOException 如果文件写入失败
     */
    public static void writeToFile(final Properties p, final String fileName, final String header) throws IOException {
        writeToFile(p, new File(fileName), header);
    }

    /**
     * 将Properties对象写入指定文件中。
     * @param p 要写入的Properties对象
     * @param file 目标文件
     * @throws IOException 如果文件写入失败
     */
    public static void writeToFile(final Properties p, final File file) throws IOException {
        writeToFile(p, file, null);
    }

    /**
     * 将Properties对象写入指定文件中，并可选添加头部信息。
     * @param p 要写入的Properties对象
     * @param file 目标文件
     * @param header 可选的头部信息
     * @throws IOException 如果文件写入失败
     */
    public static void writeToFile(final Properties p, final File file, final String header) throws IOException {
        if (file == null) {
            throw new IOException("Target file is null");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            p.store(fos, header);
        }
    }

    // ---------------------------------------------------------------- to/from string

    /**
     * 从字符串创建Properties对象。
     * @param data 包含Properties内容的字符串
     * @return 创建的Properties对象
     * @throws IOException 如果字符串解析失败
     */
    public static Properties createFromString(final String data) throws IOException {
        if (StrUtil.isBlank(data)) {
            throw new IOException("Input data is null");
        }
        final Properties p = new Properties();
        loadFromString(p, data);
        return p;
    }

    /**
     * 从字符串加载Properties内容并追加到现有的Properties对象中。
     * @param p 要填充的Properties对象
     * @param data 包含Properties内容的字符串
     * @throws IOException 如果字符串解析失败
     */
    public static void loadFromString(final Properties p, final String data) throws IOException {
        if (StrUtil.isBlank(data)) {
            throw new IOException("Input data is null");
        }
        try (final ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8))) {
            p.load(is);
        }
    }

    // ---------------------------------------------------------------- subsets

    /**
     * 从原始Properties对象中提取子集，复制具有指定前缀的键值对。可以选择是否去除前缀。
     * @param p 原始Properties对象
     * @param prefix 键名前缀
     * @param stripPrefix 是否去除前缀
     * @return 提取的子集Properties对象
     */
    public static Properties subset(final Properties p, String prefix, final boolean stripPrefix) {
        Objects.requireNonNull(p, "Properties object cannot be null");
        if (StrUtil.isNotBlank(prefix) && !prefix.endsWith(StringPools.DOT)) {
            prefix += '.';
        }
        final Properties result = new Properties();
        final int baseLen = prefix.length();
        for (String key : p.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                result.setProperty(stripPrefix ? key.substring(baseLen) : key, p.getProperty(key));
            }
        }
        return result;
    }

}
