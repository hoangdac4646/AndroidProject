package com.example.root.minigame.Sudoku;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.minigame.Sound.Click_button;
import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.Chat.Adapter_listview;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;
import com.example.root.minigame.Chat.SendMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuActivity extends AppCompatActivity implements CellGroupFragment.OnFragmentInteractionListener {
    int CountTime =0;
    ImageView Finnish,DauHang,TamDung,Sound;
    EditText Content;
    Button Gui;
    CountDownTimer countDownTimer;
    TextView Timer;
    ListView Chat;
    MediaPlayer mediaPlayer;
    int CheckHuy=0;
    Adapter_listview adapter_listview;
    List<SendMessage> SendMessages = new ArrayList<>();
    int DemChoiLai=0;
    private TextView clickedCell;
    private int clickedGroup;
    private int clickedCellId;
    private Board startBoard;
    private Board currentBoard;
    private int IndexFile;
    private int FileID;
    int Dem=0;
    public void RandomIndexFileAndFileID(){
        Random rand = new Random();
        IndexFile = rand.nextInt(3) + 1;
        if(IndexFile == 1){
            FileID = R.raw.b1;
        }else if(IndexFile == 2){
            FileID = R.raw.b2;
        }else if(IndexFile == 3){
            FileID = R.raw.b3;
        }
    }

    public void Init(){
        Finnish = findViewById(R.id.img_finish);
        Main.click_button = new Click_button(SudokuActivity.this);
        DauHang = findViewById(R.id.img_dauhang);
        TamDung = findViewById(R.id.img_pause);
        Sound = findViewById(R.id.img_Sound);
        Chat = findViewById(R.id.lv_sudokuChat);
        Timer = findViewById(R.id.tv_timer);
        Gui = findViewById(R.id.btn_sudokuGui);
        Content = findViewById(R.id.edt_sudokuContent);
        adapter_listview = new Adapter_listview(SudokuActivity.this,R.layout.custom_listview,SendMessages);
        Chat.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
    }
    private ArrayList<Board> readFileMapSudoku(int FileID) {
        ArrayList<Board> boards = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(FileID);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                Board board = new Board();
                for (int i = 0; i < 9; i++) {
                    String rowCells[] = line.split(" ");
                    for (int j = 0; j < 9; j++) {
                        if (rowCells[j].equals("-")) {
                            board.setValue(i, j, 0);
                        } else {
                            board.setValue(i, j, Integer.parseInt(rowCells[j]));
                        }
                    }
                    line = bufferedReader.readLine();
                }
                boards.add(board);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            Toast.makeText(this, "Không Thể Đọc FILE", Toast.LENGTH_SHORT).show();
        }
        return boards;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    private Board chooseRandomBoard(ArrayList<Board> boards) {
        int randomNumber = (int) (Math.random() * boards.size());
        return boards.get(randomNumber);
    }

    private boolean isStartPiece(int group, int cell) {
        int row = ((group-1)/3)*3 + (cell/3);
        int column = ((group-1)%3)*3 + ((cell)%3);
        return startBoard.getValue(row, column) != 0;
    }

    @Override
    public void onFragmentInteraction(int groupId, int cellId, View view) {
        clickedCell = (TextView) view;
        clickedGroup = groupId;
        clickedCellId = cellId;
        if (!isStartPiece(groupId, cellId)) {
            if(TextUtils.isEmpty(clickedCell.getText())){
                clickedCell.setText("1");
            }else{
                int value = Integer.parseInt(clickedCell.getText().toString()) + 1;
                if(value == 10){
                    value = 1;
                }
                clickedCell.setText(value + "");
            }
        } else {
            Toast.makeText(this, "Không Thể Thay Đổi", Toast.LENGTH_SHORT).show();
        }
    }

    public void VeMapSudoku(){
        ArrayList<Board> boards = readFileMapSudoku(FileID);
        startBoard = chooseRandomBoard(boards);
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());

        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            thisCellGroupFragment.setGroupId(i);
        }

        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                if (currentValue != 0) {
                    tempCellGroupFragment.setValue(groupPosition, currentValue);
                }
            }
        }

    }

    private boolean KiemTraDungTheoGroup() {
        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 0; i < 9; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i]);
            if (!thisCellGroupFragment.checkGroupCorrect()) {
                return false;
            }
        }
        return true;
    }

    //Kiem Tra Sudoku Đúng
    public boolean onKiemTraDung(View view) {
        currentBoard.isBoardCorrect();
        try {
            if(KiemTraDungTheoGroup() && currentBoard.isBoardCorrect()) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }

    public void onLayDapAn(View view){
        if(IndexFile == 1){
            FileID = R.raw.g1;
        }else if(IndexFile == 2){
            FileID = R.raw.g2;
        }else if(IndexFile == 3){
            FileID = R.raw.g3;
        }
        VeMapSudoku();
    }
    public void onClearMap(){
        ArrayList<Board> boards = readFileMapSudoku(R.raw.reset);
        startBoard = chooseRandomBoard(boards);
        currentBoard = new Board();
        currentBoard.copyValues(startBoard.getGameCells());

        int cellGroupFragments[] = new int[]{R.id.cellGroupFragment, R.id.cellGroupFragment2, R.id.cellGroupFragment3, R.id.cellGroupFragment4,
                R.id.cellGroupFragment5, R.id.cellGroupFragment6, R.id.cellGroupFragment7, R.id.cellGroupFragment8, R.id.cellGroupFragment9};
        for (int i = 1; i < 10; i++) {
            CellGroupFragment thisCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[i-1]);
            thisCellGroupFragment.setGroupId(i);
        }

        CellGroupFragment tempCellGroupFragment;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = j / 3;
                int row = i / 3;

                int fragmentNumber = (row * 3) + column;
                tempCellGroupFragment = (CellGroupFragment) getSupportFragmentManager().findFragmentById(cellGroupFragments[fragmentNumber]);
                int groupColumn = j % 3;
                int groupRow = i % 3;

                int groupPosition = (groupRow * 3) + groupColumn;
                int currentValue = currentBoard.getValue(i, j);

                tempCellGroupFragment.setValue(groupPosition, -1);
            }
        }
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED){
                        Toast.makeText(SudokuActivity.this, "Bạn Đã Bị Mất Kết Nối!!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Messages.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case Messages.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    StringBuilder stringBuilder = new StringBuilder();
                    //Gui tin nhan dang -1toi ten bao
                    if(readMessage.indexOf("-5")!=-1){
                        StringBuilder stringBuilder1  =new StringBuilder();
                        for(int i=2;i<readMessage.length();i++){
                            stringBuilder1.append(readMessage.charAt(i));
                        }
                        FileID = Integer.parseInt(stringBuilder1.toString());
                        VeMapSudoku();
                        StartingMenu.mConnection.sendMessage("-6"+FileID);
                    }else  if(readMessage.indexOf("-6")!=-1){
                        StringBuilder stringBuilder1  =new StringBuilder();
                        for(int i=2;i<readMessage.length();i++){
                            stringBuilder1.append(readMessage.charAt(i));
                        }
                        FileID = Integer.parseInt(stringBuilder1.toString());
                        VeMapSudoku();
                    }

                    if(readMessage.indexOf("-1")!=-1){
                        for(int i=2;i<readMessage.length();i++){
                            stringBuilder.append(readMessage.charAt(i));
                        }
                        SendMessages.add(new SendMessage(stringBuilder.toString(),"Đối thủ"));
                        adapter_listview.notifyDataSetChanged();
                    }
                    else {
                        //Doi thu xong gui ket qua
                        if (readMessage.toString().equals("Finish")) {
                            countDownTimer.cancel();
                            Toast.makeText(SudokuActivity.this, "You lose ! ", Toast.LENGTH_SHORT).show();
                        }

                        //Dau thu dau hang
                        else if (readMessage.toString().equals("Giveup")) {
                            countDownTimer.cancel();
                            Toast.makeText(SudokuActivity.this, "You win ! ", Toast.LENGTH_SHORT).show();
                        }
                        //Tam dung 5s
                        else if(readMessage.toString().equals("pause")){
                            final Dialog dialog = new Dialog(SudokuActivity.this);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            countDownTimer.cancel();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    countDownTimer.start();
                                }
                            }, 5000);
                        }
                        else if(readMessage.equals("HuyChoiMoi")){
                            Toast.makeText(SudokuActivity.this, "Đối thủ đã thoát ! ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                        }else if(readMessage.equals("ChoiMoi")){
                            AlertDialog.Builder builder = new AlertDialog.Builder(SudokuActivity.this);
                            builder.setTitle("You win ! ");
                            builder.setMessage("Bạn có muốn chơi lại không ? ");
                            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Main.click_button.PlayButtonSound();
                                    Toast.makeText(SudokuActivity.this, "Choi moi ne haha", Toast.LENGTH_SHORT).show();
                                    RandomIndexFileAndFileID();
                                    onNewMap();
                                    StartingMenu.mConnection.sendMessage("-2"+FileID);

                                }
                            });
                            builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Main.click_button.PlayButtonSound();
                                    StartingMenu.mConnection.sendMessage("HuyChoiLai2");
                                    startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                                }
                            });
                            builder.create().show();
                        }else if(readMessage.indexOf("-2")!=-1){
                            Toast.makeText(SudokuActivity.this, "Choi moi ne", Toast.LENGTH_SHORT).show();
                            StringBuilder stringBuilder1  =new StringBuilder();
                            for(int i=2;i<readMessage.length();i++){
                                stringBuilder1.append(readMessage.charAt(i));
                            }
                            FileID = Integer.parseInt(stringBuilder1.toString());
                            onNewMap();
                        }else if(readMessage.equals("HuyChoiLai2")){
                            startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                        }

                    }
                    break;
                case Messages.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(Messages.DEVICE_NAME);

                    break;
                case Messages.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Messages.TOAST),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };
    //=========================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        Init();

        //Load nhac
        stopService(Main.backgroundMusic);
        mediaPlayer = MediaPlayer.create(SudokuActivity.this,R.raw.background_music2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();



        StartingMenu.mConnection.setHandle(mHandler);
        countDownTimer  =new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                CountTime++;
                Timer.setText(String.format("%02d : %02d", CountTime/60 , (CountTime % (60))));
            }
            @Override
            public void onFinish() {
                this.start();
            }
        };
        countDownTimer.start();
        Finnish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
                if(onKiemTraDung(v) ==true){
                    StartingMenu.mConnection.sendMessage("Finish");
                    DauHang.setFocusable(false);
                    countDownTimer.cancel();
                    Toast.makeText(SudokuActivity.this, "You lose ! ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(SudokuActivity.this);
                    builder.setTitle("You lose !");
                    builder.setMessage("Bạn có muốn chơi lại không ? ");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StartingMenu.mConnection.sendMessage("ChoiMoi");
                            CheckHuy=1;
                        }
                    });
                    builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StartingMenu.mConnection.sendMessage("HuyChoiMoi");
                        }
                    });
                    builder.create().show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(CheckHuy==0){
                                StartingMenu.mConnection.sendMessage("HuyChoiMoi");
                                startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                            }
                            // Do something after 5s = 5000ms

                        }
                    }, 5000);

                }
                else{
                    Toast.makeText(SudokuActivity.this, "Sai dap an !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        DauHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
                onLayDapAn(v);
                StartingMenu.mConnection.sendMessage("Giveup");
                Toast.makeText(SudokuActivity.this, "You lose ! ", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(SudokuActivity.this);
                builder.setTitle("You lose !");
                builder.setMessage("Bạn có muốn chơi lại không ? Tự hủy sau 5s ! ");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Main.click_button.PlayButtonSound();
                        StartingMenu.mConnection.sendMessage("ChoiMoi");
                        CheckHuy=1;

                    }
                });
                builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Main.click_button.PlayButtonSound();
                        StartingMenu.mConnection.sendMessage("HuyChoiMoi");
                        startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                    }
                });
                builder.create().show();
                countDownTimer.cancel();
               /*  final Handler handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         if(CheckHuy==0){
                             StartingMenu.mConnection.sendMessage("HuyChoiMoi");
                             startActivity(new Intent(SudokuActivity.this,StartingMenu.class));
                         }
                         // Do something after 5s = 5000ms

                     }
                 }, 5000);*/

            }
        });
        Gui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
                String D ="-1";
                D +=Content.getText().toString();
                StartingMenu.mConnection.sendMessage(D);
                SendMessages.add(new SendMessage(Content.getText().toString(),"Me"));
                adapter_listview.notifyDataSetChanged();
                Content.setText("");
            }
        });
        TamDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
                StartingMenu.mConnection.sendMessage("pause");
                final Handler handler = new Handler();
                final Dialog dialog  =new Dialog(SudokuActivity.this);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                countDownTimer.cancel();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        dialog.cancel();
                        countDownTimer.start();
                    }
                }, 5000);

            }
        });
        RandomIndexFileAndFileID();
        String D = "-5"+FileID;
        StartingMenu.mConnection.sendMessage(D);
    }
    public void onNewMap(){
        CountTime=0;
        countDownTimer.cancel();
        countDownTimer.start();
        onClearMap();
        VeMapSudoku();
    }

}
