package com.jht.androiduiwidgets.helper;

import android.os.Handler;
import android.view.View;

@SuppressWarnings({"unused", "WeakerAccess"})
public class ViewHelper {
    private View view;
    protected Handler handler;

    public ViewHelper(View view, Handler handler) {
        this.view = view;
        this.handler = handler;
    }

    public void disable(int timeoutMS) {
        view.setEnabled(false);
        handler.postDelayed(() -> view.setEnabled(true), timeoutMS);

    }


}
