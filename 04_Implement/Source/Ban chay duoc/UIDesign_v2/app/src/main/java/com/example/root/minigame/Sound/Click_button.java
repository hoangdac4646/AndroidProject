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
        mediaPlayer = MediaPlayer.create(context, R.raw.buttonclick_sound);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();
    }
    public void check_mate(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.checkmate);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();
    }
    public void chess_move(){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.chesspiece_move);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();
    }


}
