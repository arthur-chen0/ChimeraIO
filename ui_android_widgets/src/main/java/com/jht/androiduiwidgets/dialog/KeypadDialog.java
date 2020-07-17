package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import android.util.AttributeSet;

import com.jht.androiduiwidgets.IInputConfiguration;
import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.keypad.Keypad;

/**
 * This class manages the keypad widgets that are shown throughout the software.
 */
public class KeypadDialog extends DialogBase {

    /**
     * The default constructor for the keypad.
     *
     * @param context   context that owns the keypad.
     */
    public KeypadDialog(Context context) {
        this(context, null, 0);
    }


    /**
     * The constructor for the keypad with attributes.
     *
     * @param context   context that owns the keypad.
     * @param attrs     the xml attributes for the keypad.
     */
    public KeypadDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    /**
     * The constructor for the keypad with attributes and style
     *
     * @param context   context that owns the keypad.
     * @param attrs     the xml attributes for the keypad.
     * @param defStyle  the default style to use for the keypad.
     */
    public KeypadDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCloseOnOutsideTouch(true);
    }


    public void init(final IInputConfiguration settings, final OnClickListener onConfirm, final boolean hideOnConfirm) {
        final Keypad keypad = findViewById(R.id.default_keypad_widget);
        keypad.setKeypadSettings(settings);
        keypad.setOnConfirm(v -> {
            if(onConfirm != null) {
                onConfirm.onClick(v);
            }
            if(hideOnConfirm) {
                hide();
            }
        });

    }

    /**
     *
     * @return  The message_dialog layout.
     */
    @Override
    protected int getLayoutResId() { return R.layout.keypad_default; }
}
