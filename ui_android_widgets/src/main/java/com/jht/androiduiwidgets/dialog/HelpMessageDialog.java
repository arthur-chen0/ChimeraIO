package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jht.androiduiwidgets.R;

/**
 * This class implements a simple dialog with a message and OK button.
 *
 */
public class HelpMessageDialog extends DialogBase {


    private TextView message;
    private TextView title;

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     */
    public HelpMessageDialog(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     */
    public HelpMessageDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     * @param defStyleAttr  Default style for the view.
     */
    public HelpMessageDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Cache the text view.
        message = findViewById(R.id.message_text_content);
        title = findViewById(R.id.message_title_content);

        findViewById(R.id.cornerCloseButton).setOnClickListener(v -> hide());


    }

    /**
     * Initialize the dialog.
     *
     * @param message   The message to display.
     * @param title     The help title.
     */
    public void init(String title, String message) {
        this.title.setText(title);
        this.message.setText(message);
    }


    /**
     *
     * @return  The message_dialog layout.
     */
    @Override
    protected int getLayoutResId() { return R.layout.help_dialog_box; }
}
