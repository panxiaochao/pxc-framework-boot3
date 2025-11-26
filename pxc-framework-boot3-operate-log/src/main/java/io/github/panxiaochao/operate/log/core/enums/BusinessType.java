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
package io.github.panxiaochao.operate.log.core.enums;

/**
 * <p>
 * 业务类型枚举
 * </p>
 *
 * @author Lypxc
 * @since 2025-05-08
 * @version 1.0
 */
public enum BusinessType {

    /**
     * 新增
     */
    INSERT,
    /**
     * 修改
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查询
     */
    QUERY,
    /**
     * 授权
     */
    GRANT,
    /**
     * 导出
     */
    EXPORT,
    /**
     * 导入
     */
    IMPORT,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 登出
     */
    LOGOUT,
    /**
     * 强退
     */
    FORCE_LOGOUT,
    /**
     * 生成代码
     */
    GENERATE_CODE,
    /**
     * 清空数据
     */
    CLEAN,
    /**
     * 其它
     */
    OTHER

}
