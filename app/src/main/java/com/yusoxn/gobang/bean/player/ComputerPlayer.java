package com.yusoxn.gobang.bean.player;

import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.utils.EvaluateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI玩家
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class ComputerPlayer extends BasePlayer {


    /**
     * 可用于判断这些方向上的棋子
     */
    private int[][] dir = {
            //左上右下
            {-1, -1}, {1, 1},
            //上下
            {-1, 0}, {1, 0},
            //右上左下
            {-1, 1}, {1, -1},
            //左右
            {0, -1}, {0, 1}};

    public ComputerPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public ChessPoint getChessPosition() {

        List<ChessPoint> goodPos = new ArrayList<>();
        int maxScore = 0;
        for (int i = 0; i < mBoard.length; ++i) {
            for (int j = 0; j < mBoard[i].length; ++j) {
                if (mBoard[i][j] == ChessPoint.NULL) {
                    int score1 = getScore(i, j, 1);
                    if(score1 == EvaluateUtil.MAX_EVALUATE) {
                        return new ChessPoint(i, i, color);
                    }
                    if(maxScore < score1) {
                        maxScore = score1;
                        goodPos.clear();
                        goodPos.add(new ChessPoint(i, j, color));
                    } else if(maxScore == score1) {
                        goodPos.add(new ChessPoint(i, j, color));
                    }

                    int score2 = getScore(i, j, -1);

                    if(maxScore < score2) {
                        maxScore = score2;
                        goodPos.clear();
                        goodPos.add(new ChessPoint(i, j, color));
                    } else if(maxScore == score2) {
                        goodPos.add(new ChessPoint(i, j, color));
                    }
                }
            }
        }
        Log.i("Player", playerName + "下棋");
        Random random = new Random();
        int p = random.nextInt(goodPos.size());

        return goodPos.get(p);
    }

    @Override
    public boolean isAI() {
        return true;
    }

    private int getScore(int x, int y, int tColor) {
        int score = 0;

        for (int i = 0; i < 8; i += 2) {
            //计数用，检查每个方向，开始为1，也就是它本身
            int dis = 1;
            //表示两端的空格数
            int lrm = 2;
            //记录该线上能放tColor和已放tColor的棋子个数，直到不能放为止
            int cou = 1;
            //记录是否遇到空格了
            boolean space = false;
            //分别检测同一线上的两个方向
            int tx = x;
            int ty = y;
            for(int j = 0;j < 4;++j) {
                tx += dir[i][0];
                ty += dir[i][1];
                if(tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    cou++;
                    if(mBoard[tx][ty] == tColor * color && !space) {
                        dis++;
                    } else if(mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        cou--;
                        break;
                    } else {
                        space = true;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            tx = x;
            ty = y;
            space = false;
            for(int j = 0;j < 4;++j) {
                tx += dir[i + 1][0];
                ty += dir[i + 1][1];
                if(tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    cou++;
                    if(mBoard[tx][ty] == tColor * color && !space) {
                        dis++;
                    } else if(mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        cou--;
                        break;
                    } else {
                        space = true;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            //如果小于5说明这条线怎么放都不能凑成5个，所以不得分
            if(cou >= 5) {
//                int s = EvaluateUtil.getEvaluate(dis, lrm) + tColor * 10;
//                score = s > score ? s : score;
                score += EvaluateUtil.getEvaluate(dis, lrm) + tColor * 10;
            }
        }

        return score;
    }
}
