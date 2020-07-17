package com.jht.androiduiwidgets.edittext;

import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import com.jht.androidcommonalgorithms.type.JHTCommonTypes;
import com.jht.androiduiwidgets.edittext.validator.IUserInputValidator;
import com.jht.androiduiwidgets.edittext.validator.TextInputValidator;
import com.jht.translations.LanguageHelper;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleUserTextInputProperties implements IUserTextInputProperties {

    private String inputValue = "";
    private String hint = "";
    private String label = "";
    private JHTCommonTypes.Unit units = JHTCommonTypes.Unit.Imperial;
    private JHTCommonTypes.DisplayUnit displayUnits = JHTCommonTypes.DisplayUnit.NONE;
    private IUserInputValidator validator = new TextInputValidator();
    private JHTCommonTypes.DateFormat dateFormat = JHTCommonTypes.DateFormat.YearMonthDay;
    private JHTCommonTypes.TimeFormat timeFormat = JHTCommonTypes.TimeFormat.Twelve_Hour;
    private UserTextInput.InputType inputType = UserTextInput.InputType.TEXT;
    private boolean isPassword = false;
    private UserTextInput.CAPITALIZE_MODES capitalizeMode = UserTextInput.CAPITALIZE_MODES.none;
    private int maxChars = Integer.MAX_VALUE;
    private int minChars = 0;
    private int imeOptions = EditorInfo.IME_ACTION_DONE;
    private boolean autoCorrect = false;
    private int textFilterFlags = 0;

    private double min;
    private double max;
    private double resolution;

    public SimpleUserTextInputProperties() {

    }

    public SimpleUserTextInputProperties(IUserTextInputProperties properties) {
        inputValue = properties.inputValue();
        hint = properties.hint();
        label = properties.label();

        units = properties.units();
        displayUnits = properties.displayUnits();
        validator = properties.validator();
        dateFormat = properties.dateFormat();
        timeFormat = properties.timeFormat();
        inputType = properties.inputType();
        isPassword = properties.isPassword();
        capitalizeMode = properties.capitalizeMode();
        maxChars = properties.maxChars();
        minChars = properties.minChars();
        imeOptions = properties.imeOptions();
        autoCorrect = properties.autoCorrect();
        textFilterFlags = properties.textFilterFlags();

        min = properties.min();
        max = properties.max();
        resolution = properties.resolution();
    }

    @Override
    public @NonNull String inputValue() {
        return inputValue;
    }

    @Override
    public @NonNull IUserTextInputProperties inputValue(@NonNull String inputValue) { this.inputValue = inputValue; return this; }

    @Override
    public @NonNull String hint() {
        return hint;
    }

    @Override
    public @NonNull IUserTextInputProperties hint(@NonNull String hint) { this.hint = hint; return this; }

    @Override
    public @NonNull String label() {
        return label;
    }

    @Override
    public @NonNull IUserTextInputProperties label(@NonNull String label) { this.label = label; return this; }

    @Override
    public @NonNull JHTCommonTypes.Unit units() {
        return units;
    }

    @Override
    public @NonNull IUserTextInputProperties units(@NonNull JHTCommonTypes.Unit units) {
        this.units = units; return this;
    }

    @Override
    public @NonNull JHTCommonTypes.DisplayUnit displayUnits() {
        return displayUnits;
    }

    @Override
    public @NonNull IUserTextInputProperties displayUnits(@NonNull JHTCommonTypes.DisplayUnit displayUnits) { this.displayUnits = displayUnits; return this; }

    @Override
    public @NonNull IUserInputValidator validator() {
        return validator;
    }

    @Override
    public @NonNull IUserTextInputProperties validator(@NonNull IUserInputValidator validator) { this.validator = validator; return this; }

    @Override
    public @NonNull JHTCommonTypes.DateFormat dateFormat() {
        return dateFormat;
    }

    @Override
    public @NonNull IUserTextInputProperties dateFormat(@NonNull JHTCommonTypes.DateFormat dateFormat) { this.dateFormat = dateFormat; return this; }

    @Override
    public @NonNull JHTCommonTypes.TimeFormat timeFormat() {
        return timeFormat;
    }
    public @NonNull IUserTextInputProperties timeFormat(@NonNull JHTCommonTypes.TimeFormat timeFormat) { this.timeFormat = timeFormat; return this; }

    @Override
    public @NonNull UserTextInput.InputType inputType() {
        return inputType;
    }

    @Override
    public @NonNull IUserTextInputProperties inputType(@NonNull UserTextInput.InputType inputType) { this.inputType = inputType; return this; }

    @Override
    public boolean isPassword() {
        return isPassword;
    }

    @Override
    public IUserTextInputProperties isPassword(boolean isPassword) { this.isPassword = isPassword; return this; }

    @Override
    public @NonNull UserTextInput.CAPITALIZE_MODES capitalizeMode() {
        return capitalizeMode;
    }

    @Override
    public @NonNull IUserTextInputProperties capitalizeMode(@NonNull UserTextInput.CAPITALIZE_MODES capitalizeMode) { this.capitalizeMode = capitalizeMode; return this; }

    @Override
    public int maxChars() {
        return maxChars;
    }

    @Override
    public IUserTextInputProperties maxChars(int maxChars) { this.maxChars = maxChars; return this; }

    @Override
    public int minChars() {
        return minChars;
    }

    @Override
    public IUserTextInputProperties minChars(int minChars) { this.minChars = minChars; return this; }

    @Override
    public int imeOptions() {
        return imeOptions;
    }

    @Override
    public IUserTextInputProperties imeOptions(int imeOptions) { this.imeOptions = imeOptions; return this; }


    @Override
    public boolean autoCorrect() {
        return autoCorrect;
    }

    @Override
    public IUserTextInputProperties autoCorrect(boolean autoCorrect) { this.autoCorrect = autoCorrect; return this; }

    @Override
    public int textFilterFlags() {
        return textFilterFlags;
    }

    @Override
    public IUserTextInputProperties textFilterFlags(int textFilterFlags) { this.textFilterFlags = textFilterFlags; return this; }


    @Override
    public double max() {
        return max;
    }

    @Override
    public IUserTextInputProperties max(double max) {
        this.max = max;
        if(resolution() != 0) {
            this.maxChars = LanguageHelper.getDecimalFormat(resolution()).format(max).length();
        }
        return this;
    }

    @Override
    public double min() {
        return min;
    }

    @Override
    public IUserTextInputProperties min(double min) { this.min = min; return this; }

    @Override
    public double resolution() {
        return resolution;
    }

    @Override
    public IUserTextInputProperties resolution(double resolution) { this.resolution = resolution; return this; }
}
