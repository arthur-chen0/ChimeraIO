package com.jht.androiduiwidgets.dialog;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;




public class CustomDatePicker extends DatePicker
{

    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
      //  reorderSpinners(context);
    }

    public void reorderSpinners(Context context) {

        try {
            Log.e("dateBug", "is this even getting called?");

            Field field_mDelegate = DatePicker.class.getDeclaredField("mDelegate");

            field_mDelegate.setAccessible(true);
            //DatePicker.class.getDeclaredClasses()
            //final  DatePickerSpinnerDelegate mSpinners = ( DatePickerSpinnerDelegate) field_mDelegate.get(this);



//            for(int i = 0; i < DatePicker.class.get.length; i++ ){
//                Log.e("dateBug", "class: " + DatePicker.class.getClasses()[i]);
//            }

//            Field field_mSpinners = DatePicker.class.getDeclaredField("mSpinners");
//            field_mSpinners.setAccessible(true);
//            final LinearLayout mSpinners = (LinearLayout) field_mSpinners.get(this);

//            Log.e("dateBug", "here 0");
//
//            Field field_mDaySpinner = DatePicker.class.getDeclaredField("mDaySpinner");
//            field_mDaySpinner.setAccessible(true);
//            NumberPicker mDaySpinner = (NumberPicker) field_mDaySpinner.get(this);
//
//            Log.e("dateBug", "here 1");
//
//            Field field_mMonthSpinner = DatePicker.class.getDeclaredField("mMonthSpinner");
//            field_mMonthSpinner.setAccessible(true);
//            NumberPicker mMonthSpinner = (NumberPicker) field_mMonthSpinner.get(this);
//            mMonthSpinner.setOnTouchListener((v, event) -> {
//                InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(mSpinners.getWindowToken(), 0);
//                return false;
//            });
//
//            Log.e("dateBug", "here 2");
//
//            Field field_mYearSpinner = DatePicker.class.getDeclaredField("mYearSpinner");
//            field_mYearSpinner.setAccessible(true);
//            NumberPicker mYearSpinner = (NumberPicker) field_mYearSpinner.get(this);
//
//            Log.e("dateBug", "here 3");
//
//            Method method = this.getClass().getSuperclass().getDeclaredMethod("setImeOptions", NumberPicker.class, int.class, int.class);
//            method.setAccessible(true);
//
//            Log.e("dateBug", "here 4");
//
//            mSpinners.removeAllViews();
////            char[] order = DateFormat.getDateFormatOrder(getContext());
//            char[] order = new char[] {'M', 'y', 'd'};
//
//            Log.e("dateBug", "here 5");
//
//            final int spinnerCount = order.length;
//            for (int i = 0; i < spinnerCount; i++) {
//                Log.e("dateBug", "in for with: " + order[i]);
//                switch (order[i]) {
//                    case 'd':
//                        mSpinners.addView(mDaySpinner);
//                        method.invoke(this, mDaySpinner, spinnerCount, i);
//                        break;
//                    case 'M':
//                        mSpinners.addView(mMonthSpinner);
//                        method.invoke(this, mMonthSpinner, spinnerCount, i);
//                        break;
//                    case 'y':
//                        mSpinners.addView(mYearSpinner);
//                        method.invoke(this, mYearSpinner, spinnerCount, i);
//                        break;
//                    default:
//                        throw new IllegalArgumentException();
//                }
//            }
        } catch (Exception e) {
            Log.e("dateBug", "something went wrong");
            e.printStackTrace();
        }
    }

}
