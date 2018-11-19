package com.example.root.minigame.BattleShip;

import android.content.Intent;
import android.content.res.TypedArray;
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

import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;
import com.example.root.minigame.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BattleShipGameActivity extends AppCompatActivity implements View.OnClickListener {
    private TableLayout map_bs1, map_bs2;

    TypedArray Ship4, ship4_land , ship3 , ship3_land , ship2 , ship2_land;
    int ship1 = R.drawable.s3;
    int ship1_land = R.drawable.s3_land;


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

    boolean cont = false;//kiem tra tiep tuc danh neu danh trung

    int countFiredShip = 0;
    int MAX_SHIP = 10;
    boolean turn = false;

    boolean[] isClick = new boolean[100];

    ArrayList<Ship> shipMap1 = new ArrayList<>();// cai mang thuyen
    ArrayList<Ship> shipMap2 = new ArrayList<>();
    ArrayList<Integer> isFire = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_battleship);
        AnhXa();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Bundle");
        shipMap1 = (ArrayList<Ship>) bundle.getSerializable("shipmap");
        if(StartingMenu.mConnection != null){
            StartingMenu.mConnection.setHandle(mShipBattleHandler);
            String temp ="M"+ ParseArrayToString(shipMap1);
            StartingMenu.mConnection.sendMessage(temp);
        }
        else{
            Toast.makeText(getApplicationContext(), Messages.NO_CONNECTION, Toast.LENGTH_LONG).show();
        }
        //
        if(Main.thisPlayer.isHost()){
            turn = true;
        }

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

        Ship4 = getResources().obtainTypedArray(R.array.ship4_array);
        ship4_land = getResources().obtainTypedArray(R.array.ship4_array_land);
        ship3 = getResources().obtainTypedArray(R.array.ship3_array);
        ship3_land = getResources().obtainTypedArray(R.array.ship3_array_land);
        ship2 = getResources().obtainTypedArray(R.array.ship2_array);
        ship2_land = getResources().obtainTypedArray(R.array.ship2_array_land);

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
                final Button mbutton2 = new Button(this);
                mbutton2.setLayoutParams(new TableRow.LayoutParams(50, 50));
                mbutton2.setId(i * MAX_ROW + j + 100);
                row1.addView(mbutton2);
                mbutton2.setBackground(null);
                mbutton2.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(View v) {
                        if(turn == true){
                            if(isClick[mbutton2.getId() - MAX_ROW*MAX_COL] == false){//kiem tra da click chua
                                cont = false;

                                if (selection == 0){
                                    SetBackgroundButton(map_bs2, shipMap2, mbutton2);
                                    StartingMenu.mConnection.sendMessage((mbutton2.getId())+"");
                                }
                                else if(selection == 1) {
                                    SetBombType(map_bs2, shipMap2, mbutton2);
                                    selection = 0;//sau khi dung bomb thi dung lai sung thuong
                                }
                                else if(selection == 2){
                                    SetRadaType(mbutton2);
                                    selection = 0;
                                }

//                                if (cont == false) {//danh sai thi doi luot
//                                    turn = false;
//                                }
//                                else{//danh dung thi danh tiep
//                                    turn = true;
//                                }

                                isClick[mbutton2.getId() - MAX_ROW*MAX_COL] = true;
                            }
                            else{
                                Toast.makeText(BattleShipGameActivity.this, "Vi tri da ban", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(BattleShipGameActivity.this, "Chua toi luot ban danh", Toast.LENGTH_SHORT).show();
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
                            mButton.setBackground(Ship4.getDrawable(k));
                            continue;
                        case 3:
                            mButton.setBackground(ship3.getDrawable(k));
                            continue;
                        case 2:
                            mButton.setBackground(ship2.getDrawable(k));
                            continue;
                        case 1:
                            mButton.setBackgroundResource(ship1);
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
                            mButton.setBackground(ship4_land.getDrawable(k));
                            continue;
                        case 3:
                            mButton.setBackground(ship3_land.getDrawable(k));
                            continue;
                        case 2:
                            mButton.setBackground(ship2_land.getDrawable(k));
                            continue;
                        case 1:
                            mButton.setBackgroundResource(ship1_land);
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

    private boolean CheckShipIsFire(Ship ship) {
        int len = 0;//bien dem so luong phan tau da no

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

    private void SetAroundShip(TableLayout map_bs, Ship ship) {
        int num_Row = ship.getIndex() / MAX_ROW;
        int num_Col = ship.getIndex() % MAX_COL;

        if (ship.getOrien()) {
            for (int i = -1; i <= 1; i++) {
                if (num_Row + i < 0 || num_Row + i >= MAX_COL) {//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs.getChildAt(num_Row + i);

                for (int j = -1; j <= ship.getLen(); j++) {
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL) {//kiem tra cot co trong ban do hay ko
                        continue;
                    }

                    if (i == 0 && (j == -1 || j == ship.getLen())) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);

                        isClick[mButton.getId()] = true;//set da click

                    } else if (i != 0) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                        isClick[mButton.getId()] = true;//set da click
                    }

                }
            }
        } else {
            for (int i = -1; i <= ship.getLen(); i++) {
                if (num_Row + i < 0 || num_Row + i >= MAX_ROW) {//kiem tra dong co nam trong ban do hay ko
                    continue;
                }

                TableRow Around_row = (TableRow) map_bs.getChildAt(num_Row + i);

                for (int j = -1; j <= 1; j++) {
                    if (num_Col + j < 0 || num_Col + j >= MAX_COL) {//kiem tra cot co trong ban do hay ko
                        continue;
                    }

                    if (j == 0 && (i == -1 || i == ship.getLen())) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                        isClick[mButton.getId() - MAX_ROW*MAX_COL] = true;//set da click
                    } else if (j != 0) {
                        Button mButton = (Button) Around_row.getChildAt(num_Col + j);

                        mButton.setBackgroundResource(R.color.black_overlay);
                        isClick[mButton.getId() - MAX_ROW*MAX_COL] = true;//set da click
                    }
                }
            }
        }
    }

    private void SetBombType(TableLayout map_bs, ArrayList<Ship> shipMap, Button mbutton) {
        int index = mbutton.getId();
        int row = index / MAX_COL;
        int col = index % MAX_ROW;

        if(row >= MAX_ROW){//truong hop id cua button qua 100
            row -= MAX_ROW;
        }

        for (int i = -1; i <= 1; i++) {
            if (row + i < 0 || row + i >= MAX_ROW) {//kiem tra vi tri co trong ban do hay ko
                continue;
            }

            TableRow Row = (TableRow) map_bs.getChildAt(row + i);

            for (int j = -1; j <= 1; j++) {
                if (col + j < 0 || col + j >= MAX_COL) {//kiem tra vi tri co trong  ban do hay ko
                    continue;
                }

                if ((i == -1 || i == 1) && (j == -1 || j == 1)) {//chi xet vi tri nam trong chu thap
                    continue;
                }

                mbutton = (Button) Row.getChildAt(col + j);

                SetBackgroundButton(map_bs, shipMap, mbutton);
                isClick[mbutton.getId()] = true;
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

    private void ShowFiredShip(TableLayout map_bs, Ship ship){
        int row = ship.getIndex() / MAX_ROW;
        int col = ship.getIndex() % MAX_COL;

        if(row >= MAX_ROW){
            row -= MAX_ROW;
        }

        if (ship.getOrien()){
            TableRow Row = (TableRow) map_bs.getChildAt(row);

            for (int i = 0; i < ship.getLen(); i++){
                Button mbutton = (Button) Row.getChildAt(col + i);

                switch (ship.getLen()) {
                    case 4:
                        mbutton.setBackgroundResource(drawble_ship4[i]);
                        break;
                    case 3:
                        mbutton.setBackgroundResource(drawable_ship3[i]);
                        break;
                    case 2:
                        mbutton.setBackgroundResource(drawable_ship2[i]);
                        break;
                    case 1:
                        mbutton.setBackgroundResource(drawable_ship1[i]);
                        break;
                }
            }
        }
        else{
            for (int i = 0; i < ship.getLen(); i++){
                TableRow Row = (TableRow) map_bs.getChildAt(row + i);
                Button mbutton = (Button) Row.getChildAt(col);

                switch (ship.getLen()) {
                    case 4:
                        mbutton.setBackgroundResource(drawable_ship4_land[i]);
                        break;
                    case 3:
                        mbutton.setBackgroundResource(drawable_ship3_land[i]);
                        break;
                    case 2:
                        mbutton.setBackgroundResource(drawable_ship2_land[i]);
                        break;
                    case 1:
                        mbutton.setBackgroundResource(drawable_ship1_land[i]);
                        break;
                }

            }
        }

    }

    private void SetBackgroundButton(TableLayout map_bs, ArrayList<Ship> shipMap, Button mbutton) {
        boolean isShip = false;

        for (int i = 0; i < shipMap.size(); i++) {//kiem tra id cac thuyen
            for (int j = 0; j < shipMap.get(i).getLen(); j++) {//kiem tra id cua thuyen da chon
                if (shipMap.get(i).getOrien()) {//neu tau nam ngang
                    if (mbutton.getId() == shipMap.get(i).getIndex() + j) {
                        isFire.add(mbutton.getId());

                        mbutton.setBackgroundResource(R.drawable.drawable_dau_x);
                        cont = true;//danh trung thi danh tiep

                        if (CheckShipIsFire(shipMap.get(i))) {
                            ShowFiredShip(map_bs, shipMap.get(i));
                            SetAroundShip(map_bs, shipMap.get(i));
                            countFiredShip++;

                            if(countFiredShip == MAX_SHIP){
                                if(turn == true){//neu ben phan dang danh
                                    Toast.makeText(this, "Ben phai thang", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(this, "Ben trai thang", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        isShip = true;
                        break;
                    }
                } else {
                    if (mbutton.getId() == shipMap.get(i).getIndex() + j * MAX_ROW) {
                        isFire.add(mbutton.getId());

                        mbutton.setBackgroundResource(R.drawable.drawable_dau_x);
                        cont = true;//danh trung thi danh tiep

                        if (CheckShipIsFire(shipMap.get(i))) {
                            ShowFiredShip(map_bs, shipMap.get(i));
                            SetAroundShip(map_bs, shipMap.get(i));
                            countFiredShip++;

                            if(countFiredShip == MAX_SHIP){
                                if(turn == true){//neu ben trai dang danh
                                    Toast.makeText(this, "Ben phai thang", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(this, "Ben trai thang", Toast.LENGTH_SHORT).show();
                                }
                            }
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
            cont = false;
        }
    }

    public void Attack(TableLayout map, int index , ArrayList<Ship> ships) {
        int num_Row = index / MAX_ROW;
        int num_Col = index - num_Row * MAX_ROW;
        TableRow Check_row = (TableRow) map.getChildAt(num_Row);
        Button check_mButton = (Button) Check_row.getChildAt(num_Col);
        SetBackgroundButton(map, ships, check_mButton);
    }

    public String ParseArrayToString(ArrayList<Ship> shipMap){
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
    public ArrayList<Ship> ParseStringToArray(String line){
        ArrayList<Ship> shipMap = new ArrayList<>();

        StringTokenizer tokens = new StringTokenizer(line, ";");

        for (int i = 0; i < tokens.countTokens(); i++){
            String token = tokens.nextToken();
            StringTokenizer elements = new StringTokenizer(token, ",");
            try{
                String element = elements.nextToken();
                int index = Integer.parseInt(element) + 100;

                element = elements.nextToken();
                int len = (Integer.parseInt(element));

                element = elements.nextToken();
                boolean orien = false;
                if(Integer.parseInt(element) != 0) {
                    orien = true;
                }
                Ship tmp = new Ship(index, len , orien);
                shipMap.add(tmp);

            }catch (NumberFormatException e){

            }
        }

        return shipMap;
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
                    String writeMessage = new String(writeBuf);
                    if(!cont){
                        turn = false;
                    }
                    else{
                        turn = true;
                    }

                    break;
                case Messages.MESSAGE_READ:
                    Toast.makeText(BattleShipGameActivity.this, "Ship: Readding data...", Toast.LENGTH_SHORT).show();
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    try{
                        int index = Integer.parseInt(String.valueOf(readMessage));
                        Attack(map_bs1, index - 100 , shipMap1);

                    }catch (NumberFormatException e){
                        if(readMessage.charAt(0) == 'M')
                        {
                            String temp = readMessage.substring(1);
                            shipMap2 = ParseStringToArray(temp);
                            if(shipMap2.size() == 0){
                                Toast.makeText(BattleShipGameActivity.this, Messages.Error_Mes, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(!cont){
                        turn = true;
                    }
                    else{
                        turn = false;
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


}
