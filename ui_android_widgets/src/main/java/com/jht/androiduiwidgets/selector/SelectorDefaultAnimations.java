package com.jht.androiduiwidgets.selector;

import android.content.Context;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public class SelectorDefaultAnimations {
    /**
     *
     * @return Gets the default animation for the recycler view layout.
     */
    static public LayoutAnimationController getDefaultLayoutAnimation() {

        ScaleAnimation scale = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scale);
        animationSet.addAnimation(fade);
        animationSet.setFillAfter(true);
        animationSet.setDuration(100);
        return new LayoutAnimationController(animationSet);
    }

    /**
     *
     * @return Gets the default animation for the recycler view layout.
     */
    static public LayoutAnimationController getDefaultHorizontalLayoutAnimation() {

        ScaleAnimation scale = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(scale);
        animationSet.addAnimation(fade);
        animationSet.setFillAfter(true);
        animationSet.setDuration(100);
        return new LayoutAnimationController(animationSet);
    }

    /**
     *
     * @return Gets the default animation for the recycler view items.
     */
    static public RecyclerView.ItemAnimator getDefaulItemAnimation() {

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(150);
        itemAnimator.setRemoveDuration(150);
        itemAnimator.setMoveDuration(150);
        itemAnimator.setChangeDuration(150);
        return itemAnimator;
    }

    static public LinearLayoutManager setDefaultAnimations(Context context, RecyclerView view) {
        view.setItemAnimator(getDefaulItemAnimation());
        view.setLayoutAnimation(getDefaultLayoutAnimation());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        view.setLayoutManager(manager);
        return manager;
    }


    static public LinearLayoutManager setDefaultHorizontalAnimations(Context context, RecyclerView view) {
        view.setItemAnimator(getDefaulItemAnimation());
        view.setLayoutAnimation(getDefaultHorizontalLayoutAnimation());
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        view.setLayoutManager(manager);
        return manager;
    }
}
