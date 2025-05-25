package org.example.auditCenter;

import org.example.dataBase.SAGDatabase;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-19:59
 * @Description : 身份审计中心，第一道防线主要通过查询数据库是否有接入终端的身份
 */
public class IdentityAuditCenter extends BaseAuditCenter{



    public boolean isExist(SAGDatabase sagDatabase){
        return sagDatabase.MTDatabase.contains(auditMTIdentity);
    }

    public void transParam(ScopeAuditCenter scopeAuditCenter){
        scopeAuditCenter.auditMTScope = auditMTScope;
        scopeAuditCenter.pastTimestampStr = auditMTTimestampStr;
        scopeAuditCenter.manageSAGGatewayID = targetGateway;

    }

}
