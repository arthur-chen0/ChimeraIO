package com.jht.androiduiwidgets.edittext.validator;

import java.util.Locale;

@SuppressWarnings("unused")
public class IntegerInputValidator extends TextInputValidator {

    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerInputValidator() {

    }
    public IntegerInputValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }
    public IntegerInputValidator(int min, int max, int minChars, int maxChars) {
        super(minChars, maxChars);

        this.min = min;
        this.max = max;

    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        try {
            int v = Integer.parseInt(value);
            if(v < min) {
                return new InputValidationResult(InputValidationResult.RESULT.LESS_THAN_MIN, String.format(Locale.ENGLISH, "%d - %d", min, max), value);

            }
            else if(v > max) {
                return new InputValidationResult(InputValidationResult.RESULT.MORE_THAN_MAX, String.format(Locale.ENGLISH, "%d - %d", min, max), value);
            }
        }
        catch(Exception ignored) {}

        return super.validate(value, previousValue);
    }
}
