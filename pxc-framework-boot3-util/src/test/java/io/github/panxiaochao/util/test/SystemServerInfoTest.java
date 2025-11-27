package io.github.panxiaochao.util.test;

import io.github.panxiaochao.boot3.core.utils.SystemServerUtil;

/**
 * <p>
 * SystemServerInfoTest 系统信息测试类
 * </p>
 *
 * @author lypxc
 * @since 2025-06-12
 * @version 1.0
 */
public class SystemServerInfoTest {

    public static void main(String[] args) {
        System.out.println(SystemServerUtil.ofCpuInfo());
    }

}
