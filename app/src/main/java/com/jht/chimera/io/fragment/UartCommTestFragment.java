package com.jht.chimera.io.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.jht.chimera.io.CmdHandler;
import com.jht.chimera.io.MainActivity;
import com.jht.chimera.io.R;
import com.jht.chimera.io.commLib.IMCUCallback;
import com.jht.chimera.io.commLib.IOManager;
import com.jht.chimera.io.commLib.IcommCallback;
import com.jht.chimera.io.commLib.SerialPortCommManager;
import com.jht.serialport.SerialPort;
import com.jht.serialport.SerialPortConfiguration;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class UartCommTestFragment extends Fragment{

    private static final String TAG = UartCommTestFragment.class.getSimpleName();

    private Context context;
    private Activity activity;
    private InputMethodManager inputMethodManager;
    private CmdHandler cmdHandler = new CmdHandler();

    private Spinner device_path_selector;
    private Spinner baud_rate_selector;
    private Button open_button;
    private Button write_button;
    private Button clear_button;
    private Button mBtn_StressStart;
    private Button mBtn_StressStop;
    private EditText mEdTxt_no_iteration;
    private EditText data_edit;
    private TextView received_data_text;

    private ArrayList<String> dataArrlist = new ArrayList<>();
    private StringBuilder log = new StringBuilder();
    private MainActivity.FragmentTouchListener listener;

    private long no_iteration;
    private long no_fail;
    private StressTestThread mThread;
    private boolean mStressTestRxFlag = false;
    private long Tx_tick;
    private long Elapse_tick;
    private ScrollView mScrollView;
    private StressTestThreadLinstener mLinstener = new StressTestThreadLinstener() {
        @Override
        public void onFinishLinstener(Level level) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    switch (level){
                        case STOP:
                            Toast.makeText(context, "Stress Test Stop", Toast.LENGTH_SHORT).show();
                            break;
                        case COMPLETE:
                            Toast.makeText(context, "Stress Test Complete", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    mBtn_StressStart.setEnabled(true);
                }
            });


        }

        @Override
        public void updateTxtViewLinstener(String txt) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //stuff that updates ui
                    received_data_text.append(txt);
                    mScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 100);
                }
            });
        }
    };

    public UartCommTestFragment(Activity activity) {
      this.activity = activity;
    }

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
        return inflater.inflate(R.layout.uart_comm_test_fragment, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View view){

//        init Serial port widget
        device_path_selector = view.findViewById(R.id.selector_device_path);
        ArrayList device_path = getSerialPorts();
        ArrayAdapter<String> serialAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, device_path);
        device_path_selector.setAdapter(serialAdapter);
        device_path_selector.setSelection(device_path.indexOf("/dev/ttymxc3"));

//        init baud rate widget
        baud_rate_selector = view.findViewById(R.id.selector_baud_rate);
        ArrayList<SerialPortConfiguration.BaudRate> baud_rate = new ArrayList<>(Arrays.asList(SerialPortConfiguration.BaudRate.values()));
        ArrayAdapter<SerialPortConfiguration.BaudRate> baudRateAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, baud_rate);
        baud_rate_selector.setAdapter(baudRateAdapter);
        baud_rate_selector.setSelection(baud_rate.indexOf(SerialPortConfiguration.BaudRate.B115200));

//        init open button
        open_button = view.findViewById(R.id.btn_open);
        open_button.setOnClickListener(v -> {
            Log.d("arthur_button", "open button text: " + open_button.getText());
            if(open_button.getText().equals("open")){
                open_button.setText("close");
                IOManager.getInstance().start(new SerialPort((String)device_path_selector.getSelectedItem(),serialPortSetting()));
                cmdHandler.getVersion();
            }
            else{
                open_button.setText("open");
                SerialPortCommManager.getInstance().stop();
            }
        });

        SerialPortCommManager.getInstance().setCallback(callback);

        write_button = view.findViewById(R.id.btn_write);
        clear_button = view.findViewById(R.id.btn_clear);
        data_edit = view.findViewById(R.id.edit_data);

        data_edit.setText("Test data");

        received_data_text = view.findViewById(R.id.text_received_data);
        write_button.setOnClickListener(v -> {
            SerialPortCommManager.getInstance().write(data_edit.getText().toString());
        });

        clear_button.setOnClickListener(v -> {
            data_edit.getText().clear();

        });
        //TODO
        mScrollView = view.findViewById(R.id.ResultScrollView);
        IOManager.getInstance().setCallback(mcuCallback);

        mEdTxt_no_iteration = view.findViewById(R.id.edtxt_no_interation);
        mBtn_StressStart = view.findViewById(R.id.btn_stress_start);
        mBtn_StressStart.setOnClickListener(v -> {
            if(mEdTxt_no_iteration.getText().toString().length()!=0){
                try {
                    no_iteration = Long.parseLong(mEdTxt_no_iteration.getText().toString());
                    if(no_iteration < 1){
                        Toast.makeText(this.context, "Number of iteration need great than 0",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        mBtn_StressStart.setEnabled(false);
                        mThread = new StressTestThread(no_iteration, mLinstener);
                        mThread.start();

                    }
                }catch (NumberFormatException e){
                    Toast.makeText(context, "Check input number not over the range", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Number of iteration cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        mBtn_StressStop = view.findViewById(R.id.btn_stress_stop);
        mBtn_StressStop.setOnClickListener(v -> {
            if(mThread.isRunning()&&mThread!=null){
                mThread.interrupt();
            }
        });
    }
    class StressTestThread extends Thread{
        long iteration;
        volatile boolean running = false;
        String result;
        StressTestThreadLinstener linstener;
        private long startTick;
        public StressTestThread(long iteration, StressTestThreadLinstener linstener){
            this.iteration = iteration;
            this.linstener = linstener;
        }
        @Override
        public void run() {
            super.run();
            if(!IOManager.getInstance().isRunning()){
                linstener.onFinishLinstener(StressTestThreadLinstener.Level.STOP);
                return;
            }
            running = true;
            no_fail = 0;
            linstener.updateTxtViewLinstener("*****Start Stress Test*****\n");
            startTick = System.currentTimeMillis();
            for(int i = 0; i<iteration;i++) {
                if (!running){
                    return;
                }
                try {
                    // Execute Commend
                    cmdHandler.getPowerState((byte) 0x00);
                    // Tick
                    Tx_tick = System.currentTimeMillis();
                    mStressTestRxFlag = true;
                    // Wait Get Commend
                    while (mStressTestRxFlag){
                        if((System.currentTimeMillis() - Tx_tick) > 501){
                            // Fail to reply within 500ms
                            no_fail++;
                            break;
                        }
                    }

                    if(mStressTestRxFlag){
                        // Fail to reply within 500ms
                        result = String.format("Epoch " + (i+1) + "/"+iteration+" Result: Fail\n");
                    }else{
                        // Success to get reply message
                        Elapse_tick = System.currentTimeMillis() - Tx_tick;
                        result = String.format("Epoch " + (i+1) + "/"+iteration+" Result: Success "+ Elapse_tick + " ms\n");
                    }
                    linstener.updateTxtViewLinstener(result);
                } catch (Exception e) {
                    Log.e(TAG, "run: ", e);
                }
            }
            // Show Stress Test Result
            long durationInMillis = System.currentTimeMillis() - startTick;
            long millis = durationInMillis % 1000;
            long second = (durationInMillis / 1000) % 60;
            long minute = (durationInMillis / (1000 * 60)) % 60;
            long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
            result = String.format("Total Spend: %02d h %02d min %02ds %dms\n", hour, minute, second, millis);
            linstener.updateTxtViewLinstener(result);
            result = String.format("Success: %d Fail: %d\n",(no_iteration-no_fail),no_fail);
            linstener.updateTxtViewLinstener(result);
            linstener.updateTxtViewLinstener("*****Complete Stress Test*****\n");
            linstener.onFinishLinstener(StressTestThreadLinstener.Level.COMPLETE);

        }
        public boolean isRunning() {
            return running;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
            linstener.onFinishLinstener(StressTestThreadLinstener.Level.STOP);

        }
    }
    private ArrayList getSerialPorts() {

        ArrayList<String> ports = new ArrayList<String>();

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

    public IcommCallback callback = new IcommCallback() {
        @Override
        public void ondReceived(String data) {
            requireActivity().runOnUiThread(() -> {
                Log.d("arthur_test", "ondReceived: " + data);
                DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

                String msg = timeFormat.format(Calendar.getInstance().getTime()) + "  " + device_path_selector.getSelectedItem() + ": " + data + System.getProperty("line.separator");
                if (dataArrlist.size() >= 10)
                    dataArrlist.remove(0);
                dataArrlist.add(msg);
                log.delete(0, log.length());

                for (String st: dataArrlist)
                    log.append(st);
                received_data_text.setText(log.toString());
            });
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        listener = event -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (inputMethodManager.isAcceptingText()) {
                    Log.d(TAG, "arthur_trace: hide keyboard");
                    ViewCompat.getWindowInsetsController(requireView()).hide(WindowInsetsCompat.Type.ime());
                }
            }
            return true;
        };
        ((MainActivity) this.getActivity()).registerFragmentTouchListener(listener);

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) this.getActivity()).unRegisterFragmentTouchListener(listener);
        if(mThread.isRunning()&&mThread!=null){
            mThread.interrupt();
        }
    }
    private IMCUCallback mcuCallback = new IMCUCallback() {
        @Override
        public void operationMode(int mode) {

        }

        @Override
        public void version(int version) {
            String versionString = "" + (version >> 8 & 0x00ff) + "." + (version & 0x00ff);
            Log.d(TAG, "version: " + versionString);
        }

        @Override
        public void safeKeyStatus(boolean status) {

        }

        @Override
        public void powerState(byte source, boolean _switch) {

        }

        @Override
        public void externalPower(IOManager.PowerVoltage powerVoltage, int current) {

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
        public void powerMonitorEvent(short mainVol, short extendVol, short usbVol, short mainCurrent, short extendCurrent, boolean extendError) {

        }

        @Override
        public void consolePowerVoltage(short voltage) {
            Log.d(TAG, "consolePowerVoltage: " + voltage);
            Elapse_tick = System.currentTimeMillis();
            mStressTestRxFlag = false;
        }

        @Override
        public void extendPowerVoltage(short voltage, boolean check) {

        }

        @Override
        public void usbVoltage(short voltage) {

        }

        @Override
        public void powerMonitorState(boolean enable, int interval) {

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
