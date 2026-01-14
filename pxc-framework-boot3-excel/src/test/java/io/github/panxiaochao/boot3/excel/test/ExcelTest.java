package io.github.panxiaochao.boot3.excel.test;

import io.github.panxiaochao.boot3.utils.ConvertUtil;
import io.github.panxiaochao.boot3.utils.StrUtil;
import io.github.panxiaochao.boot3.utils.meta.constants.DatabaseType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Excel测试类
 * </p>
 *
 * @author lypxc
 * @since 2026-01-14
 * @version 1.0
 */
public class ExcelTest {

    public static void main(String[] args) {
        // BigInteger bigInteger = new BigInteger("12345678901234567890");
        // System.out.println(bigInteger.toString());

        Enum<?>[] enumConstants = DatabaseType.class.getEnumConstants();
        Map<Object, String> enumValueMap = new HashMap<>();
        Method codeMethod = getMethod(DatabaseType.class, "dbType");
        Method valueMethod = getMethod(DatabaseType.class, "driverClassName");
        for (Enum<?> enumConstant : enumConstants) {
            Object codeValue = ReflectionUtils.invokeMethod(codeMethod, enumConstant);
            String textValue = ConvertUtil.toString(ReflectionUtils.invokeMethod(valueMethod, enumConstant));
            enumValueMap.put(codeValue, textValue);
        }
        System.out.println(enumValueMap);
    }

    private static Method getMethod(Class<?> cls, String methodName) {
        String getterMethodName = "get" + StrUtil.upperFirst(methodName);
        return ReflectionUtils.findMethod(cls, getterMethodName);
    }

}
