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
package io.github.panxiaochao.boot3.crypto.encrypt;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * <p>
 * 对称AES加密器.
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-24
 * @version 1.0
 */
public class AesBytesEncryptor implements BytesEncryptor {

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String ALGORITHM_AES = "AES";

    public static final int DEFAULT_KEY_SIZE = 128;

    private final SecretKey secretKey;

    private final Cipher encryptor;

    private final Cipher decryptor;

    public AesBytesEncryptor() {
        this(null);
    }

    public AesBytesEncryptor(String password) {
        try {
            this.secretKey = new SecretKeySpec(secretKey(password), ALGORITHM_AES);
            this.encryptor = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            this.decryptor = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Not a valid encryption algorithm", ex);
        }
        catch (NoSuchPaddingException ex) {
            throw new IllegalStateException("Should not happen", ex);
        }
    }

    /**
     * 加密内容.
     * @param content 字符串内容
     * @return 加密后的字节数组
     */
    public byte[] encrypt(String content) {
        return encrypt(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 加密字节内容.
     * @param bytes 字节内容
     * @return 加密后的字节数组
     */
    @Override
    public byte[] encrypt(byte[] bytes) {
        synchronized (this.encryptor) {
            try {
                this.encryptor.init(Cipher.ENCRYPT_MODE, this.secretKey);
                return this.encryptor.doFinal(bytes);
            }
            catch (InvalidKeyException ex) {
                throw new IllegalArgumentException("Unable to initialize due to invalid secret key", ex);
            }
            catch (IllegalBlockSizeException ex) {
                throw new IllegalStateException("Unable to invoke Cipher due to illegal block size", ex);
            }
            catch (BadPaddingException ex) {
                throw new IllegalStateException("Unable to invoke Cipher due to bad padding", ex);
            }
        }
    }

    /**
     * 解密字节内容.
     * @param encryptedBytes 加密的字节内容
     */
    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        synchronized (this.decryptor) {
            try {
                this.decryptor.init(Cipher.DECRYPT_MODE, this.secretKey);
                return this.decryptor.doFinal(encryptedBytes);
            }
            catch (InvalidKeyException ex) {
                throw new IllegalArgumentException("Unable to initialize due to invalid secret key", ex);
            }
            catch (IllegalBlockSizeException ex) {
                throw new IllegalStateException("Unable to invoke Cipher due to illegal block size", ex);
            }
            catch (BadPaddingException ex) {
                throw new IllegalStateException("Unable to invoke Cipher due to bad padding", ex);
            }
        }
    }

    /**
     * 获取密钥以字节数组返回
     * @param password 密钥
     * @return 密钥字节
     */
    private byte[] secretKey(String password) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES);
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

}
