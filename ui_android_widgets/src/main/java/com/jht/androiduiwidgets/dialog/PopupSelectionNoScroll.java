package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

import com.jht.androiduiwidgets.R;
import com.jht.androiduiwidgets.selector.SelectorAdapterBase;


/**
 * This class is used to provide a user with a list of items and choose one.  This is the same as
 * PopupSelection except there is no scroll bar.
 *
 */
public class PopupSelectionNoScroll extends DialogBase {


    private RecyclerView popup;

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     */
    public PopupSelectionNoScroll(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the dialog
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     */
    public PopupSelectionNoScroll(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the option selector item.
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     * @param defStyleAttr  Default style for the view.
     */
    public PopupSelectionNoScroll(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        popup = findViewById(R.id.pop_up_selector);
        popup.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    /**
     *
     * @return  The popup_selection_no_scroll layout.
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.popup_selection_no_scroll; }


    /**
     * Initialize the popup.
     *
     * @param adapter       The adapter that contains the selection list
     * @param position      The currently selected item for scrolling.
     * @param width         The width of the dialog
     * @param height        The height of the dialog.
     * @param paddingStart  The dialog offset.
     * @param paddingTop    The dialog offset.
     */
    public void init(SelectorAdapterBase adapter, int position, float width, float height, float paddingStart, float paddingTop) {
        popup.setAdapter(adapter);
        if(popup.getLayoutManager() != null) {
            popup.getLayoutManager().scrollToPosition(position);
        }
        init(width, height, paddingStart, paddingTop);

    }


}
