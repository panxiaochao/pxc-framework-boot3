package io.github.panxiaochao.boot3.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.panxiaochao.boot3.excel.annotation.ExcelEnumFormat;
import io.github.panxiaochao.boot3.utils.ConvertUtil;
import io.github.panxiaochao.boot3.utils.ObjectUtil;
import io.github.panxiaochao.boot3.utils.StrUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Excel 枚举转换器
 * </p>
 *
 * @author lypxc
 * @since 2026-01-14
 * @version 1.0
 */
public class ExcelEnumConvert implements Converter<Object> {

    private static final String GETTER_PREFIX = "get";

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 获取单元格字符串类型，确保该对象不会显示为空
        cellData.checkEmpty();
        // Excel 中填入的是枚举中指定的描述
        Object textValue = switch (cellData.getType()) {
            case STRING, DIRECT_STRING, RICH_TEXT_STRING -> cellData.getStringValue();
            case NUMBER -> cellData.getNumberValue();
            case BOOLEAN -> cellData.getBooleanValue();
            default -> throw new IllegalArgumentException("单元格类型异常!");
        };
        // 如果是空值
        if (ObjectUtil.isEmpty(textValue)) {
            return null;
        }
        Map<Object, String> enumCodeToTextMap = beforeConvert(contentProperty);
        // 从Java输出至Excel是code转text
        // 因此从Excel转Java应该将text与code对调
        Map<Object, Object> enumTextToCodeMap = new HashMap<>();
        enumCodeToTextMap.forEach((key, value) -> enumTextToCodeMap.put(value, key));
        // 应该从text -> code中查找
        return enumTextToCodeMap.get(textValue);
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isEmpty(object)) {
            return new WriteCellData<>("");
        }
        Map<Object, String> enumValueMap = beforeConvert(contentProperty);
        String value = ConvertUtil.toString(enumValueMap.get(object));
        return new WriteCellData<>(value);
    }

    private Map<Object, String> beforeConvert(ExcelContentProperty contentProperty) {
        ExcelEnumFormat anno = getAnnotation(contentProperty.getField());
        Map<Object, String> enumValueMap = new HashMap<>();
        Enum<?>[] enumConstants = anno.enumClass().getEnumConstants();
        Method codeMethod = getMethod(anno.enumClass(), anno.codeField());
        Method valueMethod = getMethod(anno.enumClass(), anno.valueField());
        for (Enum<?> enumConstant : enumConstants) {
            Object codeValue = ReflectionUtils.invokeMethod(codeMethod, enumConstant);
            String textValue = ConvertUtil.toString(ReflectionUtils.invokeMethod(valueMethod, enumConstant));
            enumValueMap.put(codeValue, textValue);
        }
        return enumValueMap;
    }

    private ExcelEnumFormat getAnnotation(Field field) {
        return AnnotationUtils.getAnnotation(field, ExcelEnumFormat.class);
    }

    private Method getMethod(Class<?> cls, String methodName) {
        String getterMethodName = GETTER_PREFIX + StrUtil.upperFirst(methodName);
        return ReflectionUtils.findMethod(cls, getterMethodName);
    }

}
