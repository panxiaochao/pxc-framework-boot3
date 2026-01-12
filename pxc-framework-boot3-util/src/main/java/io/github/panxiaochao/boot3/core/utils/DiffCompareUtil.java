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
package io.github.panxiaochao.boot3.core.utils;

import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import io.github.panxiaochao.boot3.core.utils.diff.DiffCompareResultInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * <p>
 * 差异比较工具类，支持String，List<String>，Set<String>，Object等类型的差异比较
 * </p>
 *
 * @author lypxc
 * @since 2026-01-09
 * @version 1.0
 */
public class DiffCompareUtil {

    /**
     * 差异比较行生成器
     */
    private static final DiffRowGenerator DIFF_ROW_GENERATOR = DiffRowGenerator.create()
        // 开启行内细节对比
        .showInlineDiffs(true)
        // 按单词粒度，可以更加精确的对比单词颗粒级别的差异
        .inlineDiffByWord(false)
        // 不合并模式，这样OLD 和 NEW 就会分别展示在差异对比结果中
        .mergeOriginalRevised(false)
        // 自定义旧文本包裹标签
        .oldTag(f -> f ? "<del>" : "</del>")
        // 自定义新文本包裹标签
        .newTag(f -> f ? "<ins>" : "</ins>")
        .build();

    /**
     * 差异比较：字符串
     * @param originalStr 原始字符串
     * @param revisedStr 修订后的字符串
     * @return 差异比较结果
     */
    public static DiffCompareResultInfo diffString(String originalStr, String revisedStr) {
        if (StringUtils.isAllBlank(originalStr, revisedStr)) {
            return null;
        }
        List<DiffCompareResultInfo> diffList = diffList(List.of(originalStr), List.of(revisedStr));
        return diffList.stream().findFirst().orElse(null);
    }

    /**
     * 差异比较：集合字符串
     * @param originalSet 原始集合字符串
     * @param revisedSet 修订后的集合字符串
     * @return 差异比较结果列表
     */
    public static List<DiffCompareResultInfo> diffSet(Set<String> originalSet, Set<String> revisedSet) {
        return diffList(new ArrayList<>(originalSet), new ArrayList<>(revisedSet));
    }

    /**
     * 差异比较：对象
     * @param originalObj 原始对象
     * @param revisedObj 修订后的对象
     * @return 差异比较结果列表
     */
    public static List<DiffCompareResultInfo> diffObject(Object originalObj, Object revisedObj) {
        // 处理空值情况
        if (originalObj == null && revisedObj == null) {
            return Collections.emptyList();
        }
        // 转换为漂亮的 JSON 字符串
        String originalJson = JacksonUtil.pretty(originalObj);
        String revisedJson = JacksonUtil.pretty(revisedObj);
        // 转换为列表字符串
        List<String> originalList = Arrays.asList(originalJson.split("\n"));
        List<String> revisedList = Arrays.asList(revisedJson.split("\n"));
        return diffList(originalList, revisedList);
    }

    /**
     * 差异比较：列表字符串
     * @param originalList 原始列表字符串
     * @param revisedList 修订后的列表字符串
     * @return 差异比较结果列表
     */
    public static List<DiffCompareResultInfo> diffList(List<String> originalList, List<String> revisedList) {
        if (CollectionUtil.isEmpty(originalList) && CollectionUtil.isEmpty(revisedList)) {
            return Collections.emptyList();
        }
        List<DiffRow> rows = DIFF_ROW_GENERATOR.generateDiffRows(originalList, revisedList);
        //@formatter:off
        return IntStream.range(0, rows.size())
                .mapToObj(i -> {
                    DiffRow row = rows.get(i);
                    return DiffCompareResultInfo.of(i, row.getTag().name(), row.getOldLine(), row.getNewLine());
        }).toList();
    }

}
