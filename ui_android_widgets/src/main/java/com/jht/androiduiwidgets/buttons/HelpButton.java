package com.jht.androiduiwidgets.buttons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.dialog.DialogFactory;

/**
 * The selector adapter is used to create a selector list utilizing a recycler view.
 */
@SuppressWarnings("unused")
public class HelpButton extends RelativeLayout {

    String helpTitle;
    String helpMessage;


    public HelpButton(Context context) {
        this(context, null, android.R.attr.textViewStyle);
    }

    public HelpButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("InflateParams")
    public HelpButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        addView(LayoutInflater.from(context).inflate(R.layout.help_button, null, false));

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HelpButton, defStyle, 0);
        CharSequence chars = a.getText(R.styleable.HelpButton_title);
        helpTitle = chars == null ? "" : chars.toString();
        chars = a.getText(R.styleable.HelpButton_message);
        helpMessage = chars == null ? "" : chars.toString();
        a.recycle();

        findViewById(R.id.help_button).setOnClickListener(v -> {
            if(!helpMessage.equals("")) {
                DialogFactory.helpMessage(v, helpTitle, helpMessage);
            }
        });

    }

    public void hide() {
        setVisibility(View.INVISIBLE);
        setClickable(false);
    }

    public void setMessage(String title, String message) {
        helpTitle = title;
        helpMessage = message;
    }



}
