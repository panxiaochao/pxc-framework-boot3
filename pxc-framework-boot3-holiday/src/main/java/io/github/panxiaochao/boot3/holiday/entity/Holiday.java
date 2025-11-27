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
package io.github.panxiaochao.boot3.holiday.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Holiday 实体
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-02
 * @version 1.0
 */
@Getter
@Setter
public class Holiday implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 年份
     */
    private String year;

    /**
     * 节假日期集合
     */
    private List<Days> days = new ArrayList<>();

    /**
     * 工作补班日期集合
     */
    private List<WorkDays> workdays = new ArrayList<>();

    /**
     * 节假日期详情
     */
    @Setter
    @Getter
    public static class Days {

        /**
         * 节日名称
         */
        private String name;

        /**
         * 日期
         */
        private String date;

    }

    /**
     * 工作补班日期详情
     */
    @Setter
    @Getter
    public static class WorkDays {

        /**
         * 日期
         */
        private String date;

    }

}
