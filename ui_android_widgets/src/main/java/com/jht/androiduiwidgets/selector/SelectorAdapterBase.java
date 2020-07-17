package com.jht.androiduiwidgets.selector;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The selector adapter is used to create a selector list utilizing a recycler view.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SelectorAdapterBase extends RecyclerView.Adapter<SelectorItemHolderBase> {



    // The context that contains the adapter and views.
    private Context context;
    private ISelectorItem menu;
    private IOnItemSelected onItemSelected;
    private int itemSpacing;



    /**
     * Create a selector with no help button and no arrow.
     *
     * @param context       The context that contains the adapter and views.
     * @param menu          The base item that contains the menu list.
     */
    public SelectorAdapterBase(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected) {
        this(context, menu, onItemSelected, -1);
    }

    /**
     * Create a selector with no help button and no arrow.
     *
     * @param context       The context that contains the adapter and views.
     * @param menu          The base item that contains the menu list.
     */
    public SelectorAdapterBase(@NonNull Context context, @NonNull ISelectorItem menu, IOnItemSelected onItemSelected, int itemSpacing) {

        this.context = context;
        this.onItemSelected = onItemSelected;
        this.menu = menu;
        this.itemSpacing = itemSpacing;

        int position = 0;
        for(int i = 0; i < menu.numChildren(); i++) {
            position = menu.getChild(i).setAbsolutePosition(position);
        }
    }

    /**
     * Helper to get the context.
     *
     * @return The context that owns the adapter.
     */
    protected Context getContext() {
        return context;
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
        Log.e("blePairBug", "onBindViewHolder, position: " + position);
        ISelectorItem item = menu.getChild(position);
        if (item != null) {
            Log.e("blePairBug", "onBindViewHolder, item is not null");
            holder.update(this, item, onItemSelected);
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
        return menu.getChild(viewType).createViewHolder(context, parent);
    }

    /**
     * @return Returns the number of items in the list.
     */
    @Override
    public int getItemCount() {
        return menu.numChildren();
    }

    protected ISelectorItem menu() { return menu; }

    protected IOnItemSelected onItemSelected() { return onItemSelected; }



    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     *                 <code>position</code>. Type codes need not be contiguous.
     */
    public int getItemViewType(int position) {
        return position;
    }


    public int getItemSpacing() { return itemSpacing; }

}
