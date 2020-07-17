package com.jht.androiduiwidgets.selector;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jht.androiduiwidgets.R;

import java.util.List;

/**
 * This is a simple implementation of the ISelectorItem.  This only needs a label for every item.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleSelectorItem extends SelectorItemBase {

    // The label for the item to display.
    protected String label;

    private int gravity;
    private float paddingLeft;
    private float paddingRight;
    private float paddingTop;
    private float paddingBottom;

    /**
     * Default constructor for an item with children.
     *
     * @param label     The label for the item.
     * @param subItems  List of sub items for this item. Cannot be null.
     */
    public SimpleSelectorItem(String label, int gravity, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom, @Nullable List<ISelectorItem> subItems) {
        super(subItems);
        this.label = label;
        this.gravity = gravity;
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;

    }

    public SimpleSelectorItem(String label, int gravity, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        this(label, gravity, paddingLeft, paddingTop, paddingRight, paddingBottom, null);
    }


        /**
         * Default constructor for an item with children.
         *
         * @param label     The label for the item.
         * @param subItems  List of sub items for this item. Cannot be null.
         */
    public SimpleSelectorItem(String label, @Nullable List<ISelectorItem> subItems) {
        this(label, Gravity.CENTER, 0, 0, 0, 0, subItems);

    }



    /**
     * Default constructor for an item with no children.
     *
     * @param label     The label for the item.
     */
    public SimpleSelectorItem(String label) {
        this(label, null);
    }
    /**
     * Used to get the item's label.
     * @return      Return the label
     */
    @NonNull
    @Override
    public String toString() { return label; }


    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.simple_selector_item, parent,false);
        return new SimpleSelectorItemHolder(v);
    }

    public int gravity() { return gravity; }
    public float paddingLeft() { return paddingLeft; }
    public float paddingRight() { return paddingRight; }
    public float paddingTop() { return paddingTop; }
    public float paddingBottom() { return paddingBottom; }


}
