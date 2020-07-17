package com.jht.androiduiwidgets.selector;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jht.androiduiwidgets.R;
import com.jht.translations.LanguageHelper;

/**
 * This class is used by the recycler view to manage the views.  It caches the views within the selector
 * item.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleSelectorItemHolder extends SelectorItemHolderBase {
    private TextView textView;
    private View selectorView;

    /**
     * Cache the views in the holder.
     *
     * @param v     The root view.
     */
    public SimpleSelectorItemHolder(View v) {
        super(v);
        textView = v.findViewById(R.id.selector_text);
        selectorView = v;
    }


    /**
     * The item has changed.  We need to update the views.
     *
     * @param adapter   The adapter managing the views.
     * @param item      The item with the view data.
     */
    protected void update(final SelectorAdapterBase adapter, final ISelectorItem item, final IOnItemSelected onItemSelected) {
        final ISelectorItem parent = item.parent();
        if(item instanceof SimpleSelectorItem) {
            final SimpleSelectorItem i = (SimpleSelectorItem)item;

            if(textView != null) {
                if(item.translate()) {
                    textView.setText(LanguageHelper.loadResource(selectorView.getContext(), item.toString()));
                }
                else {
                    textView.setText(item.toString());
                }
                textView.setActivated(item.selected());
                //textView.setGravity(i.gravity());
                //textView.setPadding((int)i.paddingLeft(), (int)i.paddingTop(), (int)i.paddingRight(), (int)i.paddingBottom());
            }

            selectorView.setActivated(item.selected());
            textView.setActivated(item.selected());
            selectorView.setPaddingRelative((int)itemView.getContext().getResources().getDimension(R.dimen.selector_subitem_margin) * (parent.getGeneration()), 0, 0, 0) ;

            if (selectorView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) selectorView.getLayoutParams();
                boolean killMargin = item.parent().parent() == null && item.getIndex() == item.parent().numChildren() -1;
                int spacing = adapter.getItemSpacing() == -1 ? (int)selectorView.getContext().getResources().getDimension(R.dimen.selector_subitem_spacing) : adapter.getItemSpacing();
                p.setMargins(0, 0, 0, killMargin ? 0 : spacing);
                selectorView.requestLayout();
            }

            selectorView.setOnClickListener(v -> {
                ISelectorItem root = item;
                while(root.parent() != null) {
                    root = root.parent();
                }
                if(parent.getSelection() >= 0) {
                    adapter.notifyItemChanged(parent.getSelection());
                }
                parent.setSelection(item.getIndex());
                item.onSelection();
                adapter.notifyItemChanged(item.getIndex());
                if(onItemSelected != null) {
                    ISelectorItem selectedItem = item;
                    while (selectedItem.numChildren() > 0 && selectedItem.showSubItems() && selectedItem.getSelection() >= 0) {
                        selectedItem = selectedItem.getChild(selectedItem.getSelection());
                    }
                    adapter.notifyItemChanged(selectedItem.getIndex());
                    onItemSelected.onItemSelected(selectedItem);
                }
            });
        }
    }
}
