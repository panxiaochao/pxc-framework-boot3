package io.github.panxiaochao.boot3.redis.dict;

import io.github.panxiaochao.boot3.redis.utils.RedissonUtil;
import io.github.panxiaochao.boot3.utils.dict.AbstractDictResolver;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * <p>
 * 字典服务接口，使用 Redis 提供字典操作的基本方法
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
@RequiredArgsConstructor
public class RedisDictResolver extends AbstractDictResolver {

    private final String dictCacheKeyPrefix;

    @Override
    public Map<String, String> getAllDictByDictCode(String dictCode) {
        String dictCacheKey = dictCacheKeyPrefix + dictCode;
        Map<String, String> dictMap = RedissonUtil.getMapAll(dictCacheKey);
        return (dictMap == null || dictMap.isEmpty()) ? Map.of() : dictMap;
    }

    @Override
    public void loadAllDict(Map<String, Map<String, String>> dictMap) {
        dictMap.forEach((dictCode, map) -> {
            RedissonUtil.putAllMap(dictCacheKeyPrefix + dictCode, map);
        });
    }

}
