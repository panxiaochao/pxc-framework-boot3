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
package io.github.panxiaochao.boot3.utils.diff;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 比对结果信息
 * </p>
 *
 * @author lypxc
 * @since 2026-01-12
 * @version 1.0
 */
@Getter
@Setter
@ToString
public class DiffCompareResultInfo {

    /**
     * 行号
     */
    private int lineNum;

    /**
     * 比对结果标签，可选值：EQUAL、INSERT、DELETE、CHANGE
     */
    private String tag;

    /**
     * 旧行内容
     */
    private String oldLine;

    /**
     * 新行内容
     */
    private String newLine;

    /**
     * 构造方法：差异比较结果信息
     * @param lineNum 行号
     * @param tag 比对结果标签
     * @param oldLine 旧行内容
     * @param newLine 新行内容
     */
    public DiffCompareResultInfo(int lineNum, String tag, String oldLine, String newLine) {
        this.lineNum = lineNum;
        this.tag = tag;
        this.oldLine = oldLine;
        this.newLine = newLine;
    }

    /**
     * 静态工厂方法：差异比较结果信息
     * @param lineNum 行号
     * @param tag 比对结果标签
     * @param oldLine 旧行内容
     * @param newLine 新行内容
     * @return 差异比较结果信息
     */
    public static DiffCompareResultInfo of(int lineNum, String tag, String oldLine, String newLine) {
        return new DiffCompareResultInfo(lineNum, tag, oldLine, newLine);
    }

}
