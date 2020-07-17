package com.jht.androiduiwidgets.autofit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatEditText;

import com.jht.androiduiwidgets.R;

// I wrote this class to get around issues and bugs I was seeing in com.jht.common.widgets.AutofitTextView.
// Specifically, JHTAutofitTextView requires you specify width and height in your layout. But the quality of
// auto-fitting will be higher. No more multi-line Arabic text that appears with the bottom cropped off or isn't properly
// vertically centered. No need to resort to hacks like android:includeFontPaddding="false". 
// Specifying the AutofitTextView_preferSingleLine flag = true will cause the fitting process to favor a single line fit.
// I.e. if the font size of the text when fit to a single line is within 15% of the size of the text fit to multi-line, it will
// use the single line font size. This is useful to avoid situations where a single long word *almost* fits on a single line
// except for the last couple of letters. So we allow a small amount of further shrinking to fit, without making the text too
// much smaller.
@SuppressWarnings({"unused"})
public class AutofitEditTextView extends AppCompatEditText {
    // Minimum size of the text in pixels
    private static final int DEFAULT_MIN_TEXT_SIZE = 8; // DP

    // The Paint object.
    private TextPaint mPaint;

    // Attributes
    private boolean mSizeToFit;
    private int mMaxLines;
    private float mMinTextSize;	// Pixels.
    private float mMaxTextSize; // Pixels.
    private boolean mPreferSingleLine;

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        mPaint = new TextPaint();
        boolean sizeToFit = true;
        mMaxLines = super.getMaxLines();
        // Want to use density (i.e. DP) not scaledDensity (i.e. SP) because SP varies across consoles; we don't want this variance in our UI.
        float scaledDensity = context.getResources().getDisplayMetrics().density;
        int minTextSize = (int)scaledDensity * DEFAULT_MIN_TEXT_SIZE;
        mPreferSingleLine = false;
        if(attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.JHTAutofitTextView, defStyle, 0);
            sizeToFit = ta.getBoolean(R.styleable.JHTAutofitTextView_JHTAutofitTextView_sizeToFit, true);
            minTextSize = ta.getDimensionPixelSize(R.styleable.JHTAutofitTextView_JHTAutofitTextView_minTextSize, minTextSize);
            mPreferSingleLine = ta.getBoolean(R.styleable.JHTAutofitTextView_JHTAutofitTextView_preferSingleLine, mPreferSingleLine);
            ta.recycle();
        }
        setSizeToFit(sizeToFit);
        setRawTextSize(super.getTextSize());
        setMinTextSize(minTextSize);
    }

    // Set the text size in pixels.
    private void setRawTextSize(float size) {
        if (size != mMaxTextSize) {
            mMaxTextSize = size;
            resizeText();
        }
    }

    // TODO: this code could be optimized to use a binary search.
    private void resizeText() {
        if(!mSizeToFit) {
            return;
        }

        int targetWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int targetHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if(targetWidth > 0 && targetHeight > 0) {
            Context context = getContext();
            Resources r = Resources.getSystem();
            if(context != null) {
                r = context.getResources();
            }
            DisplayMetrics displayMetrics = r.getDisplayMetrics();
            CharSequence textSequence = getText();
            if(textSequence == null || textSequence.toString().isEmpty()) {
                textSequence = getHint();
            }
            String text = textSequence == null ? "" : textSequence.toString();

            mPaint.set(getPaint());

            // Find text size that fits into the box (width, height) and optionally within mMaxLines.
            float size = mMaxTextSize;
            boolean fitsWidthAndHeight = false;
            while(size >= mMinTextSize) {
                // Generate a StaticLayout to obtain the (x,y) dimensions, number of lines etc. required to contain the text.
                mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, displayMetrics));
                StaticLayout layout;
                layout = new StaticLayout(text, mPaint, targetWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);


                // Does text fit in the box (width, height).
                int newWidth = layout.getWidth();
                int newHeight = layout.getHeight();
                if(newWidth <= targetWidth && newHeight <= targetHeight && (mMaxLines <= 0 || layout.getLineCount() <= mMaxLines)) {
                    fitsWidthAndHeight = true;
                    break;
                }
                --size;
            }

            // If we would prefer a single line, try to find the text size that enables that.
            if(fitsWidthAndHeight && mPreferSingleLine) {
                while(size >= mMinTextSize) {
                    // Generate a StaticLayout to obtain the (x,y) dimensions, number of lines etc. required to contain the text.
                    mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, displayMetrics));
                    StaticLayout layout;
                    layout = new StaticLayout(text, mPaint, targetWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);



                    // We'd prefer the text can fit on a single line if the size will be within tolerance.
                    if(layout.getLineCount() == 1) {
                        break;
                    }
                    --size;
                }

                // If the new single line text size is too small, reset size back to the known good multi-line size that fits.
                //if(mPreferSingleLine && foundSingleLine) {
                //	float ratio = size / sizeLarger;
                //Log.d("DEBUG","Text size ratio = " + ratio);
                //if(ratio < 0.85f) {
                //	size = sizeLarger;
                //}
                //}
            }

            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public AutofitEditTextView(Context context) {
        this(context, null, android.R.attr.editTextStyle);
    }

    public AutofitEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public AutofitEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    // If true, the text will automatically be resized to fit its constraints; if false, it will not be resized.
    public void setSizeToFit(boolean sizeToFit) {
        mSizeToFit = sizeToFit;
        resizeText();
    }

    // Set the text size; specified with a given unit (see TypedValue) and value.
    @Override
    public void setTextSize(int unit, float size) {
        Context context = getContext();
        Resources r = Resources.getSystem();
        if(context != null) {
            r = context.getResources();
        }
        setRawTextSize(TypedValue.applyDimension(unit, size, r.getDisplayMetrics()));
    }

    // Returns text size in pixels.
    @Override
    public float getTextSize() {
        return mMaxTextSize;
    }

    // Set the minimum text size in pixels.
    public void setMinTextSize(float minSizePx) {
        if (minSizePx != mMinTextSize) {
            mMinTextSize = minSizePx;
            resizeText();
        }
    }

    // Set the minimum text size; specified with a given unit (see TypedValue) and value.
    public void setMinTextSize(int unit, float minSize) {
        Context context = getContext();
        Resources r = Resources.getSystem();
        if (context != null) {
            r = context.getResources();
        }
        setMinTextSize(TypedValue.applyDimension(unit, minSize, r.getDisplayMetrics()));
    }

    // Set the minimum text size where size is specified in DP.
    @SuppressWarnings("unused")
    public void setMinTextSizeDP(int minSizeDP) {
        setMinTextSize(TypedValue.COMPLEX_UNIT_DIP, minSizeDP);
    }

    // Returns the minimum size (in pixels) of the text size in this AutofitTextView
    @SuppressWarnings("unused")
    public float getMinTextSize() {
        return mMinTextSize;
    }

    @Override
    public void setLines(int lines) {
        super.setLines(lines);
        mMaxLines = lines;
        resizeText();
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        if (maxLines != mMaxLines) {
            mMaxLines = maxLines;
            resizeText();
        }
    }

    @Override
    public int getMaxLines() {
        return mMaxLines;
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int lengthBefore, final int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        resizeText();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(w != oldw) {
            resizeText();
        }
    }

}
