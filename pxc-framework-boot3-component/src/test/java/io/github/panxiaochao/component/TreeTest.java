package io.github.panxiaochao.component;

import io.github.panxiaochao.component.tree.Tree;
import io.github.panxiaochao.component.tree.TreeBuilder;
import io.github.panxiaochao.component.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2025-03-19
 * @version 1.0
 */
public class TreeTest {

    public static void main(String[] args) {
        // 构建 TreeNode 列表
        List<TreeNode<String>> nodeList = new ArrayList<>();
        nodeList.add(TreeNode.of("1", "0", "系统管理", 5, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));
        nodeList.add(TreeNode.of("11", "1", "用户管理", 222222, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));
        nodeList.add(TreeNode.of("111", "11", "用户添加", 0, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));
        nodeList.add(TreeNode.of("2", "0", "店铺管理", 1, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));
        nodeList.add(TreeNode.of("21", "2", "商品管理", 44, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));
        nodeList.add(TreeNode.of("221", "21", "商品添加", 2, (extraMap) -> {
            extraMap.put("a", "1");
            extraMap.put("b", new Object());
            extraMap.put("c", new ArrayList<>());
        }));

        List<Tree<String>> treeSingle = TreeBuilder.of("0").append(nodeList).deep(1).fastBuild().toTreeList();

        System.out.println(treeSingle.toString());
    }

}
