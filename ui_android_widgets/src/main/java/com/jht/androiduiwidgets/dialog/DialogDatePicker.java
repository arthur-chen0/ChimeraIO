package com.jht.androiduiwidgets.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

import com.jht.androiduiwidgets.R;

import java.util.Calendar;


public class DialogDatePicker extends Dialog implements View.OnClickListener {

    protected Context mContext;
    protected ImageButton mBtnOk, mBtnCancel;
    protected CustomDatePicker mCustomDatePicker;
    protected OnDateSetListener mOnDateSetListener;
    protected int mYear, mMonth, mDayOfMonth;


    public interface OnDateSetListener {
        void onDateSet(DatePicker datePicker, int year, int month, int dateOfMonth);
    }

    public DialogDatePicker( Context context, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context);
        this.mContext = context;
        mOnDateSetListener = listener;
        mYear = year;
        mMonth = monthOfYear;
        mDayOfMonth = dayOfMonth;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_date_picker);

        mCustomDatePicker = findViewById(R.id.datePicker);
        mBtnOk = findViewById(R.id.buttonOk);
        mBtnCancel = findViewById(R.id.buttonCancel);
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        mCustomDatePicker.updateDate(mYear, mMonth, mDayOfMonth);
    }

    public void reorderSpinners() {
        if (mCustomDatePicker == null) return;
        mCustomDatePicker.reorderSpinners(getContext());
    }

    public DatePicker getDatePicker() {
        return mCustomDatePicker;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.buttonOk) {
            if (mOnDateSetListener != null) {
                mOnDateSetListener.onDateSet(mCustomDatePicker, mCustomDatePicker.getYear(), mCustomDatePicker.getMonth(), mCustomDatePicker.getDayOfMonth());
            }
            dismiss();
        } else if (i == R.id.buttonCancel) {
            dismiss();
        }
    }

}