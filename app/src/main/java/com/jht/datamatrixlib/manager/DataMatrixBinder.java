package com.jht.datamatrixlib.manager;

import android.util.Log;

import com.jht.datamatrixlib.adapter.IAdapter;
import com.jht.datamatrixlib.adapter.IntAdapter;
import com.jht.datamatrixlib.callback.DMCallback;
import com.jht.datamatrixlib.callback.SettingsEventCallback;

import java.util.ArrayList;
import java.util.List;

public class DataMatrixBinder {

    private final String TAG = getClass().getSimpleName();

//    private DataManager dataManager = new DataManager();
    public List<IAdapter> eventList = new ArrayList<>();

    private static class SingletonHolder {
        static private final DataMatrixBinder mDataMatrixInstance = new DataMatrixBinder();
    }

    static public DataMatrixBinder getInstance() {
        return SingletonHolder.mDataMatrixInstance;
    }

    public void registerEventCallback(SettingsEventCallback settingsEventCallback){
        DataManager.getInstance().registerEventCallback(settingsEventCallback);
    }

    public IntAdapter createIntAdapter(Object key) {
        return createIntAdapter(key, null, true);
    }

    public IntAdapter createIntAdapter(Object key, DMCallback<Integer> cb) {
        return createIntAdapter(key.toString(), cb, true);
    }

    public IntAdapter createIntAdapter(Object key, DMCallback<Integer> cb, boolean forceImmediateCallback) {
        IAdapter baseAdapter = getMatchingAdapter(key.toString());
        IntAdapter adapter;
        if (baseAdapter == null) {
            adapter = new IntAdapter(key.toString(), cb, forceImmediateCallback);
            addAdapterIfNeeded(adapter);
            return adapter;
        } else {
            adapter = (IntAdapter) baseAdapter;
        }

        return adapter;
    }

    private void addAdapterIfNeeded(IAdapter adapter) {
        if (adapter.hasCallback() && !eventList.contains(adapter)) {
            eventList.add(adapter);
        }
    }

    private IAdapter getMatchingAdapter(Object key) {
        for (IAdapter adapter : eventList) {
            if (adapter.getKey().equals(key)) {
                return adapter;
            }
        }

        return null;
    }

    public void putValueDouble(String key, double value) {

    }

    public double getValueDouble(String key) {
        return 0;
    }

    public void putValueFloat(String key, float value) {

    }

    public float getValueFloat(String key) {
        return 0;
    }

    public void putValueString(String key, String value) {

    }

    public String getValueString(String key) {
        return null;
    }

    public void putValueLong(String key, long value) {

    }

    public long getValueLong(String key) {
        return 0;
    }

    public void putValueInt(String key, int value) {
        DataManager.getInstance().putValueInt(key, value);
    }

    public int getValueInt(String key) {
        return 0;
    }

    public void putValueBoolean(String key, boolean value) {

    }

    public int getValueBoolean(String key) {
        return 0;
    }

    public void putValueStringList(String key, List<String> list) {

    }

    public List<String> getValueStringList(String key) {
        return null;
    }

    public void putBytes(String key, byte[] bytes) {

    }

    public byte[] getBytes(String key) {
        return new byte[0];
    }

    public void sendEventNotification(String keyWhichIsReallyAnEventType) {

    }


}
