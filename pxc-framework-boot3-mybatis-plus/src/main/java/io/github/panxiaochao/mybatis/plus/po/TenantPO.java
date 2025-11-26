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
package io.github.panxiaochao.mybatis.plus.po;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 租户基类
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-02
 * @version 1.0
 */
@Getter
@Setter
public abstract class TenantPO extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    @TableId("tenant_id")
    private String tenantId;

}
