package com.jht.chimera.io.lib;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jht.androidcommonalgorithms.events.JHTNotification;
import com.jht.androidcommonalgorithms.timer.JHTTimer;
import com.jht.serialport.SerialPort;
import com.jht.chimera.io.lib.IOPacket.parseState;
import com.jht.chimera.io.lib.IOPacket.CommandType;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class McuUpdateManager {

    private static final String TAG = McuUpdateManager.class.getName();
    public final static McuUpdateManager Instance = new McuUpdateManager();

    public volatile boolean _isPortOpened = false;
    public volatile boolean isRunning = false;
    public volatile int _txRetryCounter = 0;
    public SerialPort _port = null;

    public InputStream _inStream = null;
    public OutputStream _outStream = null;
    public InputStream fileInStream = null;

    public Thread _updateRxThread = null;
    public Thread _updateTxThread = null;

    public parseState updateResult = parseState.Success;
    public WriteArea writeArea = WriteArea.AP;

    public IOPacket mcuPacket = new IOPacket();
    public IOPacket command = null;

    public String _txMessage = "";

    public int percent = 0;

    public Context context;

    public int apStartAddress;
    public int apEndAddress;
    public int nvramStartAddress;
    public int nvramEndAddress;
    public int writeIndex = 128;
    public int writeTotalPackage;

    public byte[] updateData;

    public static final JHTNotification _notifyUpdateRxPacketSync = new JHTNotification();

    public LinkedBlockingQueue<IOPacket> _updateTxCommandQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<byte[]> _updateTxPacketQueue = new LinkedBlockingQueue<>();
    public LinkedBlockingQueue<byte[]> _updateReceiveDataBufferQueue = new LinkedBlockingQueue<>();

    public List<byte[]> _updateDateList = new ArrayList<>();

    enum WriteArea {
        AP,
        NVRAM
    }

    public boolean start(SerialPort device) {
        if (_isPortOpened) {
            Log.w(TAG, "The serial port had already opened!");
            return true;
        }

        if (device != null) {
            _port = device;
            try {
                _port.open();
            } catch (IOException e) {
//                Log.e(TAG,"Failed to open serial port: " + path);
                e.printStackTrace();
                return false;
            }
            _inStream = _port.getInputStream();
            _outStream = _port.getOutputStream();
            if (_inStream == null || _outStream == null)
                return false;

            _updateTxCommandQueue.clear();
            _updateTxPacketQueue.clear();
            _updateReceiveDataBufferQueue.clear();
            _updateRxThread = new UpdateRxThread();
            _updateTxThread = new UpdateTxThread();
            _updateRxThread.start();
            _updateTxThread.start();

//            startUpdate(uri);
        } else {
            Log.e(TAG, "serialPort instance is null!");
            return false;
        }

        return _isPortOpened;
    }

    public void stop() {
        if (_isPortOpened) {
            _updateRxThread.interrupt();
            _updateTxThread.interrupt();

            if (_port != null) {
                _port.close();
                _port = null;
                _inStream = null;
                _outStream = null;
            }

            _updateRxThread = null;
            _updateTxThread = null;
            _isPortOpened = false;
        }
    }

    public void startUpdate(SerialPort device, String fileName) {
//        this.uri = uri;
        this.context = getContext();
        int length = 0;
        int index = 0;
        byte[] tempData = new byte[512];
        device.setTraceDebug(true);
        start(device);
        try {
//            fileInStream = new DataInputStream(new FileInputStream(uri.getPath()));
            fileInStream = new DataInputStream(getContext().getAssets().open(fileName));
//            length = (int) new File(uri.getPath()).length();
            length = fileInStream.available();
            updateData = new byte[length];
            fileInStream.read(updateData);

            for (int i = 0; i < length; i++) {
                tempData[index] = updateData[i];
                if (index < 511)
                    index++;
                else {
                    _updateDateList.add(tempData);
                    tempData = new byte[512];
                    index = 0;
//                    Log.d(TAG,"update data list size: " + _updateDateList.size() + " File length: " + length + " update data index: " + i);
                }
            }

//            Log.d(TAG,"update data list size: " + _updateDateList.size() + " File length: " + length);
            _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.OPERATION_MODE));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class UpdateTxThread extends Thread {
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (!_updateTxCommandQueue.isEmpty()) {
                        if (updateResult != null)
                            Log.d(TAG, "_txCommandQueue size: " + _updateTxCommandQueue.size() + "  Parse updateResult: " + updateResult.toString());
                        if (updateResult != null && updateResult == parseState.Success) {
//                            Log.d(TAG,"_txCommandQueue size: " + _txCommandQueue.size() + "  Parse result: " + result.toString());
                            mcuPacket.packetReset();
                            command = _updateTxCommandQueue.take();
                            _updateTxPacketQueue.put(command.getTxPacket());
                            updateResult = null;
                        }
                    }
                    if(!_updateTxPacketQueue.isEmpty()) {
                        Log.d(TAG, "TxThread txPacket Queue size: " + _updateTxPacketQueue.size());

                        if (_txRetryCounter < 3) {
                            txHandler(_updateTxPacketQueue.peek(), command.getTxMessage());

                            _notifyUpdateRxPacketSync.waitForNotification(3000);
                            _txRetryCounter++;
                        } else {
                            broadcastUpdateStatus(1, 0);
//                            restartUpdate();
                            _updateTxPacketQueue.poll();
                            _txRetryCounter = 0;
                        }
                    }

                        this.sleep(500);


                } catch (Exception e) {
                    e.printStackTrace();
//                    }
                }
            }
        }
    }

    private void txHandler(byte[] command, String txMessage) {
        String data = "";
        if (command != null) {
            try {
                for (int i = 0; i < command.length; i++) {
                    data += String.format("%X", command[i]) + " ";
                }
                Log.d(TAG, "txHandler write command: " + txMessage);

                _outStream.write(command);
                _txMessage = txMessage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class UpdateRxThread extends Thread {
        private int index = 0;

        @Override
        public void run() {
            super.run();

            isRunning = true;
            while (!isInterrupted()) {
                try {
                    String data = "";
                    byte[] buffer = new byte[600];
                    index = _inStream.read(buffer);

                    for (int i = 0; i < index; i++)
                        data += String.format("%X", buffer[i]) + " ";

                    Log.d(TAG, "Update rxThread data buffer: " + data + " index: " + index);

                    if (index < 0)
//                        break;
                        Log.e(TAG, "Read error!");
                    else {
                        byte[] bufferData = new byte[index];
                        for (int i = 0; i < index; i++) {
                            bufferData[i] = (byte) (buffer[i] & 0xFF);
                        }
                        _updateReceiveDataBufferQueue.add(bufferData);
                    }
                    if (_updateReceiveDataBufferQueue.size() > 0)
                        updateReceiveDataHandler(_updateReceiveDataBufferQueue.poll(), index);

                } catch (IOException e) {
                    isRunning = false;
                    Log.e(TAG, "UART read I/O exception: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
//                catch (InterruptedException e){
//                    Thread.currentThread().interrupt();
//                }
                catch (Exception e) {
                    isRunning = false;
                    Log.e(TAG, "Exception in IO rxThread: " + e.getMessage());
                    e.printStackTrace();
//                    restartRxThread();
                }
            }
        }
    }

    public void updateReceiveDataHandler(byte[] rxPacket, int length) {

        for (int index = 0; index < length; index++) {
            updateResult = mcuPacket.rxPacketParse(rxPacket[index]);

            if ((updateResult == parseState.InvalidStartByte) || (updateResult == parseState.InvalidCrc) || (updateResult == parseState.InvalidLength)) {
                Log.e(TAG, "error parse updateResult: " + updateResult);
                mcuPacket.packetReset();
                updateResult = parseState.Incomplete;
            } else if (updateResult == parseState.Success)
                mcuPacket.packetReset();
        }

        Log.e(TAG, "error parseResult: " + mcuPacket.getResult());

        Log.d(TAG, "receiveHandler parse updateResult: " + updateResult);
    }

    public void updateProcess(int command, byte[] payload, CommandType commandType) {
        IOProtocol.Commands commandCode = IOProtocol.Commands.fromInt(command);
        Log.d(TAG, "command Code: " + command + "  command name:" + commandCode.toString());
        if (commandCode.toString().equals(_txMessage) && commandType == CommandType.UPDATE) {
            _updateTxPacketQueue.poll();
            _txRetryCounter = 0;
            _notifyUpdateRxPacketSync.setNotification();

            switch (commandCode) {
                case OPERATION_MODE:
                    _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.START_UPDATE, (byte) 0x6A, (byte) 0x6F, (byte) 0x68, (byte) 0x6E, (byte) 0x73, (byte) 0x6F, (byte) 0x6E));
                break;
                case START_UPDATE:
                    new JHTTimer(5000,() -> _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.UPDATE_PARAMETERS))).start();
                    broadcastUpdateStatus(command, 0);
                    break;
                case UPDATE_PARAMETERS:
                    apStartAddress = ((payload[3] & 0xFF) << 24) + ((payload[2] & 0xFF) << 16) + ((payload[1] & 0xFF) << 8) + (payload[0] & 0xFF);
                    apEndAddress = ((payload[7] & 0xFF) << 24) + ((payload[6] & 0xFF) << 16) + ((payload[5] & 0xFF) << 8) + (payload[4] & 0xFF);
                    nvramStartAddress = ((payload[16] & 0xFF) << 24) + ((payload[15] & 0xFF) << 16) + ((payload[14] & 0xFF) << 8) + (payload[13] & 0xFF);
                    nvramEndAddress = ((payload[20] & 0xFF) << 24) + ((payload[19] & 0xFF) << 16) + ((payload[18] & 0xFF) << 8) + (payload[17] & 0xFF);
                    writeIndex = (apStartAddress - 0x8000000) / 512;
                    writeTotalPackage = ((apEndAddress - apStartAddress + 1) + (nvramEndAddress - nvramStartAddress + 1)) / 512;
                    Log.d(TAG, "AP Start address: " + String.format("%X", apStartAddress) + "  AP End address: " + String.format("%X", apEndAddress));
                    Log.d(TAG, "Nvram Start address: " + String.format("%X", +nvramStartAddress) + "  Nvram End address: " + String.format("%X", nvramEndAddress));
                    Log.d(TAG,"AP writeIndex init: " + writeIndex);
                    Log.d(TAG, "writeTotalPackage: " + writeTotalPackage);

                    _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.ERASE));
                    break;
                case ERASE:
                    writeCode();
                    break;
                case WRITE_PAGE:
                    boolean writeResult = writeCode();
                    Log.d(TAG, "write result: " + writeResult);
                    percent++;
                    broadcastUpdateStatus(command, percent * 100.0f / writeTotalPackage);
                    if (writeResult)
                        _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.READ_PAGE, (byte) 0xF0, (byte) 0xFF, (byte) 0x00, (byte) 0x08, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00));
//                        _updateTxCommandQueue.add(new McuPacket(McuProtocol.McuCommands.PROGRAM_STATE));
                    break;
                case READ_PAGE:
                    String checkDataString = "";
                    for (int i = 0; i < payload.length; i++)
                        checkDataString += String.format("%X", payload[i]) + " ";

                    Log.d(TAG, "Read code: " + checkDataString);
                    _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.PROGRAM_STATE));
                    break;
                case PROGRAM_STATE: //0x00 update not finish.  0x01 update finish
                    if ((payload[0] & 0xFF) == (byte) 0x00)
                        _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.PROGRAM_STATE));
                    else {
                        broadcastUpdateStatus(command, 0);
                        JHTTimer rebootTimer = new JHTTimer(() ->
                                _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.END_UPDATE)));
                        rebootTimer.start(300);
                    }
                    break;
                case END_UPDATE:
                    break;
            }
        }
    }

    public boolean writeCode() {
        boolean result = false;
        byte[] tempDate = new byte[0];
        byte[] lengthArray = new byte[4];
        byte[] addressArray = new byte[4];

        if (writeArea == WriteArea.AP) {
            if (apStartAddress < apEndAddress) {
                lengthArray = ByteBuffer.allocate(4).putInt(512).array();
                addressArray = ByteBuffer.allocate(4).putInt(apStartAddress).array();

                if (_updateDateList.size() >= writeIndex) {
                    Log.d(TAG, "write ap index: " + writeIndex);
                    tempDate = _updateDateList.get(writeIndex);
                    writeIndex++;
                }
                apStartAddress += 512;
                result = false;
            } else if (apStartAddress >= apEndAddress) {
                Log.d(TAG, "write ap finish");
                writeArea = WriteArea.NVRAM;
                writeIndex = (nvramStartAddress - 0x8000000) / 512;
                Log.d(TAG,"Nvram writeIndex init: " + writeIndex);
                result = false;
            }
            Log.d(TAG, " write area: " + writeArea.toString() + "  start address: " + apStartAddress + "  " + Arrays.toString(addressArray));
        }

        if (writeArea == WriteArea.NVRAM) {

            if (nvramStartAddress < nvramEndAddress) {

                lengthArray = ByteBuffer.allocate(4).putInt(512).array();
                addressArray = ByteBuffer.allocate(4).putInt(nvramStartAddress).array();

                if (_updateDateList.size() >= writeIndex) {
                    Log.d(TAG, "write nvarm index: " + writeIndex);
                    tempDate = _updateDateList.get(writeIndex);
                    writeIndex++;
                }
                nvramStartAddress += 512;
                result = false;
            } else if (nvramStartAddress >= nvramEndAddress) {
                Log.d(TAG, "write nvram finish");
                return true;
            }
            Log.d(TAG, " write area: " + writeArea.toString() + "  start address: " + nvramStartAddress + "  " + Arrays.toString(addressArray));
        }


        byte[] writeDate = new byte[tempDate.length + 8];
        writeDate[0] = addressArray[3];
        writeDate[1] = addressArray[2];
        writeDate[2] = addressArray[1];
        writeDate[3] = addressArray[0];

        writeDate[4] = lengthArray[3];
        writeDate[5] = lengthArray[2];
        writeDate[6] = lengthArray[1];
        writeDate[7] = lengthArray[0];
        for (int i = 0; i < writeDate.length - 8; i++) {
            writeDate[i + 8] = tempDate[i];
        }
        _updateTxCommandQueue.add(new IOPacket(IOProtocol.Commands.WRITE_PAGE, writeDate));

        return result;
    }

    public void broadcastUpdateStatus(int statusCommand, float percent) {
        Intent intent = new Intent("com.jht.updateio.result");
        intent.putExtra("status", statusCommand);
        intent.putExtra("percent", percent);
        context.sendBroadcast(intent);
    }

    public Context getContext(){
        Context context = null;
        final Class<?> activityThreadClass;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            Application application = (Application) method.invoke(null, (Object[]) null);
            context = application.getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }
}
