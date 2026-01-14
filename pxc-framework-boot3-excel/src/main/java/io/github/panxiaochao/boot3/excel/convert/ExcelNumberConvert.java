package io.github.panxiaochao.boot3.excel.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import io.github.panxiaochao.boot3.utils.ConvertUtil;
import io.github.panxiaochao.boot3.utils.ObjectUtil;

import java.math.BigDecimal;

/**
 * <p>
 * Excel 数值长度位15位 大于15位的数值转换位字符串 转换器
 * </p>
 *
 * @author lypxc
 * @since 2026-01-14
 * @version 1.0
 */
public class ExcelNumberConvert implements Converter<Number> {

    @Override
    public Class<Number> supportJavaTypeKey() {
        return Number.class;
    }

    @Override
    public Number convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 空指针安全检查
        if (ObjectUtil.isEmpty(cellData.getNumberValue())) {
            return null;
        }
        return cellData.getNumberValue();
    }

    @Override
    public WriteCellData<?> convertToExcelData(Number number, ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration) {
        // 空指针安全检查
        if (ObjectUtil.isEmpty(number)) {
            return new WriteCellData<>();
        }

        String numberStr = ConvertUtil.toString(number);
        // 对于超过15位的数字，使用字符串类型存储以避免科学计数法
        if (numberStr.length() > 15) {
            return new WriteCellData<String>(numberStr);
        }

        // 对于15位以下的数字，保持使用数值类型存储
        WriteCellData<Number> cellData = new WriteCellData<>(new BigDecimal(numberStr));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }

}
