package io.github.panxiaochao.boot3.excel.util;

import io.github.panxiaochao.boot3.utils.StringPools;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Excel 工具类
 * </p>
 *
 * @author lypxc
 * @since 2026-01-15
 * @version 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtil {

    /**
     * 正向解析表达式内容, 例如：0=男,1=女,2=未知
     * @param propertyValue 属性值
     * @param staticExpressionContent 静态表达式内容
     * @param separator 分隔符
     * @return 解析后的值
     */
    public static String forwardParseByExpressionContent(String propertyValue, String staticExpressionContent,
            String separator) {
        // 参数验证
        if (StringUtils.isBlank(propertyValue) || StringUtils.isBlank(staticExpressionContent)) {
            return StringUtils.EMPTY;
        }

        StringBuilder result = new StringBuilder();
        // 提前拆分属性值，避免重复调用split
        String[] values = StringUtils.containsAny(propertyValue, separator) ? propertyValue.split(separator)
                : new String[] { propertyValue };

        // 处理静态表达式内容
        String[] convertSource = staticExpressionContent.split(StringPools.COMMA);

        for (String value : values) {
            for (String item : convertSource) {
                // 限制分割次数，避免值中包含 = 号的问题
                String[] itemArray = item.split("=", 2);
                if (itemArray.length == 2 && itemArray[0].equals(value)) {
                    result.append(itemArray[1]).append(separator);
                    break;
                }
            }
        }
        return StringUtils.stripEnd(result.toString(), separator);
    }

    /**
     * 反向解析表达式内容, 例如：男=0,女=1,未知=2
     * @param propertyValue 属性值
     * @param staticExpressionContent 静态表达式内容
     * @param separator 分隔符
     * @return 解析后的值
     */
    public static String reverseParseByExpressionContent(String propertyValue, String staticExpressionContent,
            String separator) {
        // 参数验证
        if (StringUtils.isBlank(propertyValue) || StringUtils.isBlank(staticExpressionContent)) {
            return StringUtils.EMPTY;
        }

        StringBuilder result = new StringBuilder();

        // 提前拆分标签值，避免重复调用split
        String[] labels = StringUtils.containsAny(propertyValue, separator) ? propertyValue.split(separator)
                : new String[] { propertyValue };

        // 处理静态表达式内容
        String[] convertSource = staticExpressionContent.split(StringPools.COMMA);

        for (String currentLabel : labels) {
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray.length == 2 && itemArray[1].equals(currentLabel)) {
                    result.append(itemArray[0]).append(separator);
                    break;
                }
            }
        }

        return StringUtils.stripEnd(result.toString(), separator);
    }

}
