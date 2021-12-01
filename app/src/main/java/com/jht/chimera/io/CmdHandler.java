package com.jht.chimera.io;

import android.util.Log;

import com.jht.androidcommonalgorithms.timer.PeriodicTimer;
import com.jht.chimera.io.commLib.IOManager;
import com.jht.chimera.io.commLib.PowerType;

public class CmdHandler {
    public final String TAG = getClass().getSimpleName();
    private PeriodicTimer fanStateTimer;

    public void updateIO(String fileName) {
        IOManager.getInstance().updateIO(fileName);
    }

    public void beep() {
        IOManager.getInstance().setTone(1);
    }

    public void enterERP() {
        IOManager.getInstance().enterERP();
    }

    public void enterSuspend() {
        IOManager.getInstance().enterSuspend();
    }

    public void getOperationMode(){
        IOManager.getInstance().getOperationMode();
    }

    public void getVersion() {
        IOManager.getInstance().getVersion();
    }

    public void getSafeKeyState() {
        IOManager.getInstance().getSafeKeyState();
        //Log.d("SafeKey","=== Get Safe Key state ===");
    }

    public void getDevicePowerState(byte source) {
        IOManager.getInstance().getDevicePowerState(source);
    }

    public void getExternalPowerState() {
        IOManager.getInstance().getExternalPowerState();
    }

    public void powerMonitor(boolean enable, int time){
        IOManager.getInstance().powerMonitor(enable, time);
    }

    public void getPowerState(byte source){
        IOManager.getInstance().getPowerState(source);
    }

    public void getExtendPowerError(){
        IOManager.getInstance().getExtendPowerError();
    }

    public void getBoot() {
        IOManager.getInstance().getBoot();
    }

    public void getLcbType() {
        IOManager.getInstance().getLcbType();
    }

    private void getFanSpeed(){
        IOManager.getInstance().getFanSpeed();
    }

    public void getPowerMonitorState() {
        IOManager.getInstance().getPowerMonitorState();
    }

    public void checkExtendPower(){
        Log.d(TAG, "checkExtendPower");
//        setPower(PowerType.ext_14V.getID(), true);
//        new JHTTimer(1000,() -> {
////            getDevicePowerState(PowerType.ext_14V.getID());
//            getPowerState((byte)0x00);
//        }
//        ).start();

    }

    public void getKeyState(){
        IOManager.getInstance().getKeyState();
    }

    public void getRecoveryKey(){
        IOManager.getInstance().getRecoveryKey();
    }

    public void getSystemState(){
        IOManager.getInstance().getSystemState();
    }

    public void setKeyDebounceTime(int time, int time2, int gapTime){
        IOManager.getInstance().setKeyDebounceTime(time, time2, gapTime);
    }

    public void setWatchdog(float timeout, float refreshTime) {
        IOManager.getInstance().setWatchdog((int)(timeout * 1000 * 60), (int)refreshTime);
    }

    public void refresh() {
        IOManager.getInstance().refresh();
    }

    public void setTone(int type) {
        if (type >= 0 && type <= 2)
            IOManager.getInstance().setTone(type);
    }

    public void setFanSpeed(int speed) {
        switch (speed) {
            case 0:
                IOManager.getInstance().setFanSpeed(0);
                break;
            case 1:
                IOManager.getInstance().setFanSpeed(50);
                break;
            case 2:
                IOManager.getInstance().setFanSpeed(75);
                break;
            case 3:
                IOManager.getInstance().setFanSpeed(90);
                break;
            default:
                IOManager.getInstance().setFanSpeed(speed);
                break;
        }
        if (speed == 0){
            if(fanStateTimer != null) {
                fanStateTimer.stop();
                fanStateTimer = null;
            }
        }
        else {
            if (fanStateTimer == null) {
                fanStateTimer = new PeriodicTimer(5000, 5000, this::getFanSpeed);
                fanStateTimer.start();
            }
        }

    }

    public void setPower(byte source, boolean _switch) {
        Log.d(TAG, "Power_trace: " + PowerType.getInstance().getNameByID(source) + " --> " + _switch);
        IOManager.getInstance().setPower(source, _switch);
    }


    public void setRS485Communication(boolean enable) {
        IOManager.getInstance().setRS485Communication(enable);
    }

    public void setQiVoltage(int voltage){
        Log.d(TAG, "setQiVoltage: " + voltage);
        IOManager.getInstance().setQiVoltage(voltage);
    }


    public void setDebug(boolean enable, int level) {
        if (level >= 0 && level <= 3)
            IOManager.getInstance().setDebug(enable, level);
    }

    /*
    public void setCECconfig(int address) {
        byte[] configArray = ByteBuffer.allocate(4).putInt(address).array();
        IOManager.getInstance().setCECconfig(configArray[0]);
    }
     */

    public void sendCECcommand(byte[] command) {
        IOManager.getInstance().sendCECcommand(command);
    }

    public void getCECResponse() {
        IOManager.getInstance().getCECresponse();
    }

    public void sendIRcommand(byte[] code) {
        IOManager.getInstance().sendIRcommand(code);
    }

    public void reboot(int delayTime) {
        IOManager.getInstance().reboot(delayTime);
    }
}
