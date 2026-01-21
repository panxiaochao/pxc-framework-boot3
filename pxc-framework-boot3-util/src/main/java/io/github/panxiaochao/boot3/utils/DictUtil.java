package io.github.panxiaochao.boot3.utils;

import io.github.panxiaochao.boot3.utils.dict.DictResolverProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <p>
 * 字典工具类，是一个轻量级缓存工具类，采用Key-Value缓存模式，提供字典相关的工具方法
 * </p>
 *
 * <ul>
 * <li>该类中的方法均为静态方法，不需要实例化即可调用</li>
 * <li>目前采用缓存（Redis、Caffeine、Memory等）模式来读取，避免频繁查询数据库</li>
 * <li>如果使用微服务模式，则必须使用 Redis 缓存共享模式， spring.pxc-framework-boot3.cache.cacheType=REDIS</li>
 * <li>如果使用单体模式，则可以使用 Caffeine 缓存模式，
 * spring.pxc-framework-boot3.cache.cacheType=CAFFEINE</li>
 * <li>如果使用单体模式，且不配置缓存类型，则默认使用 Memory 缓存模式</li>
 * </ul>
 *
 * @author lypxc
 * @since 2026-01-15
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DictUtil {

    private static final String DEFAULT_SEPARATOR = StringPools.COMMA;

    /**
     * 根据字典类型和字典值获取字典文本，默认分隔符为逗号
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @return 字典文本
     */
    public static String getDictText(String dictCode, String dictValue) {
        return getDictText(dictCode, dictValue, DEFAULT_SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典文本
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @param separator 字典值分隔符
     * @return 字典文本
     */
    public static String getDictText(String dictCode, String dictValue, String separator) {
        if (StrUtil.isBlank(dictCode)) {
            return StrUtil.EMPTY;
        }
        return DictResolverProvider.getDictResolver().getDictText(dictCode, dictValue, separator);
    }

    /**
     * 根据字典类型和字典文本获取字典值，默认分隔符为逗号
     * @param dictCode 字典编码
     * @param dictText 字典文本
     * @return 字典值
     */
    public static String getDictValue(String dictCode, String dictText) {
        return getDictValue(dictCode, dictText, DEFAULT_SEPARATOR);
    }

    /**
     * 根据字典类型和字典文本获取字典值
     * @param dictCode 字典编码
     * @param dictText 字典文本
     * @param separator 字典文本分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictCode, String dictText, String separator) {
        if (StrUtil.isBlank(dictCode)) {
            return StrUtil.EMPTY;
        }
        return DictResolverProvider.getDictResolver().getDictValue(dictCode, dictText, separator);
    }

    /**
     * 获取字典下所有的字典值与文本
     * @param dictCode 字典编码
     * @return key = dictValue, value = dictText 组成的 Map
     */
    public static Map<String, String> getAllDictByDictCode(String dictCode) {
        if (StrUtil.isBlank(dictCode)) {
            return Map.of();
        }
        return DictResolverProvider.getDictResolver().getAllDictByDictCode(dictCode);
    }

    /**
     * 加载所有字典项到缓存
     * @param dictAllMap 字典项 Map，key = dictCode, value = Map(dictValue, dictText)
     */
    public static void loadAllDict(Map<String, Map<String, String>> dictAllMap) {
        DictResolverProvider.getDictResolver().loadAllDict(dictAllMap);
    }

    /**
     * 加载字典项到缓存
     * @param dictCode 字典编码
     * @param dictMap 字典项 Map，key = dictValue, value = dictText
     */
    public static void loadDict(String dictCode, Map<String, String> dictMap) {
        DictResolverProvider.getDictResolver().loadDict(dictCode, dictMap);
    }

}
