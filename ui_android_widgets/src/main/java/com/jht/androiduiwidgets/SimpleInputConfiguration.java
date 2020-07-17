package com.jht.androiduiwidgets;

import android.content.Context;

import com.jht.translations.LanguageHelper;

/**
 * This class is used to configure an input.
 */
@SuppressWarnings({"unused"})
public class SimpleInputConfiguration implements IInputConfiguration {
    // This is the current value of the input.  This should be set to the initial value.  It is updated on confirm only.
    private String value = "";

    // True if the keypad should treat the input as a password.
    private boolean isPassword = false;

    // The minimum value for the input.
    private double min = Double.MIN_VALUE;

    // The maximum value for the input.
    private double max = Double.MAX_VALUE;

    // The resolution for the input.  1.0 = Integer.
    private double resolution = 1.0;

    // The maximum number of characters that can be entered.
    private int maxChars = Integer.MAX_VALUE;

    // The units for the input value.
    private String units = "";

    // Set to non zero if the input should hide.  This value is the number of ms to wait until
    // we auto hide the keypad.
    private int autoHide = 0;

    private String label = "";

    private String identifier = "";

    private boolean showProgress = true;

    public SimpleInputConfiguration() {

    }

    public SimpleInputConfiguration(double min, double max, double value, double resolution, String label, String units, String identifier) {
        this.min = min;
        this.max = max;
        this.resolution = resolution;
        this.label = label;
        this.units = units;
        this.identifier = identifier;
        value(value);
    }

    public SimpleInputConfiguration(IInputConfiguration input) {
        min(input.min());
        max(input.max());
        value(input.value());
        resolution(input.resolution());
        label(input.label());
        units(input.units());
        identifier(input.identifier());
        maxChars(input.maxChars());
        isPassword(input.isPassword());
        autoHide(input.autoHide());
    }

    /**
     *
     * @return The current input value.
     */
    @Override
    public String value() { return value; }

    @Override
    public double valueAsDouble() {
        return value == null || value.isEmpty() ? 0.0 : Double.parseDouble(value);
    }

    /**
     *
     * @return True if this input should be hidden from the user.
     */
    @Override
    public boolean isPassword() { return isPassword; }

    /**
     *
     * @return The minimum value for the input.
     */
    @Override
    public double min() { return min; }

    /**
     *
     * @return The maximum value for the input.
     */
    @Override
    public double max() { return max; }

    /**
     *
     * @return  The resolution for the input.  Typically this is 1.0, 0.5, 0.1, or 0.01
     */
    @Override
    public double resolution() { return resolution; }

    /**
     *
     * @return  The maximum number of characters the input can hold.
     */
    @Override
    public int maxChars() { return maxChars; }

    /**
     * @param context  Used to load string resources.
     * @return The units to display.
     */
    @Override
    public String units(Context context) { return LanguageHelper.loadResource(context, units); }
    @Override
    public String units() { return units; }

    public String label(String label) {
        this.label = label;
        return label;
    }

    @Override
    public String label(Context context) {
        return LanguageHelper.loadResource(context, label);
    }


    @Override
    public String label() {
        return label;
    }

    @Override
    public String identifier(String identifier) {
        this.identifier = identifier;
        return identifier;
    }


    @Override
    public String identifier() {
        return identifier;
    }

    /**
     * If the input is a part of a dialog we may want to hide it (think keypads on the run screen).
     * @return  The number of ms the dialog should appear for.  0 if it should stay there until the user closes it.
     */
    @Override
    public int autoHide() { return autoHide; }

    /**
     * Set the value.  Use this to update the input value.
     * @param value  The new value.
     * @return       The new value
     */
    @Override
    public String value(String value) { return this.value = value; }

    /**
     * Set the value.  Use this to update the input value.  It will use the resolution to format the value.
     * @param value  The new value.
     * @return       The new value
     */
    public String value(double value) {
        this.value = LanguageHelper.getDecimalFormat(resolution).format(value);
        return this.value;
    }

    /**
     * Mark the input as a password input.
     * @param isPassword  True if the input is a password.
     * @return            The new setting.
     */
    @Override
    public boolean isPassword(boolean isPassword) { return this.isPassword = isPassword; }

    /**
     * Update the min value
     * @param min   The new min value
     * @return      The new min value
     */
    @Override
    public double min(double min) { return this.min = min; }

    /**
     * Update the max value
     * @param max   The new max value
     * @return      The new max value
     */
    @Override
    public double max(double max) { return this.max = max; }

    /**
     * Update the resolution
     * @param resolution   The new resolution value
     * @return             The new resolution value
     */
    @Override
    public double resolution(double resolution) { return this.resolution = resolution; }

    /**
     * Update the maximum characters
     * @param maxChars   The new maximum characters value
     * @return           The new maximum characters value
     */
    @Override
    public int maxChars(int maxChars) { return this.maxChars = maxChars; }

    /**
     * Update the units string
     * @param units   The new units string value
     * @return              The new units string value
     */
    @Override
    public String units(String units) { return this.units = units; }


    /**
     * Update the auto hide timer.  0 = do not auto hide.
     * @param autoHide   The new auto hide timeout in ms.
     * @return           The new auto hide timeout in ms.
     */
    @Override
    public int autoHide(int autoHide) { return this.autoHide = autoHide; }

    @Override
    public void increment() {
        double v = valueAsDouble() + resolution;
        if(v > max) {
            v = max;
        }
        value(v);
    }

    @Override
    public void decrement() {
        double v = valueAsDouble() - resolution;
        if(v < min) {
            v = min;
        }
        value(v);
    }

    @Override
    public void showProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    @Override
    public boolean showProgress() {
        return showProgress;
    }

}
