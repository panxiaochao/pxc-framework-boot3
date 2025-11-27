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
package io.github.panxiaochao.boot3.crypto.keygen;

import java.security.SecureRandom;

/**
 * <p>
 * 字节随机算法密码生成器.
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-23
 * @version 1.0
 */
public class BytesKeySecureRandomGenerator implements BytesKeyGenerator {

    private static final int DEFAULT_KEY_LENGTH = 16;

    private final SecureRandom random;

    private final int keySize;

    /**
     * Creates a secure random key generator using the defaults.
     */
    BytesKeySecureRandomGenerator() {
        this(DEFAULT_KEY_LENGTH);
    }

    /**
     * Creates a secure random key generator with a custom key length.
     */
    BytesKeySecureRandomGenerator(int keySize) {
        this.random = new SecureRandom();
        this.keySize = keySize;
    }

    /**
     * Get the length, in bytes, of keys created by this generator. Most unique keys are
     * at least 8 bytes in length.
     */
    @Override
    public int getKeySize() {
        return this.keySize;
    }

    /**
     * Generate a new key.
     */
    @Override
    public byte[] generateKey() {
        byte[] bytes = new byte[this.keySize];
        this.random.nextBytes(bytes);
        return bytes;
    }

}
