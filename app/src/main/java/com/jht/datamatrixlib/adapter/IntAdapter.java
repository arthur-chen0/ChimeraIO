package com.jht.datamatrixlib.adapter;


import com.jht.datamatrixlib.callback.DMCallback;
import com.jht.datamatrixlib.manager.DataMatrixBinder;

/**
 * Created by chris.foyer on 7/21/2016.
 */
public class IntAdapter extends BaseAdapter {
    /**
     * @param key      The data matrix key
     * @param callback The interface callbacks are redirected to, can be set to null
     */
    public IntAdapter(String key, DMCallback callback) {
        this(key, callback, true);
    }

    public IntAdapter(String key, DMCallback callback, boolean forceImmediateCallback) {
        super(key, callback, forceImmediateCallback);
        if (forceImmediateCallback) {
            try {
                Integer value = DataMatrixBinder.getInstance().getValueInt(key);
                callback.dataMatrixCallback(value);
            } catch (Exception e) {
            }
        }
    }

//    public void put(int value) {
//        DataMatrixBinder.getInstance().putValueInt(key, value);
//    }
//
//    public int get(int defaultValue) {
//        return DataMatrixBinder.getInstance().getValueInt(key, defaultValue);
//    }
//
//    public int get() {
//        return DataMatrixBinder.getInstance().getValueInt(key);
//    }
}
