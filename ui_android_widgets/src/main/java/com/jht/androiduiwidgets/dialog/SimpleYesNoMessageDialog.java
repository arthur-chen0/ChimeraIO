package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jht.androiduiwidgets.R;


/**
 * This class implements a simple dialog to ask the user a yes / no quesiton.
 *
 */
public class SimpleYesNoMessageDialog extends DialogBase {

    public interface IOnYesNo {
        void onYesNo(boolean yes);
    }


    private TextView message;
    private IOnYesNo onYesNo;

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     */
    public SimpleYesNoMessageDialog(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     */
    public SimpleYesNoMessageDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     * @param defStyleAttr  Default style for the view.
     */
    public SimpleYesNoMessageDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Cache the text view.
        message = findViewById(R.id.message_text_content);
        findViewById(R.id.message_dialog_cancel).setOnClickListener(v -> {
            hide();
            if(onYesNo != null) {
                onYesNo.onYesNo(false);
            }
        });
        findViewById(R.id.message_dialog_ok).setOnClickListener(v -> {
            hide();
            if(onYesNo != null) {
                onYesNo.onYesNo(true);
            }
        });

        final ScrollView messageContainer = findViewById(R.id.message_text_content_container);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            messageContainer.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (!findViewById(R.id.message_dialog_ok).isEnabled() && scrollbarAtBottom(messageContainer, message)) {
                    findViewById(R.id.message_dialog_ok).setEnabled(true);
                }
            });
        }
        else {
            findViewById(R.id.message_dialog_ok).setEnabled(scrollbarAtBottom(messageContainer, message));
            messageContainer.getViewTreeObserver().addOnScrollChangedListener(() -> {
                if (!findViewById(R.id.message_dialog_ok).isEnabled() && scrollbarAtBottom(messageContainer, message)) {
                    findViewById(R.id.message_dialog_ok).setEnabled(true);
                }
            });
        }

        new Handler().postDelayed(() -> findViewById(R.id.message_dialog_ok).setEnabled(scrollbarAtBottom(messageContainer, message)), 100);


    }

    /**
     * Check to see if the scroll bar is at the bottom.
     * @param scrollView    The scrollview to check.
     * @param message       The view in the scrollview.
     * @return              True if we are at the bottom.
     */
    boolean scrollbarAtBottom(ScrollView scrollView, TextView message) {
        return scrollView.getScrollY() + scrollView.getHeight() >= message.getHeight();
    }

    /**
     * Initialize the dialog.
     *
     * @param message   The message to display.
     * @param onYesNo      The optional callback when the user presses either button.
     */
    public void init(String message, IOnYesNo onYesNo) {
        this.onYesNo = onYesNo;
        this.message.setText(message);
    }


    /**
     *
     * @return  The message_dialog_yes_no layout.
     */
    @Override
    protected int getLayoutResId() { return R.layout.message_dialog_yes_no; }
}
