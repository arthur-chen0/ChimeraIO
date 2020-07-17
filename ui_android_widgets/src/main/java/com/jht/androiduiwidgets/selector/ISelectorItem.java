package com.jht.androiduiwidgets.selector;


import android.content.Context;
import androidx.annotation.NonNull;

import android.view.ViewGroup;

import java.util.List;

/**
 * This interface is used by the selector list to produce a menu tree with selections along the tree.
 * The top level is a list of items.  Each item must override the toString() function as this will
 * be used to get the item name for display.
 *
 */
@SuppressWarnings("unused")
public interface ISelectorItem {

    /**
     *
     * @return The number of items in this node.
     */
    int numChildren();

    /**
     * This removes all children from this item.
     */
    void removeAllChildren();

    /**
     *
     * @param index  The requested child to return. 0 <= index < numChildren.
     * @return  Returns the child at the given index.  Returns null if the index is invalid
     */
    ISelectorItem getChild(int index);

    /**
     * Sets the selected item for this branch in the tree.  Note that -1 means that there is
     * nothing selected.
     *
     * @param selection     The index to select.  -1 for nothing.  Otherwise must be between 0 and subItems().size.
     */
    void setSelection(int selection);

    /**
     * Used to get the current selection.
     */
    int getSelection();

    /**
     * Subclasses should override this function to handle the item being selected.
     */
    void onSelection();


    /**
     * True if this item is its parents current selection.
     */
    boolean selected();

    /**
     * Used to select the item.  Note this should be called from the parent only.
     * @param selected      True if this item is selected.
     */
    void setSelected(boolean selected);

    /**
     * Used to set the parent.  This is typically called by the parent item as items are being added
     * to it.
     *
     * @param parent    The parent.
     * @param index     The index the item is in the parent's list.
     */
    void setParent(ISelectorItem parent, int index);

    /**
     *
     * @return          Returns the items parent.  The top level item will return null.
     */
    ISelectorItem parent();

    /**
     *
     * @return          Returns true if the subitems should be shown in the list.
     */
    boolean showSubItems();

    /**
     * Used to set the state of whether the sub items should be shown.  Typically this is set to true
     * when an item is selected.  However if the user selects the item again then it is selected
     * while the items are hidden.
     *
     * @param showSubItems  True if the sub items should be shown.
     */
    void setShowSubItems(boolean showSubItems);

    /**
     *
     * @return          The index of the item within it's parents list.
     */
    int getIndex();

    /**
     *
     * @return  True if this item has been updated.  Typically used by the recycler view so it knows
     *          if the item needs to be updated.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean dirty();

    /**
     * Set to true if the items data has changed which would force a redraw.
     *
     * @param dirty     True if the item data has changed.  False if the redraw has happened.
     */
    void setDirty(boolean dirty);

    /**
     * This is used to get the depth of an item so it can be indented if necessary.
     * @return      The generation.  0 if this is the top level.
     */
    int getGeneration();

    /**
     * Search for the items in itemList that are active and add them to the active list.
     * @param activeList  Will contain the active item list after the function call.
     */
    void getActiveList(List<ISelectorItem> activeList);

    /**
     *
     * @param absolutePosition Sets the absolute position of the item in the list.
     */
    int setAbsolutePosition(int absolutePosition);


    /**
     *
     * @return The absolute position in the list.
     */
    int getAbsolutePosition();

    /**
     * @return True if the text data references a string resource and should be translated.
     */
    boolean translate();



    /**
     * Create the view holder for this item.  Adding this function in the item allows us to have
     * one adapter class to rule them all.
     *
     * @param context   The context that will hold the view.
     * @param parent    The parent view that will contain the view within the view holder.
     */
    SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent);

    /**
     * Add a child item to the end of the list.  This will take care of updating the item's parent.
     *
     * @param child    The child to add.
     */
    void addItem(ISelectorItem child);

    /**
     * Add a child item to the list based on the index.  This will take care of updating the item's
     * parent and fixing the index for other children.
     *
     * @param child    The child to add.
     */
    void addItem(ISelectorItem child, int index);

    /**
     * Remove an item from the list.  This will take care of updating the index for each of the
     * children.
     *
     * @param index  Index of the item to remove.
     */
    void removeItem(int index);

    /**
     *
     * @return All the children going through all generations spawned by this parent.
     */
    int countAllChildren();
}
