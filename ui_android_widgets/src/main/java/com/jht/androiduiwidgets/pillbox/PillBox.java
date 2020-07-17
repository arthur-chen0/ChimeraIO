package com.jht.androiduiwidgets.pillbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.jht.androiduiwidgets.IInputConfiguration;
import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.buttons.PressAndHoldRepeatHandler;
import com.jht.androiduiwidgets.dialog.DialogFactory;

/**
 * Created by AlexN on 12/30/2018.
 */

@SuppressWarnings("unused")
public class PillBox extends AppCompatTextView {

    private Paint percentPaint;
    private Paint blackPaint;
    private Paint borderPaint;
    private Paint captionTextPaint;
    private Paint valueTextPaint;
    private Paint selectPaint;
    private Drawable minusButton;
    private Drawable plusButton;
    private Drawable editIcon;
    private String captionText;
    private String valueText;

    private int width;
    private int height;
    private float percent = 1;
    private int pixel_dp;
    private float buttonWidth;
    private Rect boxRect;
    private boolean minusSelected = false;
    private boolean plusSelected = false;
    private boolean boxSelected = false;

    public IInputConfiguration parameterSettings;
    private IValueSelectorInterface iValueSelectorInterface;

    private Handler handler = new Handler();

    public interface IValueSelectorInterface {
        void valueChanged(float value, String identifier);
    }

    public PillBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setPercent(float percent) {
        this.percent = percent;
        invalidate();
    }

    private void init(Context context) {

        //Text text color to transparent.
        //Only a text view for QA auto testing since canvas drawing values can't be read
        setTextColor(Color.TRANSPARENT);

        percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentPaint.setStyle(Paint.Style.FILL);
        percentPaint.setColor(0xFFF5B324);
        blackPaint = new Paint();
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setColor(Color.BLACK);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(0x66FFFFFF);

        captionTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        captionTextPaint.setStyle(Paint.Style.FILL);
        captionTextPaint.setColor(0xFFF4F4F4);
        captionTextPaint.setTextAlign(Paint.Align.LEFT);

        valueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        valueTextPaint.setStyle(Paint.Style.FILL);
        valueTextPaint.setColor(0xFFF4F4F4);
        valueTextPaint.setTextAlign(Paint.Align.CENTER);

        selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setStyle(Paint.Style.FILL);
        selectPaint.setColor(0x66000000);

        minusButton = context.getResources().getDrawable(R.drawable.ic_minus_selector);
        plusButton = context.getResources().getDrawable(R.drawable.ic_plus_selector);
        editIcon = context.getResources().getDrawable(R.drawable.ic_icon_edit);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        pixel_dp = (int) (1 * scale + 0.5f);

        boxRect = new Rect();
    }

    public void initParameterSettings(IInputConfiguration parameterSettings) {
        this.parameterSettings = parameterSettings;
        captionText = parameterSettings.label(getContext());

        updateValue(false);
    }

    public void setValue(float value) {
        setValue(value, true);
    }


    public void setValue(float value, boolean notify) {
        parameterSettings.value(value);
        updateValue(notify);
    }

    private void updateValue(boolean notify) {
        handler.post(() -> {
            valueText = parameterSettings.value();
            percent = (float)((parameterSettings.valueAsDouble() - parameterSettings.min()) / (parameterSettings.max() - parameterSettings.min()));
            if (notify && iValueSelectorInterface != null) {
                iValueSelectorInterface.valueChanged((float)parameterSettings.valueAsDouble(), parameterSettings.identifier());
            }
            PillBox.this.setText(valueText);
            invalidate();
        });
    }

    public void setiValueSelectorInterface(IValueSelectorInterface iValueSelectorInterface) {
        this.iValueSelectorInterface = iValueSelectorInterface;
    }

    private void incrementValue() {
        parameterSettings.increment();
        updateValue(false);
    }

    private void decrementValue() {
        parameterSettings.decrement();
        updateValue(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        buttonWidth = 0.231f * width;
        minusButton.setBounds((int) (buttonWidth * 0.1f), height - (int) (buttonWidth * 1.1f), (int) (buttonWidth * 1.1f), height - (int) (buttonWidth * 0.1f));
        plusButton.setBounds((int) (width - buttonWidth * 1.1f), height - (int) (buttonWidth * 1.1f), (int) (width - buttonWidth * 0.1f), height - (int) (buttonWidth * 0.1f));
        boxRect.set(minusButton.getBounds().right, minusButton.getBounds().top, plusButton.getBounds().left, plusButton.getBounds().bottom);


        int iconSize = Math.max(10 * pixel_dp , 10);
        int iconSpacing = 5 * pixel_dp;
        editIcon.setBounds((int) (width - buttonWidth * 1.1f) - iconSize - iconSpacing,
                height - (int) (buttonWidth * 1.1f) + iconSpacing,
                (int) (width - buttonWidth * 1.1f) - iconSpacing,
                height - (int) (buttonWidth * 1.1f) + iconSpacing + iconSize);

        captionTextPaint.setTextSize(width * 0.06f);
        valueTextPaint.setTextSize(width * 0.12f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw border
        canvas.drawRect(0, height - width / 3.6f, width, height, borderPaint);
        //Draw black background
        canvas.drawRect(buttonWidth * 1.1f, height - buttonWidth * 1.1f, width - buttonWidth * 1.1f, height - buttonWidth * 0.1f, blackPaint);
        minusButton.draw(canvas);
        plusButton.draw(canvas);
        editIcon.draw(canvas);
        if (minusSelected) {
            canvas.drawRect(minusButton.getBounds(), selectPaint);
        }
        if (plusSelected) {
            canvas.drawRect(plusButton.getBounds(), selectPaint);
        }

        if(parameterSettings == null || parameterSettings.showProgress()) {
            //Draw percent bar
            canvas.drawRect(buttonWidth * 1.1f, height - (buttonWidth * 0.1f + pixel_dp) - ((buttonWidth - 2 * pixel_dp) * percent), buttonWidth * 1.1f + pixel_dp * 4, height - (buttonWidth * 0.1f + pixel_dp), percentPaint);
        }

        //Caption and value text
        if(captionText != null) {
            canvas.drawText(captionText, buttonWidth * 0.1f, height - buttonWidth * 1.32f, captionTextPaint);
        }
        if(valueText != null) {
            canvas.drawText(valueText, width / 2.0f, height - buttonWidth * 0.42f, valueTextPaint);
        }
    }


    private void calculateTouch(float x, float y) {
        if (minusButton.getBounds().contains((int) x, (int) y)) {
            minusSelected = true;
        }
        if (plusButton.getBounds().contains((int) x, (int) y)) {
            plusSelected = true;
        }
        if(boxRect.contains((int)x, (int)y)) {
            boxSelected = true;
        }
    }

    PressAndHoldRepeatHandler buttonHandler = new PressAndHoldRepeatHandler(() -> {
        if(minusSelected) {
            decrementValue();
        }
        else if(plusSelected) {
            incrementValue();
        }
    });

    private void releaseButton() {
        buttonHandler.stop();
        updateValue(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                calculateTouch(x, y);
                if(minusSelected || plusSelected) {
                    buttonHandler.start();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(minusSelected && !minusButton.getBounds().contains((int) x, (int) y)) {
                    minusSelected = false;
                    releaseButton();
                }
                else if(plusSelected && !plusButton.getBounds().contains((int) x, (int) y)) {
                    plusSelected = false;
                    releaseButton();
                }
                else if(boxSelected && !boxRect.contains((int)x, (int)y)) {
                    boxSelected = false;
                }
                invalidate();
                break;

            case MotionEvent.ACTION_OUTSIDE:
                if(minusSelected || plusSelected) {
                    releaseButton();
                }
                minusSelected = false;
                plusSelected = false;
                boxSelected = false;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(boxSelected && boxRect.contains((int)x, (int)y)) {
                    DialogFactory.keypadDialog(this, parameterSettings, v -> updateValue(true), true);
                }
                releaseButton();
                minusSelected = false;
                plusSelected = false;
                boxSelected = false;
                invalidate();
                break;

        }
        return true;
    }
}
