package com.jht.chimera.io.commLib;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jht.serialport.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortCommManager {

    private static class SerialPortCommManagerInstance {
        // Note we can suppress this because we only store the application context which is good for
        // the life of the process thus no memory leak.
        @SuppressLint("StaticFieldLeak")
        private static final SerialPortCommManager ioManager = new SerialPortCommManager();
    }
    public static SerialPortCommManager getInstance() {
        return SerialPortCommManager.SerialPortCommManagerInstance.ioManager;
    }

    private static final String TAG = IOManager.class.getName();


    private volatile boolean _isPortOpened = false;
    private int _txRetryCounter = 0;

    private static InputStream _inStream = null;
    private static OutputStream _outStream = null;

    private static SerialPort _port = null;
    private static Thread _rxThread = null;
//    private static Thread _txThread = null;
    private volatile boolean isRunning = false;

    public IcommCallback callback;

    public void setCallback(IcommCallback callback) {
        this.callback = callback;
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

//            _txCommandQueue.clear();
//            _txPacketQueue.clear();
//            _rxPacketQueue.clear();
//            _receiveDataBufferQueue.clear();


            _rxThread = new RxThread();
//            _txThread = new TxThread();
            _rxThread.start();
//            _txThread.start();
            isRunning = true;
        } else {
            Log.e(TAG, "serialPort instance is null!");
            return false;
        }


        return _isPortOpened;
    }

    public void stop() {
        if (_isPortOpened) {
            _rxThread.interrupt();
//            _txThread.interrupt();

            if (_port != null) {
                _port.close();
//                _port = null;
                _inStream = null;
                _outStream = null;
            }

//            _rxPacketQueue.clear();
//            _txPacketQueue.clear();
            _rxThread = null;
//            _txThread = null;
            _isPortOpened = false;
            isRunning = false;
        }
    }

    private class RxThread extends Thread {

        @Override
        public void run() {
            super.run();

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
                        callback.ondReceived(new String(bufferData));
//                        _receiveDataBufferQueue.add(bufferData);
                    }


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
        isRunning = true;
    }

    public void write(String data){
        byte[] byteArray = data.getBytes();
        try{
            Log.d(TAG, "write: " + data);
            _outStream.write(byteArray);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    //    public class TxThread extends Thread {
//
//        @Override
//        public void run() {
//            while (!this.isInterrupted()) {
//                try {
//                        txHandler(dddd);
//                        sleep(50);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                } catch (Exception e) {
//                    e.printStackTrace();
////                    }
//                }
//            }
//        }
//    }

//    private void txHandler(byte[] command) {
//
//        if (command != null) {
//            try {
//                String data = "";
//
//                for (int i = 0; i < command.length; i++) {
//                    data += String.format("%X", command[i]) + " ";
//                }
////                Log.d(TAG, "txHandler write txCommand: " + this.txCommand.getTxMessage() + "  " + data);
//
//                _outStream.write(command);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
