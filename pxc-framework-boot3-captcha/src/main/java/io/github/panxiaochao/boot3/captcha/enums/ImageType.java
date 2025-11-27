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
package io.github.panxiaochao.boot3.captcha.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * 验证码生成格式类型.
 * </p>
 *
 * @author Lypxc
 * @since 2024-08-13
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum ImageType {

    JPG("jpg", "data:image/jpg;base64", "image/jpg"),

    JPEG("jpeg", "data:image/jpeg;base64", "image/jpeg"),

    PNG("png", "data:image/png;base64", "image/png"),

    GIF("gif", "data:image/gif;base64", "image/gif");

    private final String suffix;

    private final String imageData;

    private final String contentType;

}
