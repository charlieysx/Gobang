package com.yusoxn.gobang.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 棋子
 * <p>
 * Created by Yusxon on 17/3/5.
 */

public class ChessPoint implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<ChessPoint> CREATOR = new Parcelable.Creator<ChessPoint>() {

        public ChessPoint createFromParcel(Parcel in) {
            ChessPoint r = new ChessPoint();
            r.readFromParcel(in);
            return r;
        }

        public ChessPoint[] newArray(int size) {
            return new ChessPoint[size];
        }
    };

    public void readFromParcel(Parcel in) {
        x = in.readInt();
        y = in.readInt();
        color = in.readInt();
    }
}
