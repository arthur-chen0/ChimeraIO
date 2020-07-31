package com.jht.datamatrixlib.adapter;

import com.jht.datamatrixlib.callback.DMCallback;
import com.jht.datamatrixlib.manager.CallbackManager;

public class BaseAdapter implements IAdapter {

    protected String key;
    private DMCallback cb;
    private boolean forceImmediateCallback = true;

    /**
     * @param key      The data matrix key
     * @param callback The interface callbacks are redirected to, can be set to null
     */
    public BaseAdapter(String key, DMCallback callback, boolean forceCallbackImmediate) {
        cb = callback;
        this.key = key;
        forceImmediateCallback = forceCallbackImmediate;
        register();
    }

    private void register() {
        if (cb != null) {
            // TODO the callback will trigger any callback in the same process
            // Added a workaround in the adapter class to try to get the value right away
            //DataMatrixBinder.getInstance().registerEventCallbacksForThisKey(key, cb, forceImmediateCallback);

            CallbackManager.getInstance().registerEventCallbacksForThisKey(key, cb, false);
        }
    }

    @Override
    public void deregister() {

    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public boolean hasCallback() {
        return false;
    }
}
