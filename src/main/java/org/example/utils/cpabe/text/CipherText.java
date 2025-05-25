package org.example.utils.cpabe.text;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.ToString;
import org.example.utils.JsonSerializable;
import org.example.utils.cpabe.attribute.Attribute;
import org.example.utils.cpabe.structure.AccessTree;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:55
 * @Description :
 */
@Data
@ToString
public class CipherText {
    //g1
    private Element c_wave;
    //g0
    private Element c;
    //g0
    private Map<Attribute,Element> c_y_map;
    //g0
    private Map<Attribute,Element> c_y_pie_map;
    //访问树
    private AccessTree accessTree;

    public void putCy(Attribute attribute, Element cy){
        c_y_map.put(attribute,cy);
    }

    public void putCyPie(Attribute attribute,Element cy_pie){
        c_y_pie_map.put(attribute,cy_pie);
    }

    public Element getCy(Attribute attribute){
        return c_y_map.get(attribute);
    }

    public Element getCyPie(Attribute attribute){
        return c_y_pie_map.get(attribute);
    }

    public CipherText() {
        c_y_map = new HashMap<>();
        c_y_pie_map = new HashMap<>();
    }

    /**
     * @Description : 将CipherText类转成String类
     * @Author : Niushuo
     * @Date : 2023/9/4 22:37
     * @Param : [G1]
     * @Return : java.lang.String
     **/
//    public String Cipher2String() throws IOException {
//        String targetString;
//
//        //取出Element元素
//        Element c_wave = this.getC_wave();
//        Element c = this.getC();
//        Map<Attribute,Element> cYMap = this.getC_y_map();
//        Map<Attribute,Element> cYPieMap = this.getC_y_pie_map();
//        AccessTree accessTree = this.getAccessTree();
//        System.out.println("0" + c_wave);
//        //转byte，编码
//        byte[] c_wave_En = Base64.getEncoder().encode(c_wave.toBytes());
//        byte[] c_En = Base64.getEncoder().encode(c.toBytes());
//        byte[] cYMap_En = Base64.getEncoder().encode(JsonSerializable.serializableForMap(cYMap,"/src/main/resources/cYMap.json").getBytes());
//        byte[] cYPieMap_En = Base64.getEncoder().encode(JsonSerializable.serializableForMap(cYPieMap).getBytes());
//        //转string
//        String c_waveStr = new String(c_wave_En);
//        String c_Str = new String(c_En);
//        String cYMapStr = new String(cYMap_En);
//        String cYPieMapStr = new String(cYPieMap_En);
//        String accessTreeStr = accessTree.toString();
//        System.out.println("2" + c_waveStr);
//
//        targetString = c_waveStr + "," + c_Str + "," + cYMapStr + "," + cYPieMapStr + "," + accessTreeStr;
//        return targetString;
//    }

//    public CipherText String2Cipher(Field G1,String string){
//        CipherText targetCipher;
//        String[] split = string.split(",");
//        //string转byte
//        byte[] tempEn1 = c_waveStr.getBytes();
//        System.out.println(Arrays.equals(tempEn, tempEn1));
//        //byte，解码
//        byte[] tempDe = Base64.getDecoder().decode(tempEn);
//        System.out.println(tempDe);
//        //byte转element
//        Element x = G1.newElementFromBytes(tempDe);
//        return targetCipher;
//    }
}
