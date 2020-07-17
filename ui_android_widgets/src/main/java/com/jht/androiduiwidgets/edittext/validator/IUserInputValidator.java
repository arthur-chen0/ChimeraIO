package com.jht.androiduiwidgets.edittext.validator;

public interface IUserInputValidator {
    InputValidationResult validate(String value, String previousValue);
}
