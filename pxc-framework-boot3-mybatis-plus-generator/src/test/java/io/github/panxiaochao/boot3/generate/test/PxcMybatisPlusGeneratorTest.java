package io.github.panxiaochao.boot3.generate.test;

import io.github.panxiaochao.boot3.generate.enums.GenerateDbType;
import io.github.panxiaochao.boot3.generate.tool.PxcMybatisPlusGeneratorTools;

/**
 * <p>
 * 测试工具类.
 * </p>
 *
 * @author Lypxc
 * @since 2023-02-15
 */
public class PxcMybatisPlusGeneratorTest {

    public static void main(String[] args) {
        PxcMybatisPlusGeneratorTools.builder()
            // .jdbcUrl("jdbc:mysql://localhost:3308/oauth2?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
            // .jdbcUrl("jdbc:mysql://134.98.6.21:9200/kids?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
            .jdbcUrl(
                    "jdbc:mysql://134.98.6.57:3308/hzdx_wx_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
            .username("root")
            .password("Hzdx@2023")
            .dbType(GenerateDbType.MYSQL)
            // .outputDir("E:/work_2023/test")
            .outputDir("/Users/Lypxc/Documents/project/generate_pxc")
            .parent("com.telecom.boot.chat")
            .moduleName("mysql")
            .entityName("po")
            .insertFields("create_time")
            .updateFields("update_time")
            .includes("chat_question_answers")
            .build();
    }

}
