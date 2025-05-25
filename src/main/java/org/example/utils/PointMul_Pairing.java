package org.example.utils;



import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @Author : Niushuo
 * @Date : 2023-09-22-10:41
 * @Description :
 */
public class PointMul_Pairing {

    @Test
    public void PointM(){
        Pairing bp = PairingFactory.getPairing("params/curves/a.properties");
        Field G1 = bp.getG1();
        Field Zr = bp.getZr();
        Element g = G1.newRandomElement().getImmutable();
        Element a = Zr.newRandomElement().getImmutable();
        Element b = Zr.newRandomElement().getImmutable();
        BigInteger privateKey = new BigInteger("1234567890123");
        long num_mul = 0;
        for(int i = 0; i < 100; ++i){
            long begin = System.currentTimeMillis();
            g.mul(privateKey);
            num_mul += System.currentTimeMillis() - begin;
        }
        System.out.println(num_mul);

        Element ga = g.powZn(a);
        Element gb = g.powZn(b);
        long num_pairing = 0;
        long end;
        for(int i = 0; i < 100; ++i){
            end = System.currentTimeMillis();
            bp.pairing(ga,gb);
            num_pairing += System.currentTimeMillis() - end;
        }

        System.out.println(num_pairing);


        // 计算 2^256 mod 115792089237316195423570985008687907853269984665640564039457584007908834671663
        BigInteger base = BigInteger.valueOf(999999999);
        BigInteger exponent = BigInteger.valueOf(256);
        BigInteger modulus = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663");
        long num_mod = 0;
        for(int i = 0; i < 100; ++i){
            long now = System.currentTimeMillis();
            base.modPow(exponent, modulus.add(BigInteger.valueOf(i)));
            num_mod += System.currentTimeMillis() - now;
        }
        System.out.println(num_mod);


    }
}
