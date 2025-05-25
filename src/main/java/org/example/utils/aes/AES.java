package org.example.utils.aes;


import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * @Author : Niushuo
 * @Date : 2023-09-01-13:47
 * @Description : AES对称加密工具类，主要包括加密和解密
 */
public class AES {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    /**
     * @description:
     * @author: Niushuo 
     * @date: 2023/9/1 14:11
     * @param: [data, key]
     * @return: java.lang.String
     **/
    public static String encrypt(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = generateSecretKeySpec(key);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * @description:
     * @author: Niushuo 
     * @date: 2023/9/1 14:11
     * @param: [data, key]
     * @return: java.lang.String
     **/
    public static String decrypt(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = generateSecretKeySpec(key);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(data);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * @description:
     * @author: Niushuo 
     * @date: 2023/9/1 15:09
     * @param: [key]
     * @return: javax.crypto.spec.SecretKeySpec
     **/
    private static SecretKeySpec generateSecretKeySpec(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] hashedBytes = sha.digest(keyBytes);
        byte[] truncatedBytes = new byte[16];
        System.arraycopy(hashedBytes, 0, truncatedBytes, 0, truncatedBytes.length);
        return new SecretKeySpec(truncatedBytes, SECRET_KEY_ALGORITHM);
    }

    @Test
    public void encryptAndDecrypt() throws Exception{
        String data = "Hello World!";
        String key = "mysecretkey";

        try {
            // 加密数据
            long begin = System.currentTimeMillis();
            String encryptedData = AES.encrypt(data, key);
            System.out.println(System.currentTimeMillis() - begin);
            System.out.println("加密后的数据: " + encryptedData);

            // 解密数据
            long end = System.currentTimeMillis();
            String decryptedData = AES.decrypt(encryptedData, key);
            System.out.println(System.currentTimeMillis() - end);
            System.out.println("解密后的数据: " + decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
