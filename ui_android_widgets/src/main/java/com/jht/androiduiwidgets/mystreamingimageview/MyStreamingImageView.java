package com.jht.androiduiwidgets.mystreamingimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

import com.jht.androidcommonalgorithms.timer.JHTTimer;
import com.jht.androidcommonalgorithms.timer.PeriodicTimer;

import java.util.ArrayList;

@SuppressLint("ViewConstructor")
@SuppressWarnings({"unused"})
public class MyStreamingImageView extends AppCompatImageView {

    private ImageView imageView;
    private Handler handler;
    private int numFrames;
    private int index;
    private int framePeriodMS;
    private ArrayList<Bitmap> bitmapArrayList;

    private PeriodicTimer timer = new PeriodicTimer(new JHTTimer.IOnTimer() {
        @Override
        public void onTimer() {

            final int bitmapIndex = index;
            handler.post(() -> {
                try {
                    Bitmap bmp =  bitmapArrayList.get(bitmapIndex > numFrames ? 0 : bitmapIndex);
                    if ( imageView != null ){
                        imageView.setImageBitmap(bmp);
                    }
                }
                catch (Exception ignored) {}
            });
            index++;
            if (index > numFrames)
                index=0;
        }
    });

    public MyStreamingImageView(Context context, final ArrayList<Bitmap> bitmapArrayList, final int numFrames,
                                final int framePeriodMS, final ImageView imageView) {
        super(context);
        this.bitmapArrayList = new ArrayList<>(bitmapArrayList);
        this.numFrames = numFrames;
        this.framePeriodMS = framePeriodMS;
        this.imageView = imageView;
        handler = new Handler();
    }

    public void startAnimation(int delay){
        timer.start(delay, framePeriodMS);
    }

    public void stopAnimation() {
        timer.stop();
    }

    public void changeFramePeriod(int newPeriodMS){
        framePeriodMS = newPeriodMS;
        startAnimation(0);
    }


}
