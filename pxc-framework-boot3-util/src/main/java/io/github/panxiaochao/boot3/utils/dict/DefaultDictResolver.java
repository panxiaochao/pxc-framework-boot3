package io.github.panxiaochao.boot3.utils.dict;

import io.github.panxiaochao.boot3.utils.StrUtil;

import java.util.Map;

/**
 * <p>
 * 默认字典服务实现类，实现了{@link IDictResolver}接口，提供字典操作的基本方法
 * </p>
 *
 * @author lypxc
 * @since 2026-01-16
 * @version 1.0
 */
public class DefaultDictResolver extends AbstractDictResolver {

    @Override
    public String getDictText(String dictCode, String dictValue, String separator) {
        return StrUtil.EMPTY;
    }

    @Override
    public String getDictValue(String dictCode, String dictText, String separator) {
        return StrUtil.EMPTY;
    }

    @Override
    public Map<String, String> getAllDictByDictCode(String dictCode) {
        return Map.of();
    }

    @Override
    protected String findKeyByValue(Map<String, String> dictMap, String value) {
        return "";
    }

}
