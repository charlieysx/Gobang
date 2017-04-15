package com.yusoxn.gobang.bean.player;

import android.util.Log;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.utils.EvaluateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI玩家
 * <p>
 * Created by Yusxon on 17/4/9.
 */

public class ComputerPlayer2 extends BasePlayer {


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

    private List<ChessPoint> goodPos = new ArrayList<>();

    private boolean[][] tBoard = new boolean[15][15];

    public ComputerPlayer2(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public ChessPoint getChessPosition() {

        alphaBeta(1, 1, -999999999, 999999999, color);

        Log.i("Player", playerName + "下棋");
        Random random = new Random();
        int p = random.nextInt(goodPos.size());

        return goodPos.get(p);
    }

    @Override
    public boolean isAI() {
        return true;
    }

    private int alphaBeta(int d, int depth, int alpha, int beta, int tColor) {
        int value;
        List<ChessPoint> nullPosList = getNullPosList();
        if (depth == 0 || nullPosList.size() == 0 || isWin(tColor)) {
            return getMaxEvaluateValue();
        }

        int best = -999999998;

        for (ChessPoint chess : nullPosList) {
            move(chess.x, chess.y, tColor);
            value = -alphaBeta(d, depth - 1, -beta, -alpha, -tColor);
            backMove(chess.x, chess.y);
            if (value == best) {
                if(depth == d) {
                    goodPos.add(new ChessPoint(chess.x, chess.y, color));
                }
            }
            if (value > best) {
                best = value;
                if(depth == d) {
                    goodPos.clear();
                    goodPos.add(new ChessPoint(chess.x, chess.y, color));
                }
            }
            if (best > alpha) {
                alpha = best;
            }
            if (best >= beta) {
                break;
            }
        }

        return best;
    }

    /**
     * 在x, y处下一个tColor颜色的棋
     *
     * @param x
     * @param y
     * @param tColor
     */
    private void move(int x, int y, int tColor) {
        ChessPoint point = new ChessPoint(x, y, tColor);
        mBoard[x][y] = tColor;
        if (tColor == color) {
            mPosList.add(point);
        } else {
            ePosList.add(point);
        }
    }

    /**
     * 回退x， y位置上的棋子
     *
     * @param x
     * @param y
     */
    private void backMove(int x, int y) {
        int tColor = mBoard[x][y];
        mBoard[x][y] = ChessPoint.NULL;
        int pos = -1;
        for (ChessPoint chess : (tColor == color ? mPosList : ePosList)) {
            pos++;
            if (chess.x == x && chess.y == y) {
                break;
            }
        }
        if (pos > -1) {
            if (tColor == color) {
                mPosList.remove(pos);
            } else {
                ePosList.remove(pos);
            }
        }
    }

    /**
     * 获取当前的空位置
     *
     * @return
     */
    private List<ChessPoint> getNullPosList() {

        List<ChessPoint> nullPosList = new ArrayList<>();

        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                tBoard[i][j] = true;
            }
        }

        nullPosList.addAll(getNullPosList(mPosList));
        nullPosList.addAll(getNullPosList(ePosList));

        return nullPosList;
    }

    /**
     * 根据传入的棋子集合(已下的棋子)，返回以这些棋子为中心的边长为5矩形里的所有空位置
     *
     * @param chessPoints
     * @return
     */
    private List<ChessPoint> getNullPosList(List<ChessPoint> chessPoints) {
        List<ChessPoint> nullPosList = new ArrayList<>();

        if(null == chessPoints || chessPoints.size() == 0) {
            return nullPosList;
        }

        for (ChessPoint chess : chessPoints) {
            int sx = chess.x - 2;
            int sy = chess.y - 2;
            int ex = chess.x + 2;
            int ey = chess.y + 2;
            if (sx < 0) {
                sx = 0;
            }
            if (sy < 0) {
                sy = 0;
            }
            if (ex > 14) {
                ex = 14;
            }
            if (ey > 14) {
                ey = 14;
            }
            for (int i = sx; i <= ex; ++i) {
                for (int j = sy; j <= ey; ++j) {
                    if (mBoard[i][j] == ChessPoint.NULL && tBoard[i][j]) {
                        tBoard[i][j] = false;
                        nullPosList.add(new ChessPoint(i, j, 0));
                    }
                }
            }
        }

        return nullPosList;
    }

    /**
     * 获取当前局面的最大分数
     *
     * @return
     */
    private int getMaxEvaluateValue() {
        int maxEvaluateValue = 0;

        for (ChessPoint chess : mPosList) {
            int evaluateValue = getEvaluateValue(chess.x, chess.y, chess.color);
            if (evaluateValue > maxEvaluateValue) {
                maxEvaluateValue = evaluateValue;
            }
        }

        for (ChessPoint chess : ePosList) {
            int evaluateValue = getEvaluateValue(chess.x, chess.y, chess.color);
            if (evaluateValue > maxEvaluateValue) {
                maxEvaluateValue = evaluateValue;
            }
        }

        return maxEvaluateValue;
    }

    private int getEvaluateValue(int x, int y, int tColor) {
        int score = 0;

        for (int i = 0; i < 8; i += 2) {
            //计数用，检查每个方向，开始为1，也就是它本身
            int dis = 1;
            //表示两端的空格数
            int lrm = 2;
            //记录该线上能放tColor和已放tColor的棋子个数，直到不能放为止
            int cou = 1;
            //记录是否遇到空格了
            boolean space = false;
            //分别检测同一线上的两个方向
            int tx = x;
            int ty = y;
            for (int j = 0; j < 4; ++j) {
                tx += dir[i][0];
                ty += dir[i][1];
                if (tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    cou++;
                    if (mBoard[tx][ty] == tColor && !space) {
                        dis++;
                    } else if (mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        cou--;
                        break;
                    } else {
                        space = true;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            tx = x;
            ty = y;
            space = false;
            for (int j = 0; j < 4; ++j) {
                tx += dir[i + 1][0];
                ty += dir[i + 1][1];
                if (tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                    cou++;
                    if (mBoard[tx][ty] == tColor && !space) {
                        dis++;
                    } else if (mBoard[tx][ty] != ChessPoint.NULL) {
                        lrm--;
                        cou--;
                        break;
                    } else {
                        space = true;
                    }
                } else {
                    lrm--;
                    break;
                }
            }
            //如果小于5说明这条线怎么放都不能凑成5个，所以不得分
            if (cou >= 5) {
                int s = EvaluateUtil.getEvaluate(dis, lrm);
                score = s > score ? s : score;
            }
        }

        return score;
    }

    private boolean isWin(int tColor) {
        for(ChessPoint chess : (tColor == color ? mPosList : ePosList)) {
            if(isGameOver(chess)) {
                return true;
            }
        }
        return false;
    }

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

        return false;
    }
}
