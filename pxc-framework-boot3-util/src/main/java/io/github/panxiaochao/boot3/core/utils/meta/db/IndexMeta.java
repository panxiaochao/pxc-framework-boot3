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
package io.github.panxiaochao.boot3.core.utils.meta.db;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 数据库-索引元数据
 * </p>
 *
 * @author Lypxc
 * @since 2024-05-07
 * @version 1.0
 */
@Getter
@Setter
public class IndexMeta implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库 表名
     */
    private String tableName;

    /**
     * 索引值 是否可以不唯一
     */
    private boolean nonUnique;

    /**
     * 索引 名称
     */
    private String indexName;

    /**
     * 索引 字段
     */
    private String columnName;

    @Override
    public int hashCode() {
        return Objects.hash(indexName, tableName);
    }

}
