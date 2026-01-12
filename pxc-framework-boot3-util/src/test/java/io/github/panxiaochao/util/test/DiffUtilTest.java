package io.github.panxiaochao.util.test;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import io.github.panxiaochao.boot3.core.utils.DiffCompareUtil;
import io.github.panxiaochao.boot3.core.utils.JacksonUtil;
import io.github.panxiaochao.boot3.core.utils.diff.DiffCompareResultInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
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
        DiffCompareResultInfo rows0 = DiffCompareUtil.diffString("浙江省杭州市西湖区", "浙江省杭州市拱墅区");

        printDiffCompareResult(Collections.singletonList(rows0));

        List<String> original = Arrays.asList("Spring", "SpringBoot", "MyBatis");
        List<String> revised = Arrays.asList("Spring", "SpringCloud", "MyBatis");

        // 生成可视化差异行
        List<DiffCompareResultInfo> rows = DiffCompareUtil.diffList(original, revised);

        // 打印结果模拟
        printDiffCompareResult(rows);

        User user1 = new User("潘骁超", 25, "lypxc@example.com", "1234567890", "浙江省杭州市西湖区");
        User user2 = new User("潘骁超", 26, "lypxc111@example.com", "1234567890", "浙江省杭州市拱墅区");

        // 1. 转换为漂亮的 JSON 字符串
        String oldJson = JacksonUtil.pretty(user1);
        String newJson = JacksonUtil.pretty(user2);

        // 2. 按行分割，方便 Diff 库处理
        List<String> oldLines = Arrays.asList(oldJson.split("\n"));
        List<String> newLines = Arrays.asList(newJson.split("\n"));

        List<DiffCompareResultInfo> userDiffRows = DiffCompareUtil.diffList(oldLines, newLines);

        // 4. 打印结果模拟
        printDiffCompareResult(userDiffRows);

        List<DiffCompareResultInfo> rows1 = DiffCompareUtil.diffList(
                Arrays.asList("This is a test sentence.", "This is the second line.", "And here is the finish."),
                Arrays.asList("This is a test for diffutils.", "This is the second line.", "", "asdk,sadkjl"));

        printDiffCompareResult(rows1);

    }

    private static void printDiffCompareResult(List<DiffCompareResultInfo> rows) {
        for (DiffCompareResultInfo row : rows) {
            System.out.println("Line: " + row.getLineNum());
            System.out.println("Type: " + row.getTag());
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
