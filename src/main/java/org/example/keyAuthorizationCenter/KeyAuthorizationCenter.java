package org.example.keyAuthorizationCenter;

import org.example.utils.cpabe.parameter.SystemKey;

import java.security.KeyPair;
import java.util.Map;

import static org.example.utils.ecc.ECC.initKey;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-21:01
 * @Description : 密钥生成中心类，主要负责属性加密主密钥以及加解密公私钥的分发等
 */
public class KeyAuthorizationCenter {

    //生成属性加密主密钥
    public SystemKey systemKey = SystemKey.build();


    /**
     * @Description : 为特定设备生成ECC消息加解密的公私钥对
     * @Author : Niushuo
     * @Date : 2023/9/1 15:11
     * @Param : [keySize] [密钥长度]
     * @Return : java.security.KeyPair
     **/
    public KeyPair genKey(int keySize) throws Exception {
        return initKey(keySize,"EC");
    }


}
