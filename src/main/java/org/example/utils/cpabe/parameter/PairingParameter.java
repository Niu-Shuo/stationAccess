package org.example.utils.cpabe.parameter;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;
import lombok.ToString;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:41
 * @Description :
 */
@Data
@ToString
public class PairingParameter {
    private Pairing pairing;
    private Field G0;
    private Field G1;
    private Field Zr;
    private Element generator;

    private PairingParameter() {

    }


    public static PairingParameter getInstance() {
        PairingParameter pairingParameter = new PairingParameter();
        Pairing pairing = PairingFactory.getPairing("params/curves/a.properties");
        pairingParameter.setPairing(pairing);
        pairingParameter.setG0(pairing.getG1());
        pairingParameter.setG1(pairing.getGT());
        pairingParameter.setZr(pairing.getZr());
        pairingParameter.setGenerator(pairingParameter.getG0().newRandomElement().getImmutable());
        return pairingParameter;
    }


}
