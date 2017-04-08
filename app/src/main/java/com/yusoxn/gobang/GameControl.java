package com.yusoxn.gobang;

import android.support.annotation.NonNull;
import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.bean.IPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏控制器
 * <p>
 * Created by Yusxon on 17/3/4.
 */

public class GameControl {

    /**
     * 游戏接口
     */
    public interface OnGameListener {
        /**
         * 游戏结束
         *
         * @param winner 赢的玩家
         */
        void onGameOver(IPlayer winner);
    }

    /**
     * 绘图接口
     */
    public interface OnViewListener {
        /**
         * 添加棋子
         *
         * @param chessPoint
         */
        void addChessPoint(ChessPoint chessPoint);

        /**
         * 批量添加棋子
         *
         * @param chessPoints
         */
        void addChessPoints(List<ChessPoint> chessPoints);

        /**
         * 设置选择框位置
         *
         * @param selectPoint
         */
        void setSelectPoint(ChessPoint selectPoint);

        /**
         * 重画，在添加棋子等信息后需要调用该方法绘制
         */
        void reDraw();

        /**
         * 获取行高
         *
         * @return
         */
        float getViewLineHeight();
    }

    private IPlayer[] mPlayers;

    private OnGameListener mGameListener;
    private OnViewListener mViewListener;

    private int currentPlayer = 0;

    /**
     * 存储棋子坐标
     */
    private ArrayList<ChessPoint> points = new ArrayList<>();

    public GameControl(@NonNull OnViewListener listener) {
        mViewListener = listener;
    }

    /**
     * 设置玩家
     *
     * @param playerFirst  第一个玩家(先手)
     * @param playerSecond 第二个玩家
     * @return
     */
    public GameControl setPlayers(@NonNull IPlayer playerFirst, @NonNull IPlayer playerSecond) {
        mPlayers = new IPlayer[2];
        mPlayers[0] = playerFirst;
        mPlayers[1] = playerSecond;
        return this;
    }

    /**
     * 设置游戏监听器
     *
     * @param listener
     * @return
     */
    public GameControl setOnGameListener(OnGameListener listener) {
        mGameListener = listener;
        return this;
    }

    /**
     * 开始游戏
     */
    public void start() {
        if (null == mViewListener || null == mPlayers || null == mPlayers[0] || null == mPlayers[1] || null ==
                mGameListener) {
            return;
        }
        IPlayer player = mPlayers[currentPlayer];
        if (player.isAI()) {
            ChessPoint point = player.getChessPosition();
            mViewListener.addChessPoint(point);
            mViewListener.setSelectPoint(point);
            checkChess(point);
            //进行异或运算，1变为0，0变为1
            currentPlayer ^= 1;
        } else {
            ChessPoint point = player.getChessPosition();
            mViewListener.setSelectPoint(point);
        }
        mViewListener.reDraw();
    }

    /**
     * 重新开始游戏
     */
    public void resume() {
        currentPlayer = 0;
        points.clear();
    }

    /**
     * 悔棋
     */
    public void regret() {
        //悔棋需要移除两步
        points.remove(points.size());
        points.remove(points.size());
    }

    /**
     * 检查下了该棋子后是否游戏结束
     *
     * @param point
     */
    private void checkChess(ChessPoint point) {
        mGameListener.onGameOver(mPlayers[currentPlayer]);
    }

    public void calcRawXY(float x, float y) {
        int rawX = (int) (x / mViewListener.getViewLineHeight());
        int rawY = (int) (y / mViewListener.getViewLineHeight());
        Log.i("onTouch", x + "--" + y);
        Log.i("onTouch", rawX + "--" + rawY);
        mViewListener.addChessPoint(new ChessPoint(rawX, rawY, ChessPoint.BLACK));
        mViewListener.setSelectPoint(new ChessPoint(rawX, rawY, 0));
        mViewListener.reDraw();
    }
}
