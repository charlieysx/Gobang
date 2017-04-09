package com.yusoxn.gobang.interfaces;

import com.yusoxn.gobang.bean.ChessPoint;

/**
 * 玩家接口
 * <p>
 * Created by Yusxon on 17/3/4.
 */

public interface IPlayer {
    /**
     * 获取玩家名称
     *
     * @return
     */
    String getPlayerName();

    /**
     * 获取下棋位置
     *
     * @return
     */
    ChessPoint getChessPosition();

    /**
     * 是否是AI
     *
     * @return
     */
    boolean isAI();

    /**
     * 获取棋子颜色
     *
     * @return
     */
    int getChessColor();

    /**
     * 设置下棋位置
     */
    void setChessPosition(ChessPoint chessPosition);

    /**
     * 设置当前棋盘状态
     *
     * @param board
     */
    void setChessBoard(int[][] board);
}