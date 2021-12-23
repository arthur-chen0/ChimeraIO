package com.jht.chimera.io.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.jht.chimera.io.MainActivity;
import com.jht.chimera.io.R;
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
import java.util.Date;

public class UartCommTestFragment extends Fragment {

    private static final String TAG = UartCommTestFragment.class.getSimpleName();

    private Context context;
    private InputMethodManager inputMethodManager;

    private Spinner device_path_selector;
    private Spinner baud_rate_selector;
    private Button open_button;
    private Button write_button;
    private Button clear_button;
    private EditText data_edit;
    private TextView received_data_text;

    private ArrayList<String> dataArrlist = new ArrayList<>();
    private StringBuilder log = new StringBuilder();
    private MainActivity.FragmentTouchListener listener;

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
                SerialPortCommManager.getInstance().start(new SerialPort((String)device_path_selector.getSelectedItem(),serialPortSetting()));
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

}
