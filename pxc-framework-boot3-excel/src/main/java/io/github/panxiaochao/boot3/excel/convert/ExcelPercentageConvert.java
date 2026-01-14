package io.github.panxiaochao.boot3.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.panxiaochao.boot3.utils.ConvertUtil;
import io.github.panxiaochao.boot3.utils.ObjectUtil;

/**
 * <p>
 * Excel 数值转换为字符串百分比
 * </p>
 *
 * @author lypxc
 * @since 2026-01-14
 * @version 1.0
 */
public class ExcelPercentageConvert implements Converter<Object> {

    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 空指针安全检查
        if (ObjectUtil.isEmpty(cellData.getNumberValue())) {
            return "";
        }
        // 转换为普通字符串，避免科学计数法
        return cellData.getNumberValue().toPlainString();
    }

    @Override
    public WriteCellData<?> convertToExcelData(Object object, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 空指针安全检查
        if (ObjectUtil.isEmpty(object)) {
            return new WriteCellData<>("");
        }

        String numberStr = ConvertUtil.toString(object);
        return new WriteCellData<>(numberStr + "%");
    }

}
