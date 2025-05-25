package org.example.mobileTerminal;

import com.sun.deploy.util.StringUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import org.example.auditCenter.IdentityAuditCenter;
import org.example.auditCenter.TimeSliceAuditCenter;
import org.example.keyAuthorizationCenter.KeyAuthorizationCenter;
import org.example.secureAccessGateway.SecureAccessGateway;
import org.example.utils.cpabe.parameter.MasterPrivateKey;
import org.example.utils.cpabe.text.CipherText;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static org.example.utils.ecc.ECC.decryptByPrivateKey;
import static org.example.utils.ecc.ECC.encryptByPublicKey;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-19:49
 * @Description : 移动终端类，审计申请、核验申请的生成以及加密等操作
 */
public class MobileTerminal {

    public final String MTIdentity;
    //消息加解密公私钥
    public PublicKey publicKey;
    private PrivateKey privateKey;
    //属性加密主密钥
    public org.example.utils.cpabe.parameter.PublicKey MKey;
    public List<String> MTScope;

    public String auditRequest;
    public CipherText auditCertification;
    public String encryptAuditCertification;
    public String auditCertificationStr;
    public String verifyRequest;
    private int SessionKey_MT;
    public int MTSign;
    private BigInteger MTPrivateKey;
    public Element MTPublicKey;

    public MobileTerminal(String string,List<String> list){
        this.MTIdentity = string;
        this.MTScope = list;
    }

    /**
     * @Description : 获取KAC颁发的消息加解密密钥
     * @Author : Niushuo
     * @Date : 2023/9/1 15:32
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
     * @Date : 2023/9/2 20:12
     * @Param : [keyAuthorizationCenter]
     * @Return : void
     **/
    public void getMKey(KeyAuthorizationCenter keyAuthorizationCenter){
        MKey = keyAuthorizationCenter.systemKey.getPublicKey();
    }

    /**
     * @Description : 生成审计申请
     * @Author : Niushuo
     * @Date : 2023/9/3 21:55
     * @Param : [identityAuditCenter, secureAccessGateway]
     * @Return : void
     **/
    public void genAuditRequest(IdentityAuditCenter identityAuditCenter, SecureAccessGateway secureAccessGateway) throws Exception {
        //审计中心的公钥base64编码
        String publicKeyBase64 = Base64.getEncoder().encodeToString(identityAuditCenter.publicKey.getEncoded());
        //时间戳
        long timeStamp = System.currentTimeMillis();
        String timeStampStr = Long.toString(timeStamp);
        //作用域
        String MTScopeStr = StringUtils.join(this.MTScope," ");
        //目的网关
        String targetGateway = secureAccessGateway.Identity;
        //生成审计申请
        String param = this.MTIdentity + "," + timeStampStr + "," + MTScopeStr + "," + targetGateway;
        System.out.println("未加密的审计申请参数" + param);
        this.auditRequest = encryptByPublicKey(param,publicKeyBase64);
        System.out.println("加密的审计申请内容：" + this.auditRequest);
    }

    /**
     * @Description : 对审计结果解密得到各参数
     * @Author : Niushuo
     * @Date : 2023/9/4 22:35
     * @Param : [timeSliceAuditCenter]
     * @Return : void
     **/
    public void decryptAuditCertification(TimeSliceAuditCenter timeSliceAuditCenter) throws Exception {
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String contentDe = decryptByPrivateKey(encryptAuditCertification, privateKeyBase64);
        System.out.println("解密的审计结果：" + contentDe);
        String deStr = new String(Base64.getDecoder().decode(contentDe));
        System.out.println("解码的审计结果：" + deStr);
        this.auditCertificationStr = deStr;
        auditCertification = timeSliceAuditCenter.auditCertification;
        System.out.println("解密接受的审计证明：" + auditCertification);
    }

//    public CipherText String2Cipher(String string){
//        CipherText cipherText = new CipherText();
////        String temp = string.substring(11,string.length() - 1);
////        int start = temp.indexOf("{") + 1;
////        int end = temp.indexOf("}");
////        System.out.println(temp.substring(start,end));
////        String[] split = temp.split(",");
////        for(String str : split)
////            System.out.println(str);
////        cipherText.setC_wave();
////        cipherText.setC();
////        cipherText.setC_y_map();
////        cipherText.setC_y_pie_map();
////        cipherText.setAccessTree();
//        return cipherText;
//    }

    /**
     * @Description : 生成核验申请并发送
     * @Author : Niushuo
     * @Date : 2023/9/6 22:00
     * @Param : [secureAccessGateway]
     * @Return : void
     **/
    public void genVerifyRequest(SecureAccessGateway secureAccessGateway) throws Exception {
        //安全接入网关的公钥base64编码
        String publicKeyBase64 = Base64.getEncoder().encodeToString(secureAccessGateway.publicKey.getEncoded());
        //时间戳
        long timeStamp = System.currentTimeMillis();
        String timeStampStr = Long.toString(timeStamp);
        System.out.println("发送的终端标识：" + this.MTIdentity);
        System.out.println("发送的时间戳：" + timeStampStr);
        //生成核验申请
        String param = this.MTIdentity + "," + auditCertificationStr + "," + timeStampStr;
        this.verifyRequest = encryptByPublicKey(param,publicKeyBase64);
        System.out.println("发送的加密的核验申请：" + this.verifyRequest);
        //secureAccessGateway.verifyRequest = this.verifyRequest;
        secureAccessGateway.auditCertification = this.auditCertification;
    }

    /**
     * @Description : 生成会话密钥 MT-SAG
     * @Author : Niushuo
     * @Date : 2023/9/6 22:45
     * @Param : [secureAccessGateway]
     * @Return : void
     **/
    public void genSessionKey_MT(SecureAccessGateway secureAccessGateway){
        Field G1 = secureAccessGateway.G1;
        Field Zr = secureAccessGateway.Zr;
        Element P = secureAccessGateway.P;
        String MTScope_Str = String.join("",this.MTScope);
        System.out.println("作用域List转的String：" + MTScope_Str);
        BigInteger MTScope_Int = new BigInteger(MTScope_Str.getBytes());
        System.out.println("String转到BigInter：" + MTScope_Int);
        Element theta = secureAccessGateway.SAGPublicKey.mul(MTScope_Int).add(secureAccessGateway.SAGPublicKey);
        System.out.println("签名：" + theta);

        byte[] theta_byte = Base64.getEncoder().encode(theta.toBytes());
        String theta_Str = new String(theta_byte);
        int theta_Hash = theta_Str.hashCode();
        int MTScope_Hash = MTScope_Str.hashCode();
        System.out.println("MTScope_Hash：" + MTScope_Hash);
        System.out.println("theta_Hash：" + theta_Hash);
        System.out.println(secureAccessGateway.theta_Hash == theta_Hash);
        if((secureAccessGateway.SAGSign ^ MTScope_Hash) == theta_Hash){
            System.out.println("双向认证失败");
            return;
        }
        //本次的私钥随机生成
        Random random = new Random();
        this.MTPrivateKey = new BigInteger(160,random);
        System.out.println("私钥：" + this.MTPrivateKey);
        //公钥
        this.MTPublicKey = P.duplicate().mul(MTPrivateKey);
        Element MT_Key = secureAccessGateway.SAGPublicKey.duplicate().mul(this.MTPrivateKey);
        System.out.println("MT_Key：" + MT_Key);
        byte[] MT_KeyByte = Base64.getEncoder().encode(MT_Key.toBytes());
        String MT_KeyStr = new String(MT_KeyByte);
        this.SessionKey_MT = (MTScope_Str + MT_KeyStr).hashCode();

        this.MTSign = theta_Hash ^ MTScope_Hash;
        System.out.println("双向认证请求：" + this.MTSign);
    }
}
