package io.github.panxiaochao.boot3.component;

import io.github.panxiaochao.boot3.component.select.Select;
import io.github.panxiaochao.boot3.component.select.SelectBuilder;
import io.github.panxiaochao.boot3.component.select.SelectOption;

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
public class SelectTest {

    public static void main(String[] args) {
        // 构建 SelectOption 列表
        List<SelectOption<String>> selectOptionsList = new ArrayList<>();
        selectOptionsList.add(SelectOption.of("0", "张三0", 5));
        selectOptionsList.add(SelectOption.of("1", "张三1", 4));
        selectOptionsList.add(SelectOption.of("2", "张三2", 3));
        selectOptionsList.add(SelectOption.of("3", "张三3", 2));
        selectOptionsList.add(SelectOption.of("4", "张三4", 7));
        selectOptionsList.add(SelectOption.of("5", "张三5", 8));
        selectOptionsList.add(SelectOption.of("6", "张三6", 1));

        List<Select<String>> select = SelectBuilder.of(selectOptionsList).desc().fastBuild().toSelectList();

        System.out.println(select.toString());

    }

}
