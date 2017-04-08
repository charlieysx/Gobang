package com.yusoxn.gobang.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.yusoxn.gobang.R;
import com.yusoxn.gobang.bean.ChessPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏的View，即棋盘棋子等的绘制
 * <p>
 * Created by Yusxon on 17/3/4.
 */

public class GameView extends View {

    /**
     * 棋盘宽度(由于棋盘是正方形,所以棋盘高度等于棋盘宽度)
     */
    private int mPanelWidth;
    /**
     * 格子的行高
     */
    private float mLineHeight;
    /**
     * 棋盘的最大行数
     */
    private int MAX_LINE_NUM = 15;

    /**
     * 空心画笔
     */
    private Paint mStrokePaint = new Paint();
    /**
     * 实心的画笔
     */
    private Paint mFillPaint = new Paint();
    /**
     * 黑白棋子,选择框
     */
    private Bitmap mWhiteChess = null;
    private Bitmap mBlackChess = null;
    private Bitmap mSelect = null;

    /**
     * 棋子的缩放比例
     */
    private float ChessScale = 5.0f / 6;

    /**
     * 选择框的缩放比例
     */
    private float selectScale = 6.5f / 7;

    /**
     * 存储棋子坐标
     */
    private ArrayList<ChessPoint> points = new ArrayList<>();

    /**
     * 选择框
     */
    private ChessPoint selectPoint = null;

    private ArrayList<ChessPoint> fivePoints = new ArrayList<>();


    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        //初始化空心画笔
        mStrokePaint.setColor(0x88000000);
        //设置抗锯齿
        mStrokePaint.setAntiAlias(true);
        //设置防抖动
        mStrokePaint.setDither(true);
        //设置为空心
        mStrokePaint.setStyle(Paint.Style.STROKE);

        //初始化实心画笔
        mFillPaint.setColor(Color.BLACK);
        //设置抗锯齿
        mFillPaint.setAntiAlias(true);
        //设置防抖动
        mFillPaint.setDither(true);
        //设置为空心
        mFillPaint.setStyle(Paint.Style.FILL);

        //初始化棋子,选择框
        mWhiteChess = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_white);
        mBlackChess = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_black);
        mSelect = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_select);

        fivePoints.add(new ChessPoint(3, 3, 0));
        fivePoints.add(new ChessPoint(11, 3, 0));
        fivePoints.add(new ChessPoint(7, 7, 0));
        fivePoints.add(new ChessPoint(3, 11, 0));
        fivePoints.add(new ChessPoint(11, 11, 0));

    }

    /**
     * 测量宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //此处的逻辑判断是处理当我们自定义的View被嵌套在ScrollView中时,获得的测量模式
        // 会是UNSPECIFIED
        // 使得到的widthSize或者heightSize为0
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        Log.d("pyh", "onMeasure: width" + width + "height" + heightSize);
        //调用此方法使我们的测量结果生效
        setMeasuredDimension(width, width);
    }

    /**
     * 宽高变化时调用此方法
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //此处的参数w就是在onMeasure()方法中设置的自定义View的大小
        //计算出棋盘宽度和行高
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE_NUM;

        //将棋子根据行高变化
        int chessSize = (int) (ChessScale * mLineHeight);
        mWhiteChess = Bitmap.createScaledBitmap(mWhiteChess, chessSize, chessSize, false);
        mBlackChess = Bitmap.createScaledBitmap(mBlackChess, chessSize, chessSize, false);

        int selectSize = (int) (selectScale * mLineHeight);
        mSelect = Bitmap.createScaledBitmap(mSelect, selectSize, selectSize, false);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawBoard(canvas);
        drawFivePoint(canvas);
        drawChess(canvas);
        drawSelect(canvas);
    }

    /**
     * 绘制棋盘
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;

        for (int i = 0; i < MAX_LINE_NUM; ++i) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            //画横线
            canvas.drawLine(startX, y, endX, y, mStrokePaint);
            //画竖线
            canvas.drawLine(y, startX, y, endX, mStrokePaint);
        }
    }

    /**
     * 绘制五个小点
     *
     * @param canvas
     */
    private void drawFivePoint(Canvas canvas) {
        for (ChessPoint point : fivePoints) {
            canvas.drawCircle(
                    (point.x) * mLineHeight + mLineHeight / 2,
                    (point.y) * mLineHeight + mLineHeight / 2, (1 / 5.0f * mLineHeight), mFillPaint);
        }
    }

    /**
     * 绘制棋子
     *
     * @param canvas
     */
    private void drawChess(Canvas canvas) {
        for (ChessPoint point : points) {
            canvas.drawBitmap((point.color == ChessPoint.BLACK) ? mBlackChess : mWhiteChess,
                    (point.x + (1 - ChessScale) / 2) * mLineHeight,
                    (point.y + (1 - ChessScale) / 2) * mLineHeight, null);
        }
    }

    /**
     * 绘制选择框
     *
     * @param canvas
     */
    private void drawSelect(Canvas canvas) {
        if (null != selectPoint) {
            canvas.drawBitmap(mSelect,
                    (selectPoint.x + (1 - selectScale) / 2) * mLineHeight,
                    (selectPoint.y + (1 - selectScale) / 2) * mLineHeight, null);
        }
    }

    /**
     * 添加棋子
     *
     * @param chessPoint
     */
    public void addChessPoint(ChessPoint chessPoint) {
        if (null != chessPoint) {
            points.add(chessPoint);
        }
    }

    /**
     * 批量添加棋子
     *
     * @param chessPoints
     */
    public void addChessPoints(List<ChessPoint> chessPoints) {
        if (null != chessPoints) {
            points.addAll(chessPoints);
        }
    }

    /**
     * 设置选择框位置
     *
     * @param selectPoint
     */
    public void setSelectPoint(ChessPoint selectPoint) {
        if (null != selectPoint) {
            this.selectPoint = selectPoint;
        }
    }

    /**
     * 重画，在添加棋子等信息后需要调用该方法绘制
     */
    public void reDraw() {
        invalidate();
    }

    /**
     * 获取行高
     *
     * @return
     */
    public float getViewLineHeight() {
        return mLineHeight;
    }
}
