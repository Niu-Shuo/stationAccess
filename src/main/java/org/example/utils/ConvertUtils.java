package org.example.utils;

import java.nio.charset.Charset;

/**
 * @Author : Niushuo
 * @Date : 2023-08-31-21:11
 * @Description :
 */
public class ConvertUtils {
    /**
     * @Description : 比特数组去掉前后的0转字符串
     * @Author : Niushuo
     * @Date : 2023/9/4 17:52
     * @Param : [bytes]
     * @Return : java.lang.String
     **/
    public static String byteToStr(byte[] bytes){
        int startIndex = 0;
        int endIndex = bytes.length;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != 0){
                startIndex = i;
                break;
            }
        }

        for (int i = bytes.length - 1; i >= 0; i--) {
            if (bytes[i] != 0){
                endIndex = i;
                break;
            }
        }
        return new String(bytes,startIndex,endIndex - startIndex + 1, Charset.defaultCharset());
    }
}
