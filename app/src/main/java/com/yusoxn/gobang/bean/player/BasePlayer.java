package com.yusoxn.gobang.bean.player;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.interfaces.IPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用玩家
 * <p>
 * Created by Yusxon on 17/4/8.
 */

public class BasePlayer implements IPlayer {

    protected String playerName;
    protected int color;
    protected ChessPoint tempChessPoint;
    protected int[][] mBoard;
    /**
     * 存放我方的棋子
     */
    protected List<ChessPoint> mPosList = new ArrayList<>();
    /**
     * 存放对方的棋子
     */
    protected List<ChessPoint> ePosList = new ArrayList<>();

    public BasePlayer(String playerName, int color) {
        this.playerName = playerName;
        this.color = color;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public ChessPoint getChessPosition() {
        return null;
    }

    @Override
    public boolean isAI() {
        return false;
    }

    @Override
    public int getChessColor() {
        return color;
    }

    @Override
    public void setChessPosition(ChessPoint chessPosition) {
        tempChessPoint = new ChessPoint(chessPosition.x, chessPosition.y, chessPosition.color);
    }

    @Override
    public void setChessBoard(List<ChessPoint> chessList) {
        mBoard = new int[15][15];
        mPosList.clear();
        ePosList.clear();
        for(ChessPoint chess : chessList) {
            mBoard[chess.x][chess.y] = chess.color;
            if(chess.color == color) {
                mPosList.add(new ChessPoint(chess.x, chess.y, color));
            } else {
                ePosList.add(new ChessPoint(chess.x, chess.y, chess.color));
            }
        }
    }

    @Override
    public void initChessBoard() {
        tempChessPoint = null;
    }
}
