package com.jht.androiduiwidgets.selector;

import android.content.Context;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * The selector adapter is used to create a selector list utilizing a recycler view.
 */
@SuppressWarnings({"unused"})
public class StandardSelectorItemAdapter extends SelectorAdapterBase {

    // The context that contains the adapter and views.
    private boolean isExpandable;
    private ArrayList<ISelectorItem> activeList;


    public StandardSelectorItemAdapter(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected) {
        this(context, menu, onItemSelected, false);
    }

    public StandardSelectorItemAdapter(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected, int itemSpacing) {
        this(context, menu, onItemSelected, itemSpacing, false);
    }

    public StandardSelectorItemAdapter(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected, boolean isExpandable) {
        this(context, menu, onItemSelected, -1, isExpandable);
    }


    public StandardSelectorItemAdapter(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected, int itemSpacing, boolean isExpandable) {
        super(context, menu, onItemSelected, itemSpacing);
        this.isExpandable = isExpandable;

        activeList = new ArrayList<>();
        if(isExpandable) {
            menu.getActiveList(activeList);
        }
    }

    /**
     * This is called by the recycler view to bind a view holder to a view.  Here we update the view
     * based on the item in the list.
     *
     * @param holder   The view holder to bind to the data.
     * @param position The position in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull SelectorItemHolderBase holder, final int position) {
        if(isExpandable) {
            ISelectorItem item = activeList.get(position);
            if(item != null) {
                holder.update(this, item, onItemSelected());
            }
        }
        else {
            super.onBindViewHolder(holder, position);
        }
    }

    /**
     * Used to create the view holder.
     * @param parent    The view parent.
     * @param viewType  The view type.
     * @return          The view holder for an item.
     */
    @Override
    @NonNull
    public SelectorItemHolderBase onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        if(isExpandable) {
            return activeList.get(viewType).createViewHolder(getContext(), parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }



    /**
     * @return Returns the number of items in the list.
     */
    @Override
    public int getItemCount() {
        return isExpandable ? activeList.size() : menu().numChildren();
    }

    /**
     * Used by the holder when the active list changes.
     * @param list  The new active list.
     */
    void setActiveList(ArrayList<ISelectorItem> list) {
        activeList = list;
    }

    /**
     * Used by the holder to get the current list.
     */
    ArrayList<ISelectorItem> getActiveList() {
        return activeList;
    }


}
