package com.jht.androiduiwidgets.selector;

import androidx.recyclerview.widget.DiffUtil;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.androiduiwidgets.R;
import com.jht.translations.LanguageHelper;

import java.util.ArrayList;

/**
 * This class is used by the recycler view to manage the views.  It caches the views within the selector
 * item.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class StandardSelectorItemHolder extends SelectorItemHolderBase {
    private TextView textView;
    private ImageView preImage;
    private ImageView postImage;
    private ImageView busy;
    private RelativeLayout preImageContainer;
    private RelativeLayout postImageContainer;
    private RelativeLayout busyContainer;
    private View selectorView;

    private boolean animatingBusyView = false;
    private boolean isExpandable;

    protected StandardSelectorItemHolder(View v) {
        this(v, false);
    }
    /**
     * Cache the views in the holder.
     *
     * @param v     The root view.
     */
    protected StandardSelectorItemHolder(View v, boolean isExpandable) {
        super(v);
        this.isExpandable = isExpandable;
        textView = v.findViewById(R.id.selector_text);
        preImage = v.findViewById(R.id.selector_item_base_pre_image);
        preImageContainer = v.findViewById(R.id.selector_item_base_pre_image_container);
        postImage = v.findViewById(R.id.selector_item_base_post_image);
        postImageContainer = v.findViewById(R.id.selector_item_base_post_image_container);
        busyContainer = v.findViewById(R.id.selector_item_base_busy_container);
        busy = v.findViewById(R.id.selector_item_base_busy);
        selectorView = v;
    }


    /**
     * The item has changed.  We need to update the views.
     *
     * @param adapterBase   The adapter managing the views.
     * @param item      The item with the view data.
     */
    protected void update(final SelectorAdapterBase adapterBase, final ISelectorItem item, final IOnItemSelected onItemSelected) {
        final ISelectorItem parent = item.parent();
        if(adapterBase instanceof StandardSelectorItemAdapter) {
            final StandardSelectorItemAdapter adapter = (StandardSelectorItemAdapter) adapterBase;
            if (textView != null) {
                if (item.translate()) {
                    textView.setText(LanguageHelper.loadResource(selectorView.getContext(), item.toString()));
                }
                else {
                    textView.setText(item.toString());
                }
                textView.setActivated(item.selected());
            }

            if (item instanceof StandardSelectorItem) {
                StandardSelectorItem theItem = (StandardSelectorItem) item;
                if (theItem.showPreImage() && theItem.preImage() != null) {
                    preImageContainer.setVisibility(View.VISIBLE);
                    preImage.setImageDrawable(theItem.preImage());
                } else {
                    preImageContainer.setVisibility(View.GONE);
                }
                if (theItem.showPostImage() && theItem.postImage() != null) {
                    postImageContainer.setVisibility(View.VISIBLE);
                    postImage.setImageDrawable(theItem.postImage());

                } else {
                    postImageContainer.setVisibility(View.GONE);
                }
                busyContainer.setVisibility(theItem.showBusy() ? View.VISIBLE : View.GONE);
                if (theItem.showBusy() && theItem.animateBusy() && !animatingBusyView) {
                    busy.startAnimation(AnimationUtils.loadAnimation(selectorView.getContext(), R.anim.rotate_progress));
                    animatingBusyView = true;
                } else if ((!theItem.showBusy() || !theItem.animateBusy()) && animatingBusyView) {
                    busy.clearAnimation();
                    animatingBusyView = false;
                }


                if (selectorView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) selectorView.getLayoutParams();
                    boolean killMargin = item.parent().parent() == null && item.getIndex() == item.parent().numChildren() -1;
                    int spacing = adapter.getItemSpacing() == -1 ? (int)selectorView.getContext().getResources().getDimension(R.dimen.selector_subitem_spacing) : adapter.getItemSpacing();
                    p.setMargins(0, 0, 0, killMargin ? 0 : spacing);
                    selectorView.requestLayout();
                }

            }

            selectorView.setActivated(item.selected());
            selectorView.setPaddingRelative((int) itemView.getContext().getResources().getDimension(R.dimen.selector_subitem_margin) * (parent.getGeneration()), 0, 0, 0);
            preImage.setActivated(item.selected());
            postImage.setActivated(item.selected());
            textView.setActivated(item.selected());

            selectorView.setOnClickListener(v -> updateSelection(item, adapter, onItemSelected));
        }
    }

    protected void updateSelection(ISelectorItem item, StandardSelectorItemAdapter adapter, IOnItemSelected onItemSelected) {
        if(isExpandable) {
            ArrayList<ISelectorItem> newList = new ArrayList<>();
            ArrayList<ISelectorItem> currentList = adapter.getActiveList();
            final ISelectorItem parent = item.parent();
            ISelectorItem root = item;
            while(root.parent() != null) {
                root = root.parent();
            }
            parent.setSelection(item.getIndex());
            root.getActiveList(newList);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SelectorListDiffUtil(adapter.getActiveList(), newList));
            adapter.setActiveList(newList);
            result.dispatchUpdatesTo(adapter);
            for(ISelectorItem i : newList) {
                i.setDirty(false);
            }
            item.onSelection();
            if(onItemSelected != null) {
                ISelectorItem selectedItem = item;
                while(selectedItem.numChildren() > 0 && selectedItem.showSubItems() && selectedItem.getSelection() >= 0) {
                    selectedItem = selectedItem.getChild(selectedItem.getSelection());
                }
                onItemSelected.onItemSelected(selectedItem);
            }
        }
        else {

            ISelectorItem root = item;
            final ISelectorItem parent = item.parent();
            while(root.parent() != null) {
                root = root.parent();
            }
            if(parent.getSelection() >= 0) {
                adapter.notifyItemChanged(parent.getSelection());
            }
            parent.setSelection(item.getIndex());
            item.onSelection();
            if(onItemSelected != null) {
                ISelectorItem selectedItem = item;
                while (selectedItem.numChildren() > 0 && selectedItem.showSubItems() && selectedItem.getSelection() >= 0) {
                    selectedItem = selectedItem.getChild(selectedItem.getSelection());
                }
                adapter.notifyItemChanged(item.getIndex());
                onItemSelected.onItemSelected(selectedItem);
            }
        }
    }
}
