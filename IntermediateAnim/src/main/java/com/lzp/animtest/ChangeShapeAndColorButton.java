package com.lzp.animtest;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by li.zhipeng on 2017/7/11.
 * <p>
 * 改变形状和颜色的按钮
 */

public class ChangeShapeAndColorButton extends AppCompatButton {

    /**
     * 按钮的状态
     */
    public interface Status {
        // 普通状态
        int NORMAL = 0;
        // 成功状态
        int SUCCESS = 1;
    }

    /**
     * 动画市场
     */
    private int mDuration = 500;

    /**
     * 目前的状态
     */
    private int mCurrentStaus = Status.NORMAL;

    /**
     * 普通状态的背景色
     */
    private int mNormalBgColor = Color.parseColor("#0000ff");

    /**
     * 成功状态的颜色
     */
    private int mSuccessBgColor = Color.parseColor("#00ffff");

    /**
     * 要绘制的颜色
     */
    private int mPaintColor = mNormalBgColor;

    /**
     * 是否正在动画中
     */
    private boolean isAnim;

    /**
     * 圆角的大小
     */
    private float mRadius;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 绘制的形状区域
     */
    private RectF mRectF;

    /**
     * 设置最大距离
     */
    private int maxPadding;

    /**
     * 当前的Padding
     */
    private int mCurrentPadding;

    /**
     * 最小大小
     */
    private int minSize = 20;

    public ChangeShapeAndColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 把默认背景去掉
        setBackgroundResource(0);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 计算最大间距，除以2是因为要均分间距，才能居中绘制
        maxPadding = Math.abs(width - height) / 2;
        // 最小是宽或高
        minSize = width > height ? height : width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRectF == null) {
            mRectF = new RectF();
        }
        // 判断宽高
        int width = getWidth();
        int height = getHeight();

        int paddingLR = width - mCurrentPadding * 2 < minSize ? (width - minSize) / 2 : mCurrentPadding;
        int paddingTB = height - mCurrentPadding * 2 < minSize ? (height - minSize) / 2 : mCurrentPadding;

        mRectF.set(paddingLR, paddingTB, getWidth() - paddingLR, getHeight() - paddingTB);
        // 开始画后面的背景
        mPaint.setColor(mPaintColor);
        canvas.clipRect(mRectF);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        // 这里是画文字的部分，我们不管
        super.onDraw(canvas);


    }

    /**
     * 设置按钮状态
     */
    public void setStatus(int status) {
        if (status != mCurrentStaus && !isAnim) {
            switch (mCurrentStaus) {
                case Status.NORMAL:
                    starteAnimSet(mNormalBgColor, mSuccessBgColor, 0, getHeight() / 2, 0, maxPadding);
                    break;
                case Status.SUCCESS:
                    starteAnimSet(mSuccessBgColor, mNormalBgColor, getHeight() / 2, 0, maxPadding, 0);
                    break;
            }
            mCurrentStaus = status;
        }
    }

    /**
     * 获取当前状态
     */
    public int getStatus() {
        return mCurrentStaus;
    }

    /**
     * 开启动画效果
     */
    private void starteAnimSet(int fromColor, int toColor, int fromRadius, int roRadius, int fromPadding, int toPadding) {
        isAnim = true;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getColorAnim(fromColor, toColor), getRadiusAnim(fromRadius, roRadius), getShapeAnim(fromPadding, toPadding));
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    /**
     * 颜色动画
     */
    private ValueAnimator getColorAnim(int fromColor, int toColor) {
        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
        colorAnim.setDuration(mDuration);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mPaintColor = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        return colorAnim;
    }

    /**
     * 圆角动画
     */
    private ValueAnimator getRadiusAnim(int fromRadius, int toRadius) {
        {
            ValueAnimator radiusAnim = ValueAnimator.ofFloat(fromRadius, toRadius);
            radiusAnim.setDuration(mDuration);
            radiusAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mRadius = (float) valueAnimator.getAnimatedValue();
//                    invalidate();
                }
            });
            return radiusAnim;
        }
    }

    /**
     * 形状动画
     */
    private ValueAnimator getShapeAnim(int fromPadding, int toPadding) {
        {
            ValueAnimator shapeAnim = ValueAnimator.ofInt(fromPadding, toPadding);
            shapeAnim.setDuration(mDuration);
            shapeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mCurrentPadding = (int) valueAnimator.getAnimatedValue();
                }
            });
            return shapeAnim;
        }
    }
}
