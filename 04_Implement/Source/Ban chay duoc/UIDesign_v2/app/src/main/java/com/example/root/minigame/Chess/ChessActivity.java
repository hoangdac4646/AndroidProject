package com.example.root.minigame.Chess;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.root.minigame.Activities.CreatingRoom;
import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.BattleShip.BattleShipGameActivity;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ChessActivity extends AppCompatActivity {

    private TableLayout chess_broad;
    private LinearLayout linear_row;
    private final int MAX_ROW = 8;
    private final int MAX_COL = 8;
    private boolean chessmanIsClicked = false;
    private int Whichchess = 0;
    private final int KING = 1;
    private final int QUEEN = 2;
    private final int BISHOP = 3;
    private final int KNIGHT = 4;
    private final int CASTLE = 5;
    private final int PAWN = 6;
    private int mOldIndex = -1;
    private ArrayList<Cells> CellHasPieces = new ArrayList<>();
    private ArrayList<Integer> cellHasPiecesInt = new ArrayList<>();
    private int[] addchess = {CASTLE, KNIGHT, BISHOP, QUEEN, KING};
    private boolean isWin = false;
    private int posOfKing = -1;
    private int posOfEnemyKing = -1;
    private boolean Eatable = false;
    private BluetoothAdapter mBTadapter;
    private boolean yourTurn = false;
    private boolean isBluetoothEnable = false;
    private int REQUEST_CODE_ENABLE_BLUETOOTH = 1310;
    private boolean myColor = true , EnemyColor = false; // mau trang
    private Button btn_pause, btn_sound, btn_chat;
    private TextView txt_MyStep, txt_EnemyStep , txt_p1Name, txt_p2Name, txt_time;
    private Dialog dialog;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        if(StartingMenu.mConnection != null){
            StartingMenu.mConnection.setHandle(mChessGameHandle);
        }
        inItWork();
        initMap();
        AddArrayForCheck();
        if (Main.thisPlayer.isHost()) {
            myColor = true;
            EnemyColor = false;
        }else{
            myColor = false;
            EnemyColor = true;
        }
    }

    private void inItWork() {
        CheckBluetooth();
        chess_broad = findViewById(R.id.chess_broad);
        btn_chat    = findViewById(R.id.btn_chat_chess);
        btn_pause   = findViewById(R.id.btn_pause_chess);
        btn_sound   = findViewById(R.id.btn_sound_chess);
        txt_MyStep  = findViewById(R.id.txt_p1_Step);
        txt_EnemyStep  = findViewById(R.id.txt_p2_Step);
        txt_p1Name  = findViewById(R.id.txt_p1Name_chess);
        txt_p2Name  = findViewById(R.id.txt_p2Name_chess);
        txt_time    = findViewById(R.id.txt_time);
        dialog = new Dialog(ChessActivity.this);
        txt_p1Name.setText(Main.thisPlayer.getPlayerName());
        linear_row = new LinearLayout(this);
        linear_row.setWeightSum(8f);
        linear_row.setOrientation(LinearLayout.VERTICAL);
        linear_row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chess_broad.addView(linear_row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(Main.thisPlayer.isHost()){
            yourTurn = true;
        }
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog mdialog = new Dialog(ChessActivity.this);
                mdialog.show();
                CheckBluetooth();
                if(isBluetoothEnable == true){
                    StartingMenu.mConnection.sendMessage("pause");
                }
                mdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mdialog.dismiss();
                        CheckBluetooth();
                        if(isBluetoothEnable == true){
                            StartingMenu.mConnection.sendMessage("resume");
                        }
                    }
                });
            }
        });

    }

    private void CheckBluetooth(){
        mBTadapter = BluetoothAdapter.getDefaultAdapter();
        if(mBTadapter.isEnabled() == false){
            isBluetoothEnable = false;
            Intent bluetoothOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothOnIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        else{
            isBluetoothEnable = true;
        }
    }

    private void AddArrayForCheck() {

        CellHasPieces.add(new Cells(myColor, CASTLE, 63));
        CellHasPieces.add(new Cells(myColor, KNIGHT, 62));
        CellHasPieces.add(new Cells(myColor, BISHOP, 61));
        for (int i = 0; i < 5; i++) {
            CellHasPieces.add(new Cells(myColor, addchess[i], 56 + i));
        }
        for (int i = 48; i < 56; i++) {
            CellHasPieces.add(new Cells(myColor, PAWN, i));
        }

        CellHasPieces.add(new Cells(EnemyColor, CASTLE, 7));
        CellHasPieces.add(new Cells(EnemyColor, KNIGHT, 6));
        CellHasPieces.add(new Cells(EnemyColor, BISHOP, 5));
        for (int i = 0; i < 5; i++) {
            CellHasPieces.add(new Cells(EnemyColor, addchess[i], 0 + i));
        }
        for (int i = 8; i < 16; i++) {
            CellHasPieces.add(new Cells(EnemyColor, PAWN, i));
        }

        for (int i = 0; i < CellHasPieces.size(); i++) {
            cellHasPiecesInt.add(CellHasPieces.get(i).getIndex());
            if (CellHasPieces.get(i).getChessPieces() == KING && CellHasPieces.get(i).getColor() == EnemyColor) {
                posOfEnemyKing = CellHasPieces.get(i).getIndex();
            } else if (CellHasPieces.get(i).getChessPieces() == KING && CellHasPieces.get(i).getColor() == myColor) {
                posOfKing = CellHasPieces.get(i).getIndex();
            }
            DrawPieces(CellHasPieces.get(i).getIndex(), CellHasPieces.get(i));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initMap() {


        for (int i = 0; i < MAX_ROW; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1F));
            row.setWeightSum(8F);
            for (int j = 0; j < MAX_COL; j++) {

                final ImageView mbutton = new ImageView(this);
                mbutton.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT , 1F));

                mbutton.setId(i * MAX_ROW + j);


                row.addView(mbutton);
                mbutton.setBackground(null);
                mbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CountDown(60000);
                        if (yourTurn == true) {
                            if (!checkIsEnemy(mbutton.getId(), mOldIndex)) {
                                isChessPiecesChecked(mbutton.getId());
                            }
                            if (chessmanIsClicked == false) { // o trong vua dc click
                                if (checkIsMoveable(mbutton.getId(), mOldIndex)) {
                                    CheckBluetooth();
                                    if (isBluetoothEnable == true) {
                                        NearlyStep(mbutton.getId(), txt_MyStep );
                                        String temp = mbutton.getId() + "&" + mOldIndex;
                                        StartingMenu.mConnection.sendMessage(temp);
                                        yourTurn = false;
                                    }
                                    if (Eatable) {
                                        EatEnemy(CellHasPieces.get(cellHasPiecesInt.indexOf(mOldIndex)), CellHasPieces.get(cellHasPiecesInt.indexOf(mbutton.getId())));
                                        if (checkIsMoveable(posOfKing, mbutton.getId()) && Eatable == true) {
                                            Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                        }
                                        if (checkIsMoveable(posOfEnemyKing, mbutton.getId()) && Eatable == true) {
                                            Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        DrawPieces(mbutton.getId(), CellHasPieces.get(cellHasPiecesInt.indexOf(mOldIndex)));
                                        cellHasPiecesInt.set(cellHasPiecesInt.indexOf(mOldIndex), mbutton.getId());
                                        DrawPieces(mOldIndex, null);
                                        mOldIndex = -1;
                                        if (checkIsMoveable(posOfKing, mbutton.getId()) && Eatable == true) {
                                            Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                        }
                                        if (checkIsMoveable(posOfEnemyKing, mbutton.getId()) && Eatable == true) {
                                            Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }
                            }

                        }
                    }
                });
            }
            linear_row.addView(row);
        }
    }

    private boolean checkIsEnemy(int pos, int posofpieces) {
        if (posofpieces == -1) {
            return false;
        }
        if (cellHasPiecesInt.indexOf(pos) != -1 && cellHasPiecesInt.indexOf(posofpieces) != -1) {
            if (CellHasPieces.get(cellHasPiecesInt.indexOf(pos)).getColor() !=
                    CellHasPieces.get(cellHasPiecesInt.indexOf(posofpieces)).getColor()) {
                chessmanIsClicked = false;
                return true;
            }
        }
        return false;
    }

    private void CheckWin(){

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void DrawPieces(int position, Cells mcell) {
        MediaPlayer.create(this,R.raw.chesspiece_move).start();
        int num_ROW = position / MAX_ROW;
        int num_COL = position - num_ROW * MAX_COL;

        LinearLayout row = (LinearLayout) linear_row.getChildAt(num_ROW);
        ImageView mButton = (ImageView) row.getChildAt(num_COL);
        if (mcell == null) {
            mButton.setImageDrawable(null);
            return;
        }
        switch (mcell.getChessPieces()) {
            case PAWN:
                if (position >= 0 && position <= 7 || position >= 56 && position <= 63) {
                    if (mcell.getColor() == true) {
                        mButton.setImageResource(R.drawable.queen_white);
                    } else {
                        mButton.setImageResource(R.drawable.queen_black);
                    }
                    CellHasPieces.get(cellHasPiecesInt.indexOf(mcell.getIndex())).setChessPieces(QUEEN);
                    break;
                }
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.pawn_white);
                } else {
                    mButton.setImageResource(R.drawable.pawn_black);
                }
                break;
            case KNIGHT:
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.knight_white);
                } else {
                    mButton.setImageResource(R.drawable.kinght_black);
                }
                break;
            case KING:
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.king_white);
                } else {
                    mButton.setImageResource(R.drawable.king_black);
                }
                break;
            case BISHOP:
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.bishop_white);
                } else {
                    mButton.setImageResource(R.drawable.bishop_black);
                }
                break;
            case CASTLE:
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.caslte_white);
                } else {
                    mButton.setImageResource(R.drawable.castle_black);
                }
                break;
            case QUEEN:
                if (mcell.getColor() == true) {
                    mButton.setImageResource(R.drawable.queen_white);
                } else {
                    mButton.setImageResource(R.drawable.queen_black);
                }
                break;
        }
        CellHasPieces.get(cellHasPiecesInt.indexOf(mcell.getIndex())).setIndex(position);
        mcell.setIndex(position);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ENABLE_BLUETOOTH && resultCode == RESULT_OK){
            isBluetoothEnable = true;
        }
        else if(requestCode == REQUEST_CODE_ENABLE_BLUETOOTH && resultCode == RESULT_CANCELED){
            isBluetoothEnable = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void EatEnemy(Cells mcell, Cells mcelled) {
        if (mcelled.getChessPieces() == KING) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if(myColor == mcelled.getColor()){
                MediaPlayer.create(this,R.raw.defeat).start();
            }
            else{
                MediaPlayer.create(this,R.raw.victory).start();

            }
            if(mcelled.getColor() == true){
                builder.setMessage("Xin chúc mừng BlackKing! Quân Black đã chiến thắng");
            }
            else {
                builder.setMessage("Xin chúc mừng WhiteKing! Quân White đã chiến thắng");
            }
            builder.show();

        }
        int num_ROW = mcell.getIndex() / MAX_ROW;
        int num_COL = mcell.getIndex() - num_ROW * MAX_COL;

        LinearLayout row = (LinearLayout) linear_row.getChildAt(num_ROW);
        ImageView mButton = (ImageView) row.getChildAt(num_COL);

        int num_ROW_ed = mcelled.getIndex() / MAX_ROW;
        int num_COL_ed = mcelled.getIndex() - num_ROW_ed * MAX_COL;
        LinearLayout row_ed = (LinearLayout) linear_row.getChildAt(num_ROW_ed);
        ImageView mButton_ed = (ImageView) row_ed.getChildAt(num_COL_ed);
        if ((mcelled.getIndex() >= 0 && mcelled.getIndex() <= 7) || (mcelled.getIndex() >= 56 && mcelled.getIndex() <= 63) && mcell.getChessPieces() == PAWN) {
            if (mcell.getColor() == true) {
                mButton_ed.setImageResource(R.drawable.queen_white);
            } else {
                mButton_ed.setImageResource(R.drawable.queen_black);
            }
            mButton.setImageDrawable(null);
            cellHasPiecesInt.set(CellHasPieces.indexOf(mcell), mcelled.getIndex());
            CellHasPieces.get(CellHasPieces.indexOf(mcell)).setIndex(mcelled.getIndex());
            CellHasPieces.get(CellHasPieces.indexOf(mcell)).setChessPieces(QUEEN);
            cellHasPiecesInt.remove(CellHasPieces.indexOf(mcelled));
            CellHasPieces.remove(mcelled);

            mOldIndex = -1;
            return;
        }


        mButton_ed.setImageDrawable(mButton.getDrawable());
        mButton.setImageDrawable(null);
        cellHasPiecesInt.set(CellHasPieces.indexOf(mcell), mcelled.getIndex());
        CellHasPieces.get(CellHasPieces.indexOf(mcell)).setIndex(mcelled.getIndex());
        cellHasPiecesInt.remove(CellHasPieces.indexOf(mcelled));
        CellHasPieces.remove(mcelled);

        mOldIndex = -1;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkIsMoveable(int position, int posOfChessPiece) {

        boolean check = position > posOfChessPiece ? true : false; // black or white go
        int compare;
        if (check) {
            compare = 1;// white go
        } else {
            compare = -1;// black go
        }
        for (int i = 0; i < CellHasPieces.size(); i++) {
            if (posOfChessPiece == CellHasPieces.get(i).getIndex()) {
                Cells mcell = CellHasPieces.get(i);
                switch (mcell.getChessPieces()) {
                    case PAWN: {
                        if (mcell.getColor()) {// pawn have to go straight
                            compare = -1;// white
                        } else {
                            compare = 1;//black
                        }
                        if (posOfChessPiece + ((MAX_ROW + 1) * compare) == position || posOfChessPiece + ((MAX_ROW - 1) * compare) == position) {// eat enemy
                            if (checkIsEnemy(posOfChessPiece, position)) {
                                if (cellHasPiecesInt.indexOf(position) != -1) {
                                    Eatable = true;
                                    return true;
                                }
                            }
                        }
                        if ((posOfChessPiece + MAX_ROW * compare == position ||
                                (posOfChessPiece + MAX_ROW * 2 * compare == position &&
                                        position * compare <= ((MAX_ROW * MAX_COL) / 2) * compare))) { //if in emeny map, our pawm can't go double
                            for (int k = 1; k <= (position - posOfChessPiece) * compare / MAX_ROW; k++) {// check is our checkpiece is being blocked
                                if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * k * compare) != -1) {
                                    return false;
                                }
                            }
                            Eatable = false;
                            return true;
                        }
                        break;
                    }//pawn
                    case KNIGHT: {// free to go(kimochi)
                        if (posOfChessPiece + (MAX_ROW * 2 + 1) * compare == position || posOfChessPiece + (MAX_ROW * 2 - 1) * compare == position
                                || posOfChessPiece + (MAX_ROW + 2) * compare == position || posOfChessPiece + (MAX_ROW - 2) * compare == position) {
                            if (checkIsEnemy(posOfChessPiece, position)) {
                                if (cellHasPiecesInt.indexOf(position) != -1) {
                                    Eatable = true;
                                    return true;
                                }
                            }
                            Eatable = false;
                            return true;
                        }
                        break;
                    }//knight
                    case CASTLE: {
                        for (int j = 0; j < MAX_COL; j++) {
                            if (posOfChessPiece + j * compare == position
                                    || posOfChessPiece + MAX_ROW * j * compare == position) {// the castle can go straight or across
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / MAX_ROW &&
                                        (position - posOfChessPiece) * compare % MAX_ROW == 0; k++) {
                                    if(posOfChessPiece + MAX_ROW * compare * k == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * compare * k) != -1) {// check is our checkpiece is being blocked
                                        Eatable = false;
                                        return false;
                                    }

                                }
                                for (int k = 1; k <= (position - posOfChessPiece) * compare &&
                                        (position - posOfChessPiece) * compare % MAX_ROW != 0; k++) {

                                    if(posOfChessPiece + k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare) != -1) {
                                        return false;
                                    }

                                }
                                Eatable = false;
                                return true;
                            }
                        }
                        break;
                    }//castle
                    case BISHOP: {
                        for (int j = 0; j < MAX_ROW; j++) {
                            if (posOfChessPiece + ((MAX_ROW + 1) * j) * compare == position || posOfChessPiece + ((MAX_ROW - 1) * j) * compare == position) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / (MAX_ROW + 1) && (position - posOfChessPiece) * compare % (MAX_ROW + 1) == 0; k++) {

                                    if(posOfChessPiece + (MAX_ROW + 1) * k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + (MAX_ROW + 1) * k * compare) != -1) {
                                        return false;
                                    }

                                }
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / (MAX_ROW - 1) && (position - posOfChessPiece) * compare % (MAX_ROW - 1) == 0; k++) {

                                    if(posOfChessPiece + (MAX_ROW - 1) * k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + (MAX_ROW - 1) * k * compare) != -1) {
                                        return false;
                                    }

                                }
                                Eatable = false;
                                return true;
                            }
                        }
                        break;
                    }//bishop
                    case QUEEN: {
                        for (int j = 0; j < MAX_ROW; j++) {
                            if (posOfChessPiece + ((MAX_ROW + 1) * j) * compare == position || posOfChessPiece + ((MAX_ROW - 1) * j) * compare == position) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / (MAX_ROW + 1) && (position - posOfChessPiece) * compare % (MAX_ROW + 1) == 0; k++) {

                                    if(posOfChessPiece + (MAX_ROW + 1) * k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + (MAX_ROW + 1) * k * compare) != -1) {
                                        return false;
                                    }

                                }
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / (MAX_ROW - 1) && (position - posOfChessPiece) * compare % (MAX_ROW - 1) == 0; k++) {

                                    if(posOfChessPiece + (MAX_ROW - 1) * k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + (MAX_ROW - 1) * k * compare) != -1) {
                                        return false;
                                    }

                                }
                                Eatable = false;//moveable
                                return true;
                            }
                        }
                        for (int j = 0; j < MAX_COL; j++) {
                            if (posOfChessPiece + j * compare == position
                                    || posOfChessPiece + MAX_ROW * j * compare == position) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare / MAX_ROW &&
                                        (position - posOfChessPiece) * compare % MAX_ROW == 0; k++) {
                                    if(posOfChessPiece + MAX_ROW * compare * k == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * compare * k) != -1) {
                                        return false;
                                    }

                                }
                                for (int k = 1; k <= (position - posOfChessPiece) * compare &&
                                        (position - posOfChessPiece) * compare % MAX_ROW != 0; k++) {

                                    if(posOfChessPiece + k * compare == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare) != -1) {
                                        return false;
                                    }

                                }
                                Eatable = false;//moveable
                                return true;
                            }
                        }
                        break;
                    }//queen
                    case KING: {
                        if (posOfChessPiece + 1 * compare == position || posOfChessPiece + MAX_ROW * compare == position
                                || posOfChessPiece + (MAX_ROW + 1) * compare == position || posOfChessPiece + (MAX_ROW - 1) * compare == position) {

                            if (checkIsEnemy(posOfChessPiece, position)) {
                                if (cellHasPiecesInt.indexOf(position) != -1) {
                                    Eatable = true;
                                    return true;
                                }
                            }
                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                return false;
                            }

                            Eatable = false;//moveable
                            posOfKing = position;
                            return true;
                        }
                        break;
                    }//king

                }//switch
                return false;
            }//if has cell
        }//for
        return false;
    }//checkismoveable




    public void isChessPiecesChecked(int pos){
        if(cellHasPiecesInt.indexOf(pos) != -1 && CellHasPieces.get(cellHasPiecesInt.indexOf(pos)).getColor() == myColor){
            chessmanIsClicked = true;
            mOldIndex = pos;
            return;
        }
        chessmanIsClicked = false;
    }


    private void NearlyStep(int pos , TextView view){
        int num_ROW = pos / MAX_ROW;
        int num_COL = pos - num_ROW * MAX_COL;
        String temp = "";
        switch (num_COL){
            case 0:
                temp += "A";
                break;
            case 1:
                temp += "B";
                break;
            case 2:
                temp += "C";
                break;
            case 3:
                temp += "D";
                break;
            case 4:
                temp += "E";
                break;
            case 5:
                temp += "F";
                break;
            case 6:
                temp += "G";
                break;
            case 7:
                temp += "H";
                break;
            case 8:
                temp += "I";
                break;

        }
        temp += num_ROW;
        view.setText(temp);
    }

    private boolean CountDown(int time){
        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {

                txt_time.setText("0" + millisUntilFinished / 60000 + ":" + millisUntilFinished/1000);
            }

            public void onFinish() {
                yourTurn = false;
                CheckBluetooth();
                if(isBluetoothEnable == true){
                    StartingMenu.mConnection.sendMessage("timeout");
                }
            }
        }.start();

        return true;
    }



    private Handler mChessGameHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED) {
                        Toast.makeText(getApplication(), "Cre: Bạn Đã Mất Kết Nối Tới Phòng Chờ. Đang tiến hành kết nối lại...", Toast.LENGTH_SHORT).show();
                        if (Main.thisPlayer.isHost() == false) {
                            if (StartingMenu.mConnection != null && mBTadapter.isEnabled() == true) {
                                StartingMenu.mConnection.Reconnect();
                                if (StartingMenu.mConnection.mBTconnection.getState() == BluetoothConnectionService.STATE_CONNECTED) {
                                    Toast.makeText(getApplicationContext(), "Kết nối thành công!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Kết nối thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            StartingMenu.mConnection.StartConnection(mChessGameHandle);
                        }
                    }
                    break;
                case Messages.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);

                    break;
                case Messages.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                        try {
                            String[] mes = readMessage.split("&");
                            int possition = Integer.parseInt(mes[0]);
                            int oldpos = Integer.parseInt(mes[1]);
                            NearlyStep(possition, txt_EnemyStep);
                            if (checkIsMoveable(possition, oldpos)) {
                                if (Eatable) {
                                    EatEnemy(CellHasPieces.get(cellHasPiecesInt.indexOf(oldpos)), CellHasPieces.get(cellHasPiecesInt.indexOf(possition)));
                                    if (checkIsMoveable(posOfKing, possition) && Eatable == true) {
                                        Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                    }
                                    if (checkIsMoveable(posOfEnemyKing, possition) && Eatable == true) {
                                        Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    DrawPieces(possition, CellHasPieces.get(cellHasPiecesInt.indexOf(oldpos)));
                                    cellHasPiecesInt.set(cellHasPiecesInt.indexOf(oldpos), possition);
                                    DrawPieces(oldpos, null);
                                    oldpos = -1;
                                    if (checkIsMoveable(posOfKing, possition) && Eatable == true) {
                                        Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                    }
                                    if (checkIsMoveable(posOfEnemyKing, possition) && Eatable == true) {
                                        Toast.makeText(ChessActivity.this, "Chieu Cmn Tướng Rồi. AHIHI", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            yourTurn = true;

                        } catch (NumberFormatException e) {
                            if(readMessage.equals("pause")){
                                dialog.show();
                                dialog.setCancelable(false);
                            }
                            if(readMessage.equals("resume")){
                                dialog.dismiss();
                            }
                            if(readMessage.equals("timeout")){
                                CountDown(60000);
                                yourTurn = true;
                            }
                        }



                case Messages.MESSAGE_DEVICE_NAME:
                    break;

                case Messages.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Messages.TOAST),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReciever, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReciever);
    }



    private BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (BluetoothAdapter.STATE_OFF == state) {
                    Toast.makeText(context, "Bluetooth is Off. Return to Starting room", Toast.LENGTH_SHORT).show();
                    dialog.show();dialog.setCancelable(false);
                }
                else if(BluetoothAdapter.STATE_ON == state){
                    startActivity(new Intent(context, Main.class));
                }
            }
        }
    };
}
