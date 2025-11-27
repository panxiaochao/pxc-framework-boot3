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
package io.github.panxiaochao.boot3.component.tree;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 树节点属性
 * </p>
 *
 * @author Lypxc
 * @since 2023-12-06
 */
@Getter
public class TreeNodeProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点ID的键名，默认为 "id"
     */
    private String idKey = "id";

    /**
     * 父节点ID的键名，默认为 "parentId"
     */
    private String parentIdKey = "parentId";

    /**
     * 子节点列表的键名，默认为 "children"
     *
     */
    private String childrenKey = "children";

    /**
     * 节点权重的键名，默认为 "weight"
     *
     */
    private String weightKey = "weight";

    /**
     * 节点标签的键名，默认为 "name"
     *
     */
    private String labelKey = "name";

    /**
     * 静态构造方法，用于创建一个新的 TreeNodeProperties 实例
     * @return 新的 TreeNodeProperties 实例
     */
    public static TreeNodeProperties builder() {
        return new TreeNodeProperties();
    }

    /**
     * 设置输出idKey别名
     * @param idKey 别名id的key
     * @return this
     */
    public TreeNodeProperties idKey(String idKey) {
        this.idKey = idKey;
        return this;
    }

    /**
     * 设置输出parentIdKey别名
     * @param parentIdKey 别名parentId的key
     * @return this
     */
    public TreeNodeProperties parentIdKey(String parentIdKey) {
        this.parentIdKey = parentIdKey;
        return this;
    }

    /**
     * 设置输出childrenKey别名
     * @param childrenKey 别名children的key
     * @return this
     */
    public TreeNodeProperties childrenKey(String childrenKey) {
        this.childrenKey = childrenKey;
        return this;
    }

    /**
     * 设置weightKey别名
     * @param weightKey 别名weight的key
     * @return this
     */
    public TreeNodeProperties weightKey(String weightKey) {
        this.weightKey = weightKey;
        return this;
    }

    /**
     * 设置labelKey别名
     * @param labelKey 别名label的key
     * @return this
     */
    public TreeNodeProperties labelKey(String labelKey) {
        this.labelKey = labelKey;
        return this;
    }

}
