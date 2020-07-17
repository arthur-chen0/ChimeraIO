package com.jht.androiduiwidgets.buttons;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

@SuppressWarnings("unused")
public class ButtonPressAndHoldRepeatHandler extends PressAndHoldRepeatHandler {

    public ButtonPressAndHoldRepeatHandler(View button, IOnClick onClick) {
        super(onClick);
        init(button);
    }

    public ButtonPressAndHoldRepeatHandler(View button, IOnClick onClick, int maxPresses) {
        super(onClick, maxPresses);
        init(button);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(View button) {
        button.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                start();
            }
            else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                stop();
            }
            return false;
        });
    }

}
