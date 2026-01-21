package io.github.panxiaochao.boot3.utils.dict;

import io.github.panxiaochao.boot3.utils.StringPools;

import java.util.Map;

/**
 * 字典服务接口，提供字典操作的基本方法
 *
 * @author lypxc
 * @since 2026-01-15
 * @version 1.0
 */
public interface IDictResolver {

    /**
     * 根据字典编码和字典值获取字典文本，默认分隔符为逗号
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @return 字典文本
     */
    default String getDictText(String dictCode, String dictValue) {
        return getDictText(dictCode, dictValue, StringPools.COMMA);
    }

    /**
     * 根据字典编码和字典文本获取字典值，默认分隔符为逗号
     * @param dictCode 字典编码
     * @param dictText 字典文本
     * @return 字典值
     */
    default String getDictValue(String dictCode, String dictText) {
        return getDictValue(dictCode, dictText, StringPools.COMMA);
    }

    /**
     * 加载所有字典项到缓存
     * @param dictAllMap 字典映射关系数据，key = 缓存键, value = 字典项 Map，[key = dictValue, value =
     * dictText]
     */
    default void loadAllDict(Map<String, Map<String, String>> dictAllMap) {
    };

    /**
     * 加载字典项到缓存
     * @param dictCode 缓存键
     * @param dictMap 字典项 Map，key = dictValue, value = dictText
     */
    default void loadDict(String dictCode, Map<String, String> dictMap) {
    };

    /**
     * 清空所有字典项缓存
     */
    default void clearAllDict() {
    };

    /**
     * 根据字典编码和字典值获取字典文本
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典文本
     */
    String getDictText(String dictCode, String dictValue, String separator);

    /**
     * 根据字典编码和字典文本获取字典值
     * @param dictCode 字典编码
     * @param dictText 字典文本
     * @param separator 分隔符
     * @return 字典值
     */
    String getDictValue(String dictCode, String dictText, String separator);

    /**
     * 获取指定字典编码下的所有字典项
     * @param dictCode 字典编码
     * @return 字典映射关系
     */
    Map<String, String> getAllDictByDictCode(String dictCode);

}
