package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jht.androiduiwidgets.R;

/**
 * This class implements a simple dialog with a message and OK button.
 *
 */
public class SimpleMessageDialog extends DialogBase {

    public enum SIMPLE_BUTTON_STATE {
        OK,
        CANCEL,
        NONE,
    }

    public interface IOnOk {
        void onOk();
    }

    private TextView message;
    private IOnOk onOk;
    private Button okayButton;

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     */
    public SimpleMessageDialog(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     */
    public SimpleMessageDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     * @param defStyleAttr  Default style for the view.
     */
    public SimpleMessageDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        okayButton = findViewById(R.id.message_dialog_ok);
        // Cache the text view.
        message = findViewById(R.id.message_text_content);
        findViewById(R.id.message_dialog_ok).setOnClickListener(v -> {
            hide();
            if(onOk != null) {
                onOk.onOk();
            }
        });


    }

    /**
     * Initialize the dialog.
     *
     * @param message   The message to display.
     * @param onOk      The optional callback when the user presses the ok button.
     */
    public void init(String message, IOnOk onOk, SIMPLE_BUTTON_STATE buttonState) {
        this.onOk = onOk;
        this.message.setText(message);
        switch(buttonState) {
            case OK:
                this.okayButton.setVisibility(View.VISIBLE);
                this.okayButton.setBackground(getResources().getDrawable(R.drawable.message_dialog_box_button_yes));
                break;
            case CANCEL:
                this.okayButton.setVisibility(View.VISIBLE);
                this.okayButton.setBackground(getResources().getDrawable(R.drawable.message_dialog_box_button_no));
                break;
            case NONE:
                this.okayButton.setVisibility(View.GONE);
                break;
        }
    }


    /**
     *
     * @return  The message_dialog layout.
     */
    @Override
    protected int getLayoutResId() { return R.layout.message_dialog; }
}
