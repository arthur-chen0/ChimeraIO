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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.jht.chimera.io.ExecuteAsRoot;
import com.jht.chimera.io.MainActivity;
import com.jht.chimera.io.R;
import com.jht.chimera.io.audio.AudioSourceCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class AudioTestFragment extends Fragment {

    private String TAG = AudioTestFragment.class.getSimpleName();

    private Context context;
    private InputMethodManager inputMethodManager;
    private AudioSourceCodec audioSourceCodec;

    private Spinner audio_source_selector;

    private Button btn_open;
    private TextView text_tinymix_data;

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        audioSourceCodec = new AudioSourceCodec(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.audio_test_fragment, container, false);
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        ((MainActivity) this.getActivity()).registerFragmentTouchListener(event -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (inputMethodManager.isAcceptingText()) {
                    Log.d(TAG, "arthur_trace: hide keyboard");
                    ViewCompat.getWindowInsetsController(requireView()).hide(WindowInsetsCompat.Type.ime());
                }
            }

            return true;
        });

//        ExecuteAsRoot.chmod("777", "/dev/snd/controlC2");

    }

    public void init(View view){
        audio_source_selector = view.findViewById(R.id.selector_audio_source);
        btn_open = view.findViewById(R.id.btn_audio_open);
        text_tinymix_data = view.findViewById(R.id.text_tinymix_data);

        ArrayList<AudioSourceCodec.CodecSource> audio_source = new ArrayList<>(Arrays.asList(AudioSourceCodec.CodecSource.values()));
        ArrayAdapter<AudioSourceCodec.CodecSource> audioSourceAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1, audio_source);
        audio_source_selector.setAdapter(audioSourceAdapter);

        btn_open.setOnClickListener(v -> {
            audioSourceCodec.setAudioSource((AudioSourceCodec.CodecSource)audio_source_selector.getSelectedItem());
            text_tinymix_data.setText(getTinymixParameters());
        });


    }

    public String getTinymixParameters(){
        ExecuteAsRoot checkResolution = new ExecuteAsRoot();
        checkResolution.addCommand("tinymix -D 2");
//        StringBuilder output = new StringBuilder();
        final ArrayList<String>[] output = new ArrayList[]{new ArrayList()};
        checkResolution.execute(false, true, null, output[0]);
        final String[] outputData = {""};
        output[0].forEach(line -> {

            for(String str: line.split("\\t")){
//                Log.d(TAG, "getTinymixParameters: " + str);
                outputData[0] += String.format("%-7s",str);
            }

//            Log.d(TAG, "getTinymixParameters: " + outputData);
            outputData[0] += "\n";
        });



        return outputData[0];
  }


}
