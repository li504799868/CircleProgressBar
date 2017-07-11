package com.lzp.animtest;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar circleProgressBar;
    private ChangeShapeAndColorButton changeShapeAndColorButton;

    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circleProgressBar = (CircleProgressBar) findViewById(R.id.circle_progress);
        circleProgressBar.setIntermediateMode(true);

        changeShapeAndColorButton = (ChangeShapeAndColorButton) findViewById(R.id.change_shape_and_color_btn);
        changeShapeAndColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeShapeAndColorButton.setStatus(count++ % 2);
            }
        });
//        startAnim();
    }

    /**
     * 启动一个动画
     */
    private void startAnim() {
        ValueAnimator valueAnimator = new ValueAnimator().ofInt(0, 100);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int progress = (int) valueAnimator.getAnimatedValue();
                circleProgressBar.setProgress(progress);
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
                int color = circleProgressBar.getProgressBackgroundColor();
                circleProgressBar.setProgressBackgroundColor(circleProgressBar.getProgressColor());
                circleProgressBar.setProgressColor(color);
            }
        });

        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();

    }

}
