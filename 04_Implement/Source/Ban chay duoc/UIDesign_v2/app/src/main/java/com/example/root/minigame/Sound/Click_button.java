package com.example.root.minigame.Sound;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.root.minigame.R;

public class Click_button {
    MediaPlayer mediaPlayer;
    Context context;

    public Click_button(Context context) {
        this.context = context;
    }
    public void PlayButtonSound(){
        mediaPlayer = MediaPlayer.create(context, R.raw.pingping);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();
    }
}
