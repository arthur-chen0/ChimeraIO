package com.jht.chimera.io.commLib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.jht.androidcommonalgorithms.events.JHTNotification;
import com.jht.androidcommonalgorithms.timer.PeriodicTimer;
import com.jht.serialport.SerialPort;
import com.jht.chimera.io.commLib.IOPacket.parseState;
import com.jht.chimera.io.commLib.IOPacket.parseResult;
import com.jht.chimera.io.commLib.IOPacket.CommandType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.LinkedBlockingQueue;

public class IOManager {

    private static class IOManagerInstance {
        // Note we can suppress this because we only store the application context which is good for
        // the life of the process thus no memory leak.
        @SuppressLint("StaticFieldLeak")
        private static final IOManager ioManager = new IOManager();
    }
    public static IOManager getInstance() {
        return IOManagerInstance.ioManager;
    }

    private static final String TAG = IOManager.class.getName();


    private volatile boolean _isPortOpened = false;
    private int _txRetryCounter = 0;

    private static InputStream _inStream = null;
    private static OutputStream _outStream = null;

    private static SerialPort _port = null;
    private static Thread _rxThread = null;
    private static Thread _txThread = null;
    private volatile boolean isRunning = false;

    public parseState result = parseState.Success;
    public IMCUCallback callback;

    private IOPacket ioPacket = new IOPacket();
    public IOPacket txCommand = null;

    private PeriodicTimer watchdogTimer = new PeriodicTimer();

    private Context context;

//    private PeriodicTimer watchdogTimer = new PeriodicTimer();
//
    private static final JHTNotification _notifyRxPacketSync = new JHTNotification();

    private static LinkedBlockingQueue<IOPacket> _txCommandQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<IOPacket> _txEventQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<byte[]> _txPacketQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<byte[]> _receiveDataBufferQueue = new LinkedBlockingQueue<>();

    public enum PowerVoltage {
        POWER_12V,
        POWER_14V
    }


    public void setCallback(IMCUCallback callback) {
        this.callback = callback;
    }

    public void getOperationMode() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.OPERATION_MODE));
    }

    public void getVersion() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.VERSION));
    }

    public void getSafeKeyState() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.SAFEKEY));
    }

    public void getDevicePowerState(byte source) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.POWER_STATE, source));
    }

    public void getExternalPowerState() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.POWER_STATE, (byte) 0x10));
    }

    public void getBoot() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.BOOT));
    }

    public void getLcbType() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.LCB_TYPE));
    }

    public void getFanSpeed() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.FAN_SPEED));
    }

    public void getPowerMonitorState() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.HW_MONITOR_STATE));
    }

    public void getExtendPowerError() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.HW_MONITOR, (byte)0x02));
    }

    public void getPowerState(byte source) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.HW_MONITOR, source));
    }

    public void getKeyState(){
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.KEY_STATE));
    }

    public void getRecoveryKey(){
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.RECOVERY_STATE));
    }

    public void getSystemState(){
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.SYSTEM_STATE));
    }

    public void setWatchdog(int timeout, int refreshTime) {
        Log.d(TAG, "setWatchdog: " + timeout);
        byte[] timeArray = ByteBuffer.allocate(4).putInt(timeout).array();

        if (timeout <= 0) {
            _txCommandQueue.add(new IOPacket(IOProtocol.Commands.WATCHDOG, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00));

            if (watchdogTimer != null) {
                watchdogTimer.stop();
            }
        } else {
            _txCommandQueue.add(new IOPacket(IOProtocol.Commands.WATCHDOG, (byte) 0x01, timeArray[3], timeArray[2], timeArray[1], timeArray[0]));

            if (watchdogTimer != null) {
                watchdogTimer.stop();
            }
            watchdogTimer = new PeriodicTimer();
            watchdogTimer.setOnTimer(this::refresh);
            if(refreshTime > 0)
                watchdogTimer.start(0, refreshTime * 1000 * 60);
        }
    }


    public void refresh() {
        //Twice command in once fresh. Avoid mcu loss package.
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.REFRESH));
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.REFRESH));
    }


    public void setKeyDebounceTime(int time, int time2, int gapTime) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.KEY_SETTING, (byte) time, (byte) time2, (byte) gapTime));
    }


    public void setTone(int type) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.TONE, (byte) type));
    }


    public void setFanSpeed(int speed) {
        byte[] speedArray = ByteBuffer.allocate(4).putInt(speed).array();
        //Log.d(TAG, "Set Fan speed: " + new Byte(speedArray[3]).intValue());
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.FAN, (byte) speedArray[3]));
    }


    public void setPower(byte source, boolean _switch) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.POWER, source, (byte) (_switch ? 1 : 0)));
    }


    public void setRS485Communication(boolean enable) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.RS485, (byte) (enable ? 1 : 0)));
    }


    public void setQiVoltage(int voltage) {
        // 0 : 5V    1 : 9V
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.QI,(byte)voltage));
    }


    public void enterERP() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.ERP, (byte) 0x00));
    }


    public void enterSuspend() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.ERP, (byte) 0x01));
    }


    public void setDebug(boolean enable, int level) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.DEBUG, (byte) (enable ? 1 : 0), (byte) level));
    }


    public void updateIO(String fileName) {
        stop();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        McuUpdateManager.Instance.startUpdate(_port, fileName);

    }


    public void sendIRcommand(byte[] code) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.SEND_IR_COMMAND, code));
    }


    public void setCECconfig(byte address) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.SET_CEC_CONFIG, address));
    }


    public void sendCECcommand(byte[] command) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.SEND_CEC_COMMAND, command));
    }


    public void getCECresponse() {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.GET_CEC_RESPONSE));
    }


    public void reboot(int delayTime) {
        byte[] timeArray = ByteBuffer.allocate(4).putInt(delayTime).array();
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.REBOOT, (byte) 0x01, timeArray[3]));
    }


    public void powerMonitor(boolean enable,int time) {
        _txCommandQueue.add(new IOPacket(IOProtocol.Commands.HW_MONITOR_SWITCH, (byte) (enable ? 1 : 0),(byte) time));
    }


    public boolean start(SerialPort device) {
//        this.context = context;
        if (_isPortOpened) {
            Log.w(TAG, "The serial port had already opened!");
            return true;
        }

        if (device != null) {
            _port = device;
            try {
                _port.open();
                _isPortOpened = true;
            } catch (IOException e) {
                Log.e(TAG, "Failed to open serial port");
                e.printStackTrace();
                return false;
            }
            _inStream = _port.getInputStream();
            _outStream = _port.getOutputStream();
            if (_inStream == null || _outStream == null)
                return false;

            _txCommandQueue.clear();
            _txPacketQueue.clear();
//            _rxPacketQueue.clear();
            _receiveDataBufferQueue.clear();


            _rxThread = new RxThread();
            _txThread = new TxThread();
            _rxThread.start();
            _txThread.start();

        } else {
            Log.e(TAG, "serialPort instance is null!");
            return false;
        }


        return _isPortOpened;
    }

    public void stop() {
        if (_isPortOpened) {
            _rxThread.interrupt();
            _txThread.interrupt();

            if (_port != null) {
                _port.close();
//                _port = null;
                _inStream = null;
                _outStream = null;
            }

//            _rxPacketQueue.clear();
            _txPacketQueue.clear();
            _rxThread = null;
            _txThread = null;
            _isPortOpened = false;
        }
    }

    private class RxThread extends Thread {

        @Override
        public void run() {
            super.run();

            isRunning = true;
            Log.d(TAG, "rxThread start");
            while (!isInterrupted()) {

                try {
                    byte[] buffer = new byte[64];
                    int index = _inStream.read(buffer);


                    if (index < 0)
                        Log.e(TAG, "Read error!");
                    else {
                        byte[] bufferData = new byte[index];
                        System.arraycopy(buffer, 0, bufferData, 0, index);
                        _receiveDataBufferQueue.add(bufferData);
                    }

                    if (_receiveDataBufferQueue.size() > 0)
                        receiveDataHandler(_receiveDataBufferQueue.poll(), index);

                } catch (IOException e) {
                    isRunning = false;
                    Log.e(TAG, "UART read I/O exception: " + e.getMessage());
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    isRunning = false;
                    e.printStackTrace();
                    Log.e(TAG, "Exception in IO rxThread: " + e.getMessage());
                    Log.v(TAG, "Restart rxThread");
                    restartRxThread();
                }
            }
        }
    }

    private void restartRxThread() {
        if (isRunning) {
            isRunning = false;
            _rxThread.interrupt();

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            _rxThread = null;
        }

        _rxThread = new RxThread();
        _rxThread.start();
    }

    private void receiveDataHandler(byte[] rxPacket, int length) {

        for (int index = 0; index < length; index++) {
            result = ioPacket.rxPacketParse(rxPacket[index]);

            if ((result == parseState.InvalidStartByte) || (result == parseState.InvalidCrc) || result == parseState.InvalidLength) {
                Log.e(TAG, "error parse state: " + result);
                ioPacket.packetReset();
                result = parseState.Incomplete;
            } else if (result == parseState.Success)
                ioPacket.packetReset();
        }

        if(ioPacket.getResult() != parseResult.Success){
            result = parseState.Failure;
            Log.e(TAG,"parseResult: " + ioPacket.getResult());
        }
//        Log.d(TAG,"receiveHandler parseState: " + result);
    }

    @SuppressWarnings("CharsetObjectCanBeUsed") void rxProcess(int command, byte[] payload, CommandType commandType) {
        IOProtocol.Commands commandCode = IOProtocol.Commands.fromInt(command);
        if (commandCode != null)
            Log.d(TAG, "command Code: " + command + "  command name:" + commandCode.toString());
        if(commandCode == null) return;

        result = parseState.Success;
        if (commandType == CommandType.EVENT) {
            switch (commandCode) {
                case KEY_EVENT:
                    Log.d(TAG, "Key event Row: " + String.format("%X", payload[1]) + "  Column:" + String.format("%X", payload[2]) + " event: " + payload[0]);
                    _txEventQueue.add(new IOPacket(IOProtocol.Commands.KEY_EVENT));
                    ByteBuffer key = ByteBuffer.allocate(2);
                    key.put(payload[1]);
                    key.put(payload[2]);
                    key.flip();
                    short keyValue = key.order(ByteOrder.LITTLE_ENDIAN).getShort();
//                    callback.keyEvent(payload[0],(short)((payload[2] << 8) + payload[1]));
                    callback.keyEvent(payload[0],keyValue);
                    break;
                case SAFEKEY_EVENT:
                    Log.d(TAG, "rxProcess: safeKeyEvent " + payload[0]);
                    callback.safeKeyEvent(payload[0] == 1);
                    _txEventQueue.add(new IOPacket(IOProtocol.Commands.SAFEKEY_EVENT));
                    break;
                case DEBUG_MESSAGE:
                    try {
                        callback.debugMessage(new String(payload, "ascii"));
                        _txEventQueue.add(new IOPacket(IOProtocol.Commands.DEBUG_MESSAGE));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                case LCB_STATUS:
                    Log.d(TAG, "LCB status: " + payload[0]);
                    _txEventQueue.add(new IOPacket(IOProtocol.Commands.LCB_STATUS));
                    break;
                case HW_MONITOR_EVENT:
                    short mainVol, extendVol, usbVol, mainCurrent, extendCurrent;
                    ByteBuffer voltage = ByteBuffer.allocate(2);
                    //console voltage
                    voltage.put(payload[0]);
                    voltage.put(payload[1]);
                    voltage.flip();
//                    callback.consolePowerVoltage(voltage.order(ByteOrder.LITTLE_ENDIAN).getShort());
                    mainVol = voltage.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    voltage.clear();
                    //extend voltage
                    voltage.put(payload[2]);
                    voltage.put(payload[3]);
                    voltage.flip();
//                    callback.extendPowerVoltage(voltage.order(ByteOrder.LITTLE_ENDIAN).getShort(),false);
                    extendVol = voltage.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    voltage.clear();
                    //usb voltage
                    voltage.put(payload[4]);
                    voltage.put(payload[5]);
                    voltage.flip();
//                    callback.usbVoltage(voltage.order(ByteOrder.LITTLE_ENDIAN).getShort());
                    usbVol = voltage.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    voltage.clear();

                    ByteBuffer current = ByteBuffer.allocate(2);
                    //console current
                    current.put(payload[6]);
                    current.put(payload[7]);
                    current.flip();
//                    callback.consolePowerCurrent(current.order(ByteOrder.LITTLE_ENDIAN).getShort());
                    mainCurrent = current.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    current.clear();
                    //extend crent
                    current.put(payload[8]);
                    current.put(payload[9]);
                    current.flip();
//                    callback.extendPowerCurrent(current.order(ByteOrder.LITTLE_ENDIAN).getShort());
                    extendCurrent = current.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    current.clear();
                    //extend error
                    callback.extendPowerError(payload[10] == 0);

//                    Log.d(TAG, "arthur_power_monitor: mainVol " + mainVol + " extendVol " + extendVol + " usbVol " + usbVol);
//                    Log.d(TAG, "arthur_power_monitor: mainCurrent " + mainCurrent + " extendCurrent " + extendCurrent);

                    callback.powerMonitorEvent(mainVol, extendVol, usbVol, mainCurrent, extendCurrent, payload[10] == 0);
                    break;
                case QI_STATUS:
                    Log.d(TAG, "qi status: " + payload[0]);
                    _txEventQueue.add(new IOPacket(IOProtocol.Commands.QI_STATUS));
                    break;

            }
        }


        String _txMessage = "";
        if(this.txCommand != null)
            _txMessage = this.txCommand.getTxMessage();
        if (commandCode.toString().equals(_txMessage) && commandType == CommandType.REPLAY) {
            if (ioPacket.getResult() == parseResult.Success) {
                _txPacketQueue.poll();
                _txRetryCounter = 0;
            }
            _notifyRxPacketSync.setNotification();

            switch (commandCode) {
//                case TONE:
//                case FAN:
//                case POWER:
                case ERP:
//                    ExecuteAsRoot.runSyncCommand();
//                    DataMatrix.sendEvent(HALKeys.ACTION_SHUTDOWN);
                    break;
//                case DEBUG:
                case LCB_TYPE:
                    callback.lcbType(payload[0]);
                    break;
                case FAN_SPEED:
                    ByteBuffer rpm = ByteBuffer.allocate(2);
                    rpm.put(payload[1]);
                    rpm.put(payload[2]);
                    rpm.flip();
                    int rpmValue = rpm.order(ByteOrder.LITTLE_ENDIAN).getShort();
                    callback.fanSpeed(payload[0],rpmValue);
                    break;
                case WATCHDOG:
                    break;
                case OPERATION_MODE:
                    callback.operationMode(payload[0]);
                    break;
                case VERSION:
                    int version = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN).getShort();
                    callback.version(version);
                    break;
                case SAFEKEY:
                    callback.safeKeyStatus(payload[0] == 1);
                    break;
                case POWER_STATE:

                    if(payload[0] == (byte)0x10){
                        ByteBuffer current = ByteBuffer.allocate(2);
                        current.put(payload[3]);
                        current.put(payload[4]);
                        current.flip();
                        int currentValue = current.order(ByteOrder.LITTLE_ENDIAN).getShort();
                        if(payload[1] == 1)
                            callback.externalPower(PowerVoltage.POWER_14V, currentValue);
                        else if(payload[1] == 0)
                            callback.externalPower(PowerVoltage.POWER_12V, currentValue);
                    }
                    else
                        callback.powerState(payload[0],payload[1] == 1);

                    break;
                case BOOT:
                    callback.boot(payload[0] == 1);
                    break;
                case HW_MONITOR_STATE:
                    callback.powerMonitorState(payload[0] == 1, payload[1]);
                    break;
                case KEY_STATE:
                    Log.d(TAG, "key state: " + payload[0] + " " +  payload[1] + " " +  payload[2]);
                    break;
                case SYSTEM_STATE:
                    Log.d(TAG, "system state: " + payload[0]);
                    break;
                case RECOVERY_STATE:
                    Log.d(TAG, "recovery state: " + payload[0]);
                    break;
                case SEND_CEC_COMMAND:
                    callback.cecCommand(payload[0]);
                    break;
                case GET_CEC_RESPONSE:
                    callback.getCECresponse(payload);
                    break;
                case SEND_IR_COMMAND:
                    if(payload.length > 0) {
                        Log.d(TAG, "irCommand result: " + payload[0]);
                        callback.irCommand(payload[0] == 0);
                    }
                    else {
                        callback.irCommand(false);
                    }
                    break;
                case HW_MONITOR:
                    switch(payload[0]){
                        case 0x00:
                            //Voltage
                            ByteBuffer voltage2 = ByteBuffer.allocate(2);
                            //console voltage
                            voltage2.put(payload[1]);
                            voltage2.put(payload[2]);
                            voltage2.flip();
                            callback.consolePowerVoltage(voltage2.order(ByteOrder.LITTLE_ENDIAN).getShort());
                            voltage2.clear();
                            //extend voltage
                            voltage2.put(payload[3]);
                            voltage2.put(payload[4]);
                            voltage2.flip();
                            callback.extendPowerVoltage(voltage2.order(ByteOrder.LITTLE_ENDIAN).getShort(),true);
                            voltage2.clear();
                            //usb voltage
                            voltage2.put(payload[5]);
                            voltage2.put(payload[6]);
                            voltage2.flip();
                            callback.usbVoltage(voltage2.order(ByteOrder.LITTLE_ENDIAN).getShort());
                            voltage2.clear();
                            break;
                        case 0x01:
                            //Current
                            ByteBuffer current = ByteBuffer.allocate(2);
                            //console current
                            current.put(payload[1]);
                            current.put(payload[2]);
                            current.flip();
                            callback.consolePowerCurrent(current.order(ByteOrder.LITTLE_ENDIAN).getShort());
                            current.clear();
                            //extend current
                            current.put(payload[3]);
                            current.put(payload[4]);
                            current.flip();
                            callback.extendPowerCurrent(current.order(ByteOrder.LITTLE_ENDIAN).getShort());
                            current.clear();
                            break;
                        case 0x02:
                            //Power state
                            //extend error
                            callback.extendPowerError(payload[1] == 0);
                            break;
                    }
                    break;
            }
        }
    }

    public class TxThread extends Thread {

        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (!_txCommandQueue.isEmpty() || !_txEventQueue.isEmpty()) {
                        if(_txPacketQueue.isEmpty()) {
                            if (result != null && (result == parseState.Success || result == parseState.NoReply)) {
//                            Log.d(TAG,"_txCommandQueue size: " + _txCommandQueue.size() + "  Parse result: " + result.toString());
                                ioPacket.packetReset();
                                if (!_txEventQueue.isEmpty()){
                                    //Event reply needs top priority
                                    txCommand = _txEventQueue.take();
                                }
                                else {
                                    txCommand = _txCommandQueue.take();
                                }
                                _txPacketQueue.put(txCommand.getTxPacket());
                                _txRetryCounter = 0;
                                result = null;
                            }
                        }
                    }
//                        Log.d(TAG, "TxThread txPacket Queue size: " + _txPacketQueue.size());
                    if (!_txPacketQueue.isEmpty()) {

                        if (txCommand.getTxMessage().equals("REFRESH") || txCommand.getTxMessage().equals("KEY_EVENT") || txCommand.getTxMessage().equals("SAFEKEY_EVENT") || txCommand.getTxMessage().equals("LCB_STATUS") || txCommand.getTxMessage().equals("DEBUG_MESSAGE")|| txCommand.getTxMessage().equals("QI_STATUS")) { //NON-NLS
                            result = parseState.NoReply;
                            txHandler(_txPacketQueue.poll());
                        } else {
                            if (_txRetryCounter < 3) {
                                txHandler(_txPacketQueue.peek());

                                _notifyRxPacketSync.waitForNotification(500);
                                _txRetryCounter++;
                                //Log.d(TAG, "txRetryCounter: " + _txRetryCounter);
                            } else {
                                Log.e(TAG, "MCU is no response or txCommand error : " + txCommand.getTxMessage() + " rxThread is alive : " + _rxThread.isAlive());
                                _txPacketQueue.poll();
                                _txRetryCounter = 0;
                                result = parseState.Success;
                            }
                        }
                    }

                    sleep(50);


                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
//                    }
                }
            }
        }
    }

    private void txHandler(byte[] command) {

        if (command != null) {
            try {
                String data = "";

                for (int i = 0; i < command.length; i++) {
                    data += String.format("%X", command[i]) + " ";
                }
//                Log.d(TAG, "txHandler write txCommand: " + this.txCommand.getTxMessage() + "  " + data);

                _outStream.write(command);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
