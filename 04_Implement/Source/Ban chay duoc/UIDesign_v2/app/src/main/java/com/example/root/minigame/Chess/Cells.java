package com.example.root.minigame.Chess;

public class Cells {
    private boolean color;
    private int chessPieces;
    private int index;

    public Cells(boolean color, int chessPieces, int index) {
        this.color = color;
        this.chessPieces = chessPieces;
        this.index = index;
    }
    public boolean cellsEquals(Cells temp){
        if(temp.index == this.index && temp.color == this.color && temp.chessPieces == this.chessPieces){
            return true;
        }

        return false;
    }

    public boolean getColor() {
        return color;
    }

    public void setColor(boolean color) {
        this.color = color;
    }

    public int getChessPieces() {
        return chessPieces;
    }

    public void setChessPieces(int chessPieces) {
        this.chessPieces = chessPieces;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
