package com.yusoxn.gobang.bean.player;

import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI玩家
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class ComputerPlayer extends BasePlayer {

    public ComputerPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public ChessPoint getChessPosition() {

        List<ChessPoint> points = new ArrayList<>();
        for (int i = 0; i < mBoard.length; ++i) {
            for (int j = 0; j < mBoard[i].length; ++j) {
                if (mBoard[i][j] == ChessPoint.NULL) {
                    points.add(new ChessPoint(i, j, color));
                }
            }
        }
        Log.i("Player", playerName + "下棋");
        Random random = new Random();
        int p = random.nextInt(points.size());

        return points.get(p);
    }

    @Override
    public boolean isAI() {
        return true;
    }

    private int getScore() {
        int score = 0;



        return score;
    }
}
