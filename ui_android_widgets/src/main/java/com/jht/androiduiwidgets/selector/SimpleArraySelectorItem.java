package com.jht.androiduiwidgets.selector;


import android.view.Gravity;

import java.util.ArrayList;

/**
 * This is a simple implementation of the ISelectorItem.  This only needs a label for every item.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SimpleArraySelectorItem extends SimpleSelectorItem {


    public SimpleArraySelectorItem(String[] array) {
        this(array, -1, Gravity.CENTER, 0, 0, 0, 0);
    }

    public SimpleArraySelectorItem(String[] array, int currentSelection) {
        this(array, currentSelection, Gravity.CENTER, 0, 0, 0, 0);
    }

    public SimpleArraySelectorItem(String[] array, int currentSelection, int gravity, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        super("");
        for (String label : array) {
            addItem(new SimpleSelectorItem(label, gravity, paddingLeft, paddingTop, paddingRight, paddingBottom));
        }
        setSelection(currentSelection);

    }

    public SimpleArraySelectorItem(ArrayList<String> list) {
        this(list, -1, Gravity.CENTER, 0, 0, 0, 0);
    }

    public SimpleArraySelectorItem(ArrayList<String> list, int currentSelection) {
        this(list, currentSelection, Gravity.CENTER, 0, 0, 0, 0);
    }



    public SimpleArraySelectorItem(ArrayList<String> list, int currentSelection, int gravity, float paddingLeft, float paddingTop, float paddingRight, float paddingBottom) {
        super("");

        for(String label : list) {
            addItem(new SimpleSelectorItem(label, gravity, paddingLeft, paddingTop, paddingRight, paddingBottom));
        }
        setSelection(currentSelection);
    }



}
