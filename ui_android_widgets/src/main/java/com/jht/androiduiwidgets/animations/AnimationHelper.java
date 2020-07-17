package com.jht.androiduiwidgets.animations;


import android.animation.Animator;
import android.view.View;

@SuppressWarnings({"unused"})
public class AnimationHelper
{
    public static void fadeInView(final View view, final int duration) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        view.animate().alpha(1).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}
        }).start();
    }

    public static void fadeOutView(final View view, final int duration) {
        view.animate().alpha(0).setDuration(duration).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}

            @Override
            public void onAnimationCancel(Animator animation) {}
        }).start();
    }
}
