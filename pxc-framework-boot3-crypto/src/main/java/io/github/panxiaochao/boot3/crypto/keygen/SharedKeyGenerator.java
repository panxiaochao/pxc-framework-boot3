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

/**
 * <p>
 * 密钥生成器，每次只返回相同的密钥.
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-25
 * @version 1.0
 */
public class SharedKeyGenerator implements BytesKeyGenerator {

    private final byte[] sharedKey;

    SharedKeyGenerator(int keySize) {
        this.sharedKey = new BytesKeySecureRandomGenerator(keySize).generateKey();
    }

    /**
     * Get the length, in bytes, of keys created by this generator. Most unique keys are
     * at least 16 bytes in length.
     */
    @Override
    public int getKeySize() {
        return this.sharedKey.length;
    }

    /**
     * Generate a new key.
     */
    @Override
    public byte[] generateKey() {
        return this.sharedKey;
    }

}
