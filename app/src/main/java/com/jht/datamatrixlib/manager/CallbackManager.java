package com.jht.datamatrixlib.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jht.datamatrixlib.MultiMap;
import com.jht.datamatrixlib.callback.DMCallback;
import com.jht.datamatrixlib.callback.SettingsEventCallback;

import java.util.List;


public class CallbackManager {

    private final String TAG = getClass().getSimpleName();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static MultiMap<String, DMCallback> callbackMultiMap = new MultiMap<>();

    private static class SingletonHolder {
        static private final CallbackManager mCallbackManager = new CallbackManager();
    }

    static public CallbackManager getInstance() {
        return CallbackManager.SingletonHolder.mCallbackManager;
    }

    public CallbackManager(){
        DataMatrixBinder.getInstance().registerEventCallback(settingsEventCallback);
    }

    public void registerEventCallbacksForThisKey(String key, DMCallback cb, boolean forceImmediateCallback){
        Log.d(TAG, "registerEventCallbacksForThisKey: " + key);
        callbackMultiMap.put(key,cb);
    }

    public synchronized void deRegisterEventCallbacksForThisKey(String key, DMCallback smartVariableObject) {
        try {
            callbackMultiMap.removeFromList(key, smartVariableObject);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public SettingsEventCallback settingsEventCallback = new SettingsEventCallback() {
        @Override
        public synchronized void settingChangeDouble(String key, double value) {

        }

        @Override
        public synchronized void settingChangeFloat(String key, float value) {

        }

        @Override
        public synchronized void settingChangeLong(String key, long value) {

        }

        @Override
        public synchronized void settingChangeInt(String key, int value) {
            List<DMCallback> interestedParties = callbackMultiMap.get(key);
            if (null != interestedParties) {
                synchronized (interestedParties) {
                    for (Object eventForwarderObj : interestedParties) {
                        mHandler.post(() -> {
                            try {
                                Log.d(TAG, "settingChangeInt: send value change " + value);
                                DMCallback<Integer> eventForwarder = (DMCallback<Integer>) eventForwarderObj;
                                eventForwarder.dataMatrixCallback(value);
                            } catch (Exception e) {
                                Log.e(TAG, "Error on Key " + key);
                                throw e;
                            }
                        });
                    }
                }
            }
        }

        @Override
        public synchronized void settingChangeString(String key, String value) {

        }

        @Override
        public synchronized void settingChangeBoolean(String key, boolean value) {

        }

        @Override
        public synchronized void settingChangeStringList(String key, List<String> list) {

        }

        @Override
        public synchronized void settingChangeByteArray(String key, byte[] bytes) {

        }

        @Override
        public synchronized void eventNotification(String keyWhichIsReallyAnEventType) {

        }
    };
}
