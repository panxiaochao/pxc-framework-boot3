package io.github.panxiaochao.boot3.cache.dict;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.panxiaochao.boot3.utils.dict.AbstractDictResolver;

import java.util.Map;

/**
 * <p>
 * 字典服务接口，使用 Caffeine 提供字典操作的基本方法
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
public class CaffeineDictResolver extends AbstractDictResolver {

    private final Cache<String, Map<String, String>> CAFFEINE = Caffeine.newBuilder()
        .initialCapacity(100)
        .maximumSize(1000)
        .build();

    @Override
    public Map<String, String> getAllDictByDictCode(String dictCode) {
        String dictCacheKey = CACHE_KEY_PREFIX + dictCode;
        Map<String, String> dictMap = CAFFEINE.getIfPresent(dictCacheKey);
        return (dictMap == null || dictMap.isEmpty()) ? Map.of() : dictMap;
    }

    @Override
    public void loadAllDict(Map<String, Map<String, String>> dictAllMap) {
        dictAllMap.forEach((dictCode, map) -> CAFFEINE.put(CACHE_KEY_PREFIX + dictCode, map));
    }

    @Override
    public void loadDict(String dictCode, Map<String, String> dictMap) {
        CAFFEINE.put(CACHE_KEY_PREFIX + dictCode, dictMap);
    }

}
