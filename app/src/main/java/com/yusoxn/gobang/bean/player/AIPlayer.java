package com.yusoxn.gobang.bean.player;

import android.util.Log;
import android.util.SparseArray;

import com.yusoxn.gobang.bean.ChessPoint;
import com.yusoxn.gobang.utils.EvaluateUtil2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Yusxon on 17/4/12.
 */

public class AIPlayer extends BasePlayer {


    /**
     * 可用于判断这些方向上的棋子
     */
    private int[][] dir = {
            //左上右下
            {-1, -1}, {1, 1},
            //左右
            {-1, 0}, {1, 0},
            //左下右上
            {-1, 1}, {1, -1},
            //上下
            {0, -1}, {0, 1}};

    private List<ChessPoint> goodPos = new ArrayList<>();

    private int[][] aiScore = new int[15][15];
    private int[][] humanScore = new int[15][15];

    public AIPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public void initChessBoard() {
        super.initChessBoard();
        goodPos.clear();
        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                aiScore[i][j] = humanScore[i][j] = 0;
            }
        }
    }

    @Override
    public ChessPoint getChessPosition() {
        if(mPosList.size() > 0) {
            //这是我方刚下的棋子
            ChessPoint chess = mPosList.get(0);
            updateScore(chess.x, chess.y);
        }
        if (ePosList.size() > 0) {
            //这是对方刚下的棋子
            ChessPoint chess = ePosList.get(0);
            updateScore(chess.x, chess.y);
        }
        goodPos.clear();
        goodPos.addAll(getEmptyPosList(color));

        Log.i("Player", playerName + "下棋");
        Random random = new Random();
        int p = random.nextInt(goodPos.size());
        goodPos.get(p).color = color;

        return goodPos.get(p);
    }

    @Override
    public boolean isAI() {
        return true;
    }

    /**
     * a-b剪枝算法
     *
     * @param depth
     * @param alpha
     * @param beta
     * @param tColor
     * @return
     */
    private int alphaBeta(int depth, int alpha, int beta, int tColor) {
        return 0;
    }

    /**
     * 获取对tColor角色来说较有利的空位子
     *
     * @param tColor
     * @return
     */
    private List<ChessPoint> getEmptyPosList(int tColor) {
        List<ChessPoint> emptyPosList = new ArrayList<>();
        SparseArray<List<ChessPoint>> rankPosList = new SparseArray<>();
        int rankNum = EvaluateUtil2.getRankNum();

        for (int i = 0; i < rankNum; ++i) {
            rankPosList.append(2 * i, new ArrayList<ChessPoint>());
            rankPosList.append(2 * i + 1, new ArrayList<ChessPoint>());
        }

        for (int i = 0; i < 15; ++i) {
            for (int j = 0; j < 15; ++j) {
                if (mBoard[i][j] == ChessPoint.NULL && hasChessInRectangle(i, j, 2)) {
                    int tAI = aiScore[i][j];
                    int tHuman = humanScore[i][j];
                    //角色互换,以获取对自己有利的位置
                    if (tColor != color) {
                        tAI += tHuman;
                        tHuman = tAI - tHuman;
                        tAI -= tHuman;
                    }
                    int rank = EvaluateUtil2.getRank(tAI, tHuman);
                    if (rank == 0) {
                        emptyPosList.add(new ChessPoint(i, j, 0));
                        return emptyPosList;
                    }
                    rankPosList.get(rank).add(new ChessPoint(i, j, 0));
                }
            }
        }
        if (rankPosList.get(1).size() > 0) {
            emptyPosList.add(rankPosList.get(1).get(0));
        } else {
            for (int i = 2; i < rankPosList.size(); ++i) {
                List<ChessPoint> chessPoints = rankPosList.get(i);
                for (int j = 0; j < chessPoints.size(); ++j) {
                    emptyPosList.add(chessPoints.get(j));
                }
                if (emptyPosList.size() > 0) {
                    return emptyPosList;
                }
            }
        }

        return emptyPosList;
    }

    /**
     * 判断以x， y为中心，2n为边长的矩形中是否有棋子
     * 如果没有棋子则x，y这个点不需要处理，因为得分为0
     *
     * @param x
     * @param y
     * @param n
     * @return
     */
    private boolean hasChessInRectangle(int x, int y, int n) {
        int sx = x - n;
        int sy = y - n;
        int ex = x + n;
        int ey = y + n;
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
                if (mBoard[i][j] != ChessPoint.NULL) {
                    return true;
                }
            }
        }
        return false;
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
        updateScore(x, y);
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
        updateScore(x, y);
    }

    /**
     * 更新受x，y位置影响的位置的分数
     *
     * @param x
     * @param y
     */
    private void updateScore(int x, int y) {
        Log.i("updateScore", x + ".." + y);

        for (int i = 0; i < 8; i += 2) {
            //分别检测同一线上的两个方向
            for (int k = 0; k < 2; ++k) {
                int tx = x;
                int ty = y;
                //一个方向检测4个棋子
                for (int j = 0; j < 4; ++j) {
                    tx += dir[i + k][0];
                    ty += dir[i + k][1];
                    //碰到边界则不需要再向前更新。
                    if (tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                        if (mBoard[tx][ty] == ChessPoint.NULL) {
                            update(tx, ty);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /**
     * 更新该点ai的分数和human的分数
     *
     * @param x
     * @param y
     */
    private void update(int x, int y) {
        aiScore[x][y] = getScore(x, y, color);
        humanScore[x][y] = getScore(x, y, -color);
    }

    /**
     * 获取该点的分数值
     *
     * @param x
     * @param y
     * @param tColor
     * @return
     */
    private synchronized int getScore(int x, int y, int tColor) {
        int[] consecutive = new int[4];
        int[] spaces = new int[4];

        for (int i = 0; i < 8; i += 2) {
            //两端的空格数
            int lrm = 2;
            //这条线上最多可以连成几个同色棋子
            int cou = 1;
            //以x，y为中心连续的同色棋子个数
            int dis = 1;
            //分别检测同一线上的两个方向
            for (int k = 0; k < 2; ++k) {
                int tx = x;
                int ty = y;
                //是否遇到空格
                boolean space = false;
                //一个方向检测4个棋子
                for (int j = 0; j < 4; ++j) {
                    tx += dir[i + k][0];
                    ty += dir[i + k][1];

                    if (tx >= 0 && tx < 15 && ty >= 0 && ty < 15) {
                        if(mBoard[tx][ty] == tColor) {
                            cou++;
                            if(!space) {
                                dis++;
                            }
                        } else if(mBoard[tx][ty] == -tColor) {
                            if(!space) {
                                lrm--;
                            }
                            break;
                        } else {
                            cou++;
                            space = true;
                        }
                        if((tx == 0 || tx == 14) && i + k != 3) {
                            if(!space) {
                                lrm--;
                            }
                            break;
                        }
                        if((ty == 0 || ty == 14) && i + k != 1) {
                            if(!space) {
                                lrm--;
                            }
                            break;
                        }
                    } else {
                        if(!space) {
                            lrm--;
                        }
                        break;
                    }
                }
            }
            //如果小于5说明这条线怎么放都不能凑成5个，所以不得分
            if (cou >= 5) {
                consecutive[i / 2] = dis;
                spaces[i / 2] = lrm;
            }
        }

        return EvaluateUtil2.getEvaluate(consecutive, spaces);
    }

    /**
     * 对整个棋面的估分
     *
     * @param tColor
     * @return
     */
    private int evaluate(int tColor) {
        int aiMaxScore = EvaluateUtil2.MIN;
        int humanMaxScore = EvaluateUtil2.MIN;
        for (int i = 0; i < mBoard.length; ++i) {
            for (int j = 0; j < mBoard[i].length; ++j) {
                if (mBoard[i][j] == ChessPoint.NULL) {
                    aiMaxScore = Math.max(aiMaxScore, aiScore[i][j]);
                    humanMaxScore = Math.max(humanMaxScore, humanScore[i][j]);
                }
            }
        }
        return tColor * (aiMaxScore - humanMaxScore);
    }
}
