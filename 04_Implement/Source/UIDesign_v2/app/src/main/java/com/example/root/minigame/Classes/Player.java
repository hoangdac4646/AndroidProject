package com.example.root.minigame.Classes;

import com.example.root.minigame.R;

public class Player {
    private String playerName;
    private boolean hostStatus = true;
    private int avatarID;

    public Player()
    {
        this.playerName = "";
        avatarID = R.drawable.logo_style;
    }

    public Player(String playerName)
    {
        this.playerName = playerName;
        avatarID = R.drawable.logo_style;
    }

    public Player(String playerName, int avatarID)
    {
        this.playerName = playerName;
        this.avatarID = avatarID;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isHost() {
        return hostStatus;
    }

    public void setHostStatus(boolean hostStatus) {
        this.hostStatus = hostStatus;
    }
}
