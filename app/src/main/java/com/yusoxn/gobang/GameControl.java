package com.yusoxn.gobang;

import android.os.Message;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.bean.IPlayer;
import com.yusoxn.gobang.view.GameView;

import java.util.ArrayList;

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

    private IPlayer[] mPlayers;

    private OnGameListener mGameListener;
    private GameView mGameView;

    private int currentPlayer = 0;

    private ChessPoint firstClickPoint;

    /**
     * 存储棋子坐标
     */
    private ArrayList<ChessPoint> points = new ArrayList<>();

    /**
     * 存储该位置的棋子
     */
    private ChessPoint[][] board;

    private boolean gameOver = false;

    public GameControl(@NonNull GameView gameView) {
        mGameView = gameView;
        board = new ChessPoint[15][15];
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
        if (null == mGameView || null == mPlayers || null == mPlayers[0] || null == mPlayers[1] || null ==
                mGameListener) {
            return;
        }
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    //获取当前玩家接口
                    IPlayer player = mPlayers[currentPlayer];
                    ChessPoint point;
                    if (player.isAI()) {
                        player.setChessBoard(board);
                        point = player.getChessPosition();
                        board[point.x][point.y] = point;
                        mGameView.addChessPoint(point);
                        mGameView.setSelectPoint(point);
                        points.add(point);
                    } else {
                        point = player.getChessPosition();
                        mGameView.addChessPoint(point);
                    }
                    //发送消息绘制棋盘信息
                    mHandler.sendEmptyMessage(0);
                    //判断游戏是否结束
                    if(isGameOver(point)) {
                        break;
                    }
                    //进行异或运算，1变为0，0变为1(即切换玩家)
                    currentPlayer ^= 1;
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            mGameView.reDraw();
        }
    };

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
     * @param point return
     */
    private boolean isGameOver(ChessPoint point) {
        Log.i("GameControl", points.size() + "----");
        if(points.size() == 225) {
            mGameListener.onGameOver(null);
            return true;
        }
        return false;
    }

    public void calcRawXY(float x, float y) {
        if (!mPlayers[currentPlayer].isAI()) {
            int rawX = (int) (x / mGameView.getViewLineHeight());
            int rawY = (int) (y / mGameView.getViewLineHeight());
            mGameView.setSelectPoint(new ChessPoint(rawX, rawY, 0));
            if (null == firstClickPoint || firstClickPoint.x != rawX || firstClickPoint.y != rawY) {
                if (null == firstClickPoint) {
                    firstClickPoint = new ChessPoint();
                }
                firstClickPoint.x = rawX;
                firstClickPoint.y = rawY;
                Log.i("GameControl", "点击");
                mGameView.reDraw();
            } else if (null == board[rawX][rawY]) {
                board[rawX][rawY] = new ChessPoint(rawX, rawY, mPlayers[currentPlayer].getChessColor());
                mGameView.addChessPoint(board[rawX][rawY]);
                points.add(board[rawX][rawY]);
                mPlayers[currentPlayer].setChessPosition(board[rawX][rawY]);
            }
        }
    }
}
