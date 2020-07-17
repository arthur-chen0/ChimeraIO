package com.jht.androiduiwidgets.selector;

import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

/**
 * This is used by the recycler view to determine the difference of the selector items after a user
 * action that may have hidden or revealed items.
 */
class SelectorListDiffUtil extends DiffUtil.Callback {

    private ArrayList<ISelectorItem> oldList;
    private ArrayList<ISelectorItem> newList;

    SelectorListDiffUtil(ArrayList<ISelectorItem> oldList, ArrayList<ISelectorItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition)) && !newList.get(newItemPosition).dirty() && !oldList.get(oldItemPosition).dirty();
    }
}
