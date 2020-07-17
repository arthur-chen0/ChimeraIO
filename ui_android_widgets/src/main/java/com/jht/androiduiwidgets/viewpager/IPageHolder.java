package com.jht.androiduiwidgets.viewpager;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

/**
 * This is used to manage pages in a view pager.
 */
public interface IPageHolder {

    /**
     * Create the page.
     * @param container     The container that will hold the page.
     */
    void create(ViewGroup container);

    /**
     * Destroy the page.
     * @param container     The container that will hold the page.
     */
    void destroy(ViewGroup container);

    /**
     * Returns true if the page is equal to the view passed.
     * @param view     The view that may be the page.
     */
    boolean isViewFromObject(View view);

    /**
     * Used to show the page.  Passes an animator listener to listen for animation events while showing
     * the page.
     *
     * @param animatorListener  Used to pass animation event callbacks.
     * @param start             If true then we animate in from the start of the page.
     */
    void show(Animator.AnimatorListener animatorListener, boolean start);

    /**
     * Used to hide the page.  Passes an animator listener to listen for animation events while hiding
     * the page.
     *
     * @param animatorListener  Used to pass animation event callbacks.
     * @param start             If true then we animate out to the start of the page.
     */
    void hide(Animator.AnimatorListener animatorListener, boolean start);

}
