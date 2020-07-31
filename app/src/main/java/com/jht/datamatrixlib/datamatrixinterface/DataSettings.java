package com.jht.datamatrixlib.datamatrixinterface;

import java.util.List;

public interface DataSettings {

    void putValueDouble(String key, double value);
    double getValueDouble(String key);

    void putValueFloat(String key, float value);
    float getValueFloat(String key);

    void putValueString(String key, String value);
    String getValueString(String key);

    void putValueLong(String key, long value);
    long getValueLong(String key);

    void putValueInt(String key, int value);
    int getValueInt(String key);

    void putValueBoolean(String key, boolean value);
    int getValueBoolean(String key);

//    String performDataMatrixDiagnostic (String key);

    void putValueStringList (String key, List<String> list);
    List<String> getValueStringList (String key);

    void putBytes(String key, byte[] bytes);
    byte[] getBytes(String key);

    void sendEventNotification (String keyWhichIsReallyAnEventType);

//    void requestCallbacks(String key, String progName, boolean forceImmediateCallback);

//    void addEventHandler(IGlobalSettingsEvent eventHandler, String progName);
//    void removeEventHandler(IGlobalSettingsEvent eventHandler);
}
