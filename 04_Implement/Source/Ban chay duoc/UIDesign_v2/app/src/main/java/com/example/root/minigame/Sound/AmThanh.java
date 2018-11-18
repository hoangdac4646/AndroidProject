package com.example.root.minigame.Sound;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.root.minigame.R;

public  class AmThanh extends Service {

    MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
       ;
        super.onCreate();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String Message=  intent.getStringExtra("TenLoai");

        if(Message.equals("PhongCho1")){
            if(mediaPlayer !=null){
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this,R.raw.man_hinh_cho_2);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(70,70);
            mediaPlayer.start();
        }else{
            if(mediaPlayer !=null){
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(this,R.raw.nhac_phong_cho);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(70,70);
            mediaPlayer.start();
        }
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.setLooping(false);
        mediaPlayer.stop();

    }
}
