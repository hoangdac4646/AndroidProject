package com.example.root.minigame.Chat;

public class SendMessage {
    private String message,deviceName;


    public SendMessage(String message, String deviceName) {
        this.message = message;
        this.deviceName = deviceName;
    }

    public String getmessage() {
        return message;
    }

    public String getdeviceName() {
        return deviceName;
    }
}
