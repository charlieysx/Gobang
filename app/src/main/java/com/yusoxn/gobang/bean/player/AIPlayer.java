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

    private int[][] aiScore = new int[15][15];
    private int[][] humanScore = new int[15][15];

    public AIPlayer(String playerName, int color) {
        super(playerName, color);
    }

    @Override
    public void initChessBoard() {
        super.initChessBoard();
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

        Log.i("Player", playerName + "下棋");

        return search(1);
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
        int role = color * tColor;
        int score = evaluate(role);
        int ai = 0;
        int human = 0;
        if(tColor == 1) {
            ai = score;
        } else {
            human = score;
        }
        int rank = EvaluateUtil2.getRank(ai, human);
        if(depth <= 0 || isGameOver() || rank == 0 || rank == 1) {
            return score;
        }
        int best = EvaluateUtil2.MIN;
        List<ChessPoint> emptyPosList = getEmptyPosList(color);
        for(ChessPoint chess : emptyPosList) {
            move(chess.x, chess.y, color);
            int value = -alphaBeta(depth - 1, -beta, -1 * (best > alpha ? best : alpha), -tColor);
            backMove(chess.x, chess.y);
            if(value >= best) {
                best = value;
            }
//            if(value >= beta) {
//                return value;
//            }
        }
        return best;
    }

    private ChessPoint search(int depth) {
        List<ChessPoint> result = new ArrayList<>();

        int best = EvaluateUtil2.MIN;
        List<ChessPoint> emptyPosList = getEmptyPosList(color);
        for(ChessPoint chess : emptyPosList) {
            move(chess.x, chess.y, color);
            int value = -alphaBeta(depth - 1, -EvaluateUtil2.MAX, -best, -1);
            backMove(chess.x, chess.y);
            if(value == best) {
                result.add(new ChessPoint(chess.x, chess.y, color));
            }
            if(value > best) {
                best = value;
                result.clear();
                result.add(new ChessPoint(chess.x, chess.y, color));
            }
        }

        Log.i("AIPlayer", result.size() + "...");
        Random random = new Random();
        int index = random.nextInt(result.size());
        for(ChessPoint chess : result) {
            Log.i("AIPlayer", chess.x + "..." + chess.y);
        }

        return result.get(index);
    }

    private boolean isGameOver() {
        for(ChessPoint chess : mPosList) {
            if(isGameOver(chess)) {
                return true;
            }
        }
        for(ChessPoint chess : ePosList) {
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
            emptyPosList.addAll(rankPosList.get(1));
        } else {
            for (int i = 2; i < rankPosList.size(); ++i) {
                List<ChessPoint> chessPoints = rankPosList.get(i);
                for (int j = 0; j < chessPoints.size(); ++j) {
                    emptyPosList.add(chessPoints.get(j));
                }
                if (emptyPosList.size() >= 5) {
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
                    } else {
                        if(!space) {
                            lrm--;
                        }
                        break;
                    }
                    int tempX = tx + dir[i + k][0];
                    int tempY = ty + dir[i + k][1];
                    if(tempX < 0 || tempX >= 15 || tempY < 0 || tempY >= 15) {
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

        return Math.max(aiMaxScore, humanMaxScore);
//        return tColor * (aiMaxScore - humanMaxScore);
    }
}
