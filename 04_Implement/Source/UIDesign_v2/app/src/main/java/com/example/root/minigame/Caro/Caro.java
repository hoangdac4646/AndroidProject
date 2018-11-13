package com.example.root.minigame.Caro;

public class Caro {
    private Character[][] arr;
    private int MaxRow;
    private int MaxColum;

    public Character[][] getArr() {
        return arr;
    }

    public Caro(String str, int maxRow ,int maxColum) {
        this.MaxRow = maxRow;
        this.MaxColum = maxColum;
        this.arr = new Character[maxRow][maxColum];
        for(int i = 0; i < maxRow; ++i){
            for(int j = 0; j < maxColum; ++j){
                this.arr[i][j] = str.charAt(i * maxColum + j);
            }
        }
    }

    public boolean KiemTraChienThangNgang(Character kyTuDuyet, int rowSelected, int colSeleted){
        int row = rowSelected;
        int col = colSeleted;
        int count = 1; // Tinh Luon cái vị trí minh đã đánh
        boolean BiChanTrai = false;
        boolean BiChanPhai = false;

        //Dem Ben Trai
        --col;
        while (col >= 0){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanTrai = true;
                break;
            }
            --col;
        }

        //Dem Ben Phai
        col = colSeleted; // Reset Vi Tri Ban Dau
        ++col;
        while (col < this.MaxColum){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanPhai = true;
                break;
            }
            ++col;
        }

        if(BiChanPhai == true && BiChanTrai == true){
            return false;
        }

        if((BiChanTrai == true || BiChanPhai == true) && count == 5){
            return true;
        }

        if((BiChanTrai == false && BiChanPhai == false) && count == 4){
            return true;
        }

        return false;
    }

    public boolean KiemTraChienThangDoc(Character kyTuDuyet, int rowSelected, int colSeleted){
        int row = rowSelected;
        int col = colSeleted;
        int count = 1; // Tinh Luon cái vị trí minh đã đánh
        boolean BiChanTren = false;
        boolean BiChanDuoi = false;

        //Dem Ben Tren
        --row;
        while (row >= 0){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanTren = true;
                break;
            }
            --row;
        }

        //Dem Ben Duoi
        row = rowSelected; // Reset Vi Tri Ban Dau
        ++row;
        while (row < this.MaxRow){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanDuoi = true;
                break;
            }
            ++row;
        }

        if(BiChanDuoi == true && BiChanTren == true){
            return false;
        }

        if((BiChanTren == true || BiChanDuoi == true) && count == 5){
            return true;
        }

        if((BiChanTren == false && BiChanDuoi == false) && count == 4){
            return true;
        }

        return false;
    }

    public boolean KiemTraChienThangCheoTrai(Character kyTuDuyet, int rowSelected, int colSeleted){
        int row = rowSelected;
        int col = colSeleted;
        int count = 1; // Tinh Luon cái vị trí minh đã đánh
        boolean BiChanTren = false;
        boolean BiChanDuoi = false;

        //Dem Ben Tren
        --row;
        --col;
        while (row >= 0 && col >= 0){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanTren = true;
                break;
            }
            --row;
            --col;
        }

        //Dem Ben Duoi
        row = rowSelected; // Reset Vi Tri Ban Dau
        col = colSeleted;
        ++row;
        ++col;
        while (row < this.MaxRow && col < this.MaxColum){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanDuoi = true;
                break;
            }
            ++row;
            ++col;
        }

        if(BiChanDuoi == true && BiChanTren == true){
            return false;
        }

        if((BiChanTren == true || BiChanDuoi == true) && count == 5){
            return true;
        }

        if((BiChanTren == false && BiChanDuoi == false) && count == 4){
            return true;
        }

        return false;
    }

    public boolean KiemTraChienThangCheoPhai(Character kyTuDuyet, int rowSelected, int colSeleted){
        int row = rowSelected;
        int col = colSeleted;
        int count = 1; // Tinh Luon cái vị trí minh đã đánh
        boolean BiChanTren = false;
        boolean BiChanDuoi = false;

        //Dem Ben Tren
        --row;
        ++col;
        while (row >= 0 && col < this.MaxColum){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanTren = true;
                break;
            }
            --row;
            ++col;
        }

        //Dem Ben Duoi
        row = rowSelected; // Reset Vi Tri Ban Dau
        col = colSeleted;
        ++row;
        --col;
        while (row < this.MaxRow && col >= 0){
            if(this.arr[row][col] == kyTuDuyet){
                ++count;
            }else if(this.arr[row][col] == ' '){
                break;
            }else{// Ky tu còn lại
                BiChanDuoi = true;
                break;
            }
            ++row;
            --col;
        }

        if(BiChanDuoi == true && BiChanTren == true){
            return false;
        }

        if((BiChanTren == true || BiChanDuoi == true) && count == 5){
            return true;
        }

        if((BiChanTren == false && BiChanDuoi == false) && count == 4){
            return true;
        }

        return false;
    }

    public boolean KiemTraChienThang(Character kyTuDuyet, int rowSelected, int colSeleted){
        if(KiemTraChienThangNgang(kyTuDuyet, rowSelected, colSeleted)){
            return true;
        }
        if(KiemTraChienThangDoc(kyTuDuyet, rowSelected, colSeleted)){
            return true;
        }

        if(KiemTraChienThangCheoTrai(kyTuDuyet, rowSelected, colSeleted)){
            return  true;
        }

        if(KiemTraChienThangCheoPhai(kyTuDuyet, rowSelected, colSeleted)){
            return true;
        }

        return false;
    }

    public int GoiY(Character kyTuDuyet){
        int index = -1;
        for(int i = 0; i < MaxRow; ++i){
            for(int j = 0; j < MaxColum; ++j){
                if(KiemTraChienThang(kyTuDuyet, i, j)){
                    index = i * MaxRow + j;
                }
            }
        }
        return index;
    }
}
