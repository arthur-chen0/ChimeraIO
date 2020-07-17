package com.jht.androiduiwidgets.edittext.validator;

import android.util.Log;

public class BasicInputValidator implements IUserInputValidator {

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        Log.e("bdayBug", "validating: " + value);
        return new InputValidationResult(InputValidationResult.RESULT.VALID, value, value);
    }
}
