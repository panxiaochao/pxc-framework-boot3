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
package io.github.panxiaochao.boot3.crypto.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

/**
 * <p>
 * KeyPair 密钥生成工具.
 * </p>
 *
 * @author Lypxc
 * @since 2022-12-14
 */
public class KeyGeneratorUtil {

    private static final String ALGORITHM_RSA = "RSA";

    private static final int DEFAULT_KEY_SIZE = 2048;

    private KeyGeneratorUtil() {
    }

    public static KeyPair generateRsaKeyPair() {
        return generateRsaKeyPair(null);
    }

    public static KeyPair generateRsaKeyPair(String seed) {
        return generateKeyPair(ALGORITHM_RSA, seed);
    }

    private static KeyPair generateKeyPair(String algorithm, String seed) {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            if (StringUtils.isNotBlank(seed)) {
                SecureRandom secureRandom = new SecureRandom(seed.getBytes());
                keyPairGenerator.initialize(DEFAULT_KEY_SIZE, secureRandom);
            }
            else {
                keyPairGenerator.initialize(DEFAULT_KEY_SIZE);
            }
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    public static KeyPair generateEcKey() {
        EllipticCurve ellipticCurve = new EllipticCurve(
                new ECFieldFp(new BigInteger(
                        "115792089210356248762697446949407573530086143415290314195533631308867097853951")),
                new BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
                new BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291"));

        ECPoint ecPoint = new ECPoint(
                new BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
                new BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109"));

        ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve, ecPoint,
                new BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"), 1);

        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(ecParameterSpec);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

}
