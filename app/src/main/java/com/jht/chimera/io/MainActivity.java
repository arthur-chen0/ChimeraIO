package com.jht.chimera.io;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jht.androidcommonalgorithms.timer.JHTTimer;
import com.jht.chimera.io.lib.HardwareControlDevices;
import com.jht.chimera.io.lib.IMCUCallback;
import com.jht.chimera.io.lib.IOManager;
import com.jht.chimera.io.lib.PowerType;
import com.jht.chimera.io.lib.PowerType.PowerSource;
import com.jht.serialport.SerialPort;
import com.jht.serialport.SerialPortConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IO_main";

    private final String DEVICE_PATH = "/dev/ttymxc2";
    private final SerialPortConfiguration.BaudRate BAUD_RATE = SerialPortConfiguration.BaudRate.B115200;

    private CmdHandler cmdHandler = new CmdHandler();
    private DataHandler dataHandler = new DataHandler(MainActivity.this);

    private CompoundButton.OnCheckedChangeListener switchListener;

    private PackageInfo app;

    private TextView text_appVersion;
    private TextView text_ioVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        //Start MCU communication
        IOManager.getInstance().start(new SerialPort(DEVICE_PATH,serialPortSetting()));
        IOManager.getInstance().setCallback(mcuCallback);

        text_appVersion = findViewById(R.id.text_app_version);
        text_ioVersion = findViewById(R.id.text_io_version);

        cmdHandler.getVersion();

        try {
            app = this.getPackageManager().getPackageInfo("com.jht.chimera.chimeraio", 0);
            text_appVersion.setText(app.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Init the power map
        PowerType.getInstance().addPower(HardwareControlDevices.RS_485.name(), (byte)0x01, findViewById(R.id.rs485_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.CSAFE.name(), (byte)0x03, findViewById(R.id.csafe_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.HDMI.name(), (byte)0x04, findViewById(R.id.hdmi_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.AUDIO_SA.name(), (byte)0x05, findViewById(R.id.audio_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.VOICE.name(), (byte)0x07, findViewById(R.id.voice_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.RFID.name(), (byte)0x08, findViewById(R.id.rfid_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.QI.name(), (byte)0x09, findViewById(R.id.qi_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.TV.name(), (byte)0x0A, findViewById(R.id.tv_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.CAB.name(), (byte)0x0B, findViewById(R.id.cab_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.IPOD.name(), (byte)0x0C, findViewById(R.id.ipod_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.HEART_RATE.name(), (byte)0x0D, findViewById(R.id.salutron_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.GYMKIT.name(), (byte)0x0E, findViewById(R.id.gymkit_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.USB_5V.name(), (byte)0x0F, findViewById(R.id.usb_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.EXT_14.name(), (byte)0x10, findViewById(R.id.ext14_switch));

        //Init switch listener
        switchListener = (compoundButton, b) -> {
            byte powerSourceID = PowerType.getInstance().getIDByPowerSwitch(compoundButton);

            if(powerSourceID != -1)
                Log.d("power_trace", "Set " + PowerType.getInstance().getNameBySwitch(compoundButton) + " power: " + b);
            cmdHandler.setPower(powerSourceID,b);
        };

        // Check power state when app start and set listener to all switch
        for(PowerSource power: (ArrayList<PowerSource>)PowerType.getInstance().getPowerTypeMap()){
            cmdHandler.getDevicePowerState(power.getId());
        }

        new JHTTimer(1500, () -> {
            for(PowerSource power: (ArrayList<PowerSource>)PowerType.getInstance().getPowerTypeMap()) {
                power.getPowerSwitch().setOnCheckedChangeListener(switchListener);
            }
        }).start();

        findViewById(R.id.btn_update).setOnClickListener(view -> {
            try {
//                String fileName[] = MainActivity.this.getAssets().list("");
                ArrayList<String> fileList = new ArrayList(Arrays.asList(MainActivity.this.getAssets().list("")));
                Log.d(TAG, "Update File size " + fileList.size());

                if(fileList.size() > 0){
                    AlertDialog.Builder updateDialog = new AlertDialog.Builder(MainActivity.this);

                    updateDialog.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, fileList), (dialog, which) -> {

                        Log.d("Update", "File Name: " + fileList.get(which));
                        cmdHandler.updateIO(fileList.get(which));

//                        AlertDialog.Builder progressDialog = new AlertDialog.Builder(MainActivity.this);
                        AlertDialog progressDialog = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
//                        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_progress_customize,null);
                        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_customize,null);
//                        progressDialog.setTitle("MCU Firmware Updating...");
                        progressDialog.setView(dialogView);

                        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        WindowManager.LayoutParams dialogWindow = progressDialog.getWindow().getAttributes();
                        dialogWindow.format = PixelFormat.TRANSLUCENT;
                        dialogWindow.alpha = 0.6f;

                        final TextView message = dialogView.findViewById(R.id.text_dialog_message);

                        progressDialog.show();

                        BroadcastReceiver ioResult = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                float percent = intent.getFloatExtra("percent", 0.0f);
                                int status = intent.getIntExtra("status", 0);

                                runOnUiThread(() -> {

//                                    message.getBackground().setAlpha(50);
                                    switch (status) {
                                        case 1028: //start update
                                            message.setText("MCU Update Started");
                                            break;
                                        case 1026: //update processing
                                            message.setText(String.format(Locale.ENGLISH, "MCU Update %.0f%s", percent * 1.0f, "%"));
                                            Log.d(TAG, "onReceive: MCU update processing... " + percent * 1.0f);
                                            break;
                                        case 1032: //end update
                                            message.setText("IO Update Completed");
                                            break;
                                        case 1:
                                            message.setText("IO Update Failed: MCU no response");
                                            Log.e(TAG, "IO update failed, MCU no response");
                                            break;
                                    }
                                });

                            }
                        };
                        registerReceiver(ioResult, new IntentFilter("com.jht.updateio.result"));

//                        new Thread(() -> {
//                            try {
//
//                                Thread.sleep(10000);
//                                while(McuUpdateManager.Instance.getPercent() <= McuUpdateManager.Instance.getTotalUpdatePackage()){
//
//                                    Thread.sleep(100);
//                                    runOnUiThread(() -> {
//                                        progress.setProgress(McuUpdateManager.Instance.getPercent());
//                                        percent.setText((McuUpdateManager.Instance.getPercent() * 100 / McuUpdateManager.Instance.getTotalUpdatePackage()) + "%");
//                                        Log.d("Mcu","update progress: " + McuUpdateManager.Instance.getPercent());
//                                    });
//
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }).start();

                    });

                    updateDialog.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        findViewById(R.id.btn_Exit).setOnClickListener(view -> {
            this.finish();
            onDestroy();
            System.exit(0);
        });

    }

    public SerialPortConfiguration serialPortSetting( ){

        SerialPortConfiguration serialPortConfiguration = new SerialPortConfiguration();
        serialPortConfiguration.baudRate = BAUD_RATE;
        serialPortConfiguration.CREAD = true;
        serialPortConfiguration.CLOCAL = true;
        serialPortConfiguration.csize = SerialPortConfiguration.CSIZE.CS8;
        serialPortConfiguration.makeRaw = true;
        serialPortConfiguration.NOCTTY = true;
        serialPortConfiguration.VTIME = 1;
        serialPortConfiguration.VMIN = 255;

        return serialPortConfiguration;
    }


    private IMCUCallback mcuCallback = new IMCUCallback() {

        @Override
        public void operationMode(int mode) {

        }

        @Override
        public void version(int version) {
            String versionString = "" + (version >> 8 & 0x00ff) + "." + (version & 0x00ff);
            runOnUiThread(() -> {
                Log.d(TAG, "text_ioVersion: " + versionString);
                text_ioVersion.setText(versionString);
            });
        }

        @Override
        public void safeKeyStatus(boolean status) {

        }

        @Override
        public void powerState(byte source, boolean _switch) {
            dataHandler.powerState(source, _switch);
        }

        @Override
        public void externalPower(IOManager.PowerVoltage powerVoltage, int current) {
            dataHandler.externalPower(powerVoltage,current);
        }

        @Override
        public void boot(boolean state) {

        }

        @Override
        public void fanSpeed(byte level, int rpm) {

        }

        @Override
        public void lcbType(byte type) {

        }

        @Override
        public void consolePowerVoltage(short voltage) {

        }

        @Override
        public void extendPowerVoltage(short voltage, boolean check) {

        }

        @Override
        public void usbVoltage(short voltage) {

        }

        @Override
        public void powerMonitorState(boolean enable) {

        }

        @Override
        public void consolePowerCurrent(short current) {

        }

        @Override
        public void extendPowerCurrent(short current) {

        }

        @Override
        public void extendPowerError(boolean error) {

        }

        @Override
        public void keyEvent(int event, short keyCode) {

        }

        @Override
        public void safeKeyEvent(boolean status) {

        }

        @Override
        public void debugMessage(String message) {

        }

        @Override
        public void cecCommand(int result) {

        }

        @Override
        public void getCECresponse(byte[] response) {

        }

        @Override
        public void irCommand(boolean result) {

        }
    };
}
