package com.example.root.minigame;

public class Room {
    private String hostName;

    // Image name (Without extension)
    private String gameName;

    private int ID;

    public Room(int ID,String hostName, String gameName) {
        this.ID= ID;
        this.hostName= hostName;
        this.gameName= gameName;
    }

    public int getRoomID() {
        return ID;
    }

    public void setRoomID(int ID) {
        this.ID = ID;
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
        return this.ID+"|"+ this.hostName+"|"+this.gameName;
    }
}
