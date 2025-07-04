package org.example.utils.cpabe.structure;

import lombok.Data;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:47
 * @Description :
 */
@Data
public class AccessTreeBuildModel {
    //访问树节点的唯一性标识
    private Integer id;
    //内部节点还是叶子节点
    private byte type;
    //阈值
    private int threshold;
    //索引
    private int index;
    //属性
    private String attribute;
    //父亲ID,-1 表示没有父亲
    private Integer parentId;

    private AccessTreeBuildModel(){

    }

    public static AccessTreeBuildModel innerAccessTreeBuildModel(int id,int threshold,int index,int parentId){
        AccessTreeBuildModel buildModel = new AccessTreeBuildModel();
        buildModel.setId(id);
        buildModel.setType(AccessTreeNodeType.INNER_NODE);
        buildModel.setIndex(index);
        buildModel.setThreshold(threshold);
        buildModel.setParentId(parentId);
        return buildModel;
    }

    public static AccessTreeBuildModel leafAccessTreeBuildModel(int id,int index,String attributeName,int parentId){
        AccessTreeBuildModel buildModel = new AccessTreeBuildModel();
        buildModel.setId(id);
        buildModel.setType(AccessTreeNodeType.LEAF_NODE);
        buildModel.setIndex(index);
        buildModel.setAttribute(attributeName);
        buildModel.setParentId(parentId);
        return buildModel;
    }
}
