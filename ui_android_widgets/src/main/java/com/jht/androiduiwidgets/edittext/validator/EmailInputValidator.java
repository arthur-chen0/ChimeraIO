package com.jht.androiduiwidgets.edittext.validator;

import android.os.Parcelable;
import android.util.Patterns;

@SuppressWarnings("unused")
public class EmailInputValidator extends TextInputValidator {


    public EmailInputValidator() {

    }

    public EmailInputValidator(int minChars, int maxChars) {
        super(minChars, maxChars);
    }

    @Override
    public InputValidationResult validate(String value, String previousValue) {
        if(Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            return super.validate(value, previousValue);
        }
        return new InputValidationResult(InputValidationResult.RESULT.EMAIL_ERROR, value, value);
    }
}
