package org.example.auditCenter;

import org.example.keyAuthorizationCenter.KeyAuthorizationCenter;
import org.example.mobileTerminal.MobileTerminal;
import org.example.utils.cpabe.parameter.MasterPrivateKey;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.example.utils.ecc.ECC.decryptByPrivateKey;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-20:01
 * @Description : 审计中心基类，实现基类方法包括加解密
 */
public class BaseAuditCenter {

    public PublicKey publicKey;
    private PrivateKey privateKey;
    public org.example.utils.cpabe.parameter.PublicKey mainPublicKey;

    public String auditRequest;
    public String auditMTIdentity;
    public String auditMTTimestampStr;
    public List<String> auditMTScope;
    public String targetGateway;

    /**
     * @Description :基类审计中心获取密钥授权中心消息加解密公私钥
     * @Author : Niushuo
     * @Date : 2023/9/2 15:08
     * @Param : [keyAuthorizationCenter]
     * @Return : void
     **/
    public  void getKey(KeyAuthorizationCenter keyAuthorizationCenter) throws Exception {
        KeyPair keyPair = keyAuthorizationCenter.genKey(112);
        publicKey =  keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    /**
     * @Description : 获取属性加密主密钥
     * @Author : Niushuo
     * @Date : 2023/9/4 13:11
     * @Param : [keyAuthorizationCenter]
     * @Return : void
     **/
    public void getMKey(KeyAuthorizationCenter keyAuthorizationCenter){
        mainPublicKey = keyAuthorizationCenter.systemKey.getPublicKey();
    }

    /**
     * @Description : 接收审计申请并解密拆分得到参数
     * @Author : Niushuo
     * @Date : 2023/9/3 17:17
     * @Param : [mobileTerminal]
     * @Return : void
     **/
    public void decryptAuditRequest(MobileTerminal mobileTerminal) throws Exception {
        //私钥base64编码
        String privateKeyBase64 = Base64.getEncoder().encodeToString(this.privateKey.getEncoded());
        System.out.println("接收到的审计证明内容：" + mobileTerminal.auditRequest);
        //解密
        this.auditRequest = decryptByPrivateKey(mobileTerminal.auditRequest, privateKeyBase64);
        System.out.println("解密后的审计证明内容：" + this.auditRequest);
        //解码
        String deAuditRequest = new String(Base64.getDecoder().decode(this.auditRequest));
        System.out.println("解码后的审计证明内容：" + deAuditRequest);
        //拆分获得4个参数
        String[] splitAuditRequest = deAuditRequest.split(",");
        this.auditMTIdentity = splitAuditRequest[0];
        System.out.println("解密拆分后的终端标识：" + this.auditMTIdentity);
        this.auditMTTimestampStr = splitAuditRequest[1];
        System.out.println("解密拆分后的当前时间戳：" + this.auditMTTimestampStr);
        this.auditMTScope = Arrays.asList(splitAuditRequest[2].split(" "));
        System.out.println("解密拆分后的终端申请作用域：" + this.auditMTScope);
        this.targetGateway = splitAuditRequest[3];
        System.out.println("解密拆分后的目的网关：" + this.targetGateway);
    }
}
