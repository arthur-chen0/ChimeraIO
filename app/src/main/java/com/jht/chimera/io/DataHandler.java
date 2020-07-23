package com.jht.chimera.io;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jht.androidcommonalgorithms.timer.JHTTimer;

import com.jht.chimera.io.commLib.IOManager;
import com.jht.chimera.io.commLib.PowerType;


public class DataHandler {
    private final String TAG = this.getClass().getSimpleName();

//    private FrameConfig frameConfig = FrameConfigManager.getInstance().getFrameConfig();

    private CmdHandler mCmdHandler = new CmdHandler();
    private Context context;

    private Boolean burningKey1 = null;
    private Boolean burningKey2 = null;
    private JHTTimer burningTimer = new JHTTimer();
//    private JHTTimer keyHoldTimer = new JHTTimer(60*1000, () -> {
//        //UcbKeyPressTooLong error
//        DataMatrix.putInt(HALKeys.EVENT_LOG_ERROR_CODE_INT, 0x0306);
//        Log.d(TAG, "send UcbKeyPressTooLong error code ");
//    });

    public DataHandler(Context context) {
        this.context = context;

//        context.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                try {
//                    KeyPress.KeyCode key = KeyPress.KeyCode.valueOf(intent.getStringExtra("key_code"));
//                    DataMatrix.putInt(HALKeys.EVENT_KEYPRESS_BITMAP_INT, KeyPress.createBitmapKey(key, KEY_DOWN));
//                }
//                catch(Exception ignored) {}
//
//
//            }
//        }, new IntentFilter("com.jht.keypress"));
    }

    public void version(int version) {
        String versionString = "" + (version >> 8 & 0x00ff) + "." + (version & 0x00ff);
//        DataMatrix.putString(HALKeys.DATA_VERSION_IO_STRING, "" + versionString);
//        AndroidSetting.putString(context.getContentResolver(), "chimera_io_version", versionString + ".0.0"); //NON-NLS
    }

    public void safeKeyStatus(boolean status) {
//        if(!status && DataMatrix.getInt(HALKeys.DATA_SAFETY_KEY_STATUS_INT) != SAFETY_KEY_TREADMILL)
//            return;
//        DataMatrix.putBoolean(HALKeys.DATA_IO_IS_SAFETY_PULLED_BOOL, status);
        Log.d(TAG, "Notify LCB Service mcuSafetyKeyPulled " + status);
    }

    public void powerState(byte source, boolean _switch) {

        String powerName = PowerType.getInstance().getNameByID(source);
        if(powerName == null) return;

        new Handler(Looper.getMainLooper()).post(() -> {
            Log.d(TAG, "Power_trace " + powerName + " power state is " + _switch);
            PowerType.getInstance().getSwitchByID(source).setChecked(_switch);
        });
//        ---------------------------------------------------------------------------------------
//        PowerType pt = PowerType.format(source);
//        if(pt == null) return;
//        HardwareControlDevices device = HardwareControlDevices.values()[pt.ordinal()];
//        Log.d("Power control", "Device: " + device + "  switch: " + _switch);

//        ---------------------------------------------------------------------------------------
//        if (_switch)
//            DataMatrix.putInt(HALKeys.DATA_IO_POWER_ON_DEVICE_ENUM, source);
//        else
//            DataMatrix.putInt(HALKeys.DATA_IO_POWER_OFF_DEVICE_ENUM, source);
        ///machineSettings.powerState.getValue(device).setValue(_switch);

//        if (source == PowerType.usb_5V.getID()){
//            DataMatrix.putBoolean(HALKeys.DATA_IO_USB_POWER_BOOL,_switch);
//        }
    }

    public void externalPower(IOManager.PowerVoltage powerVoltage, int current){
        Log.d(TAG, "Power source: " + powerVoltage + "  Current: " + current);


        new Handler(Looper.getMainLooper()).post(() -> {
            if(powerVoltage == IOManager.PowerVoltage.POWER_14V)
                PowerType.getInstance().getSwitchByID((byte)0x10).setChecked(true);
            else
                PowerType.getInstance().getSwitchByID((byte)0x10).setChecked(false);
        });

//        =======================================================================================
//        if (powerVoltage == IOManager.PowerVoltage.POWER_14V && current != 0) {
//            DataMatrix.putBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL,true);
//        } else {
//            mCmdHandler.setPower(PowerType.ext_14V.getID(), false);
//            DataMatrix.putBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL,false);
//        }
//
//        Log.d(TAG, "External Power Board: " + DataMatrix.getBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL));
//        mCmdHandler.setPower(PowerType.qiPower.getID(),DataMatrix.getBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL));

//        DataMatrix.putBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL,powerVoltage == McuPacketManager.PowerVoltage.POWER_14V);
//        Log.d(TAG, "externalPower: " + (powerVoltage == McuPacketManager.PowerVoltage.POWER_14V));


    }

    public void consolePowerVoltage(short voltage) {
        //Log.d(TAG, "consolePowerVoltage: " + voltage);
//        DataMatrix.putInt(HALKeys.DATA_CONSOLE_VOLTAGE_INT,voltage);
    }

    public void extendPowerVoltage(short voltage, boolean check) {
        Log.d(TAG, "extendPowerVoltage: " + voltage);
//        DataMatrix.putInt(HALKeys.DATA_EXTEND_VOLTAGE_INT,voltage);
//        if(check){
//            if(voltage > 1100)
//                DataMatrix.putBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL,true);
//            else {
//                mCmdHandler.setPower(PowerType.ext_14V.getID(), false);
//                DataMatrix.putBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL, false);
//            }
//            Log.d(TAG, "External Power Board: " + DataMatrix.getBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL));
//            mCmdHandler.setPower(PowerType.qiPower.getID(),DataMatrix.getBoolean(HALKeys.DATA_IO_EXTERNAL_POWER_BOOL));
//        }

    }

    public void usbVoltage(short voltage) {
        Log.d(TAG, "usbVoltage: " + voltage);
//        DataMatrix.putInt(HALKeys.DATA_USB_VOLTAGE_INT,voltage);
    }

    public void consolePowerCurrent(short current) {
        Log.d(TAG, "consolePowerCurrent: " + current);
//        DataMatrix.putInt(HALKeys.DATA_CONSOLE_CURRENT_INT,current);
    }

    public void extendPowerCurrent(short current) {
        Log.d(TAG, "extendPowerCurrent: " + current);
//        DataMatrix.putInt(HALKeys.DATA_EXTEND_CURRENT_INT,current);
//        if(current > 3000){
//            DataMatrix.putInt(HALKeys.ACTION_IO_POWER_OFF_DEVICE_ENUM,PowerType.qiPower.getID());
//            // TODO: We need to log an analytics event.
//            Log.e(TAG, "Extend Power Overload( " + current + " )");
//        }
    }

    //public void extendPowerError(boolean error) {
    //    DataMatrix.putBoolean(HALKeys.DATA_EXTEND_ERROR_BOOL,error);
    //}

    public void boot(boolean state) {
//        if (state)
//            DataMatrix.putInt(SettingKeys.Debug.WATCHDOG_REBOOT_COUNTER_INT, DataMatrix.getInt(SettingKeys.Debug.WATCHDOG_REBOOT_COUNTER_INT) + 1);
    }

    public void keyEvent(int event, short keyCode) {

//        KeyPress.KeyState keyState = KEY_DOWN;
//        keyHoldTimer.start();
//        if (event == 0x00) {
//            keyState = KEY_UP;
//            keyHoldTimer.stop();
//        }
//        int bitmapKey = -1;
//        switch (keyCode) {
//            case 0x101:
//                //key = JHTCommonTypes.JHT_KEY.JHT_KEY_INCLINE_UP;
//                bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_INCLINE_UP, keyState);
//                break;
//            case 0x102:
//                //key = JHTCommonTypes.JHT_KEY.JHT_KEY_INCLINE_DOWN;
//                bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_INCLINE_DOWN, keyState);
//                break;
//            case 0x201:
//                if (frameConfig.machineClassType == JHTCommonTypes.MachineClassificationType.Treadmill) {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_SPEED_UP;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_SPEED_UP, keyState);
//                } else {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_RESISTANCE_UP;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_RESISTANCE_UP, keyState);
//                }
//                break;
//            case 0x202:
//                if (frameConfig.machineClassType == JHTCommonTypes.MachineClassificationType.Treadmill) {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_SPEED_DOWN;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_SPEED_DOWN, keyState);
//                } else {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_RESISTANCE_DOWN;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_RESISTANCE_DOWN, keyState);
//                }
//                break;
//            case 0x204:
//                if (frameConfig.machineClassType == JHTCommonTypes.MachineClassificationType.Treadmill) {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_STOP;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_STOP, keyState);
//                } else {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_PAUSE;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_PAUSE, keyState);
//                }
//                break;
//            case 0x208:
//                if (frameConfig.machineClassType == JHTCommonTypes.MachineClassificationType.Treadmill) {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_GO;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_GO, keyState);
//                } else {
//                    //key = JHTCommonTypes.JHT_KEY.JHT_KEY_STOP;
//                    bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_STOP, keyState);
//                }
//                break;
//            case 0x120:
//                bitmapKey = KeyPress.createBitmapKey(KeyPress.KeyCode.KEY_COOLDOWN, keyState);
//                break;
//            case 0x110:
//                burningKey1 = keyState == KEY_DOWN;
//                break;
//            case 0x220:
//                burningKey2 = keyState == KEY_DOWN;
//                break;
//        }
//        if (bitmapKey != -1) {
//            Log.d(TAG, " key code: " + KeyPress.getKeyCode(bitmapKey) + " state : " + KeyPress.getKeyState(bitmapKey));
//            DataMatrix.putInt(HALKeys.EVENT_KEYPRESS_BITMAP_INT, bitmapKey);
//
//            // play keep beep on key down
////            if (DataMatrix.getBoolean(SettingKeys.Hardware.KEY_BEEP_BOOL) && KeyPress.getKeyState(bitmapKey) == KeyPress.KeyState.KEY_DOWN) {
////                dataMatrix.sendEvent(HALKeys.ACTION_IO_PLAY_BEEP);
////            }
//        }
//        else {
//            if(burningKey1 != null && burningKey2 != null) {
//                if (burningKey1 && burningKey2) {
//                    Log.d(TAG, "keyEvent: BurningTimer start");
//                    if (burningTimer != null) {
//                        burningTimer.stop();
//                        burningTimer = null;
//                    }
//                    burningTimer = new JHTTimer(10000, () -> {
//                        DataMatrix.putBoolean(SettingKeys.Debug.IS_DEMO_MODE_BOOL, true);
//                        Log.d(TAG, "keyEvent: launch stability APP");
//                        Intent burningAppIntent = context.getPackageManager().getLaunchIntentForPackage("com.into.stability");
//                        if(burningAppIntent != null) {
//                            burningAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            context.startActivity(burningAppIntent);
//                        }
//                    });
//                    burningTimer.start();
//                    burningKey1 = null;
//                    burningKey2 = null;
//
//                } else if (!burningKey1 && !burningKey2) {
//                    DataMatrix.putBoolean(SettingKeys.Debug.IS_DEMO_MODE_BOOL, false);
//                    Log.d(TAG, "keyEvent: launch splash screen");
//                    if (burningTimer != null)
//                        burningTimer.stop();
//                    LaunchAppUtil.launchSplashScreen();
//                    burningKey1 = null;
//                    burningKey2 = null;
//                }
//            }
//        }
    }

    public void safeKeyEvent(boolean status) {
//        if(!status && DataMatrix.getInt(HALKeys.DATA_SAFETY_KEY_STATUS_INT) != SAFETY_KEY_TREADMILL)
//            return;
//        DataMatrix.putBoolean(HALKeys.DATA_IO_IS_SAFETY_PULLED_BOOL, status);
        Log.d(TAG, "Notify LCB Service mcuSafetyKeyPulled " + status);
    }

    public void debugMessage(String message) {
        Log.e(TAG, "Message: " + message);
    }

    public void irCommand(boolean result) {
//        DataMatrix.putBoolean(HALKeys.EVENT_IO_IR_RESULT_BOOL, result);
    }
}
