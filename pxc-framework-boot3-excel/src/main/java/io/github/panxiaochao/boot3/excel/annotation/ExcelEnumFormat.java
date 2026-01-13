package io.github.panxiaochao.boot3.excel.annotation;

/**
 * <p>
 * Excel 枚举格式化注解
 * </p>
 *
 * @author lypxc
 * @since 2026-01-13
 * @version 1.0
 */
public @interface ExcelEnumFormat {

    /**
     * 字典枚举类型
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 字典枚举类中对应的 code 属性名称，默认为 code
     */
    String codeField() default "code";

    /**
     * 字典枚举类中对应的 text 属性名称，默认为 value
     */
    String valueField() default "value";

}
