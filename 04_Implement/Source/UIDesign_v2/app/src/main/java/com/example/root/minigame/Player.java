package com.example.root.minigame;

public class Player {
    private String name;

    private boolean hostStatus = false;
    private boolean readyStatus = false;

    public Player() {
        this.name= null;
        this.readyStatus= false;
        this.hostStatus = false;
    }
    public Player(String name) {
        this.name= name;
        this.readyStatus= false;
        this.hostStatus = false;
    }

    public Player(String name , boolean readyStatus, boolean hostStatus) {
        this.name= name;
        this.readyStatus= readyStatus;
        this.hostStatus = hostStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isReady() {
        return readyStatus;
    }
    public boolean isHost() {
        return hostStatus;
    }


    public void setReadyStatus(boolean readyStatus) {
        this.readyStatus = readyStatus;
    }
    public void setHostStatus(boolean readyStatus) {
        this.readyStatus = readyStatus;
    }

}
