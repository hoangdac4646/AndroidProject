package com.example.root.minigame;

public class Room {
    private String hostName;

    private String gameName;

    private int avatarID;

    public Room(int avatarID,String hostName, String gameName) {
        this.avatarID= avatarID;
        this.hostName= hostName;
        this.gameName= gameName;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(int avatarIDID) {
        this.avatarID = avatarIDID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public String toString()  {
        return this.avatarID+"|"+ this.hostName+"|"+this.gameName;
    }
}
