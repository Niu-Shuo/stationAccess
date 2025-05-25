package org.example.secureAccessGateway;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.example.dataBase.SAGDatabase;
import org.example.keyAuthorizationCenter.KeyAuthorizationCenter;
import org.example.mobileTerminal.MobileTerminal;
import org.example.utils.cpabe.attribute.Attribute;
import org.example.utils.cpabe.engine.CPABEEngine;
import org.example.utils.cpabe.parameter.MasterPrivateKey;
import org.example.utils.cpabe.parameter.UserPrivateKey;
import org.example.utils.cpabe.text.CipherText;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static org.example.utils.ecc.ECC.decryptByPrivateKey;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-20:18
 * @Description : 安全接入网关类，负责对审计证明核验以及相关加解密函数
 */
public class SecureAccessGateway {

    public  List<String> manageScope = Arrays.asList("鼓楼区","栖霞区","浦口区","江宁区");
    public PublicKey publicKey;
    private PrivateKey privateKey;
    public org.example.utils.cpabe.parameter.PublicKey MKey;
    public MasterPrivateKey masterPrivateKey;
    public String Identity;
    public String verifyRequest;
    public String verifyMTIdentity;
    public CipherText auditCertification;
    public String pastTimestampStr;
    public String nowTimestampStr;
    public List<Attribute> MTAttributeList = new ArrayList<>();

    private BigInteger SAGPrivateKey;
    public Element SAGPublicKey;
    public int SAGSign;
    public Field G1;
    public Field Zr;
    public Element P;
    public int theta_Hash;
    private int SessionKey_SAG;

    /**
     * @Description : 安全接入网关获取密钥授权中颁发的消息加解密公私钥
     * @Author : Niushuo
     * @Date : 2023/9/2 15:10
     * @Param : [keyAuthorizationCenter]
     * @Return : void
     **/
    public  void getKey(KeyAuthorizationCenter keyAuthorizationCenter) throws Exception {
        KeyPair keyPair = keyAuthorizationCenter.genKey(112);
        publicKey =  keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    /**
     * @Description : 生成属性加密主密钥
     * @Author : Niushuo
     * @Date : 2023/9/4 22:36
     * @Param : [keyAuthorizationCenter]
     * @Return : void
     **/
    public void getMKey(KeyAuthorizationCenter keyAuthorizationCenter){
        MKey = keyAuthorizationCenter.systemKey.getPublicKey();
        masterPrivateKey = keyAuthorizationCenter.systemKey.getMasterPrivateKey();
    }




    public SecureAccessGateway(String Identity,List<String> manageScope){
        this.Identity = Identity;
        this.manageScope = manageScope;
    }

    public void decryptVerifyRequest(MobileTerminal mobileTerminal) throws Exception {
        //私钥base64编码
        String privateKeyBase64 = Base64.getEncoder().encodeToString(this.privateKey.getEncoded());
        System.out.println("接收到的审计证明内容：" + mobileTerminal.verifyRequest);
        //解密
        this.verifyRequest = decryptByPrivateKey(mobileTerminal.verifyRequest, privateKeyBase64);
        System.out.println("解密后的审计证明内容：" + this.verifyRequest);
        //解码
        String deVerifyRequest = new String(Base64.getDecoder().decode(this.verifyRequest));
        System.out.println("解码后的审计证明内容：" + deVerifyRequest);
        //拆分获得3个参数
        String[] splitVerifyRequest = deVerifyRequest.split(",");
        this.verifyMTIdentity = splitVerifyRequest[0];
        System.out.println("解密得到的终端标识：" + this.verifyMTIdentity);
        this.pastTimestampStr = splitVerifyRequest[splitVerifyRequest.length - 1];
        System.out.println("解密得到的时间戳：" + this.pastTimestampStr);
    }

    /**
     * @Description : 安全接入网关核验阶段
     * @Author : Niushuo
     * @Date : 2023/9/5 19:34
     * @Param : [sagDatabase]
     * @Return : void
     **/
    public void canVerify(SAGDatabase sagDatabase){
        List<String> MTScope = sagDatabase.MTDatabase_ID_Scope.get(verifyMTIdentity);
        for(String str : MTScope)
            this.MTAttributeList.add(new Attribute(str, this.MKey));
        //解密私钥生成
        CPABEEngine cpabeEngine = new CPABEEngine();
        UserPrivateKey userPrivateKey = cpabeEngine.keyGen(masterPrivateKey, MTAttributeList);
        //解密
        String decryptAuditCertification = cpabeEngine.decryptToStr(MKey, userPrivateKey, auditCertification);
        if(decryptAuditCertification == null){
            System.out.println("作用域核验失败，申请作用域与接入网关管理作用域不匹配");
            return;
        }
        System.out.println("审计证明结果: " + decryptAuditCertification);
        String[] splitAuditCertification = decryptAuditCertification.split(",");
        long pastTimestamp = Long.parseLong(splitAuditCertification[0]);
        System.out.println("审计证明颁发时间戳：" + pastTimestamp);
        long nowTimestamp = System.currentTimeMillis();
        System.out.println("审计证明核验时间戳：" + nowTimestamp);
        long period = Long.parseLong(splitAuditCertification[1]);
        System.out.println("审计证明有效期：" + period);
        if(Math.abs(pastTimestamp - nowTimestamp) > period){
            System.out.println("时间片核验失败，审计证明不在有效期内，重新申请");
            return;
        }
        System.out.println("终端核验成功，可以进行双向认证阶段");
    }

    /**
     * @Description : 双向认证第一阶段
     * @Author : Niushuo
     * @Date : 2023/9/6 22:47
     * @Param : [mobileTerminal]
     * @Return : void
     **/
    public void SAG2MT(MobileTerminal mobileTerminal){
        //从文件导入椭圆曲线参数
        Pairing pairing = PairingFactory.getPairing("params/curves/a.properties");
        System.out.println("线性配对：" + pairing);
        //加法群
        this.G1 = pairing.getG1();
        System.out.println("椭圆曲线：" + G1);
        //数域
        this.Zr = pairing.getZr();
        Element s = Zr.newElement();
        //生成元P
        this.P = G1.newRandomElement();
        System.out.println("生成元：" + P);
        //生成随机数r，作为本次加密的私钥
        Random random = new Random();
        this.SAGPrivateKey = new BigInteger(160,random);
        System.out.println("私钥：" + this.SAGPrivateKey);
        //公钥
        this.SAGPublicKey = P.duplicate().mul(SAGPrivateKey);
        System.out.println("公钥：" + this.SAGPublicKey);

        String MTScope_Str = String.join("",mobileTerminal.MTScope);
        System.out.println("作用域List转的String：" + MTScope_Str);
        BigInteger MTScope_Int = new BigInteger(MTScope_Str.getBytes());
        System.out.println("String转到BigInter：" + MTScope_Int);
        Element theta = P.duplicate().mul(this.SAGPrivateKey.add(MTScope_Int.multiply(this.SAGPrivateKey)));
        System.out.println("签名：" + theta);

        //Element转byte再转String
        byte[] theta_byte = Base64.getEncoder().encode(theta.toBytes());
        String theta_Str = new String(theta_byte);

        int MTScope_Hash = MTScope_Str.hashCode();
        this.theta_Hash = theta_Str.hashCode();
        System.out.println("MTScope_Hash:" + MTScope_Hash);
        System.out.println("theta_Hash:" + theta_Hash);

        this.SAGSign = (MTScope_Hash ^ theta_Hash);
        System.out.println("双向认证请求：" + this.SAGSign);
        System.out.println((this.SAGSign ^ MTScope_Hash) == this.theta_Hash);
    }

    public void genSessionKey_SAG(MobileTerminal mobileTerminal){
        String MTScope_Str = String.join("",mobileTerminal.MTScope);
        System.out.println("作用域List转的String：" + MTScope_Str);
        int MTScope_Hash = MTScope_Str.hashCode();
        if((mobileTerminal.MTSign ^ MTScope_Hash) == this.theta_Hash){
            System.out.println("双向认证失败");
            return;
        }
        Element SAG_Key = mobileTerminal.MTPublicKey.mul(this.SAGPrivateKey);
        System.out.println("SAG_Key：" + SAG_Key);
        byte[] SAG_KeyByte = Base64.getEncoder().encode(SAG_Key.toBytes());
        String SAG_KeyStr = new String(SAG_KeyByte);
        this.SessionKey_SAG = SAG_KeyStr.hashCode();
        System.out.println("SessionKey_SAG：" + SessionKey_SAG);
    }
}
