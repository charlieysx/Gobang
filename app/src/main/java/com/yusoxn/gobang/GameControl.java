package com.yusoxn.gobang;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.interfaces.IPlayer;
import com.yusoxn.gobang.view.GameView;

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
     * 已下的棋子数量
     */
    private int numOfChess = 0;

    /**
     * 存储该位置的棋子
     */
    private int[][] mBoard;

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

    /**
     * 标记是否已经开始游戏
     */
    private boolean starting = false;

    public GameControl(@NonNull GameView gameView) {
        mGameView = gameView;
        mBoard = new int[15][15];
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
     * 初始化棋盘的信息
     */
    private void initBoard() {
        numOfChess = 0;
        currentPlayer = 0;
        mGameView.resetChessBoard();
        if (null == firstClickPoint) {
            firstClickPoint = new ChessPoint();
        }
        firstClickPoint.x = -1;
        for(int i = 0;i < 15;++i) {
            for(int j = 0;j < 15;++j) {
                mBoard[i][j] = ChessPoint.NULL;
            }
        }
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
                while (starting) {
                    //获取当前玩家接口
                    IPlayer player = mPlayers[currentPlayer];
                    ChessPoint point;
                    if (player.isAI()) {
                        //先把最新棋盘状态信息发给AI
                        player.setChessBoard(mBoard);
                    }

                    point = player.getChessPosition();
                    mBoard[point.x][point.y] = point.color;
                    mGameView.addChessPoint(point);
                    mGameView.setSelectPoint(point);
                    numOfChess++;

                    //发送消息绘制棋盘信息
                    mHandler.sendEmptyMessage(0);
                    //判断游戏是否结束
                    if (isGameOver(point)) {
                        mGameListener.onGameOver(mPlayers[currentPlayer]);
                        starting = false;
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
        for (int i = 0; i < 8; i += 2) {
            //计数用，检查每个方向，开始为1，也就是它本身
            int dis = 1;
            //分别检测同一线上的两个方向
            int x = point.x;
            int y = point.y;
            while (true) {
                x += dir[i][0];
                y += dir[i][1];
                if(x >= 0 && x < 15 && y >= 0 && y < 15 && mBoard[x][y] == point.color) {
                    dis++;
                    if(dis == 5) {
                        return true;
                    }
                } else {
                    break;
                }
            }
            x = point.x;
            y = point.y;
            while (true) {
                x += dir[i + 1][0];
                y += dir[i + 1][1];
                if(x >= 0 && x < 15 && y >= 0 && y < 15 && mBoard[x][y] == point.color) {
                    dis++;
                    if(dis == 5) {
                        return true;
                    }
                } else {
                    break;
                }
            }
        }
        //如果棋子数为225，说明没有空格可以下了，表示和棋
        if (numOfChess == 225) {
            mGameListener.onGameOver(null);
            return true;
        }

        return false;
    }

    /**
     * 点击了该坐标
     *
     * @param x
     * @param y
     * @return
     */
    public boolean clickXY(float x, float y) {
        //如果未开始游戏则不需要处理
        if (!starting) {
            return false;
        }
        //如果是AI下棋也不需要处理
        if (!mPlayers[currentPlayer].isAI()) {
            //计算出棋盘中对应的下标
            int rawX = (int) (x / mGameView.getViewLineHeight());
            int rawY = (int) (y / mGameView.getViewLineHeight());
            //先设置选择框
            mGameView.setSelectPoint(new ChessPoint(rawX, rawY, 0));
            //如果是第一次点击该位置则记录起来
            if (firstClickPoint.x != rawX || firstClickPoint.y != rawY) {
                firstClickPoint.x = rawX;
                firstClickPoint.y = rawY;
                mGameView.reDraw();
            } else if (mBoard[rawX][rawY] == ChessPoint.NULL) {
                firstClickPoint.x = -1;
                mPlayers[currentPlayer].setChessPosition(new ChessPoint(rawX, rawY, mPlayers[currentPlayer]
                        .getChessColor()));
            }

            return true;
        }
        return false;
    }
}
