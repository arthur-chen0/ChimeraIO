package com.jht.androiduiwidgets.buttons;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

@SuppressWarnings("unused")
public class KeyPressAndHoldRepeatHandler extends PressAndHoldRepeatHandler {

    public KeyPressAndHoldRepeatHandler(Context context, Serializable key) {
        super(null);
        init(context, key);
    }

    public KeyPressAndHoldRepeatHandler(Context context, Serializable key, IOnClick onClick, int maxPresses) {
        super(onClick, maxPresses);
        init(context, key);
    }

    private void init(final Context context, final Serializable key) {
        setOnClick(() -> {
            Intent keyIntent = new Intent("com.jht.keypress");
            keyIntent.putExtra("event", 1);
            keyIntent.putExtra("key", key);
            context.sendBroadcast(keyIntent);
        });
    }


}
