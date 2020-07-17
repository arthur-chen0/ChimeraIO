package com.jht.androiduiwidgets.edittext.validator;

import java.util.Locale;

public class TimeValidator implements IUserInputValidator {

    private int min;
    private int max;

    public TimeValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        String range = String.format(Locale.ENGLISH,"%d:00 - %d:00", min, max);
        try {
            String[] timeParts = value.split(":");
            if (timeParts.length != 2) {
                return new InputValidationResult(InputValidationResult.RESULT.TIME_FORMAT_ERROR, range, value);
            }
            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = hours * 3600 + minutes * 60;
            if (seconds < min) {
                return new InputValidationResult(InputValidationResult.RESULT.LESS_THAN_MIN, range, value);
            } else if (hours > max) {
                return new InputValidationResult(InputValidationResult.RESULT.MORE_THAN_MAX, range, value);
            }

        } catch (Exception ignored) {
        }
        return new InputValidationResult(InputValidationResult.RESULT.VALID, null, value);
    }
}
