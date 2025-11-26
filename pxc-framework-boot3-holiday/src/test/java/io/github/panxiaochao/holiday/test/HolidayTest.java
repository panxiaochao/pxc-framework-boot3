package io.github.panxiaochao.holiday.test;

import io.github.panxiaochao.holiday.config.HolidayAutoConfiguration;
import io.github.panxiaochao.holiday.core.HolidayClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * <p>
 * 节假日测试
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-03
 * @version 1.0
 */
@SpringBootTest(classes = { HolidayAutoConfiguration.class },
        properties = { "spring.pxc-framework.holiday.json-locations=/data/**" })
public class HolidayTest {

    @Resource
    private HolidayClient holidayClient;

    @Test
    void test() {
        String[] args = new String[] { "2023-01-28", "2024-01-01", "2024-01-02", "2024-01-07", "2024-02-10",
                "2024-02-13", "2024-10-02", "2023-01-01", "2024-01-02 00:00:00" };
        System.out.println("节假日天数: " + holidayClient.holidaysCount("2024"));
        Arrays.stream(args).forEach(day -> {
            System.out.println(day + " 工作日: " + holidayClient.isWeekday(day));
            System.out.println(day + " 公休日: " + holidayClient.isPublicHoliday(day));
            System.out.println(day + " 节假日: " + holidayClient.isHoliday(day));
            System.out.println(day + " 补班: " + holidayClient.isWorkDay(day));
        });
    }

}
