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
@SuppressWarnings({"unused", "WeakerAccess"})
public class StandardSelectorItem extends SimpleSelectorItem {


    // The pre and post images to display.
    private Drawable preImage;
    private Drawable postImage;
    private boolean showPreImage = true;
    private boolean showPostImage = true;
    private boolean showBusy = false;
    private boolean animateBusy = false;

    /**
     * Default constructor for an item with children.
     *
     * @param label     The label for the item.
     */
    public StandardSelectorItem(String label, @Nullable Drawable preImage, @Nullable Drawable postImage, @Nullable List<ISelectorItem> subItems) {
        super(label, subItems);
        this.preImage = preImage;
        this.postImage = postImage;
    }

    /**
     * Default constructor for an item with no children.
     *
     * @param label     The label for the item.
     */
    public StandardSelectorItem(String label, @Nullable Drawable preImage, @Nullable Drawable postImage) {
        super(label, null);
        this.preImage = preImage;
        this.postImage = postImage;
    }

    public void showBusy(boolean show, boolean animate) {
        showBusy = show;
        animateBusy = animate;
    }

    public void showImages(boolean pre, boolean post) {
        showPreImage = pre;
        showPostImage = post;
    }

    public Drawable preImage() { return preImage; }
    public Drawable postImage() { return postImage; }
    public boolean showPreImage() { return showPreImage; }
    public boolean showPostImage() { return showPostImage; }
    public boolean showBusy() { return showBusy; }
    public boolean animateBusy() { return animateBusy; }



    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.standard_selector_item, parent,false);
        return new StandardSelectorItemHolder(v);
    }
}
