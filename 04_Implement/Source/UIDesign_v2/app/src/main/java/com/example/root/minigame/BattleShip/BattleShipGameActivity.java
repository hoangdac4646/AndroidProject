package com.example.root.minigame.BattleShip;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.root.minigame.Activities.CreatingRoom;
import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;
import com.example.root.minigame.mBluetooth.GuiTinNhan;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BattleShipGameActivity extends AppCompatActivity implements View.OnClickListener {
    static TableLayout map_bs1, map_bs2;

    int[] ship4 = {R.drawable.s1_1, R.drawable.s1_2, R.drawable.s1_3, R.drawable.s1_4};
    int[] ship4_land = {R.drawable.s1_1_land, R.drawable.s1_2_land, R.drawable.s1_3_land, R.drawable.s1_4_land};

    int[] ship3 = {R.drawable.ship_2_1, R.drawable.ship_2_2, R.drawable.ship_2_3};
    int[] ship3_land = {R.drawable.ship_2_1_land, R.drawable.ship_2_2_land, R.drawable.ship_2_3_land};

    int[] ship2 = {R.drawable.s4_1, R.drawable.s4_2};
    int[] ship2_land = {R.drawable.s4_1_land, R.drawable.s4_2_land};

    int[] ship1 = {R.drawable.s3};
    int[] ship1_land = {R.drawable.s3_land};

    int[] drawble_ship4 = {R.drawable.drawable_s1_1, R.drawable.drawable_s1_2, R.drawable.drawable_s1_3, R.drawable.drawable_s1_4};
    int[] drawable_ship4_land = {R.drawable.drawable_s1_1_land, R.drawable.drawable_s1_2_land, R.drawable.drawable_s1_3_land, R.drawable.drawable_s1_4_land};

    int[] drawable_ship3 = {R.drawable.drawable_s2_1, R.drawable.drawable_s2_2, R.drawable.drawable_s2_3};
    int[] drawable_ship3_land = {R.drawable.drawable_s1_1_land, R.drawable.drawable_s1_2_land, R.drawable.drawable_s1_3_land};

    int[] drawable_ship2 = {R.drawable.drawable_s4_1, R.drawable.drawable_s4_2};
    int[] drawable_ship2_land = {R.drawable.drawable_s4_1_land, R.drawable.drawable_s4_2_land};

    int[] drawable_ship1 = {R.drawable.drawable_s3};
    int[] drawable_ship1_land = {R.drawable.drawable_s3_land};

    Button btnGun, btnBomb, btnRada, btnPause;
    int selection = 0;//0-gun, 1-bomb, 2-rada
    int countBomb = 0, countRada;
    int MAX_BOMB = 2, MAX_RADA = 1;

    int MAX_ROW = 10, MAX_COL = 10;

    ArrayList<BattleShipPreActivity.Ship> shipMap1 = BattleShipPreActivity.shipMap;// cai mang thuyen
    ArrayList<BattleShipPreActivity.Ship> shipMap = new ArrayList<>();
    ArrayList<Integer> isFire = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_battleship);
        StartingMenu.mConnection.setHandle(mShipBattleHandler);
        String temp = ParseArrayToString(shipMap1);
        if(StartingMenu.mConnection.sendMessage(temp) == -1){
            Toast.makeText(this, "Ship: Bạn Đã Mất Kết Nối Tới Phòng Chờ", Toast.LENGTH_SHORT).show();
        }
        AnhXa();
        InitMaps();
        ShowShip();
        SetOnClick();
    }

    private void AnhXa() {
        map_bs1 = findViewById(R.id.map_bs1);
        map_bs2 = findViewById(R.id.map_bs2);
        btnGun = findViewById(R.id.btnGun);
        btnBomb = findViewById(R.id.btnBomb);
        btnRada = findViewById(R.id.btnRada);
        //btnPause = findViewById(R.id.btnPause);
    }

    private void SetOnClick(){
        btnGun.setOnClickListener(this);
        btnBomb.setOnClickListener(this);
        btnRada.setOnClickListener(this);
        //btnPause.setOnClickListener(this);
    }

    private void InitMaps() {
        for (int i = 0; i < MAX_ROW; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            TableRow row1 = new TableRow(this);
            row1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < MAX_COL; j++) {

                final Button mbutton = new Button(this);
                mbutton.setLayoutParams(new TableRow.LayoutParams(50, 50));
                mbutton.setId(i * MAX_ROW + j);
                row.addView(mbutton);
                mbutton.setBackground(null);
                final Button mbutton1 = new Button(this);
                mbutton1.setLayoutParams(new TableRow.LayoutParams(50, 50));
                mbutton1.setId(i * MAX_ROW + j + 100);
                row1.addView(mbutton1);
                mbutton1.setBackground(null);
                mbutton1.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {

                        StartingMenu.mConnection.sendMessage(mbutton1.getId()+"");
                        if (selection == 0){
                            SetBackgroundButton(mbutton1);
                        }
                        else if(selection == 1) {
                            SetBombType(mbutton1);
                            selection = 0;//sau khi dung bomb thi dung lai sung thuong
                        }
                        else if(selection == 2){
                            SetRadaType(mbutton1);
                            selection = 0;
                        }
                    }
                });
            }
            map_bs1.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            map_bs2.addView(row1, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void ShowShip() {
        for (int i = 0; i < shipMap1.size(); i++) {
            int num_Row = shipMap1.get(i).getIndex() / 10;
            int num_Col = shipMap1.get(i).getIndex() % 10;

            if (shipMap1.get(i).getOrien()) {
                TableRow row = (TableRow) map_bs1.getChildAt(num_Row);

                for (int k = 0; k < shipMap1.get(i).getLen(); k++) {
                    Button mButton = (Button) row.getChildAt(num_Col + k);
                    //ischecked.add(mButton.getId());
                    switch (shipMap1.get(i).getLen()) {
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
                }
            } else {
                for (int k = 0; k < shipMap1.get(i).getLen(); k++) {
                    TableRow row = (TableRow) map_bs1.getChildAt(num_Row + k);
                    Button mButton = (Button) row.getChildAt(num_Col);
                    //ischecked.add(mButton.getId());
                    switch (shipMap1.get(i).getLen()) {
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGun:
                Toast.makeText(this, "Using gun", Toast.LENGTH_SHORT).show();
                selection = 0;
                break;
            case R.id.btnBomb:
                if(countBomb < MAX_BOMB) {
                    Toast.makeText(this, "Using bomb", Toast.LENGTH_SHORT).show();
                    selection = 1;
                    countBomb++;
                }
                else {
                    Toast.makeText(this, "Het so lan dung bom", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnRada:
                Toast.makeText(this, "Using rada", Toast.LENGTH_SHORT).show();
                selection = 2;
                break;
        }
    }

    public void AddShipFromGet(){

    }

    private boolean CheckShipIsFire(BattleShipPreActivity.Ship ship) {//tàu, kiểm tra tàu nổ chưa để lan ra
        int len = 0;
        for (int i = 0; i < ship.getLen(); i++) {//kiem tra tat cac toa do cua thuyen
            if (ship.getOrien()) {
                for (int j = 0; j < isFire.size(); j++) {
                    if (ship.getIndex() + i == isFire.get(j)) {//kiem tra toa to cua thuyen da duoc check chua
                        len++;
                        break;
                    }
                }
            } else {
                for (int j = 0; j < isFire.size(); j++) {
                    if (ship.getIndex() + i * MAX_ROW == isFire.get(j)) {//kiem tra toa to cua thuyen da duoc check chua
                        len++;
                        break;
                    }
                }
            }
        }
        if (len == ship.getLen()) {
            return true;
        }
        return false;
    }

    private void SetAroundShip(BattleShipPreActivity.Ship ship) {
        int num_Row = ship.getIndex() / 10;
        int num_Col = ship.getIndex() % 10;

        if (ship.getOrien()) {
            for (int i = -1; i <= 1; i++) {
                if (num_Row + i < 0 || num_Row + i >= MAX_COL) {//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs1.getChildAt(num_Row + i);

                for (int j = -1; j <= ship.getLen(); j++) {
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL) {//kiem tra cot co trong ban do hay ko
                        continue;
                    }

                    if (i == 0 && (j == -1 || j == ship.getLen())) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                    } else if (i != 0) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                    }

                }
            }
        } else {
            for (int i = -1; i <= ship.getLen(); i++) {
                if (num_Row + i < 0 || num_Row + i >= MAX_ROW) {//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs1.getChildAt(num_Row + i);

                for (int j = -1; j <= 1; j++) {
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL) {//kiem tra cot co trong ban do hay ko
                        continue;
                    }

                    if (j == 0 && (i == -1 || i == ship.getLen())) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                    } else if (j != 0) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                    }
                }
            }
        }
    }

    private void SetBombType(Button mbutton) {
        int index = mbutton.getId();
        int row = index / MAX_COL;
        int col = index % MAX_ROW;

        for (int i = -1; i <= 1; i++) {
            if (row + i < 0 || row + i >= MAX_ROW) {//kiem tra vi tri co trong ban do hay ko
                continue;
            }

            TableRow Row = (TableRow) map_bs1.getChildAt(row + i);

            for (int j = -1; j <= 1; j++) {
                if (col + j < 0 || col + j >= MAX_COL) {//kiem tra vi tri co trong  ban do hay ko
                    continue;
                }

                if ((i == -1 || i == 1) && (j == -1 || j == 1)) {//chi xet vi tri nam trong chu thap
                    continue;
                }

                mbutton = (Button) Row.getChildAt(col + j);

                SetBackgroundButton(mbutton);
            }
        }
    }

    private void SetRadaType(Button mbutton){
        int index = mbutton.getId();
        int row = index / 10;
        int col = index % 10;

        mbutton.setVisibility(View.INVISIBLE);

        CountDownTimer countdown = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(BattleShipGameActivity.this, "remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                Toast.makeText(BattleShipGameActivity.this, "done!", Toast.LENGTH_SHORT).show();
            }
        }.start();



        for (int i = -1; i <= 1; i++){
            if (row + i < 0 || row + i >= MAX_ROW) {//kiem tra vi tri co trong ban do hay ko
                continue;
            }

            TableRow Row = (TableRow) map_bs1.getChildAt(row + i);

            for (int j = -1; j <= 1; j++){
                if (col + j < 0 || col + j >= MAX_COL) {//kiem tra vi tri co trong  ban do hay ko
                    continue;
                }

                mbutton = (Button) Row.getChildAt(col + j);

                mbutton.setVisibility(View.VISIBLE);


            }
        }

        countdown.cancel();


    }

    private void SetBackgroundButton(Button mbutton) {
        boolean isShip = false;

        for (int i = 0; i < shipMap1.size(); i++) {//kiem tra id cac thuyen
            for (int j = 0; j < shipMap1.get(i).getLen(); j++) {//kiem tra id cua thuyen
                if (shipMap1.get(i).getOrien()) {//neu tau nam ngang
                    if (mbutton.getId() == shipMap1.get(i).getIndex() + j) {
                        isFire.add(mbutton.getId());

                        switch (shipMap1.get(i).getLen()) {
                            case 4:
                                mbutton.setBackgroundResource(drawble_ship4[j]);
                                break;
                            case 3:
                                mbutton.setBackgroundResource(drawable_ship3[j]);
                                break;
                            case 2:
                                mbutton.setBackgroundResource(drawable_ship2[j]);
                                break;
                            case 1:
                                mbutton.setBackgroundResource(drawable_ship1[j]);
                                break;
                        }

                        if (CheckShipIsFire(shipMap1.get(i))) {
                            SetAroundShip(shipMap1.get(i));
                        }

                        isShip = true;
                        break;
                    }
                } else {
                    if (mbutton.getId() == shipMap1.get(i).getIndex() + j * MAX_ROW) {
                        isFire.add(mbutton.getId());

                        switch (shipMap1.get(i).getLen()) {
                            case 4:
                                mbutton.setBackgroundResource(drawable_ship4_land[j]);
                                break;
                            case 3:
                                mbutton.setBackgroundResource(drawable_ship3_land[j]);
                                break;
                            case 2:
                                mbutton.setBackgroundResource(drawable_ship2_land[j]);
                                break;
                            case 1:
                                mbutton.setBackgroundResource(drawable_ship1_land[j]);
                                break;
                        }

                        if (CheckShipIsFire(shipMap1.get(i))) {
                            SetAroundShip(shipMap1.get(i));
                        }

                        isShip = true;
                        break;
                    }
                }
            }
            if (isShip) {//neu la mot phan cua thuyen thi thoat
                break;
            }
        }
        if (!isShip) {
            mbutton.setBackgroundResource(R.color.black_overlay);
        }
    }

    private final Handler mShipBattleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED) {
                        Toast.makeText(getApplication(), "Ship: Bạn Đã Mất Kết Nối Tới Phòng Chờ", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Messages.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    //setbackgroud//um//dunnog roi
                    String writeMessage = new String(writeBuf);
                    try{
                        int index = Integer.parseInt(String.valueOf(writeMessage));
                        int num_Row = index / MAX_ROW;
                        int num_Col = index - num_Row * MAX_ROW;
                        TableRow Check_row = (TableRow) map_bs2.getChildAt(num_Row);
                        Button check_mButton = (Button) Check_row.getChildAt(num_Col);
                        SetBackgroundButton(check_mButton);
                    }catch (NumberFormatException e){

                    }
                    break;
                case Messages.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    try{
                        int index = Integer.parseInt(String.valueOf(readMessage));
                        int num_Row = index / MAX_ROW;
                        int num_Col = index - num_Row * MAX_ROW;
                        TableRow Check_row = (TableRow) map_bs1.getChildAt(num_Row);
                        Button check_mButton = (Button) Check_row.getChildAt(num_Col);
                        SetBackgroundButton(check_mButton);

                    }catch (NumberFormatException e){
                        shipMap = ParseStringToArray(readMessage);
                        //CheckShipIsFire(shipMap);
                    }
                    break;
                case Messages.MESSAGE_DEVICE_NAME:
                    break;

                case Messages.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Messages.TOAST),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    public String ParseArrayToString(ArrayList<BattleShipPreActivity.Ship> shipMap){
        String line = "";

        for (int i = 0; i < shipMap.size(); i++){
            line += shipMap.get(i).getIndex();
            line += ",";
            line += shipMap.get(i).getLen();
            line += ",";

            if(shipMap.get(i).getOrien()){
                line += 1;
            }
            else {
                line += 0;
            }

            line += ";";
        }
        return line;
    }
    public ArrayList<BattleShipPreActivity.Ship> ParseStringToArray(String line){
        ArrayList<BattleShipPreActivity.Ship> shipMap = new ArrayList<>();

        StringTokenizer tokens = new StringTokenizer(line, ";");

        for (int i = 0; i < tokens.countTokens(); i++){
            String token = tokens.nextToken();
            StringTokenizer elements = new StringTokenizer(token, ",");
            BattleShipPreActivity.Ship tmp = new BattleShipPreActivity.Ship();

            String element = elements.nextToken();
            tmp.setIndex(Integer.parseInt(element));

            element = elements.nextToken();
            tmp.setLen(Integer.parseInt(element));

            element = elements.nextToken();
            if(Integer.parseInt(element) == 0){
                tmp.setOrien(false);
            }
            else{
                tmp.setOrien(true);
            }

            shipMap.add(tmp);
        }

        return shipMap;
    }
}
