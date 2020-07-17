package com.jht.androiduiwidgets.viewpager;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import java.util.List;

/**
 * Adapter used to manage the pages in MES.  This links the side menu to the pages.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class StandardPageAdapter extends PagerAdapter {

    List<IPageHolder> pages;

    public StandardPageAdapter(@NonNull List<IPageHolder> pages) {
        this.pages = pages;
    }

    @Override
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        pages.get(position).create(collection);
        return pages.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object item) {
        if(item instanceof IPageHolder) {
            IPageHolder page = (IPageHolder)item;
            page.destroy(collection);
        }
    }

    @Override
    public int getCount() {
        return pages.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        if(object instanceof IPageHolder) {
            return ((IPageHolder)object).isViewFromObject(view);
        }
        return false;
    }
}
