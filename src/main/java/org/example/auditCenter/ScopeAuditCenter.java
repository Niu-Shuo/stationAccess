package org.example.auditCenter;

import org.example.dataBase.SAGDatabase;
import org.example.secureAccessGateway.SecureAccessGateway;
import org.example.utils.cpabe.structure.AccessTree;
import org.example.utils.cpabe.structure.AccessTreeBuildModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-20:00
 * @Description : 作用域审计中心，第二道防线主要用来生成属性加密私钥
 */
public class ScopeAuditCenter extends BaseAuditCenter{
    public String pastTimestampStr;


    List<String> auditMTScope;

    protected String manageSAGGatewayID;
    protected List<String> manageSAGGatewayScope;
    protected SecureAccessGateway manageSAGGateway;

    public void getTargetGateway(SAGDatabase sagDatabase){
        manageSAGGateway = sagDatabase.SAGDatabase_ID_SAG.get(manageSAGGatewayID);
    }

    /**
     * @Description : 判断申请作用域是否属于网关管理作用域
     * @Author : Niushuo
     * @Date : 2023/9/4 12:58
     * @Param : [sagDatabase]
     * @Return : boolean
     **/
    public boolean isBelongingScope(SAGDatabase sagDatabase){
        getTargetGateway(sagDatabase);
        if(manageSAGGateway == null)
        {
            System.out.println("申请网关与目的网关不符合");
            return false;
        }

        manageSAGGatewayScope = sagDatabase.SAGDatabase_ID_Scope.get(manageSAGGatewayID);
        System.out.println("数据库查询到的网关管理作用域：" + manageSAGGatewayScope);
        List<String> tmp = new ArrayList<>(manageSAGGatewayScope);
        tmp.retainAll(auditMTScope);
        System.out.println("终端申请的作用域："+auditMTScope);
        System.out.println("数据库查询到的网关管理作用域是否改变：" + manageSAGGatewayScope);
        manageSAGGatewayScope = sagDatabase.SAGDatabase_ID_Scope.get(manageSAGGatewayID);
        return isEquals(auditMTScope,tmp);
    }

    /**
     * @Description : 比较两个作用域是否满足条件
     * @Author : Niushuo
     * @Date : 2023/9/4 12:57
     * @Param : [list1, list2]
     * @Return : boolean
     **/
    public <T extends Comparable<T>> boolean isEquals(List<T> list1, List<T> list2){
        if (list1 == null && list2 == null) {
            return true;
        }
        //Only one of them is null
        else if(list1 == null || list2 == null) {
            return false;
        }
        else if(list1.size() != list2.size()) {
            return false;
        }

        //copying to avoid rearranging original lists
        list1 = new ArrayList<T>(list1);
        list2 = new ArrayList<T>(list2);

        Collections.sort(list1);
        Collections.sort(list2);

        return list1.equals(list2);
    }

    public AccessTree genCPABEPublicKey(SAGDatabase sagDatabase){
        return getAccessTree(sagDatabase);
    }

    /**
     * @Description : 生成访问结构树，即用来加密的属性加密公钥
     * @Author : Niushuo
     * @Date : 2023/9/4 14:27
     * @Param : [sagDatabase]
     * @Return : org.example.utils.cpabe.structure.AccessTree
     **/
    public  AccessTree getAccessTree(SAGDatabase sagDatabase) {
        getTargetGateway(sagDatabase);
        manageSAGGatewayScope = sagDatabase.SAGDatabase_ID_Scope.get(manageSAGGatewayID);
        AccessTreeBuildModel[] accessTreeBuildModels = new AccessTreeBuildModel[manageSAGGatewayScope.size() + 1];
        System.out.println("管理的作用域个数：" + manageSAGGatewayScope.size());
        int threshold = (int)auditMTScope.size();
        System.out.println("需要满足的阈值条件个数：" + threshold);
        accessTreeBuildModels[0] = AccessTreeBuildModel.innerAccessTreeBuildModel(1,threshold,1,-1);
        for(int index = 0;index < manageSAGGatewayScope.size();++index)
            accessTreeBuildModels[index + 1] = AccessTreeBuildModel.leafAccessTreeBuildModel(index + 2,index + 1,manageSAGGatewayScope.get(index),1);
        return AccessTree.build(mainPublicKey, accessTreeBuildModels);
    }

    public void transParam(TimeSliceAuditCenter timeSliceAuditCenter,SAGDatabase sagDatabase){
        timeSliceAuditCenter.pastTimestampStr = pastTimestampStr;
        timeSliceAuditCenter.accessTree = genCPABEPublicKey(sagDatabase);
    }

}
