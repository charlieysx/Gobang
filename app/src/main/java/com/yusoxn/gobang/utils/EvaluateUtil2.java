package com.yusoxn.gobang.utils;

import android.util.Log;
import android.util.SparseArray;

/**
 * 评估数据
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class EvaluateUtil2 {

    /**
     * 连5
     */
    private static final int FIVE = 1000000;
    /**
     * 活4
     */
    private static final int _FOUR_ = 500000;
    /**
     * 双眠4
     */
    private static final int FOUR_FOUR = 500000;
    /**
     * 活3眠4
     */
    private static final int _THREE_FOUR = 500000;
    /**
     * 双活3
     */
    private static final int _THREE_THREE_ = 100000;
    /**
     * 活3
     */
    private static final int _THREE_ = 50000;
    /**
     * 眠4
     */
    private static final int _FOUR = 10000;
    /**
     * 3活2
     */
    private static final int _TWO_TWO_TWO_ = 500;
    /**
     * 双活2
     */
    private static final int _TWO_TWO_ = 200;
    /**
     * 双眠3
     */
    private static final int THREE_THREE = 100;
    /**
     * 活2
     */
    private static final int _TWO_ = 50;
    /**
     * 眠3
     */
    private static final int _THREE = 30;
    /**
     * 眠2
     */
    private static final int _TWO = 20;
    /**
     * 其他
     */
    private static final int OTHER = 10;
    /**
     * 怎么也拼不成5个
     */
    private static final int NO_FIVE = 0;

    public static final int MAX = FIVE * 10;
    public static final int MIN = -MAX;

    /**
     * 用于存放棋子个数及两端空格数
     */
    private static SparseArray<Integer[]> count = new SparseArray<>();

    /**
     * 获取等级时用到
     */
    private static int[] rank = {FIVE, _FOUR_, FOUR_FOUR, _THREE_FOUR, _THREE_THREE_, _THREE_, _FOUR, _TWO_TWO_TWO_,
            _TWO_TWO_, THREE_THREE, _TWO_, _THREE, _TWO, OTHER, NO_FIVE};

    public static void init() {
        count.clear();
        for (int i = 0; i < 4; ++i) {
            count.append(i + 1, new Integer[]{0, 0, 0});
        }
    }

    /**
     * 获取分数
     *
     * @param consecutive 连续的相同棋子个数
     * @param spaces      对应的连续棋子两端的空格数
     * @return
     */
    public static synchronized int getEvaluate(int[] consecutive, int[] spaces) {
        init();
        boolean isAdd = false;
        for (int i = 0; i < 4; ++i) {
            //如果连续的棋子为0，说明这一行怎么也拼不成5个，所以弃掉
            if (consecutive[i] == 0) {
                continue;
            }
            if (consecutive[i] >= 5) {
                //连5，不用判断了，直接返回
                Log.i("EvaluateUtil2", "连5，不用判断了，直接返回");
                return FIVE;
            }
            //小于5，两边却没有空格，则是没用的位置，直接弃掉
            if (spaces[i] == 0) {
                continue;
            }
            isAdd = true;
            count.get(consecutive[i])[spaces[i]]++;
        }
        if (!isAdd) {
            //没有添加过，说明传过来的参数都被弃掉了，所以这个位置没用，返回0分
            Log.i("EvaluateUtil2", "没有添加过");
            return NO_FIVE;
        }
        if (count.get(4)[2] > 0) {
            //活四
            Log.i("EvaluateUtil2", "活四");
            return _FOUR_;
        } else if (count.get(4)[1] > 1) {
            //双眠四
            Log.i("EvaluateUtil2", "双眠四");
            return FOUR_FOUR;
        } else if (count.get(4)[1] > 0 && count.get(3)[2] > 0) {
            //活三眠四
            Log.i("EvaluateUtil2", "活三眠四");
            return _THREE_FOUR;
        } else if (count.get(3)[2] > 1) {
            //双活三
            Log.i("EvaluateUtil2", "双活三");
            return _THREE_THREE_;
        } else if (count.get(3)[2] > 0) {
            //活三
            Log.i("EvaluateUtil2", "活三");
            return _THREE_;
        } else if (count.get(4)[1] > 0) {
            //眠四
            Log.i("EvaluateUtil2", "眠四");
            return _FOUR;
        } else if (count.get(2)[2] > 2) {
            //三活二
            Log.i("EvaluateUtil2", "三活二");
            return _TWO_TWO_TWO_;
        } else if (count.get(2)[2] > 1) {
            //双活二
            Log.i("EvaluateUtil2", "双活二");
            return _TWO_TWO_;
        } else if (count.get(3)[1] > 1) {
            //双眠三
            Log.i("EvaluateUtil2", "双眠三");
            return THREE_THREE;
        } else if (count.get(2)[2] > 0) {
            //活二
            Log.i("EvaluateUtil2", "活二");
            return _TWO_;
        } else if (count.get(3)[1] > 0) {
            //眠三
            Log.i("EvaluateUtil2", "眠三");
            return _THREE;
        } else if (count.get(2)[1] > 0) {
            //眠二
            Log.i("EvaluateUtil2", "眠二");
            return _TWO;
        }
        //其他情况
        return OTHER;
    }

    /**
     * 根据该位置电脑的评分和human的评分获取位置的等级
     *
     * @return
     */
    public static synchronized int getRank(int aiScore, int humanScore) {

        for (int i = 0; i < rank.length; ++i) {
            if (aiScore >= rank[i]) {
                return 2 * i;
            }
            if (humanScore >= rank[i]) {
                return 2 * i + 1;
            }
        }
        return (rank.length - 1) * 2 + 1;
    }

    /**
     * 获取等级数
     *
     * @return
     */
    public static int getRankNum() {
        return rank.length;
    }
}
