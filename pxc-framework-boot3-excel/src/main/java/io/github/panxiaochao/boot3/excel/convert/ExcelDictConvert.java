package io.github.panxiaochao.boot3.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.panxiaochao.boot3.excel.annotation.ExcelDictFormat;
import io.github.panxiaochao.boot3.excel.util.ExcelUtil;
import io.github.panxiaochao.boot3.utils.ConvertUtil;
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
        String label = cellData.getStringValue();
        String value = "";
        if (StrUtil.isBlank(code)) {
            value = ExcelUtil.forwardParseByExpressionContent(label, anno.staticExpressionContent(), anno.separator());
        }
        else {
            // value = SpringUtils.getBean(DictService.class).getDictValue(code, label,
            // anno.separator());
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
        String type = anno.dictCode();
        String value = ConvertUtil.toString(object);
        String text = "";
        if (StrUtil.isBlank(type)) {
            text = ExcelUtil.forwardParseByExpressionContent(value, anno.staticExpressionContent(), anno.separator());
        }
        else {
            // text = SpringUtils.getBean(DictService.class).getDictLabel(type, value,
            // anno.separator());
        }
        return new WriteCellData<>(text);
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return AnnotationUtils.getAnnotation(field, ExcelDictFormat.class);
    }

}
