/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.utils;

import com.jfinal.kit.Base64Kit;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 非对称加密工具类，对称加密请参考 DESUtil
 */
public class RSAUtil {


    /**
     * 生成key长度为 2048 的秘钥对
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair2048() throws Exception {
        //《2015 年加密宣言》密码指南建议，RSA 算法使用的密钥长度至少应为 2048 位。
        return getKeyPair(2048);
    }

    /**
     * 生成密钥对
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair(int keysize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keysize);
        return keyPairGenerator.generateKeyPair();
    }


    /**
     * 生成key长度为 2048 的秘钥对
     *
     * @return [publicKey, privateKey]
     * @throws Exception
     */
    public static String[] getKeyPairAsBase64() throws Exception {
        return getKeyPairAsBase64(2048);
    }

    /**
     * 生成密钥对
     *
     * @return [publicKey, privateKey]
     * @throws Exception
     */
    public static String[] getKeyPairAsBase64(int keysize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keysize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new String[]{Base64Kit.encode(keyPair.getPublic().getEncoded())
                , Base64Kit.encode(keyPair.getPrivate().getEncoded())};
    }


    /**
     * 公钥字符串转PublicKey实例
     *
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 私钥字符串转PrivateKey实例
     *
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }


    /**
     * 公钥加密
     *
     * @param content
     * @param publicKeyBase64
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] content, String publicKeyBase64) throws Exception {
        return encryptByPublicKey(content, getPublicKey(publicKeyBase64));
    }


    /**
     * 公钥加密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return Base64Kit.encode(cipher.doFinal(content));
    }


    /**
     * 公钥加密
     *
     * @param content
     * @param publicKeyBase64
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPublicKey(byte[] content, String publicKeyBase64) throws Exception {
        return encryptToBase64ByPublicKey(content, getPublicKey(publicKeyBase64));
    }


    /**
     * 公钥加密
     *
     * @param content
     * @param publicKeyBase64
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPublicKey(String content, String publicKeyBase64) throws Exception {
        return encryptToBase64ByPublicKey(content.getBytes(StandardCharsets.UTF_8), getPublicKey(publicKeyBase64));
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKeyBase64
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] content, String privateKeyBase64) throws Exception {
        return encryptByPrivateKey(content, getPrivateKey(privateKeyBase64));
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return Base64Kit.encode(cipher.doFinal(content));
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKeyBase64
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPrivateKey(byte[] content, String privateKeyBase64) throws Exception {
        return encryptToBase64ByPrivateKey(content, getPrivateKey(privateKeyBase64));
    }


    /**
     * 私钥加密
     *
     * @param content
     * @param privateKeyBase64
     * @return
     * @throws Exception
     */
    public static String encryptToBase64ByPrivateKey(String content, String privateKeyBase64) throws Exception {
        return encryptToBase64ByPrivateKey(content.getBytes(StandardCharsets.UTF_8), getPrivateKey(privateKeyBase64));
    }


    /**
     * 私钥解密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }


    /**
     * 私钥解密
     *
     * @param content
     * @param privateKeyBase64
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] content, String privateKeyBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyBase64));
        return cipher.doFinal(content);
    }


    /**
     * 私钥解密
     *
     * @param base64Content
     * @param privateKeyBase64
     * @return
     * @throws Exception
     */
    public static String decryptToStringByPrivateKey(String base64Content, String privateKeyBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKeyBase64));
        return new String(cipher.doFinal(Base64Kit.decode(base64Content)), StandardCharsets.UTF_8);
    }


    /**
     * 公钥解密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decrypByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }


    /**
     * 公钥解密
     *
     * @param content
     * @param publicKeyBase64
     * @return
     * @throws Exception
     */
    public static byte[] decrypByPublicKey(byte[] content, String publicKeyBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicKeyBase64));
        return cipher.doFinal(content);
    }


    /**
     * 公钥解密
     *
     * @param base64Content
     * @param publicKeyBase64
     * @return
     * @throws Exception
     */
    public static String decryptToStringByPublicKey(String base64Content, String publicKeyBase64) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey(publicKeyBase64));
        return new String(cipher.doFinal(Base64Kit.decode(base64Content)), StandardCharsets.UTF_8);
    }


}
