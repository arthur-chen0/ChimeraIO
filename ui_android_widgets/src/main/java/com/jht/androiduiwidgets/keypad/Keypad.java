package com.jht.androiduiwidgets.keypad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.androidcommonalgorithms.math.Resolution;
import com.jht.androiduiwidgets.IInputConfiguration;
import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.SimpleInputConfiguration;
import com.jht.translations.LanguageHelper;

/**
 * This class manages the keypad widgets that are shown throughout the software.
 */
@SuppressWarnings({"unused"})
public class Keypad extends RelativeLayout {


    /**
     * Used to notify the container that the keypad should close.
     */
    public interface OnAutoHide {
        void onAutoHide();
    }

    /**
     * This is used to run the auto correct functionality for the keypad.
     */
    private class AutoCorrect implements Runnable {

        // The value to flash.
        private String flashValue;

        // The value to set at the end.
        private String endValue;

        // The label to show in the units.  Typically this is min or max.
        private String label;

        // Manages the animation of the value.
        private Animation flashAnimation;

        // True if we want to allow the confirm button while flashing.
        private boolean confirmOnFlash;

        private boolean resetValueOnAnimationEnd = true;
        

        /**
         * Default constructor.
         *
         * @param flashValue        value to flash
         * @param endValue          value to set at the end
         * @param label             label to set in the units field.
         * @param confirmOnFlash    true if we want to allow the confirm button while flashing.
         */
        AutoCorrect(String flashValue, String endValue, String label, boolean confirmOnFlash) {
            this.flashValue = flashValue;
            this.endValue = endValue;
            this.label = label;
            this.confirmOnFlash = confirmOnFlash;
        }


        @Override
        public void run() {
            // Reset to the first entry.
            firstEntry = true;
            resetValueOnAnimationEnd = true;

            // Create the animation.
            flashAnimation = new AlphaAnimation(1, 0);
            flashAnimation.setDuration(440);
            flashAnimation.setInterpolator(new LinearInterpolator());
            flashAnimation.setRepeatCount(6);
            flashAnimation.setRepeatMode(Animation.REVERSE);
            flashAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    //confirmButton.setClickable(true);
                    if(resetValueOnAnimationEnd) {
                        value.setText(endValue);
                        resetUnitText();
                        confirmButton.setEnabled(true);
                    }
                    value.setAlpha(1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {}
            });

            keypadValue = endValue;
            value.setText(flashValue);
            if(label != null && !label.equals("")) {
                units.setText(label);
                units.setVisibility(VISIBLE);
            }
            if(confirmOnFlash) {
                confirmButton.setEnabled(true);
                //confirmButton.setClickable(true);
            }
            value.startAnimation(flashAnimation);

        }

        /**
         * Stop any current animation.
         */
        void stopAnimation() {
            resetValueOnAnimationEnd = false;
            if(flashAnimation != null) {
                flashAnimation.cancel();
                flashAnimation = null;
            }
        }
    }

    // Used to handle the auto correct feature.
    private AutoCorrect autoCorrect = null;

    // The value shown on the keypad.
    private TextView value;

    // The units shown on the keypad.
    private TextView units;

    // The confirm button which can be disabled at times.
    private ImageButton confirmButton;

    // True if this is the first entry key press.
    private boolean firstEntry = true;

    // Used to post the auto correct handler.
    private Handler handler;


    // Used to notify the container that the keypad should close.
    private OnAutoHide onAutoHide = null;

    // Contains the settings for the keypad.
    private IInputConfiguration keypadSettings;
    
    // The current keypad value.
    private String keypadValue = "";


    // The number views.  This helps us use the same on click handler for each.
    private int[] keyViews = {
            R.id.common_keypad_0_button,
            R.id.common_keypad_1_button,
            R.id.common_keypad_2_button,
            R.id.common_keypad_3_button,
            R.id.common_keypad_4_button,
            R.id.common_keypad_5_button,
            R.id.common_keypad_6_button,
            R.id.common_keypad_7_button,
            R.id.common_keypad_8_button,
            R.id.common_keypad_9_button,
    };

    /**
     * The default constructor for the keypad.
     *
     * @param context   context that owns the keypad.
     */
    public Keypad(Context context) {
        this(context, null, 0);
    }


    /**
     * The constructor for the keypad with attributes.
     *
     * @param context   context that owns the keypad.
     * @param attrs     the xml attributes for the keypad.
     */
    public Keypad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    /**
     * The constructor for the keypad with attributes and style
     *
     * @param context   context that owns the keypad.
     * @param attrs     the xml attributes for the keypad.
     * @param defStyle  the default style to use for the keypad.
     */
    public Keypad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Load the keypad view.
        LayoutInflater.from(context).inflate(R.layout.keypad, this, true);

        keypadSettings = new SimpleInputConfiguration();

        // Cache views.
        value = findViewById(R.id.common_keypad_value);
        units = findViewById(R.id.common_keypad_units);
        confirmButton = findViewById(R.id.common_keypad_confirm_button);

        // Customize based on XML attributes.
        if(attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Keypad, defStyle, R.style.jht_default_keyboard);

            findViewById(R.id.common_keypad_back_button).setBackgroundResource(a.getResourceId(R.styleable.Keypad_keypad_back_key_background, R.drawable.keypad_back_button_background));
            int keyBackground = a.getResourceId(R.styleable.Keypad_keypad_number_key_background, R.drawable.keypad_button_background);
            for (int keyView : keyViews) {
                findViewById(keyView).setBackgroundResource(keyBackground);
            }

            // Note this is stupid, but image button src does not work with the disabled state.  Only the background which is why we create a layered drawable.
            Drawable[] confirmDrawables = new Drawable[2];
            confirmDrawables[0] = getResources().getDrawable(a.getResourceId(R.styleable.Keypad_keypad_confirm_key_src, R.drawable.button_key_confirm_src));
            confirmDrawables[1] = getResources().getDrawable(a.getResourceId(R.styleable.Keypad_keypad_confirm_key_background, R.drawable.button_key_confirm_src));
            findViewById(R.id.common_keypad_confirm_button).setBackground(new LayerDrawable(confirmDrawables));


            int keypadValueBackgroundColor;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                findViewById(R.id.keypad_root_view).setBackgroundColor(a.getColor(R.styleable.Keypad_keypad_background, getResources().getColor(R.color.keypad_default_background, null)));
                keypadValueBackgroundColor = a.getColor(R.styleable.Keypad_keypad_value_background, getResources().getColor(android.R.color.black, null));
            }
            else {
                findViewById(R.id.keypad_root_view).setBackgroundColor(a.getColor(R.styleable.Keypad_keypad_background, getResources().getColor(R.color.keypad_default_background)));
                keypadValueBackgroundColor = a.getColor(R.styleable.Keypad_keypad_value_background, getResources().getColor(android.R.color.black));

            }
            findViewById(R.id.keypad_value_container).setBackgroundColor(keypadValueBackgroundColor);
            findViewById(R.id.keypad_value_container_padding).setBackgroundColor(keypadValueBackgroundColor);
            keypadSettings.autoHide(a.getInt(R.styleable.Keypad_keypad_auto_hide, 0));
            a.recycle();
        }


        // Setup the keypress on click listener.
        OnClickListener onKeyPressListener = v -> {

            for (int i = 0; i < keyViews.length; i++) {
                if (v.equals(findViewById(keyViews[i]))) {
                    onKeyPress("" + i);
                }
            }
        };

        for (int keyView : keyViews) {
            findViewById(keyView).setOnClickListener(onKeyPressListener);
        }

        // Handle the back button.
        findViewById(R.id.common_keypad_back_button).setOnClickListener(v -> {

            // Reset the auto correct and auto hide timers.
            resetTimers();

            if(firstEntry) {
                if(keypadSettings.isPassword()) {
                    return;
                }
                // On first entry clear the value.
                firstEntry = false;
                keypadValue = LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(0.0);
                value.setText(LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(0.0));
            }
            else {
                if(keypadSettings.resolution() >= 1.0) {
                    // This is an integer.  Delete the last character.
                    String inputValue = keypadValue;
                    if(inputValue.length() > 0) {
                        if ( inputValue.length() == 1 ){
                            if(keypadSettings.isPassword()) {
                                keypadValue = "";
                                value.setText("");
                            }
                            else {
                                keypadValue = LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(0.0);
                                value.setText(LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(0.0));
                            }
                        }
                        else {
                            value.setText(keypadValue = inputValue.substring(0, inputValue.length() - 1));
                        }
                    }
                }
                else {
                    // This is a double value.  Delete the last character shifting the values.
                    String inputValue = keypadValue;

                    // Rotate chars.
                    StringBuilder newInput = new StringBuilder();
                    for(int i = 0; i < inputValue.length() - 1; i++) {
                        if(inputValue.charAt(i+1) == '.') {
                            if(i == 0) {
                                newInput.append("0");
                            }
                            newInput.append(".");
                            newInput.append(inputValue.charAt(i));
                        }
                        else if(inputValue.charAt(i) != '.') {
                            newInput.append(inputValue.charAt(i));
                        }
                    }
                    value.setText(keypadValue = newInput.toString());
                }
            }

            // Validate the new input.
            validateInput();
        });

        // Create the handler for auto correct.
        handler = new Handler();

        // Initialize on confirm handler.
        findViewById(R.id.common_keypad_confirm_button).setOnClickListener(v -> keypadSettings.value(keypadValue));

    }

    /**
     * The higher level code will need to handle the confirm button.  Generally this will make the
     * input stick and then close the keypad.
     *
     * @param onClickListener   The callback to call when the confirm button is pressed.
     *
     */
    @SuppressWarnings("unused")
    public void setOnConfirm(final OnClickListener onClickListener) {
        findViewById(R.id.common_keypad_confirm_button).setOnClickListener(v -> {
            keypadSettings.value(keypadValue);
            onClickListener.onClick(v);
        });
    }

    /**
     * Generally this is called when the keypad is shown.  It initializes the UI with the current
     * input values and sets up the keypad for the first button press.
     */
    public void init() {

        if(keypadSettings.isPassword()) {
            value.setTransformationMethod(PasswordTransformationMethod.getInstance());
            value.setText(keypadValue = "");
        } else {
            value.setText(keypadValue = keypadSettings.value());
        }

        // Enable the confirm button.
        confirmButton.setEnabled(true);
        //confirmButton.setClickable(true);

        // Set the first entry to true.
        firstEntry = true;

        resetUnitText();
        startAutoHideTimer();
    }

    /**
     * Reset the unit text based on the input.  If the unit is 0 hide the text.
     */
    private void resetUnitText() {
        String unitValue = keypadSettings.units(getContext());
        if(unitValue == null || unitValue.equals("")) {
            units.setVisibility(View.GONE);
        } else {
            units.setVisibility(View.VISIBLE);
            units.setText(unitValue);
        }
    }

    /**
     * The value is not valid.  Disable the confirm button and prepare the auto correct feature.
     *
     * @param flashValue    Value to flash during the auto correct.
     * @param endValue      Value to set when auto correct is done.
     * @param label         Label to set telling the user what the flash value is. (e.g., min or max)
     * @param confirmOnFlash    True if we want to enable the confirm on flash.  Note the endValue must equal the flash value.
     */
    private void postAutoCorrect(String flashValue, String endValue, String label, boolean confirmOnFlash) {
        //confirmButton.setClickable(false);
        confirmButton.setEnabled(false);
        autoCorrect = new AutoCorrect(flashValue, endValue, label, confirmOnFlash && flashValue.equals(endValue));
        handler.postDelayed(autoCorrect, 2100);
    }


    /**
     * The value is not valid.  Disable the confirm button and prepare the auto correct feature.
     *
     * @param flashValue    Value to flash during the auto correct.
     * @param label         Label to set telling the user what the flash value is. (e.g., min or max)
     */
    private void postAutoCorrect(String flashValue, String label) {
        handler.removeCallbacksAndMessages(null);
        postAutoCorrect(flashValue, flashValue, label, true);
        startAutoHideTimer();
    }

    /**
     * Starts the auto hide timer if necessary.
     */
    private void startAutoHideTimer() {

        if(keypadSettings.autoHide() > 0) {
            handler.postDelayed(() -> {
                if(onAutoHide != null) {
                    onAutoHide.onAutoHide();
                }
            }, keypadSettings.autoHide());
        }
    }

    /**
     * Stop the auto correct.  Note this is called when a key is pressed.
     */
    private void resetTimers() {
        handler.removeCallbacksAndMessages(null);
        if(autoCorrect != null) {
            autoCorrect.stopAnimation();
        }
        autoCorrect = null;
        startAutoHideTimer();
    }

    /**
     *
     * @return The current keypad value as a double value.
     */
    private double getKeypadNumberValue() {
        if(keypadValue == null || keypadValue.equals("")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(keypadValue);
        }
        catch(Exception ex) {
            return 0.0;
        }
    }

    private String formatKeypadValue(double value) {
        return LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(value);
    }

    /**
     * The user has entered a new value.  Validate it and kick off the auto correct feature if invalid.
     *
     */
    private void validateInput() {
        if(keypadValue == null || keypadValue.equals("")) {
            confirmButton.setEnabled(false);
            return;
        }
        confirmButton.setEnabled(true);
        //confirmButton.setClickable(true);
        double value = getKeypadNumberValue();
        double resolution = keypadSettings.resolution();
        int valueI = (int)Math.round(value);
        int resolutionI = (int)Math.round(resolution);
        if(Resolution.compare(value, keypadSettings.max(), resolution) > 0) {
            postAutoCorrect(formatKeypadValue(keypadSettings.max()), keypadSettings.value(), getResources().getString(R.string.max_lc), false);
        }
        else if(Resolution.compare(value, keypadSettings.min(), resolution) < 0) {
            postAutoCorrect(formatKeypadValue(keypadSettings.min()), getResources().getString(R.string.min_lc));
        }
        else {
            // Check the resolution.  First convert the value and resolution to an integer.
            int numDecimals = Resolution.numDecimals(keypadSettings.resolution());
            value *= (10.0 * numDecimals);
            resolution *= (10.0 * numDecimals);
            if(resolution != 0 && (int)value % (int)resolution != 0) {
                // The value entered is not within the proper resolution.  Determine whether we need to
                // go up or down.  For example, treadmill incline has a resolution of 0.5.  If the user enters
                // 0.3 we will auto correct to 0.5.  If they enter 2 we will auto correct to 0.0.
                int down = (int)value / (int)resolution * (int)resolution;
                int up = down + (int)resolution;
                if(Math.abs(value - up) >= Math.abs(value - down)) {
                    postAutoCorrect(formatKeypadValue(down / (10.0 * numDecimals)), null);
                }
                else {
                    postAutoCorrect(formatKeypadValue(up / (10.0 * numDecimals)), null);
                }
            }
            else if(resolutionI > 1 && valueI % resolutionI != 0) {
                // For ascent incline we need to handle a resolution of 5.
                int down = valueI - valueI % resolutionI;
                int up = down + resolutionI;
                if(Math.abs(valueI - up) >= Math.abs(valueI - down)) {
                    postAutoCorrect(formatKeypadValue(down), null);
                }
                else {
                    postAutoCorrect(formatKeypadValue(up), null);
                }
            }
        }
    }

    /**
     * Handle a number key press.
     *
     * @param keyEntered   The number pressed (0-9).
     */
    private void onKeyPress(String keyEntered) {
        // Reset the auto correct and auto hide timers.
        resetTimers();

        String zero = LanguageHelper.getDecimalFormat(keypadSettings.resolution()).format(0);

        if (firstEntry) {
            firstEntry = false;
            if(zero.length() == 1) {
                value.setText(keypadValue = keyEntered);
            } else {
                value.setText(keypadValue = (zero.substring(0, zero.length() - 1) + keyEntered));
            }

        } else {
            int leadingZero = keypadValue.length() > 0 && keypadValue.charAt(0) == '0' ? 1 : 0;

            if(keypadValue.length() - leadingZero < keypadSettings.maxChars()) {
                if(zero.length() == 1) {
                    if(!(keypadValue.equals("0") && keyEntered.equals("0"))) {
                        if(keypadValue.equals("0")) {
                            value.setText(keypadValue = (keyEntered));
                        }
                        else {
                            value.setText(keypadValue = (keypadValue + keyEntered));
                        }
                    }
                } else {

                    String[] parts = keypadValue.split("\\.");
                    if (parts[0].equals("0")) {
                        parts[0] = parts[1].substring(0, 1);
                    } else {
                        parts[0] += parts[1].substring(0, 1);
                    }
                    if (parts[1].length() > 1) {
                        parts[1] = parts[1].substring(1) + keyEntered;
                    } else {
                        parts[1] = keyEntered;
                    }
                    value.setText(keypadValue = (parts[0] + "." + parts[1]));
                }
            }
        }

        validateInput();
    }

    /**
     * Resets the keypad and input to the original value.
     */
    public void reset() {
        value.setText(keypadValue = keypadSettings.value());
    }

    /**
     * Used to set the auto hide callback.
     */
    public void setOnAutoHide(OnAutoHide onAutoHide) {
        this.onAutoHide = onAutoHide;
    }

    /**
     *
     * @return The current keypad settings.  This can be used to modify the keypad settings.
     */
    public IInputConfiguration getSettings() {
        return keypadSettings;
    }

    /**
     * Set the keypad settings.
     *
     * @param keypadSettings    The new settings.
     */
    public void setKeypadSettings(IInputConfiguration keypadSettings) {
        this.keypadSettings = keypadSettings;
        init();
    }

    /**
     *
     * @return The current keypadValue
     */
    public String getKeypadValue() {
        return keypadValue;
    }

}
