package com.jht.chimera.io.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jht.androidcommonalgorithms.timer.JHTTimer;
import com.jht.androidcommonalgorithms.timer.PeriodicTimer;
import com.jht.chimera.io.CmdHandler;
import com.jht.chimera.io.DataHandler;
import com.jht.chimera.io.IOService;
import com.jht.chimera.io.MainActivity;
import com.jht.chimera.io.R;
import com.jht.chimera.io.commLib.HardwareControlDevices;
import com.jht.chimera.io.commLib.IMCUCallback;
import com.jht.chimera.io.commLib.IOManager;
import com.jht.chimera.io.commLib.PowerType;
import com.jht.serialport.SerialPort;
import com.jht.serialport.SerialPortConfiguration;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MCUFunctionFragment extends Fragment {

    private static final String TAG = MCUFunctionFragment.class.getSimpleName();

//    private final String DEVICE_PATH = "/dev/ttymxc3";
//    private final SerialPortConfiguration.BaudRate BAUD_RATE = SerialPortConfiguration.BaudRate.B115200;

    private String usbPath = "/mnt/media_rw/udisk";

    private CmdHandler cmdHandler = new CmdHandler();
    private DataHandler dataHandler = new DataHandler(getActivity());

    private CompoundButton.OnCheckedChangeListener switchListener;

    private Context context;
    private InputMethodManager inputMethodManager;

    private CSVWriter fanWriter;
    private CSVWriter powerWriter;

    private TextView text_ioVersion;
    private TextView text_fanRpm;
    private TextView text_main_power;
    private TextView text_extend_power;
    private TextView text_usb_power;
    private Spinner device_path_selector;
    private Spinner baud_rate_selector;
    private Button btn_open;
    private Button btn_start_fan;
    private Button btn_wd_start;
    private Button btn_erp;
    private Button btn_reboot;
    private Button btn_monitor_start;
    private EditText edit_fan_speed;
    private EditText edit_wd_timeout;
    private EditText edit_refresh_time;
    private EditText edit_monitor_interval;
    private Switch switch_qi_vol;
    private MainActivity.FragmentTouchListener listener;

    static volatile PeriodicTimer mTimer = new PeriodicTimer();

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.mcu_fution_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        try {
            mTimer.setOnTimer(() -> {
                boolean isUSBConnected = new File("/mnt/media_rw/udisk").exists();
                Log.d(TAG, "USBMountManager: usb is connected " + isUSBConnected);
            });
            mTimer.start(1000, 1000);

        } catch (Exception e) {
            Log.w(TAG, e);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        listener = new MainActivity.FragmentTouchListener() {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (inputMethodManager.isAcceptingText()) {
                        Log.d(TAG, "arthur_trace: hide keyboard");
                        ViewCompat.getWindowInsetsController(requireView()).hide(WindowInsetsCompat.Type.ime());
                    }
                }
                return true;
            }
        };
        ((MainActivity) this.getActivity()).registerFragmentTouchListener(listener);

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) this.getActivity()).unRegisterFragmentTouchListener(listener);
    }


    public void init(View view){

        initWidget(view);

        ArrayList device_path = getSerialPorts();
        ArrayAdapter<String> serialAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, device_path);
        device_path_selector.setAdapter(serialAdapter);
        device_path_selector.setSelection(device_path.indexOf("/dev/ttymxc3"));

        ArrayList<SerialPortConfiguration.BaudRate> baud_rate = new ArrayList<>(Arrays.asList(SerialPortConfiguration.BaudRate.values()));
        ArrayAdapter<SerialPortConfiguration.BaudRate> baudRateAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, baud_rate);
        baud_rate_selector.setAdapter(baudRateAdapter);
        baud_rate_selector.setSelection(baud_rate.indexOf(SerialPortConfiguration.BaudRate.B115200));
        IOManager.getInstance().setCallback(mcuCallback);

//        getView().getRootView().setOnTouchListener((v, event) -> {
//            Log.d(TAG, "arthur_trace: touch event");
//            if(event.getAction() == MotionEvent.ACTION_DOWN){
//                Log.d(TAG, "arthur_trace: hide keyboard");
////                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
////               inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(),0);
////                getView().gotView().getWindowToken();
//                ViewCompat.getWindowInsetsController(requireView()).hide(WindowInsetsCompat.Type.ime());
//          }
//            return true;
//       });

        btn_open.setOnClickListener(v -> {
            if(btn_open.getText().equals("open")){
                btn_open.setText("close");
                IOManager.getInstance().start(new SerialPort((String)device_path_selector.getSelectedItem(),serialPortSetting()));
                initPowerSwitch();
            }
            else{
                btn_open.setText("open");
                btn_start_fan.setText("start");
                btn_wd_start.setText("start");
                IOManager.getInstance().stop();
                cmdHandler.setWatchdog(0, 0);
                cmdHandler.setFanSpeed(0);
            }

        });

        btn_erp.setOnClickListener(v -> {
            cmdHandler.enterERP();
        });

        btn_reboot.setOnClickListener(v -> {
            cmdHandler.reboot(5);
        });

        btn_start_fan.setOnClickListener(v -> {
            if(edit_fan_speed != null) {
                if (btn_start_fan.getText().equals("start")) {
                    btn_start_fan.setText("stop");
                    cmdHandler.setFanSpeed(Integer.parseInt(edit_fan_speed.getText().toString()));

                    File file = new File(usbPath);
                    if(file.exists()){
                        Log.d(TAG, "arthur: usb path exists");
                        try {

                            DateFormat timeFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_");
                            String fileName = timeFormat.format(Calendar.getInstance().getTime()) + "fanData.csv";

                            File fanCSV = new File(usbPath + fileName);
                            fanWriter = new CSVWriter(new FileWriter(fanCSV));
                            String[] header = { "Time", "Level", "RPM" };
                            fanWriter.writeNext(header);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                } else {
                    btn_start_fan.setText("start");
                    cmdHandler.setFanSpeed(0);

                    try {
                        if(fanWriter != null) {
                            fanWriter.close();
                            fanWriter = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_wd_start.setOnClickListener(v -> {
//            btn_wd_start.setClickable(false);
            if((edit_wd_timeout.getText() != null) && (edit_refresh_time.getText() != null) ) {
                if (btn_wd_start.getText().equals("start")) {
                    btn_wd_start.setText("stop");
                    cmdHandler.setWatchdog(Float.parseFloat(edit_wd_timeout.getText().toString()), Float.parseFloat(edit_refresh_time.getText().toString()));
                } else {
                    btn_wd_start.setText("start");
                    cmdHandler.setWatchdog(0, 0);
                }
            }
        });

        btn_monitor_start.setOnClickListener(v -> {
            if(edit_monitor_interval.getText() != null){
                if(btn_monitor_start.getText().equals("start")){
                    btn_monitor_start.setText("stop");
                    cmdHandler.powerMonitor(true, Integer.parseInt(edit_monitor_interval.getText().toString()));
                }
                else{
                    btn_monitor_start.setText("start");
                    cmdHandler.powerMonitor(false, 3);
                }
            }
        });

        switch_qi_vol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                cmdHandler.setQiVoltage(1);
            else
                cmdHandler.setQiVoltage(0);
        });


    }

    private void initWidget(View view){
        device_path_selector = view.findViewById(R.id.selector_device_path);
        baud_rate_selector = view.findViewById(R.id.selector_baud_rate);
        btn_open = view.findViewById(R.id.btn_open);
        btn_start_fan = view.findViewById(R.id.btn_set_fan_speed);
        btn_wd_start = view.findViewById(R.id.btn_wd_start);
        btn_erp = view.findViewById(R.id.btn_erp);
        btn_monitor_start = view.findViewById(R.id.btn_monitor_start);
        btn_reboot = view.findViewById(R.id.btn_reboot);
        text_ioVersion = getActivity().findViewById(R.id.text_io_version);
        text_fanRpm = view.findViewById(R.id.text_fan_rpm);
        text_main_power = view.findViewById(R.id.text_main_power);
        text_extend_power = view.findViewById(R.id.text_extend_power);
        text_usb_power = view.findViewById(R.id.text_usb_power);
        edit_fan_speed = view.findViewById(R.id.edit_fan_speed);
        edit_wd_timeout = view.findViewById(R.id.edit_wd_timeout);
        edit_refresh_time = view.findViewById(R.id.edit_refresh_time);
        edit_monitor_interval = view.findViewById(R.id.edit_monitor_interval);
        switch_qi_vol = view.findViewById(R.id.qi_vol_switch);


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

    }

    private void initPowerSwitch(){

        AlertDialog initDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();

        initDialog.setMessage("Initial...");
        initDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initDialog.setCancelable(false);
        WindowManager.LayoutParams initDialogWindow = Objects.requireNonNull(initDialog.getWindow()).getAttributes();
        initDialogWindow.format = PixelFormat.TRANSLUCENT;
        initDialogWindow.alpha = 0.8f;

        initDialog.show();

        //Get MCU firmware version
        cmdHandler.getVersion();
        cmdHandler.getPowerMonitorState();
        cmdHandler.getSafeKeyState();
        cmdHandler.getBoot();
        cmdHandler.setDebug(true, 3);
        cmdHandler.beep();
//        cmdHandler.getKeyState();
//        cmdHandler.getRecoveryKey();
//        cmdHandler.getSystemState();
//        cmdHandler.getPowerState((byte)0x00);

        switch_qi_vol.setChecked(false);

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

    public ArrayList getSerialPorts() {

        ArrayList<String> ports = new ArrayList<>();

        for(String path: context.getResources().getStringArray(R.array.serialPorts)){
            if(new File(path).exists())
                ports.add(path);
        }
        return ports;
    }

    private SerialPortConfiguration serialPortSetting() {
        SerialPortConfiguration serialPortConfiguration = new SerialPortConfiguration();
        serialPortConfiguration.baudRate = (SerialPortConfiguration.BaudRate) baud_rate_selector.getSelectedItem();
        serialPortConfiguration.CREAD = true;
        serialPortConfiguration.CLOCAL = true;
        serialPortConfiguration.csize = SerialPortConfiguration.CSIZE.CS8;
        serialPortConfiguration.makeRaw = true;
        serialPortConfiguration.NOCTTY = true;
        serialPortConfiguration.VTIME = 1;
        serialPortConfiguration.VMIN = 255;
        serialPortConfiguration.TRACE_DEBUG = true;

        return serialPortConfiguration;
    }


    private IMCUCallback mcuCallback = new IMCUCallback() {

        @Override
        public void operationMode(int mode) {

        }

        @Override
        public void version(int version) {
            String versionString = "" + (version >> 8 & 0x00ff) + "." + (version & 0x00ff);
            requireActivity().runOnUiThread(() -> {
                Log.d(TAG, "text_ioVersion: " + versionString);
                text_ioVersion.setText(versionString);
            });
        }

        @Override
        public void safeKeyStatus(boolean status) {
            Log.d(TAG, "safeKeyStatus: " + status);
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
            Log.d(TAG, "boot: " + state);
        }

        @Override
        public void fanSpeed(byte level, int rpm) {

            requireActivity().runOnUiThread(() -> {
                Log.d(TAG, "fan speed: " + rpm);
                text_fanRpm.setText(Integer.toString(rpm));
            });
            DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String time = timeFormat.format(Calendar.getInstance().getTime());
            if(fanWriter != null){
                fanWriter.writeNext(new String[]{time, Integer.toString(level), Integer.toString(rpm)});
            }
        }

        @Override
        public void lcbType(byte type) {

        }

        @Override
        public void powerMonitorEvent(short mainVol, short extendVol, short usbVol, short mainCurrent, short extendCurrent, boolean extendError) {
            StringBuilder mainPower = new StringBuilder();
            StringBuilder extendPower = new StringBuilder();
            StringBuilder usbPower = new StringBuilder();

            mainPower.append(String.format(Locale.ENGLISH,"Voltage: %.2f",(float)mainVol / 100)).append(System.getProperty("line.separator"));
            mainPower.append(String.format(Locale.ENGLISH,"Current: %d",mainCurrent));

            if(extendError)
                extendPower.append("Extend Power Error"); //NON-NLS
            else {
                extendPower.append(String.format(Locale.ENGLISH, "Voltage: %.2f", (float) extendVol / 100)).append(System.getProperty("line.separator"));
                extendPower.append(String.format(Locale.ENGLISH, "Current: %d", extendCurrent)).append(System.getProperty("line.separator"));
            }

            usbPower.append(String.format(Locale.ENGLISH,"Voltage: %.2f",(float)usbVol / 100));

            requireActivity().runOnUiThread(() -> {
                text_main_power.setText(mainPower.toString());
                text_extend_power.setText(extendPower.toString());
                text_usb_power.setText(usbPower.toString());
            });
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
        public void powerMonitorState(boolean enable, int interval) {
            requireActivity().runOnUiThread(() -> {
                btn_monitor_start.setText(enable ? "stop" : "start");
                edit_monitor_interval.setText(String.valueOf(interval));
            });
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
            Log.d(TAG, "safeKeyEvent: status " + status);
            dataHandler.safeKeyEvent(status);
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
