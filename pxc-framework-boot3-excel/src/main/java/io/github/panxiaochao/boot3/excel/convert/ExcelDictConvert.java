package io.github.panxiaochao.boot3.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.panxiaochao.boot3.excel.annotation.ExcelDictFormat;
import io.github.panxiaochao.boot3.excel.util.ExcelUtil;
import io.github.panxiaochao.boot3.utils.ConvertUtil;
import io.github.panxiaochao.boot3.utils.DictUtil;
import io.github.panxiaochao.boot3.utils.ObjectUtil;
import io.github.panxiaochao.boot3.utils.StrUtil;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;

/**
 * <p>
 * Excel 字典转换器
 * </p>
 *
 * @author lypxc
 * @since 2026-01-14
 * @version 1.0
 */
public class ExcelDictConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String code = anno.dictCode();
        String text = cellData.getStringValue();
        String staticExpressionContent = anno.staticExpressionContent();
        String value;
        if (StrUtil.isNotBlank(staticExpressionContent)) {
            // 优先走静态表达式内容转表达式模式
            value = ExcelUtil.forwardParseByExpressionContent(text, staticExpressionContent, anno.separator());
        }
        else {
            value = DictUtil.getDictValue(code, text, anno.separator());
        }
        return value;
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isEmpty(object)) {
            return new WriteCellData<>("");
        }
        ExcelDictFormat anno = getAnnotation(contentProperty.getField());
        String code = anno.dictCode();
        String value = ConvertUtil.toString(object);
        String staticExpressionContent = anno.staticExpressionContent();
        String text;
        if (StrUtil.isNotBlank(staticExpressionContent)) {
            // 优先走静态表达式内容转表达式模式
            text = ExcelUtil.reverseParseByExpressionContent(value, staticExpressionContent, anno.separator());
        }
        else {
            text = DictUtil.getDictText(code, value, anno.separator());
        }
        return new WriteCellData<>(text);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtils.getAnnotation(field, ExcelDictFormat.class);
    }

}
