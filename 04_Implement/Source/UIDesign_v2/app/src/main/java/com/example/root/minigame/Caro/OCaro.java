package com.example.root.minigame.Caro;

import android.widget.ImageView;

public class OCaro {
    private ImageView src;
    private Character value;

    public OCaro(){
        this.src = null;
        this.value = ' ';
    }

    public OCaro(ImageView src, Character value) {
        this.src = src;
        this.value = value;
    }

    public ImageView getSrc() {
        return src;
    }

    public Character getValue() {
        return value;
    }

    public void setSrc(ImageView src) {
        this.src = src;
    }

    public void setValue(Character value) {
        this.value = value;
    }
}
