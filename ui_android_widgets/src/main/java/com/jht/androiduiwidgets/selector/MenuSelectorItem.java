package com.jht.androiduiwidgets.selector;


import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jht.androiduiwidgets.R;

import java.util.List;

/**
 * This is a simple implementation of the ISelectorItem.  This only needs a label for every item.
 */
@SuppressWarnings({"unused"})
public class MenuSelectorItem extends StandardSelectorItem {

    private Drawable triangleRight;
    private Drawable triangleDown;


    /**
     * Default constructor for an item with children.
     *
     * @param label     The label for the item.
     */
    public MenuSelectorItem(String label, @NonNull Context context, @Nullable List<ISelectorItem> subItems) {
        super(label, null, null, subItems);
        init(context);
    }
    public MenuSelectorItem(String label, @NonNull Context context) {
        super(label, null, null, null);
        init(context);
    }

    private void init(Context context) {
        triangleRight = context.getResources().getDrawable(R.drawable.triangle_right);
        triangleDown = context.getResources().getDrawable(R.drawable.triangle_down);
    }

    @Override
    public Drawable postImage() {
        if(numChildren() > 0 && showSubItems()) {
            return triangleDown;
        }
        return triangleRight;
    }

    @Override
    public boolean showPostImage() {
        return true;
    }


    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.standard_selector_item, parent,false);
        return new StandardSelectorItemHolder(v, true);
    }

}
