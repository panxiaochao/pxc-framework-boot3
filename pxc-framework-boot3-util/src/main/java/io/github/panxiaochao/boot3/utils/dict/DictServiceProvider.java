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
public class DictServiceProvider {

    /**
     * 设置字典服务实现，volatile 确保线程安全
     */
    @Setter
    private static volatile IDictService dictService;

    /**
     * 获取当前字典服务
     * @return 字典服务
     */
    public static IDictService getDictService() {
        if (dictService == null) {
            synchronized (DictServiceProvider.class) {
                if (dictService == null) {
                    // 默认返回空实现，防止NPE
                    dictService = new DefaultDictService();
                }
            }
        }
        return dictService;
    }

}
