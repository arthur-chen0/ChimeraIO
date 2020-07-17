package com.jht.chimera.io;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

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
        PowerType.getInstance().addPower("EXT_14", (byte)0x10, findViewById(R.id.ext14_switch));

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
