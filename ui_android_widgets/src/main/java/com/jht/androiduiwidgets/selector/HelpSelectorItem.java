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

@SuppressWarnings({"unused", "WeakerAccess"})
public class HelpSelectorItem extends StandardSelectorItem {

    public interface IOnHelp {
        void onHelp(View v, String title, String text);
    }

    private String helpText;
    private Drawable helpImage;
    private IOnHelp onHelp;


    public HelpSelectorItem(@NonNull Context context, String label, String helpText, IOnHelp onHelp) {
        this(context, label, helpText, onHelp, null);
    }

    public HelpSelectorItem(@NonNull Context context, String label, @Nullable String helpText, IOnHelp onHelp, List<ISelectorItem> subItems) {
        super(label, null, null, subItems);
        this.helpText = helpText;
        this.onHelp = onHelp;
        helpImage = context.getResources().getDrawable(R.drawable.selector_help);
    }


    public void update(String helpText) {
        this.helpText = helpText;
    }

    public String helpText() {
        return helpText;
    }

    @Override
    public boolean showPostImage() {
        return selected();
    }

    @Override
    public Drawable postImage() {
        return helpImage;
    }


    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(R.layout.standard_selector_item, parent,false);
        return new HelpSelectorItemHolder(v, onHelp);
    }

}
