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
package io.github.panxiaochao.crypto.keygen;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p>
 * Algorithm支持的算法有：AES、DES、DESEDE、HMACMD5、HMACSHA1、HMACSHA256、RC2等
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-19
 * @version 1.0
 */
public class KeyGenerators {

    public static final int DEFAULT_KEY_SIZE = 256;

    private KeyGenerators() {
    }

    public static String secretKeyForBase64(Algorithm algorithm) {
        return Base64.getEncoder().encodeToString(secretKey(algorithm, null));
    }

    public static String secretKeyForBase64(Algorithm algorithm, String password) {
        return Base64.getEncoder().encodeToString(secretKey(algorithm, password));
    }

    public static byte[] secretKey(Algorithm algorithm) {
        return secretKey(algorithm, null);
    }

    public static byte[] secretKey(Algorithm algorithm, String password) {
        try {
            if (algorithm == null) {
                throw new IllegalArgumentException("algorithm cannot be null");
            }
            if (StringUtils.isBlank(password)) {
                throw new NullPointerException("密钥不能为空");
            }
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.getName());
            if (StringUtils.isNotBlank(password)) {
                keyGenerator.init(DEFAULT_KEY_SIZE, new SecureRandom(password.getBytes(StandardCharsets.UTF_8)));
            }
            else {
                keyGenerator.init(DEFAULT_KEY_SIZE);
            }
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();

        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Algorithm {

        ALGORITHM_AES("AES"),

        ALGORITHM_DES("DES"),

        ALGORITHM_HMAC_SHA_1("HmacSHA1"),

        ALGORITHM_HMAC_SHA_256("HmacSHA256"),

        ALGORITHM_HMAC_SHA_512("HmacSHA512");

        /**
         * 算法名字
         */
        private final String name;

    }

}
