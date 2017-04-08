package com.yusoxn.gobang.bean;

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
}
