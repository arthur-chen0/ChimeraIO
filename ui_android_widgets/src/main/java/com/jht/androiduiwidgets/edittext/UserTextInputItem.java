package com.jht.androiduiwidgets.edittext;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jht.androiduiwidgets.selector.ISelectorItem;
import com.jht.androiduiwidgets.selector.SelectorItemBase;
import com.jht.androiduiwidgets.selector.SelectorItemHolderBase;

import java.util.ArrayList;
import java.util.List;


/**
 * This is used to create a list of networks to connect to over WiFi.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class UserTextInputItem extends SelectorItemBase {

    private TextView.OnEditorActionListener onEditorActionListener;
    private IUserTextInputProperties properties;

    static public ISelectorItem createItems(List<IUserTextInputProperties> properties, TextView.OnEditorActionListener onEditorActionListener) {
        List<ISelectorItem> menu = new ArrayList<>();
        for (int i = 0; i < properties.size(); i++) {
            UserTextInputItem item = new UserTextInputItem(properties.get(i), onEditorActionListener);
            item.setAbsolutePosition(i);
            menu.add(item);
        }
        return new SelectorItemBase(menu);
    }


    /**
     * Initialize the item passing the network information.
     *
     * @param properties  Defines the user input properties.
     */
    public UserTextInputItem(@NonNull IUserTextInputProperties properties, TextView.OnEditorActionListener onEditorActionListener) {
        this.properties = properties;
        this.onEditorActionListener = onEditorActionListener;
    }


    /**
     * @return The properties that is used to control the edit text.
     */
    public IUserTextInputProperties properties() {
        return properties;
    }


    /**
     * @return The callback to handle editor actions.
     */
    public TextView.OnEditorActionListener onEditorActionListener() {
        return onEditorActionListener;
    }


    /**
     * Used to get the item's label.
     *
     * @return Return the label
     */
    @NonNull
    @Override
    public String toString() {
        return properties.toString();
    }


    @Override
    public SelectorItemHolderBase createViewHolder(Context context, @NonNull ViewGroup parent) {

        View v = LayoutInflater.from(context).inflate(com.jht.androiduiwidgets.R.layout.simple_selector_item, parent, false);
        return new UserTextInputItemHolder(new UserTextInput(context));
    }


}
