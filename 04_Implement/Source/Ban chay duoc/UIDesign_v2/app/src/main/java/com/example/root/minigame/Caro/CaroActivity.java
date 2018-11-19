package com.example.root.minigame.Caro;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.Chat.Adapter_listview;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;
import com.example.root.minigame.Chat.SendMessage;

import java.util.ArrayList;
import java.util.List;


public class CaroActivity extends AppCompatActivity {
    int Index = 1;
    int MAXROW;
    EditText editText;
    int MAXCOLUM;
    ListOCaro listOCaro;
    TableLayout mtableLayout;
    HorizontalScrollView hsvBanCo;
    ScrollView svBanCo;
    TextView lblCountTime;
    String NamePlayer1, NamePlayer2;
    int SoLuotPlayer1, SoLoutPlayer2;
    int SumTimePlayer1, SumTimePlayer2;
    int TimeDown, CountTime;
    TextView lblSumLuotPlayer1, lblSumLuotPlayer2;
    TextView lblSumTimePlayer1, lblSumTimePlayer2;
    CountDownTimer countDownTimer;
    Boolean CheckDuocDanh;
    Button ChatCaro;
    EditText NoiDungChat;
    private List<SendMessage> SendMessages = new ArrayList<>();
    ListView message;
    android.app.AlertDialog.Builder dialogBuilder;
    android.app.AlertDialog alertDialog;
    View dialogView;
    Adapter_listview adapter_listview;
    void AnhXa(){

        ChatCaro = findViewById(R.id.btn_chatCaro);

        dialogBuilder  = new android.app.AlertDialog.Builder(CaroActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.custom_chatdialog, null);
        message  =dialogView.findViewById(R.id.lv_noidungnhan);
        adapter_listview = new Adapter_listview(CaroActivity.this,R.layout.custom_listview,SendMessages);
        message.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
        dialogBuilder .setView(dialogView);
        alertDialog = dialogBuilder.create();
        MAXROW = 20;
        MAXCOLUM = 20;
        mtableLayout = findViewById(R.id.tbBanCo);
        hsvBanCo = findViewById(R.id.hsvBanCo);
        svBanCo = findViewById(R.id.svBanCo);
        listOCaro = new ListOCaro();
        lblCountTime = findViewById(R.id.lblCountTime);
        lblSumLuotPlayer1 = findViewById(R.id.lblSumLuotPlayer1);
        lblSumLuotPlayer2 = findViewById(R.id.lblSumLuotPlayer2);
        lblSumTimePlayer1 = findViewById(R.id.lblSumTimePlayer1);
        lblSumTimePlayer2 = findViewById(R.id.lblSumTimePlayer2);

        if(Main.thisPlayer.isHost()){
            CheckDuocDanh = true;
            Index = 0;
        }else{
            CheckDuocDanh = false;
            Index = 1;
        }

        NamePlayer1 = "";
        NamePlayer2 = "";
        SoLuotPlayer1 = 0;
        SoLoutPlayer2 = 0;
        SumTimePlayer1 = 0;
        SumTimePlayer2 = 0;
        TimeDown = 31000;
        CountTime = TimeDown;
        lblCountTime.setText(String.format("%02d : %02d",(CountTime % 1000 * 60) / 1000, CountTime / 1000));
        StartingMenu.mConnection.setHandle(mHandler);
    }

    void TinhThoiGianVaLuotDi(int TinhTheo){
        if(TinhTheo == 0){ // X
            ++SoLuotPlayer1;
            lblSumLuotPlayer1.setText("SỐ NƯỚC ĐI: " + SoLuotPlayer1);
            SumTimePlayer1 += TimeDown - CountTime;
            lblSumTimePlayer1.setText(String.format("%dM:%02dS", SumTimePlayer1 / (60 * 1000), (SumTimePlayer1 % (60 * 1000)) / 1000));
        }else{ // O
            ++SoLoutPlayer2;
            lblSumLuotPlayer2.setText("SỐ NƯỚC ĐI: " + SoLoutPlayer2);
            SumTimePlayer2 += TimeDown - CountTime;
            lblSumTimePlayer2.setText(String.format("%dM:%02dS", SumTimePlayer2 / (60 * 1000), (SumTimePlayer2 % (60 * 1000)) / 1000));
        }
        //Index++;
        countDownTimer.cancel();
        CountTime = TimeDown;
        countDownTimer.start();
    }

    void VeBanCo() {
        for(int i = 0; i < MAXROW ; i++){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            for(int j = 0 ; j < MAXCOLUM ; j++){
                final OCaro element = new OCaro(new ImageView(this), ' ');
                //final ImageView img = new ImageView(this);
                element.getSrc().setLayoutParams(new TableRow.LayoutParams(50, 50));
                element.getSrc().setId(i * MAXROW + j);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    element.getSrc().setBackground(ContextCompat.getDrawable(this,R.drawable.cua_caro_item));
                }
                row.addView(element.getSrc());
                final int finalI = i;
                final int finalJ = j;
                element.getSrc().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Kiem tra Duoc Đánh chưa
                        if(element.getValue() != ' ' || CheckDuocDanh == false){
                            return;
                        }
                        if(Index % 2 == 0){;
                            element.getSrc().setImageResource(R.drawable.red_x);
                            element.setValue('X');
                            TinhThoiGianVaLuotDi(Index);
                        } else{
                            element.getSrc().setImageResource(R.drawable.green_o);
                            element.setValue('O');
                            TinhThoiGianVaLuotDi(Index);
                        }
                        StartingMenu.mConnection.sendMessage("" + ((finalI * MAXROW) + finalJ));
                        Caro caro = new Caro(listOCaro.getString(), MAXROW, MAXCOLUM);
                        //Kiem Tra Chien Thang
                        if((Index) % 2 == 0){;
                            if(caro.KiemTraChienThang('X', finalI, finalJ)){
                                ThongBaoChienThang("Chúc Mùng", "Bạn Đã Chiến Thắng");
                                StartingMenu.mConnection.sendMessage("ClientLost");
                            }
                        } else{
                            if(caro.KiemTraChienThang('O', finalI, finalJ)){
                                ThongBaoChienThang("Chúc Mùng", "Bạn Đã Chiến Thắng");
                                StartingMenu.mConnection.sendMessage("HostLost");
                            }
                        }
                    }
                });
                listOCaro.Add(element);
            }
            mtableLayout.addView(row, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }
    }


    void ResetBanCo()
    {
        SumTimePlayer1 = SumTimePlayer2 = 0;
        SoLuotPlayer1 = SoLoutPlayer2 = 0;
        for(int i = 0; i < listOCaro.Count(); ++i){
            listOCaro.getoCaroList().get(i).getSrc().setImageResource(0);
            listOCaro.getoCaroList().get(i).setValue(' ');
        }
    }


    void ThongBaoChienThang(String Title, String Message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(Title);
        alertDialog.setIcon(R.mipmap.icon_challenge);
        alertDialog.setMessage(Message);
        alertDialog.setPositiveButton("Chơi Lại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResetBanCo();
            }
        });
        alertDialog.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED){
                        Toast.makeText(CaroActivity.this, "Bạn Đã Bị Mất Kết Nối!!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Messages.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    if(writeMessage.indexOf("-8") == -1) // Khong Tim Thay Nghi la kh phai tin nhan
                    {
                        CheckDuocDanh = false;
                    }
                    break;
                case Messages.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    StringBuilder stringBuilder = new StringBuilder();


                    if(readMessage.indexOf("-8") != -1){
                        StringBuilder stringBuilderRead = new StringBuilder();
                        for(int i=2;i<readMessage.length();i++){
                            stringBuilderRead.append(readMessage.charAt(i));
                        }
                        SendMessages.add(new SendMessage(stringBuilderRead.toString(),"Đối thủ"));
                        adapter_listview.notifyDataSetChanged();
                    }
                    else {
                        if (readMessage.equals("ClientLost")) {
                            ThongBaoChienThang("Chia Buồn", "Bạn Đã Thua");
                        } else if (readMessage.equals("HostLost")) {
                            ThongBaoChienThang("Chia Buồn", "Bạn Đã Thua");
                            CheckDuocDanh = true;
                        } else if (readMessage.equals("MatLuot")) {
//                         if(Index == 0){
//                             SumTimePlayer2 += TimeDown - 1000;
//                             lblSumTimePlayer2.setText(String.format("%dM:%02dS", SumTimePlayer2 / (60 * 1000), (SumTimePlayer2 % (60 * 1000)) / 1000));
//                         }else{
//                             SumTimePlayer1 += TimeDown - 1000;
//                             lblSumTimePlayer1.setText(String.format("%dM:%02dS", SumTimePlayer1 / (60 * 1000), (SumTimePlayer1 % (60 * 1000)) / 1000));
//                         }
                            CheckDuocDanh = true;
                        } else {
                            if (Index == 0) {
                                TinhThoiGianVaLuotDi(1);
                            } else {
                                TinhThoiGianVaLuotDi(0);
                            }
                            listOCaro.Danh(Integer.parseInt(readMessage), (Index + 1) % 2);
                            CheckDuocDanh = true;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ticktactoe);

        AnhXa();
        ChatCaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

            }
        });

        VeBanCo();



        //Bắt Đầu Game

        //Dem Thời Gian
        countDownTimer = new CountDownTimer(CountTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                CountTime -= 1000;
                lblCountTime.setText(String.format("%02d : %02d", CountTime / (60 * 1000), (CountTime % (60 * 1000)) / 1000));
            }

            @Override
            public void onFinish() {
                if(CheckDuocDanh == true){
                    if(Index == 0){ // X
                        SumTimePlayer1 += TimeDown - 1000;
                        lblSumTimePlayer1.setText(String.format("%dM:%02dS", SumTimePlayer1 / (60 * 1000), (SumTimePlayer1 % (60 * 1000)) / 1000));
                    }else{ // O
                        SumTimePlayer2 += TimeDown - 1000;
                        lblSumTimePlayer2.setText(String.format("%dM:%02dS", SumTimePlayer2 / (60 * 1000), (SumTimePlayer2 % (60 * 1000)) / 1000));
                    }
                }else{
                    if(Index == 0){ // X
                        SumTimePlayer2 += TimeDown - 1000;
                        lblSumTimePlayer2.setText(String.format("%dM:%02dS", SumTimePlayer2 / (60 * 1000), (SumTimePlayer2 % (60 * 1000)) / 1000));
                    }else{ // O
                        SumTimePlayer1 += TimeDown - 1000;
                        lblSumTimePlayer1.setText(String.format("%dM:%02dS", SumTimePlayer1 / (60 * 1000), (SumTimePlayer1 % (60 * 1000)) / 1000));
                    }
                }

                CountTime = TimeDown;
                this.start();
                if(CheckDuocDanh == true){
                    StartingMenu.mConnection.sendMessage("MatLuot");
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}


