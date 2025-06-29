package org.example.utils.cpabe.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import org.example.utils.cpabe.attribute.Attribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:41
 * @Description :
 */
@Data
public class UserPrivateKey {
    private Element D;
    private PairingParameter pairingParameter;
    public Map<Attribute,Element> D_j_map;
    public Map<Attribute,Element> D_j_pie_map;
    private List<Attribute> userAttributes;

    private UserPrivateKey() {
        D_j_map = new HashMap<>();
        D_j_pie_map = new HashMap<>();
    }


    public void putDj(Attribute attribute,Element D_j){
        D_j_map.put(attribute,D_j);
    }

    public void putDjPie(Attribute attribute,Element D_j_pie){
        D_j_pie_map.put(attribute,D_j_pie);
    }


    public Element getDj(Attribute attribute){
        for (Attribute attribute1 : D_j_map.keySet()) {
            if (attribute1.equals(attribute)){
                return D_j_map.get(attribute1);
            }
        }
        return null;
    }

    public Element getDjPie(Attribute attribute){
        for (Attribute attribute1 : D_j_pie_map.keySet()) {
            if (attribute1.equals(attribute)){
                return D_j_pie_map.get(attribute1);
            }
        }
        return null;
    }

    public static UserPrivateKey build(MasterPrivateKey masterPrivateKey, List<Attribute> attributes){
        UserPrivateKey userPrivateKey = new UserPrivateKey();
        userPrivateKey.setPairingParameter(masterPrivateKey.getPairingParameter());
        userPrivateKey.setUserAttributes(attributes);
        Element r = masterPrivateKey.getPairingParameter().getZr().newRandomElement().getImmutable();
        Element alpha = masterPrivateKey.getAlpha();
        Element beta = masterPrivateKey.getBeta();
        Element g = masterPrivateKey.getPairingParameter().getGenerator();
        Element D = g.powZn((alpha.add(r)).div(beta)).getImmutable();
        userPrivateKey.setD(D);
        for (Attribute attribute : attributes){
            Element r_j = masterPrivateKey.getPairingParameter().getZr().newRandomElement().getImmutable();
            Element D_j = g.powZn(r).mul(masterPrivateKey.hash(attribute.getAttributeValue()).powZn(r_j)).getImmutable();
            Element D_j_pie = g.powZn(r_j).getImmutable();
            userPrivateKey.putDjPie(attribute,D_j_pie);
            userPrivateKey.putDj(attribute,D_j);
        }
        return userPrivateKey;
    }


}
