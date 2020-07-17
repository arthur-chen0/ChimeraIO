package com.jht.androiduiwidgets.dialog;

import android.view.View;

import com.jht.androiduiwidgets.IInputConfiguration;
import com.jht.androiduiwidgets.selector.SelectorAdapterBase;

/**
 * This is a convenient class for creating dialogs.  This takes care of creating the object, initializing it,
 * and showing it.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DialogFactory {



    /**
     * Used to create a simple message dialog with an ok button.
     *
     * @param anchor    The view that caused the message to appear.
     * @param message   The message to display.
     * @param onOk      Optional callback when the user presses the ok button.
     * @return          The dialog.
     */
    public static SimpleMessageDialog message(View anchor, String message, SimpleMessageDialog.IOnOk onOk, SimpleMessageDialog.SIMPLE_BUTTON_STATE buttonState) {
        SimpleMessageDialog dialog = new SimpleMessageDialog(anchor.getContext());
        dialog.init(message, onOk, buttonState);
        dialog.show(anchor);
        return dialog;
    }



    /**
     * Used to create a simple message dialog with a yes and no button.
     *
     * @param anchor    The view that caused the message to appear.
     * @param message   The message to display.
     * @param onYesNo   Optional callback when the user presses either button.
     * @return          The dialog.
     */
    public static SimpleYesNoMessageDialog confirm(View anchor, String message, SimpleYesNoMessageDialog.IOnYesNo onYesNo) {
        SimpleYesNoMessageDialog dialog = new SimpleYesNoMessageDialog(anchor.getContext());
        dialog.init(message, onYesNo);
        dialog.show(anchor);
        return dialog;
    }


    /**
     * This is generally used for a spinner / drop down box.  It's purpose is to give the user
     * a bunch of items to select for a given field.
     *
     * @param anchor        The view that caused the message to appear.
     * @param adapter       The adapter that will contain the options.
     * @param position      The current selected item.  Used so we can scroll to the correct place.
     * @param width         Width of the dialog box.
     * @param height        Height of the dialog box.
     * @param paddingStart  Display offset for the dialog.
     * @param paddingTop    Display offset for the dialog.
     * @return              The dialog.
     */
    public static DialogBase popupSelection(View anchor, SelectorAdapterBase adapter, int position, float width, float height, float paddingStart, float paddingTop) {
        if(height <= 0) {
            // This is a match_parent or wrap_content.  We assume there is no need to scroll.
            PopupSelectionNoScroll dialog = new PopupSelectionNoScroll(anchor.getContext());
            dialog.init(adapter, position, width, height, paddingStart, paddingTop);
            dialog.setCloseOnOutsideTouch(true);
            dialog.show(anchor);
            return dialog;
        }
        else {
            // Assume we need to scroll.
            PopupSelection dialog = new PopupSelection(anchor.getContext());
            dialog.init(adapter, position, width, height, paddingStart, paddingTop);
            dialog.setCloseOnOutsideTouch(true);
            dialog.show(anchor);
            return dialog;
        }
    }


    /**
     * Used to create a help dialog message with a title and message.
     *
     * @param anchor    The view that caused the message to appear.
     * @param title   The title to display.
     * @param message   The message to display.
     * @return          The dialog.
     */
    public static HelpMessageDialog helpMessage(View anchor, String title, String message) {
        HelpMessageDialog dialog = new HelpMessageDialog(anchor.getContext());
        dialog.init(title, message);
        dialog.show(anchor);
        return dialog;
    }


    /**
     * Used to create a keypad dialog message
     *
     * @return          The dialog.
     */

    public static KeypadDialog keypadDialog(View anchor, IInputConfiguration settings, View.OnClickListener onConfirm, boolean hideOnConfirm) {
        KeypadDialog dialog = new KeypadDialog(anchor.getContext());
        dialog.init(settings, onConfirm, hideOnConfirm);
        dialog.show(anchor);
        return dialog;
    }



}
