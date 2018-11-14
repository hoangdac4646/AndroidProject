package com.example.root.minigame.BattleShip;

import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.BattleShip.BattleShipGameActivity;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.R;

import java.util.ArrayList;
import java.util.Random;

public class BattleShipPreActivity extends AppCompatActivity implements View.OnClickListener {
    private TableLayout map_bs;
    private ImageView img_ship_1_1,img_ship_1_2,img_ship_1_3,img_ship_1_4, img_ship_2_1,img_ship_2_2,img_ship_2_3,img_ship_3_1, img_ship_3_2,img_ship_4;
    private Button btn_turn, btn_autoA, btn_play;
    private int Index_Click, MAX_ROW = 10, MAX_COL = 10, ship_Type = 0;
    boolean Ship_Orien = true; // landscap


    int[] ship4 = {R.drawable.s1_1,R.drawable.s1_2, R.drawable.s1_3, R.drawable.s1_4};
    int[] ship4_land = {R.drawable.s1_1_land,R.drawable.s1_2_land, R.drawable.s1_3_land, R.drawable.s1_4_land};

    int[] ship3 = {R.drawable.ship_2_1, R.drawable.ship_2_2, R.drawable.ship_2_3};
    int[] ship3_land = {R.drawable.ship_2_1_land, R.drawable.ship_2_2_land, R.drawable.ship_2_3_land};

    int[] ship2 = {R.drawable.s4_1,R.drawable.s4_2};
    int[] ship2_land = {R.drawable.s4_1_land,R.drawable.s4_2_land};

    int[] ship1 = {R.drawable.s3};
    int[] ship1_land = {R.drawable.s3_land};
    int Clicking = 0;

    MediaPlayer mediaPlayer;

    ArrayList<Integer> ischecked = new ArrayList<>();
    Animation animation;

    int countShip = 0;
    int MAX_SHIP = 10;

    public ArrayList<Ship> shipMap = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregame_battleship);
        if(StartingMenu.mConnection != null){
            StartingMenu.mConnection.setHandle(mPreBTHandler);
        }
        else{
            Toast.makeText(this, "Máy Bạn Không có kết nối", Toast.LENGTH_SHORT).show();
        }
        AnhXa();
        InitMap();

        SetOnAnimation();
        SetOnCLick();
    }

    private void AnhXa() {
        map_bs      = findViewById(R.id.map_battleship);

        img_ship_1_1  = findViewById(R.id.img_ship_1_1);
        img_ship_1_2  = findViewById(R.id.img_ship_1_2);
        img_ship_1_3  = findViewById(R.id.img_ship_1_3);
        img_ship_1_4  = findViewById(R.id.img_ship_1_4);
        img_ship_2_1  = findViewById(R.id.img_ship_2_1);
        img_ship_2_2  = findViewById(R.id.img_ship_2_2);
        img_ship_2_3  = findViewById(R.id.img_ship_2_3);
        img_ship_3_1  = findViewById(R.id.img_ship_3_1);
        img_ship_3_2  = findViewById(R.id.img_ship_3_2);
        img_ship_4    = findViewById(R.id.img_ship_4);

        btn_turn      = findViewById(R.id.btn_turn);
        btn_autoA     = findViewById(R.id.btn_autoA);
        btn_play      = findViewById(R.id.btn_play);

    }

    private void SetOnAnimation(){
        // animation button
        animation = new AlphaAnimation(1, 0.5f);
        animation.setDuration(150);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
    }

    private void SetOnCLick(){
        img_ship_1_1.setOnClickListener(this);
        img_ship_1_2.setOnClickListener(this);
        img_ship_1_3.setOnClickListener(this);
        img_ship_1_4.setOnClickListener(this);
        img_ship_2_1.setOnClickListener(this);
        img_ship_2_2.setOnClickListener(this);
        img_ship_2_3.setOnClickListener(this);
        img_ship_3_1.setOnClickListener(this);
        img_ship_3_2.setOnClickListener(this);
        img_ship_4.setOnClickListener(this);

        btn_turn.setOnClickListener(this);
        btn_autoA.setOnClickListener(this);
        btn_play.setOnClickListener(this);
    }



    @Override
    protected void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    public void InitMap(){
        for(int i = 0; i < MAX_ROW    ; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j = 0 ; j < MAX_COL ; j ++){

                final Button mbutton = new Button( this);
                mbutton.setLayoutParams(new TableRow.LayoutParams(50, 50));
                mbutton.setId(i * MAX_ROW + j);
                row.addView(mbutton);
                mbutton.setBackground(null);
                mbutton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        Index_Click = mbutton.getId();

                        if(!AddShip(Index_Click)){
                            Toast.makeText(BattleShipPreActivity.this, "Khong add duoc", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            setVisibleButton();
                        }

                    }
                });
                mbutton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(BattleShipPreActivity.this, "Ne", Toast.LENGTH_SHORT).show();
                        RemoveShip(mbutton.getId());
                        return true;
                    }
                });
            }
            map_bs.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void setVisibleButton() {
        switch (Clicking){
            case R.id.img_ship_1_1:
                img_ship_1_1.setVisibility(View.GONE);
                break;
            case R.id.img_ship_1_2:
                img_ship_1_2.setVisibility(View.GONE);
                break;
            case R.id.img_ship_1_3:
                img_ship_1_3.setVisibility(View.GONE);
                break;
            case R.id.img_ship_1_4:
                img_ship_1_4.setVisibility(View.GONE);
                break;
            case R.id.img_ship_2_1:
                img_ship_2_1.setVisibility(View.GONE);
                break;
            case R.id.img_ship_2_2:
                img_ship_2_2.setVisibility(View.GONE);
                break;
            case R.id.img_ship_2_3:
                img_ship_2_3.setVisibility(View.GONE);
                break;
            case R.id.img_ship_3_1:
                img_ship_3_1.setVisibility(View.GONE);
                break;
            case R.id.img_ship_3_2:
                img_ship_3_2.setVisibility(View.GONE);
                break;
            case R.id.img_ship_4:
                img_ship_4.setVisibility(View.GONE);
                break;
        }
        Clicking = 0;
    }

    boolean  Check_Position_ischecked(int temp){
        for(int i = 0; i < ischecked.size(); i++){
            if(temp == ischecked.get(i)){
                return false;
            }
        }
        return true;
    }

    private boolean AddShip(int index){
        if(countShip >= MAX_SHIP){
            Toast.makeText(this, "Da du so thuyen quy dinh", Toast.LENGTH_SHORT).show();
            return false;
        }

        int num_Row = index / MAX_ROW;
        int num_Col = index - num_Row * MAX_ROW;
        TableRow Check_row = (TableRow) map_bs.getChildAt(num_Row);
        Button check_mButton = (Button) Check_row.getChildAt(num_Col);

        if(Ship_Orien) {
            TableRow row = (TableRow) map_bs.getChildAt(num_Row);
            if (num_Col > MAX_COL - ship_Type) {//ra ngoai ban do
                return false;
            }

            //kiem tra vi tri dat thuyen va xung quanh co gan thuyen nao khac khong
            for (int i = -1; i <= 1; i++){
                if(num_Row + i < 0 || num_Row + i >= MAX_COL){//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs.getChildAt(num_Row + i);

                for (int j = -1; j <= ship_Type; j++ ){
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL){//kiem tra cot co trong ban do hay ko
                        continue;
                    }

                    Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                    if(!Check_Position_ischecked(mButton.getId())){
                        return false;
                    }
                }
            }

            Ship tmp = new Ship(index, ship_Type, Ship_Orien);
            tmp.setIndex(index);
            tmp.setLen(ship_Type);
            tmp.setOrien(Ship_Orien);

            shipMap.add(tmp);

            for (int k = 0; k < ship_Type; k++) {
                Button mButton = (Button) row.getChildAt(num_Col + k);
                ischecked.add(mButton.getId());
                switch (ship_Type){
                    case 4:
                        mButton.setBackgroundResource(ship4[k]);
                        continue;
                    case 3:
                        mButton.setBackgroundResource(ship3[k]);
                        continue;
                    case 2:
                        mButton.setBackgroundResource(ship2[k]);
                        continue;
                    case 1:
                        mButton.setBackgroundResource(ship1[k]);
                        continue;
                }
            }//for
        }
        else if (!Ship_Orien){
            if(num_Row > MAX_ROW - ship_Type){
                return false;
            }

            for (int i = -1; i <= ship_Type; i++){
                if(num_Row + i < 0 || num_Row +i >= MAX_ROW){//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs.getChildAt(num_Row + i);

                for (int j = -1; j <= 1; j++ ){
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL){//kiem tra cot co trong ban do hay ko
                        continue;
                    }
                    Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                    if(!Check_Position_ischecked(mButton.getId())){
                        return false;
                    }
                }
            }

            shipMap.add(new Ship(index,ship_Type,Ship_Orien));

            for(int k = 0; k < ship_Type; k++){
                TableRow row = (TableRow) map_bs.getChildAt(num_Row + k);
                Button mButton = (Button) row.getChildAt(num_Col);
                ischecked.add(mButton.getId());
                switch (ship_Type){
                    case 4:
                        mButton.setBackgroundResource(ship4_land[k]);
                        continue;
                    case 3:
                        mButton.setBackgroundResource(ship3_land[k]);
                        continue;
                    case 2:
                        mButton.setBackgroundResource(ship2_land[k]);
                        continue;
                    case 1:
                        mButton.setBackgroundResource(ship1_land[k]);
                        continue;
                }
            }
        }

        countShip++;

        return true;
    }

    private void RemoveShip(int index){
        int row = index / MAX_ROW;
        int col = index % MAX_COL;
        Ship rmv = new Ship();

        for (int i = 0; i < shipMap.size(); i++){
            if(shipMap.get(i).getIndex() == index){
                rmv = shipMap.get(i);
                break;
            }
        }

        if(rmv.getOrien()){
            TableRow Row = (TableRow) map_bs.getChildAt(row);

            for (int i = 0; i < rmv.getLen(); i++){
                Button mbutton = (Button) Row.getChildAt(col + i);

                mbutton.setBackground(null);

                ischecked.remove(ischecked.indexOf(mbutton.getId()));
            }
        }
        else{
            for (int i = 0; i < rmv.getLen(); i++){
                TableRow Row = (TableRow) map_bs.getChildAt(row + i);
                Button mbutton = (Button) Row.getChildAt(col);

                mbutton.setBackground(null);

                ischecked.remove(ischecked.indexOf(mbutton.getId()));
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_ship_1_1:
                ship_Type = 1;
                img_ship_1_1.startAnimation(animation);
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_1_1;
                break;
            case R.id.img_ship_1_2:
                ship_Type = 1;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.startAnimation(animation);
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_1_2;
                break;
            case R.id.img_ship_1_3:
                ship_Type = 1;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.startAnimation(animation);;
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_1_3;
                break;
            case R.id.img_ship_1_4:
                ship_Type = 1;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.startAnimation(animation);;
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_1_4;
                break;
            case R.id.img_ship_2_1:
                ship_Type = 2;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.startAnimation(animation);;
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_2_1;
                break;
            case R.id.img_ship_2_2:
                ship_Type = 2;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.startAnimation(animation);;
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_2_2;

                break;
            case R.id.img_ship_2_3:
                ship_Type = 2;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.startAnimation(animation);;
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_2_3;

                break;
            case R.id.img_ship_3_1:
                ship_Type = 3;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.startAnimation(animation);;
                img_ship_3_2.clearAnimation();
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_3_1;

                break;
            case R.id.img_ship_3_2:
                ship_Type = 3;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.startAnimation(animation);;
                img_ship_4.clearAnimation();
                Clicking = R.id.img_ship_3_2;

                break;
            case R.id.img_ship_4:
                ship_Type = 4;
                img_ship_1_1.clearAnimation();
                img_ship_1_2.clearAnimation();
                img_ship_1_3.clearAnimation();
                img_ship_1_4.clearAnimation();
                img_ship_2_1.clearAnimation();
                img_ship_2_2.clearAnimation();
                img_ship_2_3.clearAnimation();
                img_ship_3_1.clearAnimation();
                img_ship_3_2.clearAnimation();
                img_ship_4.startAnimation(animation);
                Clicking = R.id.img_ship_4;

                break;
            case R.id.btn_turn:
                if(Ship_Orien == true){
                    Ship_Orien = false;
                    Toast.makeText(this, "Doc", Toast.LENGTH_SHORT).show();
                }
                else if(Ship_Orien == false) {
                    Ship_Orien = true;
                    Toast.makeText(this, "Ngang", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_autoA:
                Toast.makeText(this, "Auto", Toast.LENGTH_SHORT).show();
                randomAllShip();
                break;
            case R.id.btn_play:
                Intent intent = new Intent(BattleShipPreActivity.this, BattleShipGameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("shipmap", shipMap);
                intent.putExtra("Bundle", bundle);
                startActivity(intent);
                break;
        }
    }

    private void randomAllShip(){
        if(shipMap.size() != 0){//lo sap xep thuyen ma lam bieng muon random thi phai xoa nhung thuyen da sap xep
            for (int i = 0; i < shipMap.size(); i++){
                RemoveShip(shipMap.get(i).getIndex());
            }

            shipMap.clear();//xoa mang de chuan bi random
        }

        Random rnd = new Random();
        int[] len ={4, 3, 3, 2, 2, 2, 1, 1, 1, 1};

        for (int i = 0; i < 10; i++) {
            int orien = rnd.nextInt(2);//1-landscap

            if (orien == 0) {
                Ship_Orien = false;
            } else if (orien == 1) {
                Ship_Orien = true;
            }

            ship_Type = len[i];
            boolean isAdd = false;

            while (isAdd == false){
                int index = rnd.nextInt(100);
                isAdd= AddShip(index);
            }
        }

        img_ship_4.setVisibility(View.INVISIBLE);
        img_ship_3_2.setVisibility(View.INVISIBLE);
        img_ship_3_1.setVisibility(View.INVISIBLE);
        img_ship_2_3.setVisibility(View.INVISIBLE);
        img_ship_2_2.setVisibility(View.INVISIBLE);
        img_ship_2_1.setVisibility(View.INVISIBLE);
        img_ship_1_4.setVisibility(View.INVISIBLE);
        img_ship_1_3.setVisibility(View.INVISIBLE);
        img_ship_1_2.setVisibility(View.INVISIBLE);
        img_ship_1_1.setVisibility(View.INVISIBLE);
    }

    private Handler mPreBTHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Messages.MESSAGE_STATE_CHANGE:
            }
        }

    };


}
