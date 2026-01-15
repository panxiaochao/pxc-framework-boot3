package io.github.panxiaochao.boot3.excel.annotation;

import io.github.panxiaochao.boot3.utils.StringPools;

/**
 * <p>
 * Excel 字典格式化注解
 * </p>
 *
 * @author lypxc
 * @since 2026-01-13
 * @version 1.0
 */
public @interface ExcelDictFormat {

    /**
     * 如果是字典类型，请设置字典的 code 值 (如: sex)
     */
    String dictCode() default "";

    /**
     * 静态表达式内容转表达式，不走数据字典模式 (如: 0=男, 1=女, 2=未知)
     */
    String staticExpressionContent() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default StringPools.COMMA;

}
