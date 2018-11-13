package com.example.root.minigame;

public class ChatMessage {
    private Player player;
    private String message;

    public ChatMessage()
    {
        player = new Player();
        message = "";
    }

    public ChatMessage(Player player , String message)
    {
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
