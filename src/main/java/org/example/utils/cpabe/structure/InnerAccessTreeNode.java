package org.example.utils.cpabe.structure;

import lombok.Data;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:49
 * @Description :
 */
@Data
public class InnerAccessTreeNode extends AccessTreeNode {
    /**
     * 阈值
     */
    private int threshold;

    public InnerAccessTreeNode(int threshold,int index,AccessTreeNode parent){
        this.threshold = threshold;
        super.setIndex(index);
        super.setParent(parent);
    }

    public InnerAccessTreeNode(int threshold,int index){
        this.threshold = threshold;
        super.setIndex(index);
    }

    @Override
    public byte getAccessTreeNodeType() {
        return AccessTreeNodeType.INNER_NODE;
    }


    @Override
    public String toString(){
        return ("type : inner Node, threshold : " + threshold + ",   children size : " + getChildrenSize()  + "  secretNumber :  " + getSecretNumber() + "  parentId  " + this.getParentId());
    }

}
