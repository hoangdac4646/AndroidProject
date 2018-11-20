package com.example.root.minigame.Caro;

import com.example.root.minigame.R;

import java.util.ArrayList;

public class ListOCaro {
    private ArrayList<OCaro> oCaroList;

    public ArrayList<OCaro> getoCaroList() {
        return oCaroList;
    }
    public ListOCaro() {
        this.oCaroList = new ArrayList<OCaro>();
    }

    public ListOCaro(ArrayList<OCaro> oCaroList) {
        this.oCaroList = oCaroList;
    }

    public void Add(OCaro x){
        oCaroList.add(x);
    }

    public int Count(){
        return oCaroList.size();
    }

    public String getString(){
        String result = "";
        for(int i = 0; i < oCaroList.size(); ++i){
            result += oCaroList.get(i).getValue();
        }
        return result;
    }

    //1 la O, 0 là X
    public void Danh(int index, int type){
        if(type == 0){
            oCaroList.get(index).getSrc().setImageResource(R.drawable.red_x);
            oCaroList.get(index).setValue('X');
        }else if(type == 1){
            oCaroList.get(index).getSrc().setImageResource(R.drawable.green_o);
            oCaroList.get(index).setValue('O');
        }else{
            oCaroList.get(index).getSrc().setImageResource(R.drawable.maudo);
            oCaroList.get(index).setValue(' ');
        }
    }
}
