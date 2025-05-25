package org.example.utils.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @Author : Niushuo
 * @Date : 2023-09-21-19:33
 * @Description :
 */
public class Hash {
    /**
     * @Description : 二进制转十六进制
     * @Author : Niushuo
     * @Date : 2023/9/21 19:36
     * @Param : [bytes]
     * @Return : java.lang.String
     **/
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    /**
     * @Description : sha-256
     * @Author : Niushuo
     * @Date : 2023/9/21 19:36
     * @Param : [bytes]
     * @Return : long
     **/
    public static long sha256(byte[] bytes) throws Exception {
        long t1 = System.nanoTime();

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes);
        String encodestr = byte2Hex(messageDigest.digest());//结果以16进制的字符串输出，256/8=32位

        long t2 = System.nanoTime();

        System.out.println("It changes into byte is : " + Arrays.toString(bytes));
        System.out.println("The length of bytes is " + 8 * bytes.length + " bits");
        System.out.println("The SHA-256 result is " + encodestr);
        System.out.println(t1);
        System.out.println(t2);
        System.out.println("The time it cost is " + (t2 - t1) + " ns");
        System.out.println("-------------------------------------------------------");
        return t2 - t1;
    }

    /**
     * @Description : 测试
     * @Author : Niushuo
     * @Date : 2023/9/21 19:36
     * @Param : [args]
     * @Return : void
     **/
    public static void main(String[] args) throws Exception {
        //使用String类型，便于打印
        String string = "7214083869398510205830034914356820453685029199381676443177786398";

        //转换为byte类型，byte长度表示其具有多少字节长度
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        System.out.println("The string is : " + string);
        System.out.println("The length of string is " + 8 * string.length() + " bits");

        System.out.println("-------------------------------------------------------");

        int num = 1000;
        long time = 0;
        for (int i = 0; i < num + 1; i++) {
            long t = sha256(bytes);
            if (i == 0) continue;
            time = time + t;
        }
        System.out.println("time is " + time / num +" ns");

    }
}
