package com.jht.androiduiwidgets.buttons;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

import com.jht.androiduiwidgets.R;

/**
 * Created by KevinShih on 2018/6/4.
 */

@SuppressWarnings("unused")
public class ThreeStateImageButton extends AppCompatImageButton {
    private static final int[] BUTTON_FIRST_STATE = {R.attr.jht_first_state};
    private static final int[] BUTTON_SECOND_STATE = {R.attr.jht_second_state};
    private static final int[] BUTTON_THIRD_STATE = {R.attr.jht_third_state};

    enum ButtonState {
        FIRST_STATE,
        SECOND_STATE,
        THIRD_STATE,
        NUMBER_BUTTON_STATES
    }

    ButtonState mState = ButtonState.FIRST_STATE;
    private OnStateChangeListener mStateChangeListener;

    public interface OnStateChangeListener {
        void onStateChange(ButtonState state);
    }

    public ThreeStateImageButton(Context context) {
        super(context);
    }

    public ThreeStateImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeStateImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnStateChangeListener(OnStateChangeListener l) {
        mStateChangeListener = l;
    }

    public void setState(int stateOrdinal) {
        if(stateOrdinal >= 0 && stateOrdinal < ButtonState.NUMBER_BUTTON_STATES.ordinal()) {
            setState(ButtonState.values()[stateOrdinal]);
        }
    }

    public void setState(ButtonState state) {
        if(mState != state) {
            mState = state;
            refreshDrawableState();

            if(mStateChangeListener != null) {
                mStateChangeListener.onStateChange(mState);
            }
        }
    }

    public ButtonState getState() {
        return mState;
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 3);
        if(mState == ButtonState.FIRST_STATE) {
            mergeDrawableStates(drawableState, BUTTON_FIRST_STATE);
        } else if(mState == ButtonState.SECOND_STATE) {
            mergeDrawableStates(drawableState, BUTTON_SECOND_STATE);
        } else if(mState == ButtonState.THIRD_STATE) {
            mergeDrawableStates(drawableState, BUTTON_THIRD_STATE);
        }

        return drawableState;
    }
}
