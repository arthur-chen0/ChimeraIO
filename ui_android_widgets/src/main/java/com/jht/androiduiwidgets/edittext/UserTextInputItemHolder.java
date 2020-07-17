package com.jht.androiduiwidgets.edittext;

import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.jht.androiduiwidgets.selector.IOnItemSelected;
import com.jht.androiduiwidgets.selector.ISelectorItem;
import com.jht.androiduiwidgets.selector.SelectorAdapterBase;
import com.jht.androiduiwidgets.selector.SelectorItemHolderBase;


/**
 * This class is used by the recycler view to manage the views.  It caches the views within the selector
 * item.
 */
class UserTextInputItemHolder extends SelectorItemHolderBase {

    /**
     * Cache the views in the holder.
     *
     * @param v The root view.
     */
    UserTextInputItemHolder(View v) {
        super(v);
    }


    /**
     * The item has changed.  We need to update the views.
     *
     * @param adapter The adapter managing the views.
     * @param item    The item with the view data.
     */
    @Override
    protected void update(final SelectorAdapterBase adapter, final ISelectorItem item, final IOnItemSelected onItemSelected) {
        if (item instanceof UserTextInputItem) {
            if (itemView instanceof UserTextInput) {
                ((UserTextInput) itemView).setInputProperties(((UserTextInputItem)item).properties());
                if (item.getIndex() == item.parent().numChildren() - 1) {
                    ((UserTextInput) itemView).setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else {
                    ((UserTextInput) itemView).setImeOptions(EditorInfo.IME_ACTION_NEXT);
                }
            }
        }

    }
}
