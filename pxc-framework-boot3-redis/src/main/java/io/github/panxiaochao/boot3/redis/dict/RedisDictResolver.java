package io.github.panxiaochao.boot3.redis.dict;

import io.github.panxiaochao.boot3.redis.utils.RedissonUtil;
import io.github.panxiaochao.boot3.utils.dict.AbstractDictResolver;

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
public class RedisDictResolver extends AbstractDictResolver {

    @Override
    public Map<String, String> getAllDictByDictCode(String dictCode) {
        String dictCacheKey = CACHE_KEY_PREFIX + dictCode;
        Map<String, String> dictMap = RedissonUtil.getMapAll(dictCacheKey);
        return (dictMap == null || dictMap.isEmpty()) ? Map.of() : dictMap;
    }

    @Override
    public void loadAllDict(Map<String, Map<String, String>> dictAllMap) {
        dictAllMap.forEach((dictCode, map) -> RedissonUtil.putAllMap(CACHE_KEY_PREFIX + dictCode, map));
    }

    @Override
    public void loadDict(String dictCode, Map<String, String> dictMap) {
        RedissonUtil.putAllMap(CACHE_KEY_PREFIX + dictCode, dictMap);
    }

    @Override
    public void clearAllDict() {
        RedissonUtil.deleteKeyByPattern(CACHE_KEY_PREFIX + "*");
    }

}
