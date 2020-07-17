package com.jht.androiduiwidgets.autofit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;

@SuppressWarnings({"unused"})
public class AutofitButton extends AppCompatButton {
    private CharSequence text = "";
    private float textSize;

    public AutofitButton(Context context) {
        this(context, null, 0);
    }

    public AutofitButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutofitButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutofitButton(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        CharSequence cs = this.getText();
        if (cs != null) {
            this.text = this.getText();
            if (this.getTransformationMethod() != null) {
                this.text = this.getTransformationMethod().getTransformation(getText(), this);
            }
        }

        this.textSize = this.getTextSize();
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.text = text;
        if (lengthBefore != lengthAfter) {
            setBestTextSize(getInnerWidth());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int innerWidth = getInnerWidth();
        setBestTextSize(innerWidth);
    }

    private int getInnerWidth() {
        Drawable[] drawable = getCompoundDrawables();
        Drawable leftDrawable = drawable[0];
        Drawable rightDrawable = drawable[2];

        int ldw = 0, rdw = 0;
        if (leftDrawable != null) {
            ldw = leftDrawable.getMinimumWidth() + this.getCompoundDrawablePadding();
        }

        if (rightDrawable != null) {
            rdw = rightDrawable.getMinimumWidth() + this.getCompoundDrawablePadding();
        }

        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight() -ldw - rdw;
    }

    private void setBestTextSize(int innerWidth) {
        if (innerWidth <= 0) {
            return;
        }

        this.textSize = this.getTextSize();

        TextPaint paint = this.getPaint();
        StaticLayout s1;
        boolean found = false;
        float MIN_TEXT_SIZE = 1f;
        while (!found && this.textSize > 0 && this.textSize >= MIN_TEXT_SIZE) {
            paint.setTextSize(this.textSize);
            s1 = new StaticLayout(this.text, paint, innerWidth, Layout.Alignment.ALIGN_CENTER, 0, 0, true);

            if (s1.getLineCount() <= 1) {
                found = true;
            } else {
                this.textSize -= 1f;
            }
        }

        this.setTextSize(((this.textSize -1f) / getResources().getDisplayMetrics().density));
    }
}
