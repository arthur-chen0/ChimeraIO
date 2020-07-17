package com.jht.androiduiwidgets.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jht.androiduiwidgets.R;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class PageIndicator extends LinearLayout
{
	private List<ImageButton> pageButtons = new ArrayList<>();
	private ViewPager parent;
	private int parentId = 0;
	private PagerAdapter adapter;

	private DataSetObserver adapterObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateIndicators();
        }
    };

	private ViewPager.OnAdapterChangeListener pagerObserver = new ViewPager.OnAdapterChangeListener() {
        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            linkToParentAdapter();
        }
    };

	private ViewPager.OnPageChangeListener pageObserver = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            updateCurrentPage();

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public PageIndicator(Context context)
    {
        this(context, null, 0);
    }
	public PageIndicator(Context context, AttributeSet attrs)
    {
	    this(context, attrs, 0);
	}

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        boolean horizontal = true;

        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator, defStyleAttr, 0);
            horizontal = a.getBoolean(R.styleable.PageIndicator_horizontal, true);
            parentId = a.getResourceId(R.styleable.PageIndicator_view_pager, 0);
            a.recycle();
        }

        setOrientation(horizontal ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

                linkToParent((ViewPager)(((ViewGroup)getParent()).findViewById(parentId)));
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if(adapter != null) {
                    adapter.unregisterDataSetObserver(adapterObserver);
                }
                adapter = null;
                if(parent != null) {
                    parent.removeOnAdapterChangeListener(pagerObserver);
                    parent.removeOnPageChangeListener(pageObserver);
                }
                parent = null;
            }
        });

	}

	private void linkToParent(ViewPager parentPager) {
	    if(parent != null) {
	        parent.removeOnAdapterChangeListener(pagerObserver);
            parent.removeOnPageChangeListener(pageObserver);
        }
        parent = parentPager;
        if(parent != null) {
            parent.addOnAdapterChangeListener(pagerObserver);
            parent.addOnPageChangeListener(pageObserver);
            linkToParentAdapter();
        }
    }

    private void linkToParentAdapter() {
	    if(parent.getAdapter() != null) {
	        if(adapter != null) {
	            adapter.unregisterDataSetObserver(adapterObserver);
            }
            adapter = parent.getAdapter();
            if (adapter != null) {
                parent.getAdapter().registerDataSetObserver(adapterObserver);
            }
        }

        // Update views.
        updateIndicators();
    }

    private void updateIndicators() {
	    removeAllViews();
	    pageButtons.clear();
	    if(adapter != null && adapter.getCount() > 1) {
	        for(int i = 0; i < adapter.getCount(); i++) {
	            ImageButton button = new ImageButton(getContext());
	            button.setBackground(getResources().getDrawable(R.drawable.page_indicator_icon));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(10, 10);
                params.setMargins(5,5,5,5);
                button.setLayoutParams(params);
                addView(button);
                pageButtons.add(button);
            }
        }
        updateCurrentPage();
    }

	
	private void updateCurrentPage()
	{

        for(int i = 0; i < pageButtons.size(); i++) {
            pageButtons.get(i).setSelected(parent.getCurrentItem() == i);
        }
	}
}
