package org.example.auditCenter;


import org.example.mobileTerminal.MobileTerminal;
import org.example.utils.cpabe.engine.CPABEEngine;
import org.example.utils.cpabe.structure.AccessTree;
import org.example.utils.cpabe.text.CipherText;
import org.example.utils.cpabe.text.PlainText;

import java.util.Base64;

import static org.example.utils.ecc.ECC.encryptByPublicKey;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-20:01
 * @Description : 时间片审计中心，第三道防线主要用来检查时间可行性以及生成审计证明
 */
public class TimeSliceAuditCenter extends BaseAuditCenter{

    public CipherText auditCertification;

    public String pastTimestampStr;
    public String nowTimestampStr;

    public AccessTree accessTree;



    /**
     * @Description : 生成审计证明
     * @Author : Niushuo
     * @Date : 2023/9/4 22:34
     * @Param : []
     * @Return : void
     **/
    public void genAuditCertification(){
        CPABEEngine cpabeEngine = new CPABEEngine();
        long nowTimestamp = System.currentTimeMillis();
        nowTimestampStr = Long.toString(nowTimestamp);
        long pastTimestamp = Long.parseLong(pastTimestampStr);
        System.out.println("接收审计申请的时间戳：" + nowTimestamp);
        System.out.println("发送审计申请的时间戳：" + pastTimestamp);
        long period = 1000;
        if(Math.abs(nowTimestamp - pastTimestamp) > period){
            System.out.println("超时");
            return;
        }
        long certificationPeriod = 100;
        String originalCertificationStr = System.currentTimeMillis() + "," + certificationPeriod;
        PlainText originalCertification = new PlainText(originalCertificationStr, mainPublicKey);

        //加密生成审计证明
        auditCertification = cpabeEngine.encrypt(mainPublicKey,originalCertification,accessTree);
        System.out.println("加密生成的审计证明：" + auditCertification);
    }

    /**
     * @Description : 像移动终端返回加密的审计结果
     * @Author : Niushuo
     * @Date : 2023/9/4 22:35
     * @Param : [mobileTerminal]
     * @Return : void
     **/
    public void transParam(MobileTerminal mobileTerminal) throws Exception {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(mobileTerminal.publicKey.getEncoded());
        String auditCertificationStr = auditCertification.toString();
        System.out.println("发送之前的审计证明字符串：" + auditCertificationStr);
        mobileTerminal.encryptAuditCertification = encryptByPublicKey(auditCertificationStr,publicKeyBase64);
        System.out.println("加密的审计结果" +mobileTerminal.encryptAuditCertification);
    }


}
