package com.jht.datamatrixlib.manager;

import android.os.Bundle;
import android.util.Log;

import com.jht.datamatrixlib.callback.SettingsEventCallback;
import com.jht.datamatrixlib.datamatrixinterface.DataSettings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataManager implements DataSettings {

    private final String TAG = getClass().getSimpleName();

    private SettingsEventCallback settingsEventCallback;

    final Map<String, Class<?>> mKeyToDataType = new HashMap<>();

    final Map<String, Double> mSettingsMapDbl = new HashMap<>();
    final Map<String, Float> mSettingsMapFloat = new HashMap<>();
    final Map<String, Long> mSettingsMapLong = new HashMap<>();
    final Map<String, Integer> mSettingsMapInt = new HashMap<>();
    final Map<String, String> mSettingsMapString = new HashMap<>();
    final Map<String, byte[]> mSettingsMapBytes = new HashMap<>();
    final Map<String, Boolean> mSettingsMapBoolean = new HashMap<>();
    final Map<String, List<String>> mSettingsMapStringList = new HashMap<>();
    final Map<String, Bundle> mSettingsMapBundle = new HashMap<>();

    Lock mBroadcastLock = new ReentrantLock();

    private static class SingletonHolder {
        static private final DataManager mDataManager = new DataManager();
    }

    static public DataManager getInstance() {
        return DataManager.SingletonHolder.mDataManager;
    }


    public void registerEventCallback(SettingsEventCallback settingsEventCallback){
        this.settingsEventCallback = settingsEventCallback;
    }

    private boolean isActionKey(String key) {
        String lowerCaseKey = key.toLowerCase();
        return (lowerCaseKey.contains("action") || lowerCaseKey.contains("event"));
    }

    @Override
    public void putValueDouble(String key, double value) {
    }

    @Override
    public double getValueDouble(String key) {
        return 0;
    }

    @Override
    public void putValueFloat(String key, float value) {

    }

    @Override
    public float getValueFloat(String key) {
        return 0;
    }

    @Override
    public void putValueString(String key, String value) {

    }

    @Override
    public String getValueString(String key) {
        return null;
    }

    @Override
    public void putValueLong(String key, long value) {

    }

    @Override
    public long getValueLong(String key) {
        return 0;
    }

    @Override
    public void putValueInt(String key, int value) {
        mBroadcastLock.lock();

        synchronized (mSettingsMapInt) {
            Log.d(TAG, key + " put int " + value);
            if (isActionKey(key) == false)
                mSettingsMapInt.put(key, value);

            if (settingsEventCallback != null) {
                Log.d(TAG, "putValueInt: settingchangeInt " + key + " " + value);
                settingsEventCallback.settingChangeInt(key, value);
            }
        }
        mBroadcastLock.unlock();
    }

    @Override
    public int getValueInt(String key) {
        return 0;
    }

    @Override
    public void putValueBoolean(String key, boolean value) {

    }

    @Override
    public int getValueBoolean(String key) {
        return 0;
    }

    @Override
    public void putValueStringList(String key, List<String> list) {

    }

    @Override
    public List<String> getValueStringList(String key) {
        return null;
    }

    @Override
    public void putBytes(String key, byte[] bytes) {

    }

    @Override
    public byte[] getBytes(String key) {
        return new byte[0];
    }

    @Override
    public void sendEventNotification(String keyWhichIsReallyAnEventType) {

    }
}
