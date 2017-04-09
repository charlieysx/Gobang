package com.yusoxn.gobang;

import android.os.Handler;
import android.os.Message;
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

    /**
     * 记录首次点击的位置(防止点错，需要点击同一位置两次才能下子)
     */
    private ChessPoint firstClickPoint;

    /**
     * 存储棋子坐标
     */
    private ArrayList<ChessPoint> points = new ArrayList<>();

    /**
     * 存储该位置的棋子
     */
    private ChessPoint[][] mBoard;

    private int[][] dir = {{-1, -1}, {1, 1}, {-1, 0}, {1, 0}, {-1, 1}, {1, -1}, {0, -1}, {0, 1}};

    private boolean starting = false;

    public GameControl(@NonNull GameView gameView) {
        mGameView = gameView;
        mBoard = new ChessPoint[15][15];
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

    private void initBoard() {
        mGameView.resetChessBoard();
        if (null == firstClickPoint) {
            firstClickPoint = new ChessPoint();
        }
        firstClickPoint.x = -1;
        for(ChessPoint point : points) {
            mBoard[point.x][point.y] = null;
        }
        points.clear();
    }

    /**
     * 开始游戏
     */
    public void start() {
        if (null == mGameView || null == mPlayers || null == mPlayers[0] || null == mPlayers[1] || null ==
                mGameListener) {
            return;
        }
        initBoard();
        starting = true;
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    //获取当前玩家接口
                    IPlayer player = mPlayers[currentPlayer];
                    ChessPoint point;
                    if (player.isAI()) {
                        player.setChessBoard(mBoard);
                        point = player.getChessPosition();
                        mBoard[point.x][point.y] = point;
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
                    if (isGameOver(point)) {
                        mGameListener.onGameOver(mPlayers[currentPlayer]);
                        starting = false;
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
     * 检查下了该棋子后是否游戏结束
     *
     * @param point return
     */
    private boolean isGameOver(ChessPoint point) {
        if (points.size() == 225) {
            mGameListener.onGameOver(null);
            return true;
        }
        Log.i("isGameOver", point.x + "---" + point.y + "---" + point.color);
        int dis;
        for (int i = 0; i < 8; i += 2) {
            dis = 1;
            int[] x = {point.x, point.x};
            int[] y = {point.y, point.y};
            while (true) {
                int f = 0;
                for (int j = 0; j < 2; ++j) {
                    x[j] += dir[i + j][0];
                    y[j] += dir[i + j][1];
                    if (x[j] >= 0 && x[j] < 15 && y[j] >= 0 && y[j] < 15 && mBoard[x[j]][y[j]] != null &&
                            mBoard[x[j]][y[j]].color == point.color) {
                        dis++;
                        if(dis >= 5) {
                            return true;
                        }
                    } else {
                        f += 1;
                    }
                }
                if (f == 2) {
                    break;
                }
            }
            Log.i("isGameOver", dis + "---" + i);
            if (dis >= 5) {
                return true;
            }
        }

        return false;
    }

    public boolean calcRawXY(float x, float y) {
        if (!starting) {
            return false;
        }
        if (!mPlayers[currentPlayer].isAI()) {
            int rawX = (int) (x / mGameView.getViewLineHeight());
            int rawY = (int) (y / mGameView.getViewLineHeight());
            mGameView.setSelectPoint(new ChessPoint(rawX, rawY, 0));
            if (firstClickPoint.x != rawX || firstClickPoint.y != rawY) {
                firstClickPoint.x = rawX;
                firstClickPoint.y = rawY;
                Log.i("GameControl", "点击");
                mGameView.reDraw();
            } else if (null == mBoard[rawX][rawY]) {
                mBoard[rawX][rawY] = new ChessPoint(rawX, rawY, mPlayers[currentPlayer].getChessColor());
                mGameView.addChessPoint(mBoard[rawX][rawY]);
                points.add(mBoard[rawX][rawY]);
                mPlayers[currentPlayer].setChessPosition(mBoard[rawX][rawY]);
            }
            return true;
        }
        return false;
    }
}
