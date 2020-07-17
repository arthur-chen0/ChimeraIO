package com.jht.androiduiwidgets.edittext.validator;

public class TextInputValidator implements IUserInputValidator {

    private int minChars = 0;
    private int maxChars = Integer.MAX_VALUE;

    public TextInputValidator() {

    }
    public TextInputValidator(int minChars, int maxChars) {
        this.minChars = minChars;
        this.maxChars = maxChars;
    }
    @Override
    public InputValidationResult validate(String value, String previousValue) {
        if(value.length() < minChars) {
            return new InputValidationResult(InputValidationResult.RESULT.TOO_FEW_CHARACTERS, null, value);
        }
        else if(value.length() > maxChars) {
            return new InputValidationResult(InputValidationResult.RESULT.TOO_MANY_CHARACTERS, null, value);
        }
        return new InputValidationResult(InputValidationResult.RESULT.VALID, value, value);
    }
}
