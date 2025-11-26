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
package io.github.panxiaochao.crypto.utils;

/**
 * <p>
 * 十六进制数据编码器工具类, 以十六进制字符串表示.
 * </p>
 *
 * @author Lypxc
 * @since 2024-07-24
 * @version 1.0
 */
public class HexUtil {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private HexUtil() {
    }

    /**
     * 字节解密字符串十六进制
     * @param bytes 字符串字节
     * @return 字符串十六进制
     */
    public static String encode(byte[] bytes) {
        final int length = bytes.length;
        char[] result = new char[2 * length];
        int j = 0;
        for (byte aByte : bytes) {
            // Char for top 4 bits
            result[j++] = HEX[(0xF0 & aByte) >>> 4];
            // Bottom 4
            result[j++] = HEX[(0x0F & aByte)];
        }
        return new String(result);
    }

    /**
     * 字符串十六进制解密字符串
     * @param str 加密字符串字节
     * @return 解密后字符串
     */
    public static String decode(CharSequence str) {
        int nChars = str.length();
        if (nChars % 2 != 0) {
            throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
        }
        byte[] result = new byte[nChars / 2];
        for (int i = 0; i < nChars; i += 2) {
            int msb = Character.digit(str.charAt(i), 16);
            int lsb = Character.digit(str.charAt(i + 1), 16);
            if (msb < 0 || lsb < 0) {
                throw new IllegalArgumentException(
                        "Detected a Non-hex character at " + (i + 1) + " or " + (i + 2) + " position");
            }
            result[i / 2] = (byte) ((msb << 4) | lsb);
        }
        return new String(result);
    }

}
