package io.github.panxiaochao.boot3.utils.dict;

import io.github.panxiaochao.boot3.utils.StrUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 抽象字典解析器，提供基本的字典操作方法
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
public abstract class AbstractDictResolver implements IDictResolver {

    public static final String CACHE_KEY_PREFIX = "pxc-framework-boot3:cache:dict:";

    @Override
    public String getDictText(String dictCode, String dictValue, String separator) {
        Map<String, String> dictMap = this.getAllDictByDictCode(dictCode);
        if (StrUtil.isBlank(dictValue) || dictMap.isEmpty()) {
            return StrUtil.EMPTY;
        }

        // 包含分隔符，按分隔符拆分处理
        if (dictValue.contains(separator)) {
            return Arrays.stream(dictValue.split(separator))
                .map(value -> dictMap.getOrDefault(value, StrUtil.EMPTY))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(separator));
        }
        else {
            // 单个值，直接从字典中获取
            return dictMap.entrySet()
                .stream()
                .filter(entry -> dictValue.equals(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(StrUtil.EMPTY);
        }
    }

    @Override
    public String getDictValue(String dictCode, String dictText, String separator) {
        Map<String, String> dictMap = this.getAllDictByDictCode(dictCode);
        if (StrUtil.isBlank(dictText) || dictMap.isEmpty()) {
            return StrUtil.EMPTY;
        }

        // 包含分隔符，按分隔符拆分处理
        if (dictText.contains(separator)) {
            return Arrays.stream(dictText.split(separator))
                .map(value -> findKeyByValue(dictMap, value))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.joining(separator));
        }
        else {
            // 单个值，直接从字典中获取
            return findKeyByValue(dictMap, dictText);
        }
    }

    /**
     * 根据值查找键的方法
     */
    protected String findKeyByValue(Map<String, String> dictMap, String value) {
        return dictMap.entrySet()
            .stream()
            .filter(entry -> value.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .filter(StrUtil::isNotBlank)
            .findFirst()
            .orElse(StrUtil.EMPTY);
    }

}
