package com.example.root.minigame.BattleShip;

import java.io.Serializable;

public class Ship implements Serializable {
    private int index;
    private int len;
    private boolean orien;

    public Ship(int index, int len, boolean orien) {
        this.index = index;
        this.len = len;
        this.orien = orien;
    }
    public Ship(){
        index = -1;
        len = 0;
        orien = true;
    }

    public int getIndex(){
        return index;
    }

    public int getLen(){
        return len;
    }

    public boolean getOrien(){
        return orien;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public void setLen(int len){
        this.len = len;
    }

    public void setOrien(boolean orien){
        this.orien = orien;
    }
}
