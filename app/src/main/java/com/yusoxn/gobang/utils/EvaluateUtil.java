package com.yusoxn.gobang.utils;

import android.util.SparseArray;

/**
 * 评估数据
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class EvaluateUtil {

    /**
     * 存放分数
     */
    private static SparseArray<Integer[]> evaluates;
    /**
     * 最大的分数(五子连珠可获得该分数)
     */
    public static int MAXEVALUATE = 9999999;

    public static void init() {
        evaluates = new SparseArray<>();

        //key值(1, 2, 3, 4)代表该线连续的同色棋子个数
        //value值为数组，数组长度为3，表示两端的空格数
        //比如数组中所有的0下标对应的分数都是0，因为下标0表示两端没有空格，即没有下子的地方，无论如何都构不成5子，所以不得分
        //设置的分数值不是很准确
        evaluates.append(1, new Integer[]{0, 1, 110});
        evaluates.append(2, new Integer[]{0, 120, 1200});
        evaluates.append(3, new Integer[]{0, 1100, 2200});
        evaluates.append(4, new Integer[]{0, 2100, 90000});
    }

    /**
     * 获取分数
     *
     * @param dis
     * @param lrm
     * @return
     */
    public static int getEvaluate(int dis, int lrm) {
        if (dis >= 5) {
            return MAXEVALUATE;
        }
        return evaluates.get(dis)[lrm];
    }
}
