package com.jht.chimera.io.lib;

public interface IMCUCallback {
    void operationMode(int mode);
    void version(int version);
    void safeKeyStatus(boolean status);
    void powerState(byte source, boolean _switch);
    void externalPower(IOManager.PowerVoltage powerVoltage, int current);
    void boot(boolean state);
    void fanSpeed(byte level, int rpm);
    void lcbType(byte type);
    void consolePowerVoltage(short voltage);
    void extendPowerVoltage(short voltage, boolean check);
    void usbVoltage(short voltage);
    void powerMonitorState(boolean enable);
    void consolePowerCurrent(short current);
    void extendPowerCurrent(short current);
    void extendPowerError(boolean error);
    void keyEvent(int event, short keyCode);
    void safeKeyEvent(boolean status);
    void debugMessage(String message);
    void cecCommand(int result);
    void getCECresponse(byte[] response);
    void irCommand(boolean result);
}
