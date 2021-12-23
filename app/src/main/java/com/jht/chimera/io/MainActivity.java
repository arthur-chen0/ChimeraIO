package com.jht.chimera.io;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IO_main";

    private PackageInfo app;

    private TabLayout fragmentTab;
    private ViewPager2 viewPager;

    private TextView text_appVersion;
    private TextView text_ioVersion;

    private Context context;

    private CmdHandler cmdHandler = new CmdHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        fragmentTab = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);
        text_appVersion = findViewById(R.id.text_app_version);
        text_ioVersion = findViewById(R.id.text_io_version);

        fragmentTab.setTabMode(TabLayout.MODE_FIXED);
        fragmentTab.setTabGravity(TabLayout.GRAVITY_FILL);

        CollectionAdapter viewPagerAdapter = new CollectionAdapter(getSupportFragmentManager(),getLifecycle());

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(fragmentTab,viewPager,(tab, position) -> tab.setText(viewPagerAdapter.getPageTitle(position))).attach();

        //Get App Version
        try {
            app = getPackageManager().getPackageInfo("com.jht.chimera.chimeraio", 0);
            runOnUiThread(() -> text_appVersion.setText(app.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        findViewById(R.id.fab_close).setOnClickListener(view -> {
            this.finish();
            onDestroy();
            System.exit(0);
        });

        findViewById(R.id.fab_update).setOnClickListener(view -> {
            try {
//                String fileName[] = MainActivity.this.getAssets().list("");
                ArrayList<String> fileList = new ArrayList(Arrays.asList(getAssets().list("")));
                Log.d(TAG, "Update File size " + fileList.size());

                if(fileList.size() > 0){
                    AlertDialog.Builder updateDialog = new AlertDialog.Builder(context);

                    updateDialog.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, fileList), (dialog, which) -> {

                        Log.d("Update", "File Name: " + fileList.get(which));
                        cmdHandler.updateIO(fileList.get(which));

//                        AlertDialog.Builder progressDialog = new AlertDialog.Builder(MainActivity.this);
                        AlertDialog progressDialog = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert).create();
//                        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_progress_customize,null);
                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_customize,null);
//                        progressDialog.setTitle("MCU Firmware Updating...");
                        progressDialog.setView(dialogView);

                        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        progressDialog.setCancelable(false);
                        WindowManager.LayoutParams dialogWindow = Objects.requireNonNull(progressDialog.getWindow()).getAttributes();
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

                    });

                    updateDialog.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }


    private ArrayList<FragmentTouchListener> mFragmentTouchListeners = new ArrayList<>();


    public void registerFragmentTouchListener(FragmentTouchListener listener) {
        mFragmentTouchListeners.add(listener);
    }


    public void unRegisterFragmentTouchListener(FragmentTouchListener listener) {
        mFragmentTouchListeners.remove(listener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: " + event);
        for (FragmentTouchListener listener : mFragmentTouchListeners) {
            listener.onTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    public interface FragmentTouchListener {

        boolean onTouchEvent(MotionEvent event);
    }


}

