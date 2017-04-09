package com.yusoxn.gobang.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Yusxon on 17/4/9.
 */

public class ComputerPlayer extends Player {

    public ComputerPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public ChessPoint getChessPosition() {

        List<ChessPoint> points = new ArrayList<>();
        for(int i = 0;i < mBoard.length;++i) {
            for(int j = 0;j < mBoard[i].length;++j) {
                if(null == mBoard[i][j]) {
                    points.add(new ChessPoint(i, j, color));
                }
            }
        }
        Random random = new Random();
        int p = random.nextInt(points.size());

        return points.get(p);
    }

    @Override
    public boolean isAI() {
        return true;
    }
}
