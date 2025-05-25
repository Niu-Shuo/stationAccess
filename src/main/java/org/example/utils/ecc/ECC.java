package org.example.utils.ecc;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @Author : Niushuo
 * @Date : 2023-09-01-14:16
 * @Description :
 */
public class ECC {
    
    /**
     * @Description : 生成密钥对
     * @Author : Niushuo
     * @Date : 2023/9/1 14:28
     * @Param : [keySize, KEY_ALGORITHM] [密钥长度，选择的密钥算法]
     * @Return : java.security.KeyPair
     **/
    public static KeyPair initKey(int keySize, String KEY_ALGORITHM) throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }

    /**
     * @Description : 公钥加密
     * @Author : Niushuo
     * @Date : 2023/9/1 14:29
     * @Param : [data, publicKey] [源数据，base64编码的公钥]
     * @Return : java.lang.String
     **/
    public static String encryptByPublicKey(String data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        Security.addProvider(new BouncyCastleProvider());
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(x509KeySpec));
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    /**
     * @Description : 私钥解密
     * @Author : Niushuo
     * @Date : 2023/9/1 14:30
     * @Param : [encryptedData, privateKey] [加密的数据，base64编码的私钥]
     * @Return : java.lang.String
     **/
    public static String decryptByPrivateKey(String encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(pkcs8KeySpec));
        return Base64.getEncoder().encodeToString(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }

    /**
     * @Description : 用私钥对信息生成数字签名
     * @Author : Niushuo
     * @Date : 2023/9/1 14:31
     * @Param : [content, priKey, signatureAl] [base64编码的加密数据，base64编码的私钥，签名算法]
     * @Return : java.lang.String
     **/
    public static String sign(String content, String priKey, String signatureAl) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(priKey));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPrivateKey privateK = (ECPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        Signature sign = Signature.getInstance(signatureAl);//"SHA256withECDSA/"
        sign.initSign(privateK);
        sign.update(Base64.getDecoder().decode(content));
        return Base64.getEncoder().encodeToString(sign.sign());
    }

    /**
     * @Description : 使用模板
     * @Author : Niushuo
     * @Date : 2023/9/1 14:32
     * @Param : []
     * @Return : void
     **/
    @Test
    public void encryptAndDecrypt() throws Exception{
        try {
            //初始化获取公钥和私钥
            KeyPair keypair = initKey(256, "EC");

            PublicKey publicKey = keypair.getPublic();
            PrivateKey privateKey = keypair.getPrivate();

            System.out.println("私钥原：" + privateKey);
            System.out.println("公钥原：" + publicKey);

            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

//            //生成固定公钥私钥
//            String publicKeyBase64 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEvVlOVXJQe6yyLlCSCWQr246yay4Hl9qfB3C5S9al9t6cNzP3lwjJIRGzFmGywspn0OwiMJWmFV7daLhzCx79kQ==";
//            String privateKeyBase64 = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDyvdnfevbZyiWDWOmwRp5hLDftlNWHzdD5YkiQW6hR6g==";

            System.out.println("公钥：" + publicKeyBase64 + publicKeyBase64.getBytes().length);
            System.out.println("-----");
            System.out.println("私钥：" + privateKeyBase64 + privateKeyBase64.getBytes().length);

            String con = "7214083869398510205830034914356820453685029199381676443177786398";
            System.out.println("加密之前：" + con);
            //加密
            long begin = System.currentTimeMillis();
            String content = encryptByPublicKey(con, publicKeyBase64);
            System.out.println(System.currentTimeMillis() - begin);
            System.out.println("加密之后：" + content);
            //解密
            String contentDe = "";
            long num_de = 0;
            for(int i = 0; i < 100; ++i){
                long end = System.currentTimeMillis();
                contentDe= decryptByPrivateKey(content, privateKeyBase64);
                num_de += System.currentTimeMillis() - end;
            }
            System.out.println(num_de);

//            System.out.println(System.currentTimeMillis() - end);
            System.out.println("解密之前：" + contentDe);
            //解密之后
            String deStr = new String(Base64.getDecoder().decode(contentDe));
            System.out.println("解密之后：" + deStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
