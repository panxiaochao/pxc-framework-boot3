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
package io.github.panxiaochao.core.exception.ext;

import io.github.panxiaochao.core.exception.ServerException;
import io.github.panxiaochao.core.ienums.IEnum;
import lombok.Getter;

/**
 * <p>
 * Api and Server exception
 * </p>
 *
 * @author Lypxc
 * @since 2022-11-28
 */
@Getter
public class ApiException extends ServerException {

    private static final long serialVersionUID = -4367714276298639594L;

    /**
     * 错误码
     */
    private final int code;

    public ApiException(IEnum<Integer> responseEnum) {
        super(responseEnum);
        this.code = responseEnum.getCode();
    }

    public ApiException(IEnum<Integer> responseEnum, String message) {
        super(responseEnum, message);
        this.code = responseEnum.getCode();
    }

    public ApiException(IEnum<Integer> responseEnum, Throwable cause) {
        super(responseEnum, cause);
        this.code = responseEnum.getCode();
    }

    public ApiException(IEnum<Integer> responseEnum, String message, Throwable cause) {
        super(responseEnum, message, cause);
        this.code = responseEnum.getCode();
    }

}
