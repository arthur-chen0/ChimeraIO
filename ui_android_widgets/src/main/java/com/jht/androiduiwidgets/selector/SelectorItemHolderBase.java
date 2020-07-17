package com.jht.androiduiwidgets.selector;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * This class is used by the recycler view to manage the views.  It caches the views within the selector
 * item.
 */
@SuppressWarnings({"unused"})
public abstract class SelectorItemHolderBase extends RecyclerView.ViewHolder {

    /**
     * Cache the views in the holder.
     *
     * @param v     The root view.
     */
    protected SelectorItemHolderBase(View v) {
        super(v);
    }


    /**
     * The item has changed.  We need to update the views.
     *
     * @param adapter   The adapter managing the views.
     * @param item      The item with the view data.
     */
    protected abstract void update(final SelectorAdapterBase adapter, final ISelectorItem item, final IOnItemSelected onItemSelected);
}
