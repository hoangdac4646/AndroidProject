package com.example.root.minigame.Chess;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.minigame.Activities.CreatingRoom;
import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Caro.CaroActivity;
import com.example.root.minigame.Chat.Adapter_listview;
import com.example.root.minigame.Chat.SendMessage;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.Sound.Click_button;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class ChessActivity extends AppCompatActivity {

    private TableLayout chess_broad;
    private LinearLayout linear_row;
    private final int MAX_ROW = 8;
    private final int MAX_COL = 8;
    private boolean chessmanIsClicked = false;
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
    private int REQUEST_CODE_ENABLE_BLUETOOTH = 1111111;
    private boolean myColor = true , EnemyColor = false; // mau trang
    private Button btn_pause, btn_sound, btn_chat;
    private TextView txt_MyStep, txt_EnemyStep , txt_p1Name, txt_p2Name, txt_time;
    private Dialog dialog;
    private int Countdown = 30;
    private CountDownTimer countDownTimer;
    private ArrayList<Integer> theWay = new ArrayList<>();
    private ArrayList<Integer> stepToOurKing = new ArrayList<>();
    private boolean isCheckMate = false;
    private int posCheckUs;
    private Cells King;
    private Click_button sound;

    //chat
    android.app.AlertDialog.Builder dialogBuilder;
    android.app.AlertDialog alertDialog;
    View dialogView;
    Adapter_listview adapter_listview;
    private List<SendMessage> SendMessages = new ArrayList<>();
    ListView message;
    EditText editText;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        if(StartingMenu.mConnection != null){
            StartingMenu.mConnection.setHandle(mChessGameHandle);
        }
        inItWork();
        Main.click_button = new Click_button(ChessActivity.this);
        initMap();
        AddArrayForCheck();
        if (Main.thisPlayer.isHost()) {
            myColor = true;
            EnemyColor = false;
        }else{
            myColor = false;
            EnemyColor = true;
        }
        for(int i = 0 ; i < CellHasPieces.size(); i++){
            if(CellHasPieces.get(i).getChessPieces() == KING && CellHasPieces.get(i).getColor() == myColor){
                posOfKing = CellHasPieces.get(i).getIndex();
                King = CellHasPieces.get(i);
            }
            if(CellHasPieces.get(i).getChessPieces() == KING && CellHasPieces.get(i).getColor() == EnemyColor){
                posOfEnemyKing = CellHasPieces.get(i).getIndex();
            }
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
        //chat
        dialogBuilder  = new android.app.AlertDialog.Builder(ChessActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.custom_chatdialog, null);
        message  =dialogView.findViewById(R.id.lv_noidungnhan);
        adapter_listview = new Adapter_listview(ChessActivity.this,R.layout.custom_listview,SendMessages);
        message.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
        dialogBuilder .setView(dialogView);
        alertDialog = dialogBuilder.create();

        dialog = new Dialog(ChessActivity.this);
        txt_p1Name.setText(Main.thisPlayer.getPlayerName());
        linear_row = new LinearLayout(this);
        linear_row.setWeightSum(8f);
        txt_p2Name.setText(CreatingRoom.enemyPlayer.getPlayerName());
        linear_row.setOrientation(LinearLayout.VERTICAL);
        linear_row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chess_broad.addView(linear_row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if(Main.thisPlayer.isHost()){
            yourTurn = true;
        }
        sound = new Click_button(getApplicationContext());
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound.PlayButtonSound();
                final Dialog mdialog = new Dialog(ChessActivity.this);
                mdialog.show();
                CheckBluetooth();
                if(isBluetoothEnable == true){
                    StartingMenu.mConnection.sendMessage("pause");
                }
                final CountDownTimer pausecount = new CountDownTimer(30000,1000){
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        CheckBluetooth();
                        if(isBluetoothEnable == true){
                            StartingMenu.mConnection.sendMessage("resume");
                            mdialog.dismiss();
                        }
                    }
                }.start();
                mdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mdialog.dismiss();
                        CheckBluetooth();
                        if(isBluetoothEnable == true){
                            StartingMenu.mConnection.sendMessage("resume");
                            pausecount.cancel();
                        }
                    }
                });
            }
        });

        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Countdown--;
                txt_time.setText(String.format("00 : %02d", Countdown));
            }
            @Override
            public void onFinish() {
                if(yourTurn == true){
                    Countdown = 30;
                    CheckBluetooth();
                    if(mBTadapter.isEnabled()){
                        StartingMenu.mConnection.sendMessage("timeout");
                        Toast.makeText(ChessActivity.this, "Hết thời gian", Toast.LENGTH_SHORT).show();

                    }
                    yourTurn = false;
                }
                this.start();


            }
        };
        countDownTimer.start();
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound.PlayButtonSound();
                Button Gui2 = dialogView.findViewById(R.id.btn_SendMessage);
                editText = dialogView.findViewById(R.id.tv_noidungCaro);
                Gui2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String D = "-8";
                        D += editText.getText().toString();
                        SendMessages.add(new SendMessage(editText.getText().toString(), "Me"));
                        adapter_listview.notifyDataSetChanged();
                        editText.setText("");
                        StartingMenu.mConnection.sendMessage(D);
                    }
                });
                alertDialog.show();

                // chat
            }
        });
        btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sound.PlayButtonSound();
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
            DrawPieces(CellHasPieces.get(i).getIndex(), CellHasPieces.get(i));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initMap() {
        for (int i = 0; i < MAX_ROW; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1F);
            row.setLayoutParams(layoutParams);
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
                        if (yourTurn == true) {
                            if (!checkIsEnemy(mbutton.getId(), mOldIndex)) {
                                isChessPiecesChecked(mbutton.getId());
                            }
                            if (chessmanIsClicked == false) { // o trong vua dc click
                                hideTheWay();
                                if (checkIsMoveable(mbutton.getId(), mOldIndex)) {

                                    if (isBluetoothEnable == true) {
                                        NearlyStep(mbutton.getId(), txt_MyStep );
                                        String temp = mbutton.getId() + "&" + mOldIndex;
                                        StartingMenu.mConnection.sendMessage(temp);
                                        yourTurn = false;
                                    }
                                    if (Eatable) {
                                        EatEnemy(CellHasPieces.get(cellHasPiecesInt.indexOf(mOldIndex)), CellHasPieces.get(cellHasPiecesInt.indexOf(mbutton.getId())));
                                        if(stepToOurKing.indexOf(mbutton.getId()) != -1){
                                            isCheckMate = false;
                                            stepToOurKing.clear();
                                        }
                                        if (checkIsMoveable(posOfEnemyKing, mbutton.getId()) && Eatable == true) {
                                            sound.check_mate();
                                            Toast.makeText(ChessActivity.this, "Chiếu Tướng", Toast.LENGTH_SHORT).show();

                                        }
                                        sound.chess_move();
                                    } else {
                                        DrawPieces(mbutton.getId(), CellHasPieces.get(cellHasPiecesInt.indexOf(mOldIndex)));
                                        cellHasPiecesInt.set(cellHasPiecesInt.indexOf(mOldIndex), mbutton.getId());
                                        if(mOldIndex == posOfKing){
                                            posOfKing = mbutton.getId();
                                        }
                                        DrawPieces(mOldIndex, null);
                                        mOldIndex = -1;
                                        isCheckMate = false;
                                        if(stepToOurKing.indexOf(mbutton.getId()) != -1){
                                            isCheckMate = false;
                                            stepToOurKing.clear();
                                        }
                                        if (checkIsMoveable(posOfEnemyKing, mbutton.getId()) && Eatable == true) {
                                            sound.check_mate();
                                            Toast.makeText(ChessActivity.this, "Chiếu Tướng", Toast.LENGTH_SHORT).show();
                                        }
                                        sound.chess_move();
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
            if (CellHasPieces.get(cellHasPiecesInt.indexOf(pos)).getColor() != CellHasPieces.get(cellHasPiecesInt.indexOf(posofpieces)).getColor()) {
                chessmanIsClicked = false;
                return true;
            }
        }
        return false;
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void DrawPieces(int position, Cells mcell) {
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

    private void checkIsWin(Cells mcelled) {
        if (mcelled.getChessPieces() == KING) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (EnemyColor == mcelled.getColor()) {
                isWin = true;
                builder.setMessage("Xin chúc mừng "+ Main.thisPlayer.getPlayerName() +"! Quân của bạn đã chiến thắng\n Bạn Có Muốn Chơi Lại không?");
                MediaPlayer.create(this, R.raw.victory).start();
            } else {
                MediaPlayer.create(this, R.raw.defeat).start();
                builder.setMessage("Rất Tiếc "+ Main.thisPlayer.getPlayerName() +"! Quân của bạn đã thua\n Bạn Có Muốn Chơi Lại không?");
            }
            countDownTimer.cancel();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckBluetooth();
                    if(isBluetoothEnable){
                        StartingMenu.mConnection.sendMessage("restart");
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckBluetooth();
                    if(isBluetoothEnable){
                        StartingMenu.mConnection.sendMessage("quit");
                    }
                    finish();
                }
            });
            builder.show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void EatEnemy(Cells mcell, Cells mcelled) {
        Eatable = false;

        checkIsWin(mcelled);
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
        if(isCheckMate && posOfChessPiece != posOfKing){
            if(stepToOurKing.indexOf(position) == -1){
                return false;
            }
        }

        int num_ROW = position / MAX_ROW;
        int num_COL = position - num_ROW * MAX_COL;
        int num_ROW_pieces = posOfChessPiece / MAX_ROW;
        int num_COL_pieces = posOfChessPiece - num_ROW_pieces * MAX_COL;

        int compare_Row, compare_Col;
        if(num_ROW > num_ROW_pieces) compare_Row = 1;
        else compare_Row = -1;

        int compare;

        if(position > posOfChessPiece) compare = 1;
        else compare = -1;

        if(num_COL > num_COL_pieces) compare_Col = 1;
        else compare_Col = -1;

        for (int i = 0; i < CellHasPieces.size(); i++) {
            if (posOfChessPiece == CellHasPieces.get(i).getIndex()) {
                Cells mcell = CellHasPieces.get(i);
                if(isMyTeamMate(position, mcell.getColor())){
                    return false;
                }
                switch (mcell.getChessPieces()) {
                    case PAWN: {
                        if (mcell.getColor()) {// pawn have to go straight
                            compare_Row = -1;// white
                        } else {
                            compare_Row = 1;//black
                        }
                        if(num_ROW_pieces + 1 * compare_Row == num_ROW && num_COL_pieces + 1*compare_Col == num_COL){
                            if(position == posOfKing && isCheckMate){
                                stepToOurKing.add(position);
                            }
                            if (checkIsEnemy(posOfChessPiece, position)) {
                                if (cellHasPiecesInt.indexOf(position) != -1) {
                                    Eatable = true;
                                    return true;
                                }
                            }
                        }
                        if ((num_ROW_pieces + 1 * compare_Row == num_ROW || (num_ROW_pieces + 2 * compare_Row == num_ROW && num_ROW * compare_Row <= 4 * compare_Row)) && num_COL == num_COL_pieces) {// eat enemy
                            for (int k = 1; k <= (position - posOfChessPiece) * compare_Row / MAX_ROW; k++) {// check is our checkpiece is being blocked
                                if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * k * compare_Row) != -1) {
                                    Eatable = false;
                                    return false;
                                }
                            }
                            Eatable = false;
                            return true;
                        }

                        break;
                    }//pawn
                    case KNIGHT: {
                        if((num_ROW_pieces + 2*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col == num_COL) ||
                                (num_ROW_pieces + 1*compare_Row == num_ROW && num_COL_pieces + 2*compare_Col == num_COL)) {

                            if (checkIsEnemy(posOfChessPiece, position)) {
                                if (cellHasPiecesInt.indexOf(position) != -1) {
                                    if(position == posOfKing && isCheckMate){
                                        stepToOurKing.add(position);
                                    }
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
                        for (int j = 1; j <= MAX_COL; j++) {
                            int positonforcheck = posOfChessPiece + j*compare_Col;//check if on the way castle move has teammate then return
                            if (isMyTeamMate(positonforcheck, mcell.getColor())) {
                                break;
                            }
                            if (num_COL_pieces + j * compare_Col == num_COL && num_ROW == num_ROW_pieces) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare_Col &&
                                        (position - posOfChessPiece) * compare_Col % MAX_ROW != 0; k++) {

                                    if(posOfChessPiece + k * compare_Col == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare_Col) != -1) {
                                        Eatable = false;
                                        return false;
                                    }
                                }

                                Eatable = false;
                                return true;
                            }
                        }
                        for(int j = 1; j <= MAX_ROW ; j++) {
                            int positonforcheck = posOfChessPiece + j*MAX_ROW*compare_Row;//check if on the way castle move
                            if (isMyTeamMate(positonforcheck, mcell.getColor())) {
                                return false;
                            }
                            if (num_ROW_pieces + j * compare_Row == num_ROW && num_COL == num_COL_pieces) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare_Row / MAX_ROW &&
                                        (position - posOfChessPiece) * compare_Row % MAX_ROW == 0; k++) {
                                    if (posOfChessPiece + MAX_ROW * compare_Row * k == position) {
                                        if (cellHasPiecesInt.indexOf(position) != -1) {
                                            Eatable = true;
                                            return true;
                                        }
                                    }

                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * compare_Row * k) != -1) {// check is our checkpiece is being blocked
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
                        for(int j = 1; j <= MAX_ROW ; j++) {
                            int posforcheck = posOfChessPiece + j * compare_Row * MAX_ROW + 1 * compare_Col * j;
                            if (isMyTeamMate(posforcheck, mcell.getColor())) {
                                return false;
                            }
                            if(num_ROW_pieces + j*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col * j == num_COL) {


                                for (int k = 1; k <= (position - posOfChessPiece)*compare /(MAX_ROW - 1*compare_Row) && (position - posOfChessPiece) * compare % (MAX_ROW - 1*compare_Row) == 0; k++) {

                                    if(posOfChessPiece + k * compare_Row * MAX_ROW + 1 * compare_Col * k == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare_Row * MAX_ROW + 1 * compare_Col * k) != -1) {
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
                        for (int j = 1; j <= MAX_COL; j++) {
                            int positonforcheck = posOfChessPiece + j*compare_Col;//check if on the way castle move has teammate then return
                            if (isMyTeamMate(positonforcheck, mcell.getColor())) {
                                break;
                            }

                            if (num_COL_pieces + j * compare_Col == num_COL && num_ROW == num_ROW_pieces) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare_Col &&
                                        (position - posOfChessPiece) * compare_Col % MAX_ROW != 0; k++) {

                                    if(posOfChessPiece + k * compare_Col == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare_Col) != -1) {
                                        Eatable = false;
                                        return false;
                                    }
                                }
                                Eatable = false;
                                return true;
                            }
                        }

                        for(int j = 1; j <= MAX_ROW ; j++) {
                            int positonforcheck = posOfChessPiece + j*MAX_ROW*compare_Row;//check if on the way castle move
                            if (isMyTeamMate(positonforcheck, mcell.getColor())) {
                                break;
                            }
                            if (num_ROW_pieces + j * compare_Row == num_ROW && num_COL == num_COL_pieces) {
                                for (int k = 1; k <= (position - posOfChessPiece) * compare_Row / MAX_ROW &&
                                        (position - posOfChessPiece) * compare_Row % MAX_ROW == 0; k++) {
                                    if (posOfChessPiece + MAX_ROW * compare_Row * k == position) {
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + MAX_ROW * compare_Row * k) != -1) {// check is our checkpiece is being blocked
                                        return false;
                                    }
                                }
                                Eatable = false;
                                return true;
                            }
                        }//like castle
                        for(int j = 1; j <= MAX_ROW ; j++) {
                            int posforcheck = posOfChessPiece + j * compare_Row * MAX_ROW + 1 * compare_Col * j;
                            if (isMyTeamMate(posforcheck, mcell.getColor())) {
                                return false;
                            }
                            if(num_ROW_pieces + j*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col * j == num_COL) {


                                for (int k = 1; k <= (position - posOfChessPiece)*compare /(MAX_ROW - 1*compare_Row) && (position - posOfChessPiece) * compare % (MAX_ROW - 1*compare_Row) == 0; k++) {

                                    if(posOfChessPiece + k * compare_Row * MAX_ROW + 1 * compare_Col * k == position){
                                        if (checkIsEnemy(posOfChessPiece, position)) {
                                            if (cellHasPiecesInt.indexOf(position) != -1) {
                                                Eatable = true;
                                                return true;
                                            }
                                        }
                                    }
                                    if (cellHasPiecesInt.indexOf(posOfChessPiece + k * compare_Row * MAX_ROW + 1 * compare_Col * k) != -1) {
                                        return false;
                                    }

                                }
                                Eatable = false;
                                return true;
                            }
                        }

                        break;
                    }//queen
                    case KING: {
                        if ((num_ROW_pieces + 1 * compare_Row == num_ROW && num_COL_pieces == num_COL)||
                                (num_COL_pieces + 1*compare_Col == num_COL && num_ROW_pieces == num_ROW) ||
                                (num_ROW_pieces + 1*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col == num_COL)) {
                            if(isCheckMate){
                                if(stepToOurKing.indexOf(position) != -1){
                                    return false;
                                }
                            }
                            if (isMyTeamMate(position, mcell.getColor())) {
                                return false;
                            }
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
                            return true;
                        }
                        break;
                    }//king

                }//switch
                return false;
            }//if has cell
        }//for
        return false;
    }//checkismoveablec wdwdwdwdwdwdwdwdwd

    public boolean isMyTeamMate(int pos, boolean color){
        if(cellHasPiecesInt.indexOf(pos) != -1 && color == CellHasPieces.get(cellHasPiecesInt.indexOf(pos)).getColor()) {
            return true;
        }
        return false;
    }



    public boolean isChessPiecesChecked(int pos){
        if(cellHasPiecesInt.indexOf(pos) != -1 && CellHasPieces.get(cellHasPiecesInt.indexOf(pos)).getColor() == myColor ){
            hideTheWay();
            showTheWay(pos);
            chessmanIsClicked = true;
            mOldIndex = pos;
            return true;
        }
        chessmanIsClicked = false;
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showTheWay(int pos) {

        for(int i = 0; i < MAX_COL* MAX_ROW; i++){
            if(checkIsMoveable(i, pos) && i != pos){
                int num_ROW = i / MAX_ROW;
                int num_COL = i - num_ROW * MAX_COL;
                LinearLayout row = (LinearLayout) linear_row.getChildAt(num_ROW);
                ImageView mButton = (ImageView) row.getChildAt(num_COL);
                mButton.setAlpha(0.5F);
                if(Eatable){
                    mButton.setBackgroundColor(Color.parseColor("#FFFF6A5F"));
                }else{
                    if(checkIsMoveable(posOfKing , i)){
                        if(stepToOurKing.indexOf(i) != -1){
                            mButton.setBackgroundColor(Color.parseColor("#00FF7F"));
                        }
                    }
                    else{
                        mButton.setBackgroundColor(Color.parseColor("#00FF7F"));
                    }
                }
                theWay.add(i);
            }
        }
    }//dayyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void hideTheWay(){
        for(int i = 0; i < theWay.size();i++ ){
            int num_ROW = theWay.get(i) / MAX_ROW;
            int num_COL = theWay.get(i) - num_ROW * MAX_COL;
            LinearLayout row = (LinearLayout) linear_row.getChildAt(num_ROW);
            ImageView mButton = (ImageView) row.getChildAt(num_COL);
            mButton.setAlpha(1F);
            mButton.setBackground(null);
        }
    }

    //xuat ra buoc đi gần nhất
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
        temp += (num_ROW +1);
        view.setText(temp);
    }

    private boolean checkmate(Cells mcell) {

        if (posOfKing == -1) {
            return false;
        }

        int position = mcell.getIndex();

        int num_ROW = posOfKing / MAX_ROW;
        int num_COL = posOfKing - num_ROW * MAX_COL;
        int num_ROW_pieces = position / MAX_ROW;
        int num_COL_pieces = position - num_ROW_pieces * MAX_COL;

        boolean check_Row = num_ROW > num_ROW_pieces ? true : false;
        boolean check_Col = num_COL > num_COL_pieces ? true : false;
        int compare_Row, compare_Col;
        if(check_Row) compare_Row = 1;
        else compare_Row = -1;
        int compare = 1;
        if(check_Col) compare_Col = 1;
        else compare_Col = -1;
        switch (mcell.getChessPieces()) {
            case PAWN:
                stepToOurKing.add(position);
                return true;
            case KNIGHT:
                stepToOurKing.add(position);
                break;
            case CASTLE:

                for (int j = 0; j < MAX_COL ; j++) {
                    if(num_COL_pieces + j * compare_Col == num_COL && num_ROW == num_ROW_pieces){
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + k * compare_Col + num_ROW_pieces*MAX_ROW);
                        }
                        return true;
                    }
                }
                for (int j = 0; j < MAX_ROW ; j++) {
                    if(num_ROW_pieces + j * compare_Row == num_ROW && num_COL == num_COL_pieces){
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + (num_ROW_pieces + k * compare_Row)*MAX_ROW);
                        }
                        return true;
                    }
                }
                break;
            case BISHOP:
                for (int j = 0; j < MAX_ROW; j++) {
                    if(num_ROW_pieces + j*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col * j == num_COL) {
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + 1*compare_Col * k + (num_ROW_pieces + k*compare_Row)* MAX_ROW);
                        }
                        return true;
                    }
                }

                break;
            case QUEEN:
                for (int j = 0; j < MAX_COL ; j++) {
                    if(num_COL_pieces + j * compare_Col == num_COL && num_ROW == num_ROW_pieces){
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + k * compare_Col + num_ROW_pieces*MAX_ROW);
                        }
                        return true;
                    }
                }
                for (int j = 0; j < MAX_ROW ; j++) {
                    if(num_ROW_pieces + j * compare_Row == num_ROW && num_COL == num_COL_pieces){
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + (num_ROW_pieces + k * compare_Row)*MAX_ROW);
                        }
                        return true;
                    }
                }
                for (int j = 0; j < MAX_ROW; j++) {
                    if(num_ROW_pieces + j*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col * j == num_COL) {
                        for(int k = 0 ; k < j ; k++){
                            stepToOurKing.add(num_COL_pieces + 1*compare_Col * k + (num_ROW_pieces + k*compare_Row)* MAX_ROW);
                        }
                        return true;
                    }
                }
                break;
            case KING:
                if ((num_ROW_pieces + 1 * compare_Row == num_ROW && num_COL_pieces == num_COL)||
                        (num_COL_pieces + 1*compare_Col == num_COL && num_ROW_pieces == num_ROW) ||
                        (num_ROW_pieces + 1*compare_Row == num_ROW && num_COL_pieces + 1*compare_Col == num_COL)) {
                    stepToOurKing.add(position);
                    return true;
                }
                break;
        }


        return false;
    }





    private Handler mChessGameHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED) {
                        Toast.makeText(getApplication(), "Chess: Bạn Đã Mất Kết Nối Tới Phòng Chờ.", Toast.LENGTH_SHORT).show();
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
                                    sound.check_mate();
                                    Toast.makeText(ChessActivity.this, "Chiếu Tướng", Toast.LENGTH_SHORT).show();
                                    isCheckMate = true;
                                    checkmate(CellHasPieces.get(cellHasPiecesInt.indexOf(possition)));
                                }
                            } else {
                                DrawPieces(possition, CellHasPieces.get(cellHasPiecesInt.indexOf(oldpos)));
                                cellHasPiecesInt.set(cellHasPiecesInt.indexOf(oldpos), possition);
                                DrawPieces(oldpos, null);
                                if (checkIsMoveable(posOfKing, possition) && Eatable == true) {
                                    sound.check_mate();
                                    Toast.makeText(ChessActivity.this, "Chiếu Tướng", Toast.LENGTH_SHORT).show();
                                    isCheckMate = true;
                                    checkmate(CellHasPieces.get(cellHasPiecesInt.indexOf(possition)));
                                }
                            }
                        }
                        yourTurn = true;
                        countDownTimer.start();
                        Countdown = 30;

                    } catch (NumberFormatException e) {
                        if(readMessage.equals("pause")){
                            countDownTimer.cancel();
                            dialog.show();
                            dialog.setCancelable(false);
                        }
                        else if(readMessage.equals("resume")){
                            countDownTimer.start();
                            dialog.dismiss();
                        }
                        else if(readMessage.equals("timeout")){
                            Countdown = 30;
                            yourTurn = true;
                        }
                        else if(readMessage.equals("restart")){

                        }
                        else if(readMessage.equals("quit")){
                            Toast.makeText(ChessActivity.this, CreatingRoom.enemyPlayer.getPlayerName() + " đã thoát game!", Toast.LENGTH_SHORT).show();
                        }
                        else if(readMessage.indexOf("-8") != -1){
                            StringBuilder stringBuilderRead = new StringBuilder();
                            for(int i=2;i<readMessage.length();i++){
                                stringBuilderRead.append(readMessage.charAt(i));
                            }
                            SendMessages.add(new SendMessage(stringBuilderRead.toString(),"Đối thủ"));
                            adapter_listview.notifyDataSetChanged();
                        }
                        //tin nhan day neeeeeeeeeeeeeeeeee ^^
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
        countDownTimer.cancel();
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
