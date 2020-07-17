package com.jht.androiduiwidgets.selector;

import android.view.View;
import android.widget.ImageView;

import com.jht.androiduiwidgets.R;


/**
 * This class is used by the recycler view to manage the views.  It caches the views within the selector
 * item.
 */
class HelpSelectorItemHolder extends StandardSelectorItemHolder {


    private ImageView helpButton;
    private HelpSelectorItem.IOnHelp onHelp;

    /**
     * Cache the views in the holder.
     *
     * @param v     The root view.
     */
    HelpSelectorItemHolder(View v, HelpSelectorItem.IOnHelp onHelp) {
        super(v);
        helpButton = v.findViewById(R.id.selector_item_base_post_image);
        this.onHelp = onHelp;
    }


    /**
     * The item has changed.  We need to update the views.
     *
     * @param adapter   The adapter managing the views.
     * @param item      The item with the view data.
     */
    @Override
    protected void update(final SelectorAdapterBase adapter, final ISelectorItem item, final IOnItemSelected onItemSelected) {
        super.update(adapter, item, onItemSelected);
        helpButton.setVisibility(item.selected() ? View.VISIBLE : View.GONE);
        helpButton.setOnClickListener(v -> {
            if (onHelp != null) {
                if (item instanceof HelpSelectorItem) {
                    onHelp.onHelp(v, item.toString(), ((HelpSelectorItem) item).helpText());
                }
            }
        });
    }
}
