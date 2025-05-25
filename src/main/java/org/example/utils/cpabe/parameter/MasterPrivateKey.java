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
public class MasterPrivateKey extends Key{
    /**
     * beta in Z_p
     */
    private Element beta;
    /**
     * g pow alpha
     */
    private Element g_alpha;
    /**
     * alpha
     */
    private Element alpha;


    private MasterPrivateKey(){

    }

    private MasterPrivateKey(PairingParameter parameter){
        super(parameter);
    }

    public static MasterPrivateKey build(PairingParameter parameter){
        MasterPrivateKey masterPrivateKey = new MasterPrivateKey(parameter);
        masterPrivateKey.setBeta(parameter.getZr().newRandomElement().getImmutable());
        masterPrivateKey.setAlpha(parameter.getZr().newRandomElement().getImmutable());
        masterPrivateKey.setG_alpha((parameter.getGenerator().powZn(masterPrivateKey.getAlpha())).getImmutable());
        return masterPrivateKey;
    }


}
