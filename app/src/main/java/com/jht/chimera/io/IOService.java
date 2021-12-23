package com.jht.chimera.io;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.jht.androidcommonalgorithms.timer.PeriodicTimer;

import java.io.File;

public class IOService extends Service {
    private final String TAG = getClass().getSimpleName();

    static volatile PeriodicTimer mTimer = new PeriodicTimer();

    @Override
    public void onCreate() {
        super.onCreate();

//        if(Environment.isExternalStorageManager()){
//
//        }
//        else {
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
////        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
////        intent.setData(uri);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        }

        mTimer.setOnTimer(() -> {
            boolean isUSBConnected = new File("/mnt/media_rw/udisk").exists();
            Log.d(TAG, "USBMountManager: usb is connected " + isUSBConnected);
        });
        mTimer.start(1000, 1000);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}