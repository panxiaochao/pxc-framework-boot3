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
package io.github.panxiaochao.boot3.core.constants;

/**
 * <p>
 * 通用类常量.
 * </p>
 *
 * @author Lypxc
 * @since 2023-11-30
 */
public interface CommonConstant {

    /**
     * 通用成功标识
     */
    Integer OK = 1;

    /**
     * 通用失败标识
     */
    Integer FAIL = 0;

    /**
     * 是否为系统默认（是）
     */
    String YES = "Y";

    /**
     * 成功
     */
    String SUCCESS = "1";

    /**
     * 真
     */
    String TRUE = "1";

    /**
     * 真，Boolean
     */
    Boolean TRUE_BOOLEAN = Boolean.TRUE;

    /**
     * 删除，标记位 1
     */
    Integer IS_DELETE = 1;

    /**
     * 正常状态
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 菜单根节点
     */
    Long TREE_ROOT_ID = 0L;

}
