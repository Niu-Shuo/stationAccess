package org.example.utils;

import org.junit.Test;

/**
 * @Author : Niushuo
 * @Date : 2023-09-21-19:59
 * @Description :
 */
public class Computation {

    /**
     * @Description : 计算总时间
     * @Author : Niushuo
     * @Date : 2023/9/21 20:26
     * @Param : [doubles]
     * @Return : double
     **/
    double allComputation(double[] doubles){
        double[] weight = {3.67, 0.015, 0.02, 2.38, 1.53};
        double ans = 0;
        for(int i = 0; i < 5; ++i){
            ans += doubles[i] * weight[i];
        }
        return ans;
    }

    @Test
    public void AllComputation(){
        double[] doubles = {0,8,3,7,4};
        System.out.println(allComputation(doubles));
    }
}
