package com.jht.datamatrixlib.callback;

import java.util.List;

public interface SettingsEventCallback {

    void settingChangeDouble(String key, double value);
    void settingChangeFloat(String key, float value);
    void settingChangeLong(String key, long value);
    void settingChangeInt(String key, int value);
    void settingChangeString(String key, String value);
    void settingChangeBoolean(String key, boolean value);
    void settingChangeStringList(String key, List<String> list);
    void settingChangeByteArray(String key, byte[] bytes);

    void eventNotification (String keyWhichIsReallyAnEventType);
}
