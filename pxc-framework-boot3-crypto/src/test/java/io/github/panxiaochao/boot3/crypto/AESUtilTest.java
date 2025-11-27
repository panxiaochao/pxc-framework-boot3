package io.github.panxiaochao.boot3.crypto;

import io.github.panxiaochao.boot3.crypto.encrypt.AesBytesEncryptor;
import io.github.panxiaochao.boot3.crypto.utils.Base64Util;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-19
 * @version 1.0
 */
public class AESUtilTest {

    private final AesBytesEncryptor aesBytesEncryptor = new AesBytesEncryptor("123");

    @Test
    void aes_test() {
        byte[] encrypt1 = aesBytesEncryptor.encrypt("你好");
        System.out.println("加密：" + Base64Util.encodeToString(encrypt1));
        byte[] decrypt1 = aesBytesEncryptor.decrypt(encrypt1);
        System.out.println("解密：" + new String(decrypt1));
    }

}
