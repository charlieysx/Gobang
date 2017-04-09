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
                    int score = getScore(i, j, color);
                    if(score == EvaluateUtil.MAXEVALUATE) {
                        return new ChessPoint(i, i, color);
                    }
                    if(maxScore < score) {
                        maxScore = score;
                        goodPos.clear();
                        goodPos.add(new ChessPoint(i, j, color));
                    }
                    score = getScore(i, j, -color);
                    if(score == EvaluateUtil.MAXEVALUATE) {
                        return new ChessPoint(i, i, color);
                    }
                    if(maxScore < score) {
                        maxScore = score;
                        goodPos.clear();
                        goodPos.add(new ChessPoint(i, j, color));
                    } else if(maxScore == score) {
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

    private int getScore(int x, int y, int tcolor) {
        int score = 0;

        for (int i = 0; i < 8; i += 2) {
            //计数用，检查每个方向，开始为1，也就是它本身
            int dis = 1;
            //表示两端的空格数
            int lrm = 2;
            //分别检测同一线上的两个方向
            int tx = x;
            int ty = y;
            for(int j = 0;j < 4;++j) {
                tx += dir[i][0];
                ty += dir[i][1];
                if(tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    if(mBoard[tx][ty] == tcolor) {
                        dis++;
                    } else if(mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        break;
                    } else {
                        break;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            tx = x;
            ty = y;
            for(int j = 0;j < 4;++j) {
                tx += dir[i + 1][0];
                ty += dir[i + 1][1];
                if(tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    if(mBoard[tx][ty] == tcolor) {
                        dis++;
                    } else if(mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        break;
                    } else {
                        break;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            score += EvaluateUtil.getEvaluate(dis, lrm);
        }

        return score;
    }
}
