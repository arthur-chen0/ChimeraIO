package com.jht.androiduiwidgets;

import android.content.Context;

/**
 * This class is used to configure a keypad.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IInputConfiguration {
    // This is the current value of the keypad.  This should be set to the initial value.  It is updated on confirm only.


    /**
     *
     * @return The current input value.
     */
    String value();


    /**
     *
     * @return The current input value as a double.
     */
    double valueAsDouble();

    /**
     *
     * @return True if this input should be hidden from the user.
     */
    boolean isPassword();

    /**
     *
     * @return The minimum value for the input.
     */
    double min();

    /**
     *
     * @return The maximum value for the input.
     */
    double max();

    /**
     *
     * @return  The resolution for the input.  Typically this is 1.0, 0.5, 0.1, or 0.01
     */
    double resolution();

    /**
     *
     * @return  The maximum number of characters the input can hold.
     */
    int maxChars();

    /**
     * @param context  Used to load string resources.
     * @return The units to display.
     */
    String units(Context context);

    /**
     * @return The raw units.
     */
    String units();


    /**
     * @param context  Used to load string resources.
     * @return The input label (caption) to display.
     */
    String label(Context context);


    /**
     * @return The raw input label.
     */
    String label();


    /**
     * Sets the identifier.
     *
     * @return A unique id so callbacks can understan what input called this.
     */
    String identifier(String identifier);

    /**
     * @return A unique id so callbacks can understan what input called this.
     */
    String identifier();

    /**
     * If the input is a part of a dialog we may want to hide it (think keypads on the run screen).
     * @return  The number of ms the dialog should appear for.  0 if it should stay there until the user closes it.
     */
    int autoHide();


    /**
     * Set the value.  Use this to update the input value.
     * @param value  The new value.
     * @return       The new value
     */
    String value(String value);

    /**
     * Set the value.  Use this to update the input value.  It will use the resolution to format the value.
     * @param value  The new value.
     * @return       The new value
     */
    String value(double value);

    /**
     * Mark the input as a password input.
     * @param isPassword  True if the input is a password.
     * @return            The new setting.
     */
    boolean isPassword(boolean isPassword);

    /**
     * Update the min value
     * @param min   The new min value
     * @return      The new min value
     */
    double min(double min);

    /**
     * Update the max value
     * @param max   The new max value
     * @return      The new max value
     */
    double max(double max);

    /**
     * Update the resolution
     * @param resolution   The new resolution value
     * @return             The new resolution value
     */
    double resolution(double resolution);

    /**
     * Update the maximum characters
     * @param maxChars   The new maximum characters value
     * @return           The new maximum characters value
     */
    int maxChars(int maxChars);


    /**
     * Update the units string
     * @param units   The new units string value
     * @return              The new units string value
     */
    String units(String units);

    /**
     * Update the auto hide timer.  0 = do not auto hide.
     * @param autoHide   The new auto hide timeout in ms.
     * @return           The new auto hide timeout in ms.
     */
    int autoHide(int autoHide);


    /**
     * Increment the value based on the resolution.
     */
    void increment();

    /**
     * Decrement the value based on the resolution.
     */
    void decrement();

    /**
     * Used to disable the progress bar in the pill box.
     * @param showProgress  True if we should show the progress bar.
     */
    void showProgress(boolean showProgress);

    /**
     *
     * @return True if the pill box should show a progress bar.
     */
    boolean showProgress();

}
