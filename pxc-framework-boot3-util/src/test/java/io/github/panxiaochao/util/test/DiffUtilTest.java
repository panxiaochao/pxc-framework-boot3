package io.github.panxiaochao.util.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import io.github.panxiaochao.boot3.core.utils.JacksonUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * DiffUtil 测试类
 * </p>
 *
 * @author lypxc
 * @since 2026-01-08
 * @version 1.0
 */
public class DiffUtilTest {

    public static void main(String[] args) {
        testDiff();
    }

    private static void testDiff() {
        List<String> original = Arrays.asList("Spring", "SpringBoot", "MyBatis");
        List<String> revised = Arrays.asList("Spring", "SpringCloud", "MyBatis");

        // 1. 计算差异
        Patch<String> patch = DiffUtils.diff(original, revised);

        // 2. 遍历差异点
        printDeltas(patch);

        // ... 承接上文
        List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff("file_v1.txt", "file_v2.txt", original, patch,
                0);

        unifiedDiff.forEach(System.out::println);
        System.out.println("-----------------");

        // 2. 构建生成器 (核心配置)
        DiffRowGenerator generator = DiffRowGenerator.create()
            .showInlineDiffs(true) // 开启行内细节对比
            .inlineDiffByWord(true) // 按单词粒度，而非字符粒度
            .mergeOriginalRevised(true) // 合并模式，方便通过CSS控制显示
            .ignoreWhiteSpaces(true) // 忽略空格差异
            .oldTag(f -> f ? "<span class='del'>" : "</span>") // 自定义旧文本包裹标签
            .newTag(f -> f ? "<span class='ins'>" : "</span>") // 自定义新文本包裹标签
            .build();

        // 3. 生成可视化差异行
        List<DiffRow> rows = generator.generateDiffRows(original, revised);

        // 4. 打印结果模拟
        printDiffRows(rows);

        User user1 = new User("潘骁超", 25, "lypxc@example.com", "1234567890", "浙江省杭州市西湖区");
        User user2 = new User("潘骁超", 26, "lypxc111@example.com", "1234567890", "浙江省杭州市拱墅区");

        // 1. 转换为漂亮的 JSON 字符串
        String oldJson = JacksonUtil.pretty(user1);
        String newJson = JacksonUtil.pretty(user2);

        // 2. 按行分割，方便 Diff 库处理
        List<String> oldLines = Arrays.asList(oldJson.split("\n"));
        List<String> newLines = Arrays.asList(newJson.split("\n"));

        List<DiffRow> userDiffRows = generator.generateDiffRows(oldLines, newLines);

        // 4. 打印结果模拟
        printDiffRows(userDiffRows);

    }

    private static void printDiffRows(List<DiffRow> rows) {
        for (DiffRow row : rows) {
            System.out.println("Type: " + row.getTag()); // 4.16版本新特性
            System.out.println("Old: " + row.getOldLine());
            System.out.println("New: " + row.getNewLine());
            System.out.println("---");
        }
        System.out.println("-----------------");
    }

    private static void printDeltas(Patch<String> patch) {
        // 2. 遍历差异点
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            System.out.println("差异类型: " + delta.getType());
            System.out.println("原位置: " + delta.getSource());
            System.out.println("新位置: " + delta.getTarget());
        }
        System.out.println("-----------------");
    }

    public static Patch<String> diffObjects(Object oldObj, Object newObj) {
        // 1. 转换为漂亮的 JSON 字符串
        String oldJson = JacksonUtil.pretty(oldObj);
        String newJson = JacksonUtil.pretty(newObj);

        // 2. 按行分割，方便 Diff 库处理
        List<String> oldLines = Arrays.asList(oldJson.split("\n"));
        List<String> newLines = Arrays.asList(newJson.split("\n"));

        // 3. 执行比对
        return DiffUtils.diff(oldLines, newLines);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class User {

        private String name;

        private Integer age;

        private String email;

        private String phone;

        private String address;

    }

}
