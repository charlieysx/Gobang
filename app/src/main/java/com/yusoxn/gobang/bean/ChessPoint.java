package com.yusoxn.gobang.bean;

/**
 * 棋子
 * <p>
 * Created by Yusxon on 17/3/5.
 */

public class ChessPoint {
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public int color;
    public int x;
    public int y;

    public ChessPoint() {
    }

    public ChessPoint(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void set(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
