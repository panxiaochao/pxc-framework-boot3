/*
 * Copyright © 2025-2026 Lypxc (545685602@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.panxiaochao.boot3.ip2region.constants;

/**
 * <p>
 * Ip2regionConstant 常量类
 * </p>
 *
 * @author lypxc
 * @since 2025-10-09
 * @version 1.0
 */
public class Ip2regionConstant {

    /**
     * ip2region_v4.db 文件路径<br/>
     *
     * 下载地址：<a href=
     * "https://gitee.com/lionsoul/ip2region/blob/master/data/ip2region_v4.xdb">ip2region_v4.xdb</a>
     */
    public static final String IP2REGION_V4_DB_LOCATION = "classpath*:ip2region/**/ip2region_v4.xdb";

    /**
     * ip2region_v6.db 文件路径<br/>
     *
     * 下载地址：<a href=
     * "https://gitee.com/lionsoul/ip2region/blob/master/data/ip2region_v6.xdb">ip2region_v6.xdb</a>
     */
    public static final String IP2REGION_V6_DB_LOCATION = "classpath*:ip2region/**/ip2region_v6.xdb";

}
