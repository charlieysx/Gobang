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

        evaluates.append(1, new Integer[]{0, 1, 110});
        evaluates.append(2, new Integer[]{0, 120, 1200});
        evaluates.append(3, new Integer[]{0, 1100, 2200});
        evaluates.append(4, new Integer[]{0, 2100, 90000});
    }

    public static int getEvaluate(int dis, int lrm) {
        if(dis >= 5) {
            return MAXEVALUATE;
        }
        return evaluates.get(dis)[lrm];
    }
}
