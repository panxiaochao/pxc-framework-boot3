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
package io.github.panxiaochao.holiday.constants;

/**
 * <p>
 * 节假日常量类
 * </p>
 *
 * @author Lypxc
 * @since 2024-04-03
 * @version 1.0
 */
public interface HolidayConstant {

    /**
     * 自带节假日json数据位置
     */
    String DEFAULT_JSON_LOCATION = "classpath*:/json/**/*.json";

    /**
     * key 前缀
     */
    String KEY_PREFIX = "holiday:";

}
