package com.jht.androiduiwidgets.edittext.validator;

import java.util.Locale;

@SuppressWarnings("unused")
public class LongInputValidator extends TextInputValidator {

    private long min = Long.MIN_VALUE;
    private long max = Long.MAX_VALUE;

    public LongInputValidator() {

    }
    public LongInputValidator(long min, long max) {
        this.min = min;
        this.max = max;
    }
    public LongInputValidator(long min, long max, int minChars, int maxChars) {
        super(minChars, maxChars);

        this.min = min;
        this.max = max;

    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        try {
            long v = Long.parseLong(value);
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
