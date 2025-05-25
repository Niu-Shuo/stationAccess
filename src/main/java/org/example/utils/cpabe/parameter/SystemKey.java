package org.example.utils.cpabe.parameter;

import lombok.Data;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:41
 * @Description :
 */
@Data
public class SystemKey {
    private PublicKey publicKey;
    private MasterPrivateKey masterPrivateKey;

    private SystemKey() {

    }

    public static SystemKey build() {
        SystemKey systemKey = new SystemKey();
        PairingParameter pairingParameter = PairingParameter.getInstance();
        MasterPrivateKey masterPrivateKey = MasterPrivateKey.build(pairingParameter);
        PublicKey publicKey = PublicKey.build(pairingParameter, masterPrivateKey);
        systemKey.setPublicKey(publicKey);
        systemKey.setMasterPrivateKey(masterPrivateKey);
        return systemKey;
    }

}
