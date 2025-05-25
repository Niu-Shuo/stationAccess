package org.example;


import org.example.auditCenter.IdentityAuditCenter;
import org.example.auditCenter.ScopeAuditCenter;
import org.example.auditCenter.TimeSliceAuditCenter;
import org.example.dataBase.SAGDatabase;
import org.example.keyAuthorizationCenter.KeyAuthorizationCenter;
import org.example.mobileTerminal.MobileTerminal;
import org.example.secureAccessGateway.SecureAccessGateway;
import org.example.utils.cpabe.structure.AccessTree;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        //初始化各个数据库
        SAGDatabase sagDatabase = new SAGDatabase();



        MobileTerminal mobileTerminal = new MobileTerminal("NO1001",new ArrayList<>(Arrays.asList("鼓楼区","栖霞区")));
        sagDatabase.MTDatabase_ID_Scope.put(mobileTerminal.MTIdentity,mobileTerminal.MTScope);
        KeyAuthorizationCenter keyAuthorizationCenter = new KeyAuthorizationCenter();
        //"鼓楼区","栖霞区","浦口区","江宁区"
        SecureAccessGateway secureAccessGateway = new SecureAccessGateway("南京市区",new ArrayList<>(Arrays.asList("鼓楼区","栖霞区","浦口区","江宁区")));
        //secureAccessGateway.manageScope = Arrays.asList("鼓楼区","栖霞区","浦口区","江宁区");
        sagDatabase.SAGDatabase_ID_SAG.put(secureAccessGateway.Identity,secureAccessGateway);
        sagDatabase.SAGDatabase_ID_Scope.put(secureAccessGateway.Identity,secureAccessGateway.manageScope);
        System.out.println(sagDatabase.SAGDatabase_ID_SAG);
        System.out.println(sagDatabase.SAGDatabase_ID_Scope);
        IdentityAuditCenter identityAuditCenter = new IdentityAuditCenter();
        ScopeAuditCenter scopeAuditCenter = new ScopeAuditCenter();
        TimeSliceAuditCenter timeSliceAuditCenter = new TimeSliceAuditCenter();

        //分发消息加解密密钥
        mobileTerminal.getKey(keyAuthorizationCenter);
//        System.out.println(mobileTerminal.privateKey);
        secureAccessGateway.getKey(keyAuthorizationCenter);
//        System.out.println(secureAccessGateway.privateKey);
        identityAuditCenter.getKey(keyAuthorizationCenter);
//        System.out.println(identityAuditCenter.privateKey);
        scopeAuditCenter.getKey(keyAuthorizationCenter);
//        System.out.println(scopeAuditCenter.privateKey);
        timeSliceAuditCenter.getKey(keyAuthorizationCenter);
//        System.out.println(timeSliceAuditCenter.privateKey);

        //分发属性加密主密钥
        mobileTerminal.getMKey(keyAuthorizationCenter);
        System.out.println(mobileTerminal.MKey);
        secureAccessGateway.getMKey(keyAuthorizationCenter);
        System.out.println(secureAccessGateway.MKey);
        scopeAuditCenter.getMKey(keyAuthorizationCenter);
        System.out.println(scopeAuditCenter.mainPublicKey);
        timeSliceAuditCenter.getMKey(keyAuthorizationCenter);
        System.out.println(timeSliceAuditCenter.mainPublicKey);

        //移动终端给审计中心发审计申请，审计中心解密
        mobileTerminal.genAuditRequest(identityAuditCenter,secureAccessGateway);
        //审计中心对审计申请解密
        identityAuditCenter.decryptAuditRequest(mobileTerminal);
        //是否存在
        if(!identityAuditCenter.isExist(sagDatabase))
            System.out.println("该移动终端为该区域的非法终端，不予接入");

        //存在的话，继续向下传递
        System.out.println(identityAuditCenter.isExist(sagDatabase));

        //参数传给作用域审计中心
        identityAuditCenter.transParam(scopeAuditCenter);

        //生成属性加密公钥，访问结构树
        AccessTree accessTree = scopeAuditCenter.genCPABEPublicKey(sagDatabase);
        System.out.println("访问结构树的结构：" + accessTree);

        //作用域审计中心首先检查目的网关是否存在，然后审计作用域是否满足接入网关作用域
        //满足继续向下传参数
        if(!scopeAuditCenter.isBelongingScope(sagDatabase))
            System.out.println("目的网关非法");

        //向下传
        scopeAuditCenter.transParam(timeSliceAuditCenter,sagDatabase);

        //生成审计证明
        timeSliceAuditCenter.genAuditCertification();

        //返回加密的审计证明
        timeSliceAuditCenter.transParam(mobileTerminal);

        //终端解密
        mobileTerminal.decryptAuditCertification(timeSliceAuditCenter);

        //终端向网关发核验申请
        mobileTerminal.genVerifyRequest(secureAccessGateway);

        //安全接入网关的对和核验请解密
        secureAccessGateway.decryptVerifyRequest(mobileTerminal);
        //若可以通过解密出的终端标识得到的作用域，说明终端身份核验成功
        //若可以成功解密审计证明说明作用域核验成功
        //若当前时间戳和解密的时间片时间戳满足有效时间，核验成功
        secureAccessGateway.canVerify(sagDatabase);

        //双向认证 SAG->MT
        secureAccessGateway.SAG2MT(mobileTerminal);

        //终端生成会话密钥
        mobileTerminal.genSessionKey_MT(secureAccessGateway);

        //安全接入网关生成会话密钥
        secureAccessGateway.genSessionKey_SAG(mobileTerminal);
    }
}