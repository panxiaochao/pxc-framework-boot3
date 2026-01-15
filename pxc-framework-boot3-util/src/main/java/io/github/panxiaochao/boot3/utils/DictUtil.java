package io.github.panxiaochao.boot3.utils;

import java.util.Map;

/**
 * <p>
 * 字典工具类，提供字典相关的工具方法
 * </p>
 *
 * <ul>
 * <li>该类中的方法均为静态方法，不需要实例化即可调用</li>
 * <li>目前采用缓存（Redis、Caffeine等）模式来读取，避免频繁查询数据库</li>
 * <li>如果使用微服务模式，则必须使用 Redis 缓存共享模式， spring.pxc-framework-boot3.cache.cacheType=REDIS</li>
 * </ul>
 *
 * @author lypxc
 * @since 2026-01-15
 * @version 1.0
 */
public class DictUtil {

    /**
     * 根据字典类型和字典值获取字典标签
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictText(String dictCode, String dictValue, String separator) {
        return "";
    }

    /**
     * 根据字典类型和字典标签获取字典值
     * @param dictCode 字典编码
     * @param dictText 字典文本
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictCode, String dictText, String separator) {
        return "";
    }

    /**
     * 获取字典下所有的字典值与标签
     * @param dictCode 字典编码
     * @return dictValue为key，dictText为值组成的Map
     */
    public static Map<String, String> getAllDictByDictCode(String dictCode) {
        return Map.of();
    }

}
