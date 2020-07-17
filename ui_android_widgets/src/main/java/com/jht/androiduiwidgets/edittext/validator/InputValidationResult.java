package com.jht.androiduiwidgets.edittext.validator;

public class InputValidationResult {
    public enum RESULT {
        VALID,
        CHECKING,
        LESS_THAN_MIN,
        MORE_THAN_MAX,
        INVALID_RESOLUTION,
        TOO_FEW_CHARACTERS,
        TOO_MANY_CHARACTERS,
        IP_ADDRESS_ERROR,
        TIME_FORMAT_ERROR,
        EMAIL_ERROR,
        GENERAL_ERROR
    }

    private RESULT result;
    private String autoCorrect;
    private String value;

    public InputValidationResult(RESULT result, String autoCorrect, String value) {
        this.result = result;
        this.autoCorrect = autoCorrect;
        this.value = value;
    }

    public String value() { return value; }
    public RESULT result() { return result; }
    public String autoCorrect() { return autoCorrect; }
}
