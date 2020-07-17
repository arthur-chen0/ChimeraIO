package com.jht.androiduiwidgets.viewpager;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.util.List;

/*
 * This provides a simple animator for going between pages.  This is useful in the Engineering screen.
 * The current default animation here fades the page and moves it to the end and then brings in the next page
 * from the left fade it in.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CustomAnimatedViewPager extends NoScrollViewPager {

    /**
     * Initalize the view pager.
     *
     * @param context   The context that contains the view.
     */
	public CustomAnimatedViewPager(Context context) {
        super(context);
    }

    /**
     * Initalize the view pager.
     *
     * @param context   The context that contains the view.
     * @param attrs     The XML attributes for the pager.
     */
    public CustomAnimatedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Set the current page in teh view pager.
     * @param position  The page position.
     * @param pages     The list of pages.
     */
    public void setCurrentItem(final int position, final List<IPageHolder> pages) {

        // Only animate if we are currently on a page.
	    if(getCurrentItem() >= 0 && getCurrentItem() < pages.size() && getCurrentItem() != position) {
	        // Hide the current page.
	        pages.get(getCurrentItem()).hide(new Animator.AnimatorListener() {
	            private void onFinished() {
	                // Current page is hidden.  Show the next page.
                    setCurrentItem(position, false);
                    if(position >= 0 && position < pages.size()) {
                        pages.get(position).show(null, getCurrentItem() < position);
                    }
                }
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    onFinished();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onFinished();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }, getCurrentItem() < position);
        }
        else {
	        // Set the current page.  Just show it no animations.
            setCurrentItem(position, false);
            if(position >= 0 && position < pages.size()) {
                pages.get(position).show(null, true);
            }
        }
    }
    

}
