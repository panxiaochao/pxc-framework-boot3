package io.github.panxiaochao.boot3.utils.dict;

import lombok.Setter;

/**
 * <p>
 * 字典服务提供类
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
public class DictResolverProvider {

    /**
     * 设置字典服务实现，volatile 确保线程安全
     */
    @Setter
    private static volatile IDictResolver dictResolver;

    /**
     * 获取当前字典服务
     * @return 字典服务
     */
    public static IDictResolver getDictResolver(String cacheKeyPrefix) {
        if (dictResolver == null) {
            synchronized (DictResolverProvider.class) {
                if (dictResolver == null) {
                    // 默认返回空实现，防止NPE
                    dictResolver = new DefaultDictResolver();
                }
            }
        }
        // 设置缓存键前缀
        dictResolver.setCacheKeyPrefix(cacheKeyPrefix);
        return dictResolver;
    }

}
