package com.jht.androiduiwidgets.helper;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Common functions to help out with recycler views. This is especially useful when you need to
 * disable the view for a bit after selection.
 *
 */
@SuppressWarnings("unused")
public class RecyclerViewHelper extends ViewHelper {
    private boolean swallowTouches = false;

    public RecyclerViewHelper(RecyclerView view, Handler handler) {
        super(view, handler);
        view.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return swallowTouches;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    public void disable(int timeoutMS) {
        swallowTouches = true;
        handler.postDelayed(() -> swallowTouches = false, timeoutMS);

    }


}
