package com.jht.androiduiwidgets.edittext.validator;

import com.jht.androidcommonalgorithms.math.Resolution;
import com.jht.translations.LanguageHelper;

import java.util.Locale;

@SuppressWarnings("unused")
public class DoubleInputValidator extends TextInputValidator {

    private double min = Long.MIN_VALUE;
    private double max = Long.MAX_VALUE;
    private double resolution = 1.0;

    public DoubleInputValidator() {

    }
    public DoubleInputValidator(double min, double max, double resolution) {
        this.min = min;
        this.max = max;
        this.resolution = resolution;
    }
    public DoubleInputValidator(double min, double max, double resolution, int minChars, int maxChars) {
        super(minChars, maxChars);

        this.min = min;
        this.max = max;
        this.resolution = resolution;
    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        String valueToDisplay = value;
        try {
            double v = Double.parseDouble(value);

            valueToDisplay = LanguageHelper.getDecimalFormat(resolution).format(v);
            if(Resolution.compare(v, min, resolution) < 0) {
                return new InputValidationResult(InputValidationResult.RESULT.LESS_THAN_MIN, String.format(Locale.ENGLISH, "%s - %s", LanguageHelper.getDecimalFormat(resolution).format(min), LanguageHelper.getDecimalFormat(resolution).format(max)), valueToDisplay);

            }
            else if(Resolution.compare(v, max, resolution) > 0) {
                return new InputValidationResult(InputValidationResult.RESULT.MORE_THAN_MAX, String.format(Locale.ENGLISH, "%s - %s", LanguageHelper.getDecimalFormat(resolution).format(min), LanguageHelper.getDecimalFormat(resolution).format(max)), valueToDisplay);
            }
            else {
                // Check the resolution.  First convert the value and resolution to an integer.
                int numDecimals = Resolution.numDecimals(resolution);
                int valueI = (int)Math.round(v);
                int resolutionI = (int)Math.round(resolution);
                if(resolution != 0 && (int)v % (int)resolution != 0) {
                    // The value entered is not within the proper resolution.  Determine whether we need to
                    // go up or down.  For example, treadmill incline has a resolution of 0.5.  If the user enters
                    // 0.3 we will auto correct to 0.5.  If they enter 2 we will auto correct to 0.0.
                    int down = (int)v / (int)resolution * (int)resolution;
                    int up = down + (int)resolution;
                    if(Math.abs(v - up) >= Math.abs(v - down)) {
                        return new InputValidationResult(InputValidationResult.RESULT.INVALID_RESOLUTION, LanguageHelper.getDecimalFormat(resolution).format(down / (10.0 * numDecimals)), valueToDisplay);
                    }
                    else {
                        return new InputValidationResult(InputValidationResult.RESULT.INVALID_RESOLUTION, LanguageHelper.getDecimalFormat(resolution).format(up / (10.0 * numDecimals)), valueToDisplay);
                    }
                }
                else if(resolutionI > 1 && valueI % resolutionI != 0) {
                    // For ascent incline we need to handle a resolution of 5.
                    int down = valueI - valueI % resolutionI;
                    int up = down + resolutionI;
                    if(Math.abs(valueI - up) >= Math.abs(valueI - down)) {
                        return new InputValidationResult(InputValidationResult.RESULT.INVALID_RESOLUTION, LanguageHelper.getDecimalFormat(resolution).format(down), valueToDisplay);
                    }
                    else {
                        return new InputValidationResult(InputValidationResult.RESULT.INVALID_RESOLUTION, LanguageHelper.getDecimalFormat(resolution).format(up), valueToDisplay);
                    }
                }
            }
        }
        catch(Exception ignored) {}

        return super.validate(valueToDisplay, previousValue);
    }
}
