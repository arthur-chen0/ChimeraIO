package com.jht.androiduiwidgets.viewpager;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/*
 * This class eats up touch event to keep users from scrolling through pages in a page view.  This
 * is useful in many places throughout the UI.
 */
public class NoScrollViewPager extends ViewPager {

    /**
     * Initialize the pager.
     *
     * @param context   The context that holds the view.
     */
	public NoScrollViewPager(Context context) {
        super(context);
    }

    /**
     * Initialize the pager.
     *
     * @param context   The context that holds the view.
     * @param attrs     The XML attributes for the pager.
     */
    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Override the touch event so that the user cannot scroll
     * @param event     The touch event
     * @return          False
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * Override the touch event so that the user cannot scroll
     * @param event     The touch event
     * @return          False
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
    

}
