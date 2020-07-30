package com.jht.chimera.io.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jht.androidcommonalgorithms.timer.JHTTimer;
import com.jht.chimera.io.CmdHandler;
import com.jht.chimera.io.DataHandler;
import com.jht.chimera.io.R;
import com.jht.chimera.io.commLib.HardwareControlDevices;
import com.jht.chimera.io.commLib.IMCUCallback;
import com.jht.chimera.io.commLib.IOManager;
import com.jht.chimera.io.commLib.PowerType;
import com.jht.serialport.SerialPort;
import com.jht.serialport.SerialPortConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class PowerControlFragment extends Fragment {

    private static final String TAG = "IO_main";

    private final String DEVICE_PATH = "/dev/ttymxc2";
    private final SerialPortConfiguration.BaudRate BAUD_RATE = SerialPortConfiguration.BaudRate.B115200;

    private CmdHandler cmdHandler = new CmdHandler();
    private DataHandler dataHandler = new DataHandler(getActivity());

    private CompoundButton.OnCheckedChangeListener switchListener;

    private Context context;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.power_control_fragment, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    public void init(View view){

        AlertDialog initDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();

        initDialog.setMessage("Initial the main activity...");
        initDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initDialog.setCancelable(false);
        WindowManager.LayoutParams initDialogWindow = Objects.requireNonNull(initDialog.getWindow()).getAttributes();
        initDialogWindow.format = PixelFormat.TRANSLUCENT;
        initDialogWindow.alpha = 0.8f;

        initDialog.show();

        //Start MCU communication
        IOManager.getInstance().start(new SerialPort(DEVICE_PATH,serialPortSetting()));
        IOManager.getInstance().setCallback(mcuCallback);

        cmdHandler.getVersion();


        //Init the power map
        PowerType.getInstance().addPower(HardwareControlDevices.RS_485.name(), (byte)0x01, view.findViewById(R.id.rs485_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.CSAFE.name(), (byte)0x03, view.findViewById(R.id.csafe_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.HDMI.name(), (byte)0x04, view.findViewById(R.id.hdmi_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.AUDIO_SA.name(), (byte)0x05, view.findViewById(R.id.audio_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.VOICE.name(), (byte)0x07, view.findViewById(R.id.voice_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.RFID.name(), (byte)0x08, view.findViewById(R.id.rfid_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.QI.name(), (byte)0x09, view.findViewById(R.id.qi_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.TV.name(), (byte)0x0A, view.findViewById(R.id.tv_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.CAB.name(), (byte)0x0B, view.findViewById(R.id.cab_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.IPOD.name(), (byte)0x0C, view.findViewById(R.id.ipod_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.HEART_RATE.name(), (byte)0x0D, view.findViewById(R.id.salutron_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.GYMKIT.name(), (byte)0x0E, view.findViewById(R.id.gymkit_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.USB_5V.name(), (byte)0x0F, view.findViewById(R.id.usb_switch));
        PowerType.getInstance().addPower(HardwareControlDevices.EXT_14.name(), (byte)0x10, view.findViewById(R.id.ext14_switch));

//        new TabLayoutMediator(fragmentTab,viewPager,(tab, position) -> tab.setText("12345")).attach();

        //Init switch listener
        switchListener = (compoundButton, b) -> {
            byte powerSourceID = PowerType.getInstance().getIDByPowerSwitch(compoundButton);

            if(powerSourceID != -1)
                Log.d("power_trace", "Set " + PowerType.getInstance().getNameBySwitch(compoundButton) + " power: " + b);
            cmdHandler.setPower(powerSourceID,b);
        };

        // Check power state when app start and set listener to all switch
        for(PowerType.PowerSource power: (ArrayList<PowerType.PowerSource>)PowerType.getInstance().getPowerTypeMap()){
            cmdHandler.getDevicePowerState(power.getId());
        }

        new JHTTimer(1500, () -> {
            for(PowerType.PowerSource power: (ArrayList<PowerType.PowerSource>)PowerType.getInstance().getPowerTypeMap()) {
                power.getPowerSwitch().setOnCheckedChangeListener(switchListener);
            }
        }).start();

        new JHTTimer(3000, initDialog::dismiss).start();

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
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                Log.d(TAG, "text_ioVersion: " + versionString);
//                text_ioVersion.setText(versionString);
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
