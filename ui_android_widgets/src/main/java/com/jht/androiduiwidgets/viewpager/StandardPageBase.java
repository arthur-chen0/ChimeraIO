package com.jht.androiduiwidgets.viewpager;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;



/**
 * Basic functionality for each engineering page.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class StandardPageBase implements IPageHolder {

    // The page.
    private View page;

    // The context for shorthand.
    private Context context;

    // True if the page is shown.
    private boolean show = false;

    private boolean doCarousel = false;

    /**
     * Create the page.
     *
     */
    public StandardPageBase() {
        init(false);
    }

    /**
     * Create the page.
     *
     * @param doCarousel  True if we should do carousel style animations.
     */
    public StandardPageBase(boolean doCarousel) {
        init(doCarousel);
    }

    /**
     * Create the page.
     *
     * @param doCarousel  True if we should do carousel style animations.
     */
    private void init(boolean doCarousel) {
        this.doCarousel = doCarousel;
    }

    /**
     *
     * @return  The root view for the page.
     */
    protected View page() { return page; }

    abstract protected View onCreate(Context context, ViewGroup container);

    protected void onDestroy() {}

    @Override
    public void create(ViewGroup container) {
        context = container.getContext();
        page = onCreate(context, container);

        if(!show) {
            page.setAlpha(0);
            page.setTranslationX(hiddenTranslation());
        }
        else {
            page.setAlpha(1);
        }
    }

    @Override
    public void destroy(ViewGroup container) {
        container.removeView(page);
        onDestroy();
    }

    @Override
    public boolean isViewFromObject(View view) {
        return page == view;
    }

    protected int hiddenTranslation() {
        return 50;
    }



    @Override
    public void hide(final Animator.AnimatorListener listener, boolean start) {
        show = false;
        if(page != null) {

            hideAnimation(page, listener, start);

        }
    }

    public void hideAnimation(final View view, final Animator.AnimatorListener listener, boolean start) {
        final int translationX = start && doCarousel ? 0 - hiddenTranslation() : hiddenTranslation();
        view.post(new Runnable() {
            @Override
            public void run() {
                view.animate().alpha(0).translationX(translationX).setDuration(350).setListener(new Animator.AnimatorListener() {
                    private void animationFinished() {
                        view.setTranslationX(hiddenTranslation());
                        view.setAlpha(0);
                    }
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(listener != null) listener.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationFinished();
                        if(listener != null) listener.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        animationFinished();
                        if(listener != null) listener.onAnimationCancel(animation);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if(listener != null) listener.onAnimationRepeat(animation);
                    }
                }).start();
            }
        });

    }

    @Override
    public void show(final Animator.AnimatorListener listener, boolean start) {

        show = true;
        if(page != null) {
            showAnimation(page, listener, start);
        }
    }

    public void showAnimation(final View view, final Animator.AnimatorListener listener, boolean start) {

        view.setTranslationX(start && doCarousel ? 0 - hiddenTranslation() : hiddenTranslation());
        view.post(new Runnable() {
            @Override
            public void run() {
                view.animate().alpha(1).translationX(0).setDuration(350).setListener(new Animator.AnimatorListener() {
                    private void animationFinished() {
                        view.setTranslationX(0);
                        view.setAlpha(1);
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (listener != null) listener.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationFinished();
                        if (listener != null) listener.onAnimationEnd(animation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        animationFinished();
                        if (listener != null) listener.onAnimationCancel(animation);

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if (listener != null) listener.onAnimationRepeat(animation);
                    }
                }).start();
            }
        });

    }

    protected Context getContext() {
        return context;
    }

    protected <T extends View> T findViewById(int id) {
        return page.findViewById(id);
    }

    protected boolean isShown() {
        return show;
    }
}
