package com.yusoxn.gobang.bean.player;

import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;

/**
 * 人类玩家
 * <p>
 * Created by Yusxon on 17/4/8.
 */

public class HumanPlayer extends BasePlayer {


    public HumanPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public ChessPoint getChessPosition() {

        while (true) {
            if(null != tempChessPoint) {
                break;
            }
        }
        Log.i("Player", playerName + "下棋");
        ChessPoint pos = new ChessPoint(tempChessPoint.x, tempChessPoint.y, color);
        tempChessPoint = null;

        return pos;
    }
}
