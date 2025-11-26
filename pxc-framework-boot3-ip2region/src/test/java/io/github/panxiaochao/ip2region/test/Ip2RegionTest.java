package io.github.panxiaochao.ip2region.test;

import io.github.panxiaochao.ip2region.config.properties.Ip2regionProperties;
import io.github.panxiaochao.ip2region.core.Ip2regionClient;
import io.github.panxiaochao.ip2region.core.IpInfo;

/**
 * <p>
 * Ip2RegionTest 测试类
 * </p>
 *
 * @author lypxc
 * @since 2025-10-21
 * @version 1.0
 */
public class Ip2RegionTest {

    public static void main(String[] args) {
        // 创建 Ip2regionProperties 配置对象（可使用默认值）
        Ip2regionProperties properties = new Ip2regionProperties();

        // 初始化 Ip2regionClient 客户端
        Ip2regionClient client = new Ip2regionClient(properties);

        try {
            client.afterPropertiesSet(); // 加载数据库文件

            // IPv4 测试
            System.out.println(client.memorySearch("60.191.8.98"));
            System.out.println(client.memorySearch("220.248.12.158"));
            System.out.println(client.memorySearch("222.240.36.135"));
            System.out.println(client.memorySearch("172.30.13.97"));
            System.out.println(client.memorySearch("223.26.64.0"));
            System.out.println(client.memorySearch("223.26.128.0"));
            System.out.println(client.memorySearch("223.26.67.0"));
            System.out.println(client.memorySearch("223.29.220.0"));
            System.out.println(client.memorySearch("82.120.124.0"));

            // 获取特定信息测试
            System.out.println(client.getInfo("220.248.12.158", IpInfo::getAddress));
            System.out.println(client.getInfo("220.248.12.158", IpInfo::getRegion));

            // IPv6 测试
            System.out.println(client.memorySearch("240e:57f:32ff:ffff:ffff:ffff:ffff:ffff"));
            System.out.println(client.memorySearch("::ffff:1111:2222"));
            System.out.println(client.memorySearch("2001:db8::ffff:1111:2222"));
            System.out.println(client.memorySearch("::1"));
            System.out.println(client.memorySearch("2406:840:20::1"));
            System.out.println(client.memorySearch("2c0f:feb0:a::"));
            System.out.println(client.memorySearch("240e:109:8047::"));
            System.out.println(client.memorySearch("1111:1111:1111::1111"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
