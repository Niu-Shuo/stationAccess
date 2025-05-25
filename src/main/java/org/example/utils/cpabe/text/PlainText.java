package org.example.utils.cpabe.text;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import lombok.Data;
import org.example.utils.cpabe.parameter.PublicKey;

import java.nio.charset.StandardCharsets;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-22:55
 * @Description :
 */
@Data
public class PlainText {
    private Element messageValue;
    private String messageStr;

    public PlainText(String messageStr, PublicKey publicKey) {
        this(messageStr, publicKey.getPairingParameter().getG1());
    }

    private PlainText(String messageStr, Field G1) {
        this.messageStr = messageStr;
        this.messageValue = G1.newElementFromBytes(messageStr.getBytes(StandardCharsets.UTF_8)).getImmutable();
    }

}
