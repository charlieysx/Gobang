package com.yusoxn.gobang.utils;

import android.util.SparseArray;

/**
 * 评估数据
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class EvaluateUtil {

    private static SparseArray<Integer[]> evaluates;
    public static int MAXEVALUATE = 9999999;

    public static void init() {
        evaluates = new SparseArray<>();

        evaluates.append(1, new Integer[]{0, 1, 5});
        evaluates.append(2, new Integer[]{0, 100, 300});
        evaluates.append(3, new Integer[]{0, 1200, 3000});
        evaluates.append(4, new Integer[]{0, 12000, 30000});
    }

    public static int getEvaluate(int dis, int lrm) {
        if(dis >= 5) {
            return MAXEVALUATE;
        }
        return evaluates.get(dis)[lrm];
    }
}
