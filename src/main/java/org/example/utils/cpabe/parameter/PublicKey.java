package org.example.utils.cpabe.parameter;

import it.unisa.dia.gas.jpbc.Element;
import lombok.Data;
import lombok.ToString;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:41
 * @Description :
 */
@Data
@ToString
public class PublicKey extends Key {
    /**
     * h = g ^ alpha
     */
    private Element h;
    /**
     * 公开的双线性对
     */
    private Element egg_a;
    /**
     * f = g ^ (1 / beta)
     */
    private Element f;

    private PublicKey() {

    }

    private PublicKey(PairingParameter parameter) {
        super(parameter);
    }


    public static PublicKey build(PairingParameter parameter, MasterPrivateKey msk) {
        PublicKey publicKey = new PublicKey(parameter);
        publicKey.setH(parameter.getGenerator().powZn(msk.getBeta()).getImmutable());
        publicKey.setF(parameter.getGenerator().powZn(msk.getBeta().invert()).getImmutable());
        publicKey.setEgg_a((parameter.getPairing().pairing(parameter.getGenerator(), parameter.getGenerator()).mulZn(msk.getAlpha())).getImmutable());
        return publicKey;
    }

}
