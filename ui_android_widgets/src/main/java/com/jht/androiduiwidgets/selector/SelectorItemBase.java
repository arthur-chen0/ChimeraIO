package com.jht.androiduiwidgets.selector;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * This provides basic functionality for selector lists that are only one level deep.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SelectorItemBase implements ISelectorItem {
    // List of the children for this item.
    private List<ISelectorItem> subItems = new ArrayList<>();

    // The current selected child.  -1 is no selection.
    private int selection = -1;

    // True if this item is the current selected item in its parent's list.
    private boolean selected = false;

    // The index of the item in its parent's list.
    private int index = -1;

    // The item's parent.  Null for the top level.
    private ISelectorItem parent = null;

    // True if we should show the sub items.
    private boolean showSubItems = false;

    // True if we need to redraw (animate) the item.
    private boolean dirty = false;

    private int absolutePosition = 0;



    public SelectorItemBase() {
        this(null);

    }


    /**
     * Default constructor for an item with children.
     *
     * @param subItems  List of sub items for this item. Cannot be null.
     */
    public SelectorItemBase(@Nullable List<ISelectorItem> subItems) {
        if(subItems != null) {
            for(ISelectorItem item : subItems) {
                addItem(item);
            }
        }
    }


    /**
     * Sets the selected item for this branch in the tree.  Note that -1 means that there is
     * nothing selected.
     *
     * @param selection     The index to select.  -1 for nothing.  Otherwise must be between 0 and numChildren()
     */
    @Override
    public void setSelection(int selection) {
        if(this.selection != selection) {
            if(this.selection >= 0 && this.selection < subItems.size()) {
                subItems.get(this.selection).setSelected(false);
                subItems.get(this.selection).setShowSubItems(false);
            }
            this.selection = selection;
            if(this.selection >= 0 && this.selection < subItems.size()) {
                subItems.get(this.selection).setSelected(true);
                subItems.get(this.selection).setShowSubItems(true);
            }
        } else {
            dirty = true;
            if(selection != -1) {
                subItems.get(this.selection).setShowSubItems(!subItems.get(this.selection).showSubItems());
            }
        }
    }

    /**
     * No need to handle selection events.
     */
    @Override
    public void onSelection() {}


    /**
     * True if this item is its parents current selection.
     */
    @Override
    public boolean selected() {
        return selected;
    }

    /**
     * Used to select the item.  Note this should be called from the parent only.
     * @param selected  True if item is selected.
     */
    @Override
    public void setSelected(boolean selected) {
        if(this.selected != selected) {
            this.dirty = true;
        }
        this.selected = selected;
    }


    /**
     * Used to get the current selection.
     */
    @Override
    public int getSelection() {
        return selection;
    }

    /**
     * Used to set the parent.  This is typically called by the parent item as items are being added
     * to it.
     *
     * @param parent    The parent.
     * @param index     The index the item is in the parent's list.
     */
    @Override
    public void setParent(ISelectorItem parent, int index) {
        this.parent = parent;
        this.index = index;
    }

    /**
     *
     * @return          Returns the items parent.  The top level item will return null.
     */
    @Override
    public ISelectorItem parent() {
        return parent;
    }

    /**
     *
     * @return          Returns true if the subitems should be shown in the list.
     */
    @Override
    public boolean showSubItems() {
        return this.showSubItems;
    }

    /**
     * Used to set the state of whether the sub items should be shown.  Typically this is set to true
     * when an item is selected.  However if the user selects the item again then it is selected
     * while the items are hidden.
     *
     * @param showSubItems  True if the sub items should be shown.
     */
    @Override
    public void setShowSubItems(boolean showSubItems) {
        if(showSubItems != this.showSubItems) {
            this.dirty = true;
        }
        this.showSubItems = showSubItems;
    }

    /**
     *
     * @return          The index of the item within it's parents list.
     */
    @Override
    public int getIndex() {
        return index;
    }

    /**
     *
     * @return  True if this item has been updated.  Typically used by the recycler view so it knows
     *          if the item needs to be updated.
     */
    @Override
    public boolean dirty() {
        return dirty;
    }

    /**
     * Set to true if the items data has changed which would force a redraw.
     *
     * @param dirty     True if the item data has changed.  False if the redraw has happened.
     */
    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Used by the recycler view to determine if the item is new.
     * @param item      item to compare to
     * @return          true if they are the same object
     */
    @Override
    public boolean equals(Object item) {
        return item == this;
    }


    /**
     * This is used to get the depth of an item so it can be indented if necessary.
     * @return      The generation.  0 if this is the top level.
     */
    @Override
    public int getGeneration() {
        int generation = 0;
        ISelectorItem item = this;
        while(item.parent() != null) {
            generation++;
            item = item.parent();
        }
        return generation;
    }

    @Override
    public void getActiveList(List<ISelectorItem> activeList) {
        for(ISelectorItem item : subItems) {
            activeList.add(item);
            if(item.numChildren() > 0 && item.selected() && item.showSubItems()) {
                item.getActiveList(activeList);
            }
        }
    }


    @Override
    public int setAbsolutePosition(int absolutePosition) {
        this.absolutePosition = absolutePosition;
        absolutePosition++;
        for(int i = 0; i < numChildren(); i++) {
            absolutePosition = getChild(i).setAbsolutePosition(absolutePosition);
        }
        return absolutePosition;
    }

    @Override
    public int getAbsolutePosition() {
        return absolutePosition;
    }


    /**
     * @return True if the text data references a string resource and should be translated.
     */
    public boolean translate() {
        return false;
    }


    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {
        throw new UnsupportedOperationException("You cannot call createViewHolder from the base item.");
    }

    @Override
    public void addItem(ISelectorItem child) {
        addItem(child, subItems.size());
    }

    @Override
    public void addItem(ISelectorItem child, int index) {
        subItems.add(child);
        child.setParent(child, index);
        for(int i = index; i < subItems.size(); i++) {
            subItems.get(i).setParent(this, i);
        }
    }


    @Override
    public void removeItem(int index) {
        subItems.remove(index);
        for(int i = index; i < subItems.size(); i++) {
            subItems.get(i).setParent(this, i);
        }
    }


    /**
     *
     * @return The number of items in this node.
     */
    @Override
    public int numChildren() {
        return subItems.size();
    }

    /**
     * This removes all children from this item.
     */
    @Override
    public void removeAllChildren() {
        subItems.clear();
    }

    /**
     *
     * @param index  The requested child to return. 0 <= index < numChildren.
     * @return  Returns the child at the given index.  Returns null if the index is invalid
     */
    @Override
    public ISelectorItem getChild(int index) {
        if(index >= 0 && index < subItems.size()) {
            return subItems.get(index);
        }
        return null;
    }


    /**
     *
     * @return All the children going through all generations spawned by this parent.
     */
    @Override
    public int countAllChildren() {
        int numChildren = 0;
        for(ISelectorItem item : subItems) {
            numChildren += item.countAllChildren();
        }

        return numChildren + subItems.size();
    }
}
