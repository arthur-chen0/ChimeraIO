package com.jht.androiduiwidgets.edittext;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.os.Build;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

import com.jht.androidcommonalgorithms.timer.PeriodicTimer;
import com.jht.androidcommonalgorithms.type.JHTCommonTypes;
import com.jht.androidcommonalgorithms.util.ContextUtil;
import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.autofit.AutofitEditTextView;
import com.jht.androiduiwidgets.dialog.DialogDatePicker;
import com.jht.androiduiwidgets.edittext.validator.BasicInputValidator;
import com.jht.androiduiwidgets.edittext.validator.DoubleInputValidator;
import com.jht.androiduiwidgets.edittext.validator.EmailInputValidator;
import com.jht.androiduiwidgets.edittext.validator.IPAddressInputValidator;
import com.jht.androiduiwidgets.edittext.validator.IUserInputValidator;
import com.jht.androiduiwidgets.edittext.validator.InputValidationResult;
import com.jht.androiduiwidgets.edittext.validator.IntegerInputValidator;
import com.jht.androiduiwidgets.edittext.validator.TextInputValidator;
import com.jht.androiduiwidgets.edittext.validator.TimeValidator;
import com.jht.androiduiwidgets.helper.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//import java.util.logging.Handler;

/**
 * This class manages an editable field that the requires a keyboard or keypad.  We extend the
 * base Android EditText so that we can get labels and add units in the field.  The floating hint
 * is nice in the support library, but we cannot style the label as we would like.  This class
 * also takes care of validating the input using XML attributes or a validator passed.
 *
 *
 */
//@SuppressWarnings("unused")
@SuppressWarnings("unused")
public class UserTextInput extends LinearLayout {


    private static class UserTextLengthFilter extends InputFilter.LengthFilter {

        boolean active = true;

        UserTextLengthFilter(int max) {
            super(max);
        }

        void setActive(boolean active) { this.active = active; }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(active) {
                return super.filter(source, start, end, dest, dstart, dend);
            }
            return source;
        }
    }


    /**
     * Callback to notify the parent when a keyboard action is pressed.  This is useful when we
     * want the enter button to perform an action.
     *
     */
    public interface IOnIMEAction {
        boolean onIMEAction(int action);
    }

    /**
     * Callback to notify the parent view that the text has changed.
     */
    public interface IOnTextChanged {
        void onTextChanged(String text);
    }


    /**
     * Callback to notify the parent view that the focus has changed.
     */
    public interface IOnFocusChanged {
        void onFocusChanged(boolean hasFocus);
    }

    /**
     * Callback to notify the parent view that the user entered invalid text.
     */
    public interface IOnValidationError {
        String onTextInvalid(InputValidationResult result);
    }

    public interface IOnBirthdaySet {
        void onBirthdateSet();
    }

    // Flags used to set text filters.
    public static final int TEXT_FILTER_NONE = 0;
    public static final int TEXT_FILTER_DIGITS = 1;
    public static final int TEXT_FILTER_UNDERSCORES = 2;
    public static final int TEXT_FILTER_SPACES = 4;
    public static final int TEXT_FILTER_ALL_CAPS = 8;

    // Defines where to put the error message.  See the xID registration page for an example of how
    // this is used.
    private static final int ERROR_MESSAGE_NONE = 0;
    private static final int ERROR_MESSAGE_LEFT = 1;
    private static final int ERROR_MESSAGE_RIGHT = 2;


    // Defines how we want to capitalize the letters in the input.
    public enum CAPITALIZE_MODES {
        user_defined, // No speciaL CapitalizatioN.
        sentence,     // Auto capitalize the first letter in a sentence.
        word,         // Auto Capitalize The First Letter In Each Word.
        all,          // ALL CAPS
        none         // all lower case
    }


    /**
     * Used to map the input type XML attribute to an input type.  This is used for text validation.
     */
    public enum InputType {
        INTEGER,
        DOUBLE,
        NUMBER,
        TEXT,
        IP_ADDRESS,
        URL,
        EMAIL,
        TIME_HHMM_TO_S,
        TIME_CLOCK,
        DATE
    }


    // Cached views in the layout.
    private AutofitEditTextView text;
    private TextView autoCorrectValue;

    // True if the view is read only.
    private boolean readOnly = false;

    // This is the window animation to use for the picker as it is launched.  This is used for
    // date and time pickers.
    private int pickerAnimation;

    // Cache the calendar to show the correct value for the pickers.
    private Calendar calendar;

    // These are used to handle the date and time pickers.
    private DatePickerDialog.OnDateSetListener onDateSet;
    private TimePickerDialog.OnTimeSetListener onTimeSet;

    // The callbacks when the user inputs data.
    private IOnTextChanged onTextChanged;
    private IOnFocusChanged onFocusChanged;
    private IOnValidationError onTextInvalid;
    private IOnIMEAction onIMEAction;
    private IOnBirthdaySet onBirthdaySet;

    // When we bounce back and forth between read only and writable the text needs to move.
    private int textViewGravity;


    // Used to filter the input.
    private UserInputFilter textFilter;

    // Defines all of the properties that define the input.
    private IUserTextInputProperties properties;

    // The validator used to validate the input.
    private IUserInputValidator inputValidator;

    // Used to control the length filter.  We need to disable it when we add the label.
    private UserTextLengthFilter lengthFilter = null;

    private Calendar currCalendar;

    private boolean multiline = false;


    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the com.jht.view.
     */
    public UserTextInput(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the com.jht.view.
     * @param attrs     Attributes that are set from the com.jht.view's XML.
     */
    public UserTextInput(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the com.jht.view.
     * @param attrs     Attributes that are set from the com.jht.view's XML.
     * @param defStyleAttr  Default style for the com.jht.view.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"InflateParams", "CustomViewStyleable", "ClickableViewAccessibility"})
    public UserTextInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        multiline = false;

        SimpleUserTextInputProperties defaultProperties = new SimpleUserTextInputProperties();
        properties = defaultProperties;


        // Inflate the selector layout.
        View view = LayoutInflater.from(context).inflate(R.layout.user_text_input, this, true);

        text = findViewById(R.id.user_text_input_edit);
        autoCorrectValue = findViewById(R.id.jht_edit_text_auto_correct_value);
        AppCompatTextView label = findViewById(R.id.user_text_input_label);

        // Update the com.jht.view's options based on XML attributes passed.
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UserTextInput, defStyleAttr, 0);

            defaultProperties.inputType(InputType.values()[a.getInt(R.styleable.UserTextInput_input_type, InputType.TEXT.ordinal())]);

            defaultProperties.min(a.getFloat(R.styleable.UserTextInput_min_value, Integer.MIN_VALUE));
            defaultProperties.max(a.getFloat(R.styleable.UserTextInput_max_value, Integer.MAX_VALUE));
            defaultProperties.resolution(a.getFloat(R.styleable.UserTextInput_input_resolution, 1.0f));
            defaultProperties.minChars(a.getInt(R.styleable.UserTextInput_min_chars, 0));
            defaultProperties.maxChars(a.getInt(R.styleable.UserTextInput_max_chars, Integer.MAX_VALUE));
            defaultProperties.autoCorrect(a.getBoolean(R.styleable.UserTextInput_auto_correct, false));
            defaultProperties.isPassword(a.getBoolean(R.styleable.UserTextInput_password, false));
            defaultProperties.textFilterFlags(a.getInt(R.styleable.UserTextInput_textfilter, TEXT_FILTER_NONE));
            updateErrorMessageStyle(a.getInt(R.styleable.UserTextInput_error_message, ERROR_MESSAGE_NONE));
            String defaultValue = a.getString(R.styleable.UserTextInput_default_value);
            defaultProperties.inputValue(defaultValue == null ? "" : defaultValue);

            pickerAnimation = a.getResourceId(R.styleable.UserTextInput_picker_animation, 0);
            multiline = a.getBoolean(R.styleable.UserTextInput_multiline, false);

            text.setId(a.getResourceId(R.styleable.UserTextInput_secondary_id, R.id.user_text_input_edit));

            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.JHTGeneralWidget, defStyleAttr, 0);
            CharSequence labelText = a.getText(R.styleable.JHTGeneralWidget_label);
            readOnly = a.getBoolean(R.styleable.JHTGeneralWidget_read_only, false);
            if(labelText == null || labelText.toString().isEmpty()) {
                defaultProperties.label("");
            }
            else {
                defaultProperties.label(labelText.toString());
            }
            defaultProperties.displayUnits(JHTCommonTypes.DisplayUnit.values()[a.getInt(R.styleable.JHTGeneralWidget_units, 0)]);

            int labelSize = a.getInteger(R.styleable.JHTGeneralWidget_label_size_dp, -1);
            if(labelSize != -1) {
                TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(label, 5, labelSize, 1, TypedValue.COMPLEX_UNIT_DIP);
            }
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.hint}, defStyleAttr, 0);
            CharSequence hint = a.getText(0);
            if(hint != null) {
                defaultProperties.hint(hint.toString());
            }
            else {
                defaultProperties.hint("");
            }
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.imeOptions}, defStyleAttr, 0);
            defaultProperties.imeOptions(a.getInt(0, EditorInfo.IME_NULL));
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.inputType}, defStyleAttr, 0);
            int inputType = a.getInt(0, EditorInfo.TYPE_NULL);
            text.setInputType(inputType);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.gravity}, defStyleAttr, 0);
            textViewGravity = a.getInt(0, Gravity.START | Gravity.CENTER_VERTICAL);
            a.recycle();

            a = context.obtainStyledAttributes(attrs, new int[] { android.R.attr.nextFocusForward}, defStyleAttr, 0);
            int nextFocusId = a.getResourceId(0, 0);
            if(nextFocusId != 0) {
                setNextId(nextFocusId);
            }

            a.recycle();

        }

        // Initialize the input.
        setReadOnly(readOnly);
        updateInput();


        if(multiline) {
            int inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE | EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE;
            text.setSingleLine(false);
            text.setMaxLines(100);
            text.setMinLines(5);

            text.setSizeToFit(false);
            text.setHorizontallyScrolling(false);
            text.setGravity(Gravity.START | Gravity.TOP);
            (findViewById(R.id.user_text_input_container)).getLayoutParams().height *= 5;
            ViewGroup.LayoutParams lp = text.getLayoutParams();
            lp.height = (findViewById(R.id.user_text_input_container)).getLayoutParams().height;
            text.setLayoutParams(lp);
            text.setInputType(inputType);
        }


        // Setup the onFocus and onAction listeners so we can validate the input.
        setupListeners();

    }

    @SuppressWarnings("unused")
    public void setTextId(int id) {
        text.setId(id);
    }

    public void setNextId(int id) {
        text.setNextFocusForwardId(id);
        text.setNextFocusRightId(id);
    }

    /**
     * This updates the UI based on whether we have an error message.  See the xID registration page
     * for an example of this.
     *
     * @param style None, Left, or Right
     */
    private void updateErrorMessageStyle(int style) {
        switch(style) {
            case ERROR_MESSAGE_NONE :
                findViewById(R.id.user_text_input_error_message_container).setVisibility(View.GONE);
                break;
            case ERROR_MESSAGE_LEFT :
                findViewById(R.id.user_text_input_error_message_container).setVisibility(View.VISIBLE);
                break;
            case ERROR_MESSAGE_RIGHT :
                // The default layout is left to right.  We need to flip it.

                LinearLayout mainContainer = findViewById(R.id.user_text_input_parent);
                int numChildren = mainContainer.getChildCount();
                View[] children = new View[numChildren];
                for (int i=0; i < numChildren; i++){
                    children[i] = mainContainer.getChildAt(i);
                }
                mainContainer.removeAllViews();
                for(int i = numChildren; i > 0; i--) {
                    mainContainer.addView(children[i-1]);
                }

                findViewById(R.id.user_text_input_error_message_container).setVisibility(View.VISIBLE);
                LinearLayout innerErrorContainer = findViewById(R.id.user_text_input_error_message_inner_container);
                numChildren = innerErrorContainer.getChildCount();
                children = new View[numChildren];
                for (int i=0; i < numChildren; i++){
                    children[i] = innerErrorContainer.getChildAt(i);
                }
                innerErrorContainer.removeAllViews();
                for(int i = numChildren; i > 0; i--) {
                    innerErrorContainer.addView(children[i-1]);
                }

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) innerErrorContainer.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                innerErrorContainer.setLayoutParams(params);
                break;
        }
    }

    public void setValidator(IUserInputValidator validator) {
        inputValidator = validator;
    }

    /**
     * Create a validator based on the input.
     */
    private void setupValidator() {
        switch(properties.inputType()) {

            case INTEGER:
                inputValidator = new IntegerInputValidator((int)Math.round(properties.min()), (int)properties.max(), properties.minChars(), properties.maxChars());
                break;
            case DOUBLE:
                inputValidator = new DoubleInputValidator(properties.min(), properties.max(), properties.resolution(), properties.minChars(), properties.maxChars());
                break;
            case TEXT:
                inputValidator = new TextInputValidator(properties.minChars(), properties.maxChars());
                break;
            case NUMBER:
                inputValidator = new TextInputValidator(properties.minChars(), properties.maxChars());
                break;
            case IP_ADDRESS:
                inputValidator = new IPAddressInputValidator();
                break;
            case URL:
                inputValidator = new TextInputValidator(properties.minChars(), properties.maxChars());
                break;
            case EMAIL:
                inputValidator = new EmailInputValidator(properties.minChars(), properties.maxChars());
                break;
            case TIME_HHMM_TO_S:
                inputValidator = new TimeValidator((int)Math.round(properties.min()), (int)Math.round(properties.max()));
                break;
            case TIME_CLOCK:
                inputValidator = new BasicInputValidator();
                break;
            case DATE:
                inputValidator = new BasicInputValidator();
                break;
        }
    }



    /**
     * Update the input UI widgets based on the properties.
     */
    private void updateInput() {

//        if(properties.inputType().ordinal() == 9) {
//            Log.e("dateBug", "we have a date");
//            findViewById(R.id.user_text_input_container).setVisibility(GONE);
//            findViewById(R.id.birthday_container).setVisibility(VISIBLE);
//            findViewById(R.id.month).setId(text.getId());
//
//
////            findViewById(R.id.month).setOnFocusChangeListener(new OnFocusChangeListener() {
////                @Override
////                public void onFocusChange(View v, boolean hasFocus) {
////                    Log.e("dateBug", "month focus listener, hasFocus: " + hasFocus);
////                }
////            });
//
//        }

        lengthFilter = null;
        if(properties.textFilterFlags() != TEXT_FILTER_NONE) {
            textFilter = new UserInputFilter((properties.textFilterFlags() & TEXT_FILTER_SPACES) != 0, (properties.textFilterFlags() & TEXT_FILTER_UNDERSCORES) != 0, (properties.textFilterFlags() & TEXT_FILTER_DIGITS) != 0, (properties.textFilterFlags() & TEXT_FILTER_ALL_CAPS) != 0);
            text.setFilters(new InputFilter[]{textFilter});
        }
        else if(properties.maxChars() < Integer.MAX_VALUE) {
            lengthFilter = new UserTextLengthFilter(properties.maxChars());
            lengthFilter.setActive(false);
            if(textFilter != null) {
                text.setFilters(new InputFilter[]{textFilter, lengthFilter});
            }
            else {
                text.setFilters(new InputFilter[]{lengthFilter});
            }
        }
        else {
            text.setFilters(new InputFilter[0]);
        }

        if(properties.isPassword()) {
            text.setTransformationMethod(new PasswordTransformationMethod());
        }
        text.setHint(properties.hint());
        text.setHintTextColor(Color.rgb(128,128,128));
        text.setImeOptions(properties.imeOptions());

        TextView label = findViewById(R.id.user_text_input_label);
        //noinspection ConstantConditions
        if(properties.label() == null || properties.label().isEmpty()) {
            label.setVisibility(View.GONE);
        }
        else {
            label.setVisibility(View.VISIBLE);
            label.setText(properties.label());
        }

        switch(properties.inputType()) {

            case INTEGER:
            case NUMBER:
                text.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                break;
            case DOUBLE:
                text.setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case TEXT: {
                int inputType = EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                switch (properties.capitalizeMode()) {
                    case user_defined:
                        break;
                    case sentence:
                        inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES;
                        break;
                    case word:
                        inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS;
                        break;
                    case all:
                        inputType |= EditorInfo.TYPE_TEXT_FLAG_CAP_CHARACTERS;
                        break;
                    case none:
                        break;
                }
                if(multiline) {
                    inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE | EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE;
                    text.setSingleLine(false);
                    text.setMaxLines(100);
                    text.setMinLines(5);

                    text.setSizeToFit(false);
                    text.setHorizontallyScrolling(false);
                    text.setGravity(Gravity.START | Gravity.TOP);
                    (findViewById(R.id.user_text_input_container)).getLayoutParams().height *= 5;
                    ViewGroup.LayoutParams lp = text.getLayoutParams();
                    lp.height = (findViewById(R.id.user_text_input_container)).getLayoutParams().height;
                    text.setLayoutParams(lp);

                }

                text.setInputType(inputType);
            }
            break;
            case IP_ADDRESS:
                text.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
                text.setKeyListener(new DigitsKeyListener(false, true) {

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               android.text.Spanned dest, int dstart, int dend) {
                        if (end > start) {
                            String destTxt = dest.toString();
                            String resultingTxt = destTxt.substring(0, dstart)
                                    + source.subSequence(start, end)
                                    + destTxt.substring(dend);
                            if (!resultingTxt
                                    .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                                return "";
                            } else {
                                String[] splits = resultingTxt.split("\\.");
                                for (String split : splits) {
                                    if (Integer.valueOf(split) > 255) {
                                        return "";
                                    }
                                }
                            }
                        }
                        return null;
                    }

                });
                break;
            case URL:
                text.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_URI);
                break;
            case TIME_HHMM_TO_S:
                text.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_CLASS_DATETIME);
                break;
            case TIME_CLOCK:
                text.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_CLASS_TEXT);
                break;
            case DATE:
                text.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_CLASS_TEXT);
                break;
            case EMAIL:
                text.setInputType(EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
        }

        setText(properties.inputValue());

        setupValidator();

    }

    private boolean isValid = true;
    /**
     * Used to tell the input if the text is valid.  For example we may need to check that an
     * xID is available.  This check is asynchronous.
     *
     * @param valid     True if the input is valid.
     * @param message   The error message to display if it is not valid.
     */
    public void setIsValid(boolean valid, String message) {
        if(valid) {
            text.setBackground(null);
            findViewById(R.id.user_text_valid).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.user_text_input_error_message)).setText("");
        } else {
            text.setBackgroundResource(R.drawable.invalid_input_border);
            findViewById(R.id.user_text_valid).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.user_text_input_error_message)).setText(message);
        }
        findViewById(R.id.user_text_input_busy).clearAnimation();
        findViewById(R.id.user_text_input_busy).setVisibility(View.GONE);
        isValid = valid;
    }

    @SuppressWarnings("unused")
    public boolean getIsValid() {
        return isValid;
    }

    /**
     * We show a spinner if the input validation is done outside of the widget and is asynchronous.
     * @param showProgress  True if we should show the
     */
    @SuppressWarnings("unused")
    public void setValidating(boolean showProgress) {
        findViewById(R.id.user_text_valid).setVisibility(View.GONE);
        ((TextView)(findViewById(R.id.user_text_input_error_message))).setText("");
        findViewById(R.id.user_text_input_busy).setVisibility(View.VISIBLE);
        findViewById(R.id.user_text_input_busy).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate_progress));
    }

    /**
     * Set read only programatically.
     *
     * @param readOnly  True if this field should be read only.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        if(readOnly) {
            findViewById(R.id.user_text_input_container).setBackgroundColor(Color.TRANSPARENT);
            text.setTextColor(Color.WHITE);
            if(findViewById(R.id.user_text_input_label).getVisibility() == View.VISIBLE) {
                findViewById(R.id.user_text_input_container).setPadding(0, 0, 0, 0);
                text.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            }
            else {
                findViewById(R.id.user_text_input_container).setPadding(10, 0, 10, 0);
                text.setGravity(textViewGravity);
                autoCorrectValue.setGravity(textViewGravity);
            }

        } else {
            findViewById(R.id.user_text_input_container).setBackgroundColor(getContext().getResources().getColor(R.color.selector_item_gray));
            findViewById(R.id.user_text_input_container).setPadding(0, 0, 0, 0);
            text.setGravity(textViewGravity);
            autoCorrectValue.setGravity(textViewGravity);
            text.setTextColor(Color.BLACK);
        }
        text.setEnabled(!readOnly);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        findViewById(R.id.user_text_input_container).setBackgroundColor(getContext().getResources().getColor(enabled ? R.color.selector_item_gray : R.color.selector_item_disabled_gray));
        text.setEnabled(enabled);
    }

    /**
     * Update the properties for the input.
     *
     */
    @SuppressWarnings("unused")
    public void setInputProperties(@NonNull IUserTextInputProperties properties) {
        this.properties = properties;
        updateInput();
    }

    public IUserTextInputProperties getInputProperties() {
        return properties;
    }

    /**
     * Setup listeners so we can validate the user input when they leave the field or do an action.
     * Note that we need to make sure it is validated once hence the validateOnAction flag.
     */
    private boolean validateOnAction = false;
    private void setupListeners() {

        if(properties.inputType() == InputType.DATE || properties.inputType() == InputType.TIME_CLOCK) {
            text.setOnTouchListener((v, event) -> {
                if (!readOnly && event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (properties.inputType() == InputType.DATE) {
                        Log.e("dateBug", "I have touched the bday field, calendar: " + calendar);
                        text.clearFocus();
                        text.requestFocus();
                        //buildDatePicker1();
                    } else {
                        buildTimePicker();
                    }
                }
                return true;
            });

        }


        text.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                validateOnAction = true;
                if(lengthFilter != null) {
                    lengthFilter.active = false;
                }
                validateText();
                getHandler().post(() -> text.clearFocus());
            }
            if(onIMEAction != null) {
                return onIMEAction.onIMEAction(actionId);
            }
            return false;
        });

        text.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Log.e("bdayBug", "text has focus");
                if(properties.inputType() == InputType.DATE || properties.inputType() == InputType.TIME_CLOCK) {
                    InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(((Activity)getContext()).getCurrentFocus().getWindowToken(), 0);
                    if (properties.inputType() == InputType.DATE) {
                        buildDatePicker1();
                        // buildDatePicker();

                    } else {
                        buildTimePicker();
                    }
                } else {

//                    InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
//
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                    if(lengthFilter != null) {
                        lengthFilter.active = true;
                    }
                    text.setText(properties.inputValue());
                    if (properties.minChars() > 0 && text.getText() != null && text.getText().length() >= properties.minChars()) {
                        text.setSelection(properties.minChars(), text.getText().length());
                    } else {
                        text.selectAll();
                    }
                }

            } else {
                Log.e("bdayBug", "text does not have focus");
                if(lengthFilter != null) {
                    lengthFilter.active = false;
                }

                if(!validateOnAction) {
                    validateText();
                }
                validateOnAction = false;
            }
            if(onFocusChanged != null) {
                onFocusChanged.onFocusChanged(hasFocus);
            }
        });
    }


    /**
     * Update the text.  This is called when the field is initialized and when the units change.
     */
    public void initInput() {
        String unitString = StringHelper.getUnitString(getContext(), properties.displayUnits(), properties.units());
        //noinspection ConstantConditions
        if(properties.inputValue() == null || unitString == null) {
            text.setText("");
        }
        else {
            if (!properties.inputValue().isEmpty() && !unitString.isEmpty()) {
                text.setText(String.format(Locale.ENGLISH, "%s %s", properties.inputValue(), unitString));
            } else {
                text.setText(properties.inputValue());
            }
        }

    }


    // Counter used to determine the flash animation state.
    int flashAnimationCounter = 0;
    Animator.AnimatorListener flashListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            // Go to the next animation state.
            flashAnimationCounter++;

            if(flashAnimationCounter == 6) {
                // Stop the animation and show the auto correct value.
                text.animate().alpha(1.0f).setDuration(440).start();
                autoCorrectValue.setAlpha(0.0f);
                text.setEnabled(true);
                autoCorrectValue.setVisibility(View.INVISIBLE);
            } else {
                // Show or hide the range.
                autoCorrectValue.animate().alpha(flashAnimationCounter % 2 == 1 ? 0.0f : 1.0f).setDuration(440).setListener(this).start();
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    /**
     * Used to flash the auto correct value when the user enters invalid information.
     *
     * @param valueToFlash  The auto correct value to flash.
     * @param valueToSet    The value to display when the flashing is complete.
     */
    private void autoCorrect(String valueToFlash, final String valueToSet) {

        if(properties.autoCorrect()) {
            flashAnimationCounter = 0;
            text.setEnabled(false);
            text.setAlpha(0.0f);
            text.setText(valueToSet);
            autoCorrectValue.setText(valueToFlash);
            autoCorrectValue.setVisibility(View.VISIBLE);
            autoCorrectValue.animate().alpha(1.0f).setDuration(440).setListener(flashListener).start();
            properties.inputValue(valueToSet);
            initInput();
        }
    }

    /**
     *
     * @return The input text.
     */
    public String getText() {
        CharSequence t = text.getText();
        if(t == null) {
            return "";
        }

        return t.toString();
    }

    /**
     * Set the text directly.
     * @param text  The new text for the edit box.
     */
    public void setText(String text) {
        properties.inputValue(text);
        initInput();
    }

    /**
     * In some cases we want to toggle the password (like for network setup).  Use this to show the
     * password.
     */
    @SuppressWarnings("unused")
    public void showPassword() {
        text.setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {
                return source;
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {
            }
        });
    }


    /**
     * In some cases we want to toggle the password (like for network setup).  Use this to hide the
     * password behind asteriks.
     */
    @SuppressWarnings("unused")
    public void hidePassword() {
        text.setTransformationMethod(new PasswordTransformationMethod());
    }


    /**
     * Validates the user input and auto corrects it if necessary.
     */
    private void validateText() {
        if(text.getText() == null || text.getText().toString().isEmpty()) {
            findViewById(R.id.user_text_valid).setVisibility(View.GONE);
            isValid = false;
            if(inputValidator instanceof TextInputValidator) {
                InputValidationResult result = inputValidator.validate(text.getText().toString(), properties.inputValue());
                if (result.result() == InputValidationResult.RESULT.VALID) {
                    setIsValid(true, "");
                    if (onTextChanged != null) {
                        String value = result.value();
                        if (!value.equals(properties.inputValue())) {
                            onTextChanged.onTextChanged(value);
                        }
                    }
                    properties.inputValue(result.value());
                    initInput();
                } else {
                    setText("");
                }
            }
            return;
        }

        InputValidationResult result = inputValidator.validate(text.getText().toString(), properties.inputValue());
        if(result.result() == InputValidationResult.RESULT.VALID) {
            setIsValid(true, "");
            if(onTextChanged != null) {
                String value = result.value();
                if(!value.equals(properties.inputValue())) {
                    onTextChanged.onTextChanged(value);
                }
            }
            properties.inputValue(result.value());
            initInput();
        }
        else if(result.result() != InputValidationResult.RESULT.CHECKING) {
            if(properties.autoCorrect()) {
                autoCorrect(result.autoCorrect(), properties.inputValue());
            }
            else if(onTextInvalid != null) {
                properties.inputValue(text.getText().toString());
                setIsValid(false, onTextInvalid.onTextInvalid(result));
                initInput();

            } else {
                properties.inputValue(text.getText().toString());
                setIsValid(false, "");
                initInput();
            }
        }
        else {
            properties.inputValue(text.getText().toString());
        }

    }

    /**
     * This function is useful for date and time fields only.  Other input types ignore this method.

     * @param c     The time or date to set teh field to.
     */
    public void setText(Calendar c) {
        Log.e("dateBug", "SET TEXT.  year: " + c.get(Calendar.YEAR) + ", month: " + c.get(Calendar.MONTH) + ", day: " + c.get(Calendar.DAY_OF_MONTH));
        calendar = c;
        currCalendar = Calendar.getInstance();
        currCalendar.setTime(c.getTime());
        SimpleDateFormat format = null;
        JHTCommonTypes.DateFormat dateFormat = properties == null ? JHTCommonTypes.DateFormat.YearMonthDay : properties.dateFormat();
        JHTCommonTypes.TimeFormat timeFormat = properties == null ? JHTCommonTypes.TimeFormat.Twelve_Hour : properties.timeFormat();

        // TODO Need to make a format option.  Only used in one place for now.
        if(properties != null && properties.inputType() == InputType.TIME_CLOCK) {
            switch (timeFormat) {
                case Twelve_Hour:
                    format = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                    break;
                case Twenty_Four_Hour:
                    format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    break;
                default:
                    break;
            }
            if(format != null) {
                setText(format.format(c.getTime()));
            }

        }
        else if(properties != null && properties.inputType() == InputType.DATE) {
            switch (dateFormat) {

                case YearMonthDay:
                    format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                    break;
                case MonthDayYear:
                    format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    break;
                case DayMonthYear:
                    format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    break;
                case DayMonthAbrYear:
                    format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    break;
                default:
                    break;
            }
        }
        if (format != null) {
            setText(format.format(c.getTime()));
        }
    }

    /**
     * Used for date pickers to provide a callack when the user chooses a date.
     *
     * @param onDateSet     The callback that should be called when the user chooses a new date.
     */
    @SuppressWarnings("unused")
    public void setOnDatePicked(DatePickerDialog.OnDateSetListener onDateSet) {
        this.onDateSet = onDateSet;
    }

    /**
     * Used for time pickers to provide a callack when the suer chooses a date.
     *
     * @param onTimeSet     The callback that should be called when the user chooses a new date.
     */
    @SuppressWarnings("unused")
    public void setOnTimePicked(TimePickerDialog.OnTimeSetListener onTimeSet) {
        this.onTimeSet = onTimeSet;
    }

    private void buildDatePicker1() {
        if(calendar == null) {
            Log.e("dateBug", "calendar is null");
        }

        Calendar calendar = this.calendar == null ? (Calendar.getInstance()) : this.calendar;

        DialogDatePicker datePicker = new DialogDatePicker(ContextUtil.getActivity(getContext()),
                (datePicker1, year, month, dateOfMonth) -> {

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dateOfMonth);
            setText(c);
            if (onBirthdaySet != null) onBirthdaySet.onBirthdateSet();
            if(onDateSet != null) {
                onDateSet.onDateSet(datePicker1, year, month, dateOfMonth);
            } else {
                InputMethodManager inputMethodManager = (InputMethodManager)  getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        datePicker.show();

        calendar.clear();

        calendar.set(1900, Calendar.JANUARY, 1);
        if (properties.min() > calendar.getTimeInMillis()) {
            datePicker.getDatePicker().setMinDate((long)properties.min());
        }
        else {
            datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }
        calendar.clear();

        calendar.set(2100, Calendar.JANUARY, 1);
        if (properties.max() < calendar.getTimeInMillis()) {
            datePicker.getDatePicker().setMaxDate((long)properties.max());
        }
        else {
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }

        calendar.clear();
        if(currCalendar != null) {
            this.calendar = Calendar.getInstance();
            this.calendar.setTime(currCalendar.getTime());
        }

    }

    /**
     * Builds the date picker when the user wants to edit a date field.
     */
    private void buildDatePicker() {
        // Get the starting value.
        Calendar calendar = this.calendar == null ? (Calendar.getInstance()) : this.calendar;

        // Create the date picker.
        final DatePickerDialog datePicker = new DatePickerDialog(
                ContextUtil.getActivity(getContext()),
                (view, year, month, dayOfMonth) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, year);
                    c.set(Calendar.MONTH, month);
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    setText(c);
                    if(onDateSet != null) {
                        onDateSet.onDateSet(view, year, month, dayOfMonth);
                    }

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        calendar.clear();
        calendar.set(1900, Calendar.JANUARY, 1);
        if (properties.min() > calendar.getTimeInMillis()) {
            datePicker.getDatePicker().setMinDate((long)properties.min());
        }
        else {
            datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
        }
        calendar.clear();
        calendar.set(2100, Calendar.JANUARY, 1);
        if (properties.max() < calendar.getTimeInMillis()) {
            datePicker.getDatePicker().setMaxDate((long)properties.max());
        }
        else {
            datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }
        if(datePicker.getWindow() != null && datePicker.getWindow().getAttributes() != null) {
            datePicker.getWindow().getAttributes().windowAnimations = pickerAnimation;
        }
        datePicker.show();
    }


    /**
     * Builds the time picker when the user wants to edit a time field.
     */
    private void buildTimePicker() {
        // Get the starting value.
        Calendar calendar = this.calendar == null ? (Calendar.getInstance()) : this.calendar;

        // Set the time format.
        JHTCommonTypes.TimeFormat timeFormat = properties == null ? JHTCommonTypes.TimeFormat.Twelve_Hour : properties.timeFormat();

        // Create the picker.
        final TimePickerDialog timePickerDialog = new TimePickerDialog(
                ContextUtil.getActivity(getContext()),
                (view, hourOfDay, minute) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    setText(c);
                    if(onTimeSet != null) {
                        onTimeSet.onTimeSet(view, hourOfDay, minute);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                timeFormat.equals(JHTCommonTypes.TimeFormat.Twenty_Four_Hour));
        if(timePickerDialog.getWindow() != null && timePickerDialog.getWindow().getAttributes() != null) {
            timePickerDialog.getWindow().getAttributes().windowAnimations = pickerAnimation;
        }
        timePickerDialog.show();
    }

    /**
     * Sets the callback to notify the view when the user has inputed a valid value.
     * @param onTextChanged  The callback
     */
    @SuppressWarnings("unused")
    public void setOnTextChanged(IOnTextChanged onTextChanged) {
        this.onTextChanged = onTextChanged;
    }

    /**
     * Sets the callback to notify the view when the focus has changed for the edit box.
     * @param onFocusChanged  The callback
     */
    @SuppressWarnings("unused")
    public void setOnFocusChanged(IOnFocusChanged onFocusChanged) {
        this.onFocusChanged = onFocusChanged;
    }


    /**
     * Adds the callback to notify the view when the text is changed.
     * @param textWatcher  The callback
     */
    @SuppressWarnings("unused")
    public void addTextWatcher(TextWatcher textWatcher) {
        text.addTextChangedListener(textWatcher);
    }


    /**
     * Remove the callback to notify the view when the text is changed.
     * @param textWatcher  The callback
     */
    @SuppressWarnings("unused")
    public void removeTextWatcher(TextWatcher textWatcher) {
        text.removeTextChangedListener(textWatcher);
    }

    /**
     * Sets the callback to notify the view when the user has pressed an action on the keyboard.
     * @param onIMEAction  The callback
     */
    @SuppressWarnings("unused")
    public void setOnIMEAction(IOnIMEAction onIMEAction) {
        this.onIMEAction = onIMEAction;
    }

    /**
     * Sets the callback to notify the view when the user has inputed an invalid value.
     * @param onTextInvalid  The callback
     */
    @SuppressWarnings("unused")
    public void setOnTextInvalid(IOnValidationError onTextInvalid) {
        this.onTextInvalid = onTextInvalid;
    }

    /**
     * Used to set the ime options for the input.
     *
     * @param imeOptions    The ime options.
     */
    @SuppressWarnings("unused")
    public void setImeOptions(int imeOptions) {
        text.setImeOptions(imeOptions);
    }

    public void setOnBirthdateSet(IOnBirthdaySet onBirthdateSet) {
        this.onBirthdaySet = onBirthdateSet;
    }


    /**
     * Enable the widget.
     */
    @SuppressWarnings("unused")
    public void enable() {
        View mainContainer = findViewById(R.id.user_text_input_container);
        mainContainer.setAlpha(1.0f);
        text.setFocusableInTouchMode(true);
        text.setFocusable(true);
    }


    /**
     * Disable the widget.
     */
    @SuppressWarnings("unused")
    public void disable() {
        View mainContainer = findViewById(R.id.user_text_input_container);
        mainContainer.setAlpha(0.5f);
        text.setFocusable(false);
    }

    public void getFocus() {
        text.clearFocus();
        text.requestFocus();
        getHandler().postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (!imm.showSoftInput(text, InputMethodManager.SHOW_FORCED)) {
                ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }, 200);
    }


}
