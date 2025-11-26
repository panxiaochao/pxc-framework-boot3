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
package io.github.panxiaochao.component.tree;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * <p>
 * 树形构建器
 * </p>
 *
 * @author Lypxc
 * @since 2023-12-06
 */
public class TreeBuilder<E> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Tree<E> root;

    private final Map<E, Tree<E>> treeMap;

    private boolean isBuild;

    private int deep;

    private final boolean isNullChildrenAsEmpty;

    private boolean isDesc;

    /**
     * 创建Tree构建器
     * @param rootId 根节点ID
     * @param <E> ID类型
     * @return TreeBuilder
     */
    public static <E> TreeBuilder<E> of(E rootId) {
        return new TreeBuilder<>(rootId, false, null);
    }

    /**
     * 创建Tree构建器
     * @param rootId 根节点ID
     * @param isNullChildrenAsEmpty 是否子节点没有数据的情况下，给一个默认空集，默认 false
     * @param <E> ID类型
     * @return TreeBuilder
     */
    public static <E> TreeBuilder<E> of(E rootId, boolean isNullChildrenAsEmpty) {
        return of(rootId, isNullChildrenAsEmpty, null);
    }

    /**
     * 创建Tree构建器
     * @param rootId 根节点ID
     * @param isNullChildrenAsEmpty 是否子节点没有数据的情况下，给一个默认空集，默认 false
     * @param properties 树节点属性配置
     * @param <E> ID类型
     * @return TreeBuilder
     */
    public static <E> TreeBuilder<E> of(E rootId, boolean isNullChildrenAsEmpty, TreeNodeProperties properties) {
        return new TreeBuilder<>(rootId, isNullChildrenAsEmpty, properties);
    }

    /**
     * 私有参数构造
     * @param rootId 根节点ID
     * @param isNullChildrenAsEmpty 是否子节点没有数据的情况下，给一个默认空集，默认 false
     * @param properties 树节点属性配置
     */
    private TreeBuilder(E rootId, boolean isNullChildrenAsEmpty, TreeNodeProperties properties) {
        this.root = new Tree<>(properties);
        this.root.setId(rootId);
        this.treeMap = new LinkedHashMap<>();
        this.deep = -1;
        this.isNullChildrenAsEmpty = isNullChildrenAsEmpty;
        this.isDesc = false;
    }

    /**
     * 循环深度层次, 默认-1, 不受限制
     * @param deep 深度
     * @return this
     */
    public TreeBuilder<E> deep(int deep) {
        this.deep = deep;
        return this;
    }

    /**
     * 是否倒序，默认升序
     * @return this
     */
    public TreeBuilder<E> desc() {
        this.isDesc = true;
        return this;
    }

    /**
     * 增加节点列表，增加的节点是不带子节点的
     * @param map 节点列表
     * @return this
     */
    public TreeBuilder<E> append(Map<E, Tree<E>> map) {
        Assert.isTrue(!isBuild, "Current tree has not been built.");
        this.treeMap.putAll(map);
        return this;
    }

    /**
     * 增加节点列表，增加的节点是不带子节点的
     * @param <T> Bean类型
     * @param list Bean列表
     * @return this
     */
    public <T> TreeBuilder<E> append(List<TreeNode<T>> list) {
        Assert.isTrue(!isBuild, "Current tree has been built.");
        return append(list, null);
    }

    public <T> TreeBuilder<E> append(List<TreeNode<T>> list, BiConsumer<TreeNode<T>, Tree<E>> consumer) {
        Assert.isTrue(!isBuild, "Current tree has been built.");
        final Map<E, Tree<E>> map = new LinkedHashMap<>(list.size(), 1);
        E rootId = this.root.getId();
        Tree<E> tree;
        for (TreeNode<T> treeNode : list) {
            tree = new Tree<>(this.root.getTreeNodeProperties());
            // 是否需要置入空数组
            if (this.isNullChildrenAsEmpty) {
                tree.setChildren(new ArrayList<>());
            }
            // 自定义解析为空，执行默认的方法
            if (null == consumer) {
                parseTo(treeNode, tree);
            }
            else {
                consumer.accept(treeNode, tree);
            }
            if (null != rootId && !rootId.getClass().equals(tree.getId().getClass())) {
                throw new IllegalArgumentException("rootId class type is not equals tree id class type!");
            }
            map.put(tree.getId(), tree);
        }
        this.treeMap.putAll(map);
        return this;
    }

    /**
     * 来源数据-》目标数据
     * @param source 来源数据实体
     * @param target 目标节点实体
     */
    private <T> void parseTo(TreeNode<T> source, Tree<E> target) {
        target.setId((E) source.getId());
        target.setParentId((E) source.getParentId());
        target.setWeight(source.getWeight());
        target.setLabelValue(source.getLabelValue());
        // 扩展属性字段
        final Map<String, Object> extra = source.getExtra();
        if (!extra.isEmpty()) {
            extra.forEach(target::putExtra);
        }
    }

    /**
     * 开始构建树
     */
    private void buildTreeMap() {
        if (this.treeMap.isEmpty()) {
            return;
        }
        final Map<E, Tree<E>> eTreeMap = comparingByValue(this.treeMap, isDesc);
        E parentId;
        for (Tree<E> node : eTreeMap.values()) {
            if (null == node) {
                continue;
            }
            parentId = node.getParentId();
            if (Objects.equals(this.root.getId(), parentId)) {
                this.root.addChildren(node);
                continue;
            }

            final Tree<E> parentNode = eTreeMap.get(parentId);
            if (null != parentNode) {
                parentNode.addChildren(node);
            }
        }
    }

    /**
     * <p>
     * 快速构建树
     * </p>
     * @return this
     */
    public TreeBuilder<E> fastBuild() {
        buildTreeMap();
        // -1 默认不剪切
        if (this.deep > -1) {
            cutTree();
        }
        this.isBuild = true;
        this.treeMap.clear();
        return this;
    }

    private void cutTree() {
        cutTree(this.root, 0, this.deep);
    }

    /**
     * 剪切层级
     * @param tree 树节点
     * @param curDepp 当前层级
     * @param maxDeep 最大层级
     */
    private void cutTree(Tree<E> tree, int curDepp, int maxDeep) {
        if (null == tree) {
            return;
        }
        if (curDepp == maxDeep) {
            tree.setChildren(new ArrayList<>());
            return;
        }

        final List<Tree<E>> children = tree.getChildren();
        if (!CollectionUtils.isEmpty(children)) {
            for (Tree<E> child : children) {
                cutTree(child, curDepp + 1, maxDeep);
            }
        }
    }

    /**
     * 构建单实体树
     * @return 构建树实体
     */
    public Tree<E> toTree() {
        Assert.isTrue(isBuild, "Current tree has not been built.");
        return this.root;
    }

    /**
     * 构建树列表，没有顶层节点
     * @return 构建树列表
     */
    public List<Tree<E>> toTreeList() {
        Assert.isTrue(isBuild, "Current tree has not been built.");
        return this.root.getChildren();
    }

    /**
     * 按照值排序，可选是否倒序
     * @param map 需要对值排序的map
     * @param <K> 键类型
     * @param <V> 值类型
     * @param isDesc 是否倒序
     * @return 排序后新的Map
     */
    private <K, V extends Comparable<? super V>> Map<K, V> comparingByValue(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        Comparator<Map.Entry<K, V>> entryComparator = Map.Entry.comparingByValue();
        if (isDesc) {
            entryComparator = entryComparator.reversed();
        }
        map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

}
