package org.example.utils;

import org.junit.Test;

/**
 * @Author : Niushuo
 * @Date : 2023-09-21-20:47
 * @Description :
 */
public class Communication {
    int allCommunication(int[] ints){
        int[] weight = {256, 128, 128, 128, 160};
        int ans = 0;
        for(int i = 0; i < ints.length; ++i)
            ans += weight[i] * ints[i];
        return ans;
    }

    @Test
    public void AllCommunication(){
        int[] ints = {3,3,2,1,3};
        System.out.println(allCommunication(ints));
    }
}
