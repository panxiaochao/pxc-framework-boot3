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
package io.github.panxiaochao.boot3.operate.log.core.enums;

import lombok.Getter;

/**
 * <p>
 * 操作日志状态枚举
 * </p>
 *
 * @author Lypxc
 * @since 2025-05-08
 * @version 1.0
 */
@Getter
public enum OperateLogStatus {

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAIL(0);

    private final int code;

    OperateLogStatus(int code) {
        this.code = code;
    }

}
