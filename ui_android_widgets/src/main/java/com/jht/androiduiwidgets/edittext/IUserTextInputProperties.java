package com.jht.androiduiwidgets.edittext;

import androidx.annotation.NonNull;

import com.jht.androidcommonalgorithms.type.JHTCommonTypes;
import com.jht.androidcommonalgorithms.type.JHTCommonTypes.Unit;
import com.jht.androiduiwidgets.edittext.validator.IUserInputValidator;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface IUserTextInputProperties {
    @NonNull String inputValue();
    @NonNull IUserTextInputProperties inputValue(@NonNull String value);

    @NonNull String hint();
    IUserTextInputProperties hint(@NonNull String hint);
    @NonNull String label();
    IUserTextInputProperties label(@NonNull String label);
    @NonNull IUserInputValidator validator();
    IUserTextInputProperties validator(@NonNull IUserInputValidator validator);
    @NonNull JHTCommonTypes.DateFormat dateFormat();
    @NonNull IUserTextInputProperties dateFormat(@NonNull JHTCommonTypes.DateFormat dateFormat);
    @NonNull JHTCommonTypes.TimeFormat timeFormat();
    @NonNull IUserTextInputProperties timeFormat(@NonNull JHTCommonTypes.TimeFormat timeFormat);


    @NonNull UserTextInput.InputType inputType();
    @NonNull IUserTextInputProperties inputType(@NonNull UserTextInput.InputType inputType);
    boolean isPassword();
    IUserTextInputProperties isPassword(boolean isPassword);
    @NonNull UserTextInput.CAPITALIZE_MODES capitalizeMode();
    @NonNull IUserTextInputProperties capitalizeMode(@NonNull UserTextInput.CAPITALIZE_MODES capitalizeMode);
    int maxChars();
    IUserTextInputProperties maxChars(int maxChars);
    int minChars();
    IUserTextInputProperties minChars(int minChars);
    int imeOptions();
    IUserTextInputProperties imeOptions(int imeOptions);
    boolean autoCorrect();
    IUserTextInputProperties autoCorrect(boolean autoCorrect);
    int textFilterFlags();
    IUserTextInputProperties textFilterFlags(int textFilterFlags);
    double resolution();
    IUserTextInputProperties resolution(double resolution);

    double min();
    IUserTextInputProperties min(double min);
    double max();
    IUserTextInputProperties max(double max);

    @NonNull Unit units();
    @NonNull IUserTextInputProperties units(@NonNull Unit units);
    @NonNull JHTCommonTypes.DisplayUnit displayUnits();
    @NonNull IUserTextInputProperties displayUnits(@NonNull JHTCommonTypes.DisplayUnit displayUnit);

}
