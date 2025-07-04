package org.example.utils.cpabe;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.example.utils.cpabe.attribute.Attribute;
import org.example.utils.cpabe.engine.CPABEEngine;
import org.example.utils.cpabe.parameter.PublicKey;
import org.example.utils.cpabe.parameter.SystemKey;
import org.example.utils.cpabe.parameter.UserPrivateKey;
import org.example.utils.cpabe.structure.AccessTree;
import org.example.utils.cpabe.structure.AccessTreeBuildModel;
import org.example.utils.cpabe.structure.AccessTreeNode;
import org.example.utils.cpabe.text.CipherText;
import org.example.utils.cpabe.text.PlainText;

import java.util.Arrays;
import java.util.List;

/**
 * @Author : Niushuo
 * @Date : 2023-09-01-10:59
 * @Description :
 */
public class CPABE {
    //使用模板
    public static void test(){
        //1.生成系统密钥  包含公钥-私钥
        SystemKey systemKey = SystemKey.build();
        //2.设置用户属性
        List<Attribute> attributes = Arrays.asList(
                // new Attribute("学生", systemKey.getPublicKey()),
                //new Attribute("老师", systemKey.getPublicKey()),
                new Attribute("硕士", systemKey.getPublicKey()),
                new Attribute("护士", systemKey.getPublicKey())
                // new Attribute("二班", systemKey.getPublicKey())
        );
        //3.生成用户私钥
        CPABEEngine cpabeEngine = new CPABEEngine();
        UserPrivateKey userPrivateKey = cpabeEngine.keyGen(systemKey.getMasterPrivateKey(), attributes);
        //4.明文
        String plainTextStr = "你好，CP - ABE，我是JPBC";
        PlainText plainText = new PlainText(plainTextStr, systemKey.getPublicKey());
        System.out.println("plainTextStr : " + plainTextStr);
        //5.构建访问树
        AccessTree accessTree = getAccessTree(systemKey.getPublicKey());
        //6.加密
        CipherText cipherText = cpabeEngine.encrypt(systemKey.getPublicKey(), plainText, accessTree);
        System.out.println("cipherText : " + cipherText);
        //7.解密
        String decryptStr = cpabeEngine.decryptToStr(systemKey.getPublicKey(), userPrivateKey, cipherText);
        System.out.println("decryptStr : " + decryptStr);
    }

    public static AccessTree getAccessTree(PublicKey publicKey) {
        AccessTreeBuildModel[] accessTreeBuildModels = new AccessTreeBuildModel[7];
        //根节点ID必须为1
        accessTreeBuildModels[0] = AccessTreeBuildModel.innerAccessTreeBuildModel(1, 2, 1, -1);
        accessTreeBuildModels[1] = AccessTreeBuildModel.leafAccessTreeBuildModel(2, 1, "学生", 1);
        accessTreeBuildModels[2] = AccessTreeBuildModel.leafAccessTreeBuildModel(3, 2, "老师", 1);
        accessTreeBuildModels[3] = AccessTreeBuildModel.leafAccessTreeBuildModel(4, 3, "硕士", 1);
        accessTreeBuildModels[4] = AccessTreeBuildModel.innerAccessTreeBuildModel(5, 1, 4, 1);
        accessTreeBuildModels[5] = AccessTreeBuildModel.leafAccessTreeBuildModel(6, 1, "二班", 5);
        accessTreeBuildModels[6] = AccessTreeBuildModel.leafAccessTreeBuildModel(7, 2, "护士", 5);
        return AccessTree.build(publicKey, accessTreeBuildModels);
    }

    public static Pairing getPairing() {
        return PairingFactory.getPairing("params/curves/a.properties");
    }

    public static void pre(AccessTreeNode node) {
        System.out.println(node);
        for (AccessTreeNode child : node.getChildren()) {
            pre(child);
        }
    }

    public static void main(String[] args) {
        test();
    }

}
