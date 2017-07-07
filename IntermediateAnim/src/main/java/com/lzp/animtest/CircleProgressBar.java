package com.lzp.animtest;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by li.zhipeng on 2017/7/7.
 * <p>
 * 原型进度条
 */

public class CircleProgressBar extends View {

    /**
     * 圆周的角度
     */
    private static final Float CIRCULAR = 360f;

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 进度
     */
    private float mProgress = 50;

    /**
     * 最大进度
     */
    private int mMaxProgress = 100;

    /**
     * 圆心颜色
     */
    private int mBackgroundColor = Color.parseColor("#00000000");

    /**
     * 边框颜色，也就是进度的颜色
     */
    private int mProgressBorderColor = Color.parseColor("#ff00ff");

    /**
     * 边框颜色，也就是进度条补全的颜色
     */
    private int mBorderColor = Color.parseColor("#00000000");

    /**
     * 边框的宽度，也就是进度条的宽度
     */
    private int mBorderWidth = 20;

    /**
     * 自身的宽高
     */
    private int mWidth, mHeight;

    /**
     * 半径
     */
    private int mRadius;

    /**
     * 矩形区域
     */
    private RectF mContentRectF = new RectF();

    /**
     * 是否打开过度模式，也就是我们平时看到的类似追赶的效果
     */
    private boolean mIsIntermediateMode;

    /**
     * 最小弧度，进度条过度模式最小的弧度
     */
    private int mMinProgress = 5;

    /**
     * 过度动画的时间
     */
    private static final int DURATION = 1000;

    /**
     * 过度动画
     */
    private ValueAnimator valueAnimator;

    /**
     * 开始角度，在过度动画中使用
     */
    private float mStartAngle = -90f;

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 去除锯齿
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

    }

    /**
     * 设置进度
     */
    public void setProgress(float progress) {
        this.mProgress = progress;
        invalidate();
    }

    /**
     * 设置背景颜色
     */
    public void setBackgroudColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
    }

    /**
     * 获取背景颜色
     */
    public int getBackgroundColor() {
        return this.mBackgroundColor;
    }

    /**
     * 设置进度条的背景颜色
     */
    public void setProgressBackgroundColor(int color) {
        this.mBorderColor = color;
    }

    /**
     * 获取进度条背景颜色
     */
    public int getProgressBackgroundColor() {
        return this.mBorderColor;
    }

    /**
     * 获取进度条的颜色
     */
    public int getProgressColor() {
        return this.mProgressBorderColor;
    }

    /**
     * 设置进度条的颜色
     */
    public void setProgressColor(int color) {
        this.mProgressBorderColor = color;
    }

    /**
     * 是否是过度模式
     */
    public boolean isIntermediateMode() {
        return mIsIntermediateMode;
    }

    /**
     * 设置过度模式
     */
    public void setIntermediateMode(boolean intermediateMode) {
        if (mIsIntermediateMode != intermediateMode) {
            this.mIsIntermediateMode = intermediateMode;
            // 取消动画
            if (!mIsIntermediateMode) {
                valueAnimator.cancel();
            } else {
                //这里要开启动画
                startIntermediateAnim();
            }
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRadius = mWidth > mHeight ? mHeight / 2 : mWidth / 2;
        // 计算要绘制的区域
        mContentRectF.set(mWidth / 2 - mRadius + mBorderWidth / 2, mHeight / 2 - mRadius + mBorderWidth / 2,
                mWidth / 2 + mRadius - mBorderWidth / 2, mHeight / 2 + mRadius - mBorderWidth / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsIntermediateMode) {
            drawIntermediateProgress(canvas);
        } else {
            drawProgress(canvas);
        }

    }

    /**
     * 绘制过度进度条
     */
    private void drawIntermediateProgress(Canvas canvas) {
        // 首先画出背景圆
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        // 这里减去了边框的宽度
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - mBorderWidth, mPaint);
        // 绘制当前的进度

        // 画出进度条
        mPaint.setColor(mProgressBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        // 计算圆弧划过的角度
        float angle = CIRCULAR / mMaxProgress * mProgress;
        // 这里要画圆弧
        canvas.drawArc(mContentRectF, mStartAngle, angle, false, mPaint);

        // 画出另一部分的进度条
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        // 这里要画圆弧
        canvas.drawArc(mContentRectF, mStartAngle + angle, CIRCULAR - angle, false, mPaint);


    }

    /**
     * 开始过度动画
     */
    private void startIntermediateAnim() {
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator().ofFloat(mMinProgress, mMaxProgress - mMinProgress);
            valueAnimator.setDuration(DURATION);
            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    setProgress(value);
                    mStartAngle += 2;

//                    Log.e("lzp", value + "");
                }

            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                    mStartAngle = mStartAngle - CIRCULAR / mMaxProgress * mMinProgress;
                    int color = getProgressBackgroundColor();
                    setProgressBackgroundColor(getProgressColor());
                    setProgressColor(color);
                }
            });
        }
        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();
    }

    /**
     * 绘制进度条
     */
    private void drawProgress(Canvas canvas) {
        // 开始画进度条
        // 首先画出背景圆
        mPaint.setColor(mBackgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        // 这里减去了边框的宽度
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius - mBorderWidth, mPaint);
        // 画出进度条
        mPaint.setColor(mProgressBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);

        // 计算圆弧划过的角度
        float angle = CIRCULAR / mMaxProgress * mProgress;
        // 这里要画圆弧
        canvas.drawArc(mContentRectF, -90, angle, false, mPaint);
        // 画出另一部分的进度条
        mPaint.setColor(mBorderColor);
        mPaint.setStrokeWidth(mBorderWidth);
        // 这里要画圆弧
        canvas.drawArc(mContentRectF, -90 + angle, CIRCULAR - angle, false, mPaint);
    }

}
