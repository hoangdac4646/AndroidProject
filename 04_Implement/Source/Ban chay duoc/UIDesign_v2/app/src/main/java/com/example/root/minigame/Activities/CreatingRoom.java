
package com.example.root.minigame.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.minigame.Chess.ChessActivity;
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.Classes.Player;
import com.example.root.minigame.R;
import com.example.root.minigame.Sound.Click_button;
import com.example.root.minigame.Chat.Adapter_listview;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;
import com.example.root.minigame.Chat.GuiTinNhan;

import java.util.ArrayList;
import java.util.List;


public class CreatingRoom extends AppCompatActivity {
   public  MediaPlayer mediaPlayer;
    Button btn_ready, btn_return, btn_game1, btn_game2, btn_game3, btn_setting,btn_guiTinNhan;
    ListView listView_chat;
    int CheckPhatNhac=0;
    List<GuiTinNhan> Arr_TinNhan = new ArrayList<>();
    Adapter_listview adapter_listview;
    EditText NoiDung;
    ImageView iv_p1Ready, iv_p2Ready, iv_game1Tick, iv_game2Tick, iv_game3Tick;
    FrameLayout fl_p1Name, fl_p2Name, fl_btn_game1, fl_btn_game2, fl_btn_game3;
    TextView txt_p1Name, txt_p2Name;

    private BluetoothAdapter mBTAdapter;
    private final int REQUEST_CODE_ENABLE = 101;
    public static Player enemyPlayer = new Player();

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothAdapter.getDefaultAdapter().setName(Main.thisPlayer.getPlayerName());
        AnhXa();
        if(StartingMenu.mConnection != null && mBTAdapter.isEnabled()){
            StartingMenu.mConnection.setHandle(mCreatingRoomHandler);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Main.CheckNhac=1;
        stopService(Main.NhacCho);
        mediaPlayer = MediaPlayer.create(CreatingRoom.this,R.raw.nhacnen2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        setContentView(R.layout.activity_waiting_room);

        Main.click_button = new Click_button(CreatingRoom.this);
        btn_guiTinNhan  = findViewById(R.id.btn_CreateGui);
        listView_chat = findViewById(R.id.lv_listTinNhan);
        adapter_listview = new Adapter_listview(CreatingRoom.this,R.layout.custom_listview,Arr_TinNhan);
        listView_chat.setAdapter(adapter_listview);
        adapter_listview.notifyDataSetChanged();
        NoiDung = findViewById(R.id.edt_CreateContent);
        btn_guiTinNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
                if(StartingMenu.mConnection.mBTconnection.getState() !=BluetoothConnectionService.STATE_CONNECTED){

                }else{
                    String Content = "-8"+NoiDung.getText().toString();
                    StartingMenu.mConnection.sendMessage(Content);
                    Arr_TinNhan.add(new GuiTinNhan(NoiDung.getText().toString(),"Me"));
                    NoiDung.setText("");
                    adapter_listview.notifyDataSetChanged();

                }
            }
        });

        btn_ready = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_game1 = (Button) findViewById(R.id.btn_game1);
        btn_game2 = (Button) findViewById(R.id.btn_game2);
        btn_game3 = (Button) findViewById(R.id.btn_game3);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        iv_p1Ready = (ImageView) findViewById(R.id.iv_p1Ready);
        iv_p2Ready = (ImageView) findViewById(R.id.iv_p2Ready);

        iv_game1Tick = (ImageView) findViewById(R.id.iv_game1Tick);
        iv_game2Tick = (ImageView) findViewById(R.id.iv_game2Tick);
        iv_game3Tick = (ImageView) findViewById(R.id.iv_game3Tick);


        fl_p1Name = (FrameLayout) findViewById(R.id.fl_p1Name);
        fl_p2Name = (FrameLayout) findViewById(R.id.fl_p2Name);
        fl_btn_game1 = (FrameLayout) findViewById(R.id.fl_btn_game1);
        fl_btn_game2 = (FrameLayout) findViewById(R.id.fl_btn_game2);
        fl_btn_game3 = (FrameLayout) findViewById(R.id.fl_btn_game3);

        txt_p1Name = (TextView) findViewById(R.id.txt_p1Name);
        txt_p2Name = (TextView) findViewById(R.id.txt_p2Name);

        iv_p1Ready.setVisibility(View.INVISIBLE);
        iv_p2Ready.setVisibility(View.INVISIBLE);

        iv_game1Tick.setVisibility(View.INVISIBLE);
        iv_game2Tick.setVisibility(View.INVISIBLE);
        iv_game3Tick.setVisibility(View.INVISIBLE);

        fl_p1Name.setVisibility(View.VISIBLE);
        fl_p2Name.setVisibility(View.INVISIBLE);


        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
            }
        });


        if (Main.thisPlayer.isHost()) {
            mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "N/A");
            txt_p1Name.setText(Main.thisPlayer.getPlayerName());
            btn_game1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Main.click_button.PlayButtonSound();
                    if (iv_game1Tick.getVisibility() == View.VISIBLE) {
                        iv_game1Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "N/A");
                        StartingMenu.mConnection.sendMessage("untick1");
                    } else {
                        iv_game1Tick.setVisibility(View.VISIBLE);
                        iv_game2Tick.setVisibility(View.INVISIBLE);
                        iv_game3Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "Caro");
                        StartingMenu.mConnection.sendMessage("tick1");
                    }
                }
            });

            btn_game2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Main.click_button.PlayButtonSound();
                    if (iv_game2Tick.getVisibility() == View.VISIBLE) {
                        iv_game2Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "N/A");
                        StartingMenu.mConnection.sendMessage("untick2");
                    } else {
                        iv_game2Tick.setVisibility(View.VISIBLE);
                        iv_game1Tick.setVisibility(View.INVISIBLE);
                        iv_game3Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "Tàu chiến");
                        StartingMenu.mConnection.sendMessage("tick2");
                    }
                }
            });

            btn_game3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Main.click_button.PlayButtonSound();
                    if (iv_game3Tick.getVisibility() == View.VISIBLE) {
                        iv_game3Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName());
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "N/A");
                        StartingMenu.mConnection.sendMessage("untick3");
                    } else {
                        iv_game3Tick.setVisibility(View.VISIBLE);
                        iv_game2Tick.setVisibility(View.INVISIBLE);
                        iv_game1Tick.setVisibility(View.INVISIBLE);
                        mBTAdapter.setName(Main.thisPlayer.getPlayerName() + "&" + "Sudoku");
                        StartingMenu.mConnection.sendMessage("tick3");
                    }
                }
            });
        } else {
            fl_p2Name.setVisibility(View.VISIBLE);
            txt_p2Name.setText(Main.thisPlayer.getPlayerName());
            txt_p1Name.setText(enemyPlayer.getPlayerName());
            StartingMenu.mConnection.sendMessage("/&"+Main.thisPlayer.getPlayerName());
            fl_p2Name.setVisibility(View.VISIBLE);
        }


        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.click_button.PlayButtonSound();
                if (Main.thisPlayer.isHost()) {
                    if (iv_game1Tick.getVisibility() == View.INVISIBLE && iv_game2Tick.getVisibility() == View.INVISIBLE && iv_game3Tick.getVisibility() == View.INVISIBLE)
                    {
                        Toast.makeText(CreatingRoom.this, "Bạn chưa chọn game!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else iv_p1Ready.setVisibility(View.VISIBLE);
                } else {
                    iv_p2Ready.setVisibility(View.VISIBLE);
                }

                if (iv_p1Ready.getVisibility() == View.VISIBLE && iv_p2Ready.getVisibility() == View.VISIBLE) {
                    StartingMenu.mConnection.sendMessage("Start");
                    StartingGame();
                }
                else
                {
                    StartingMenu.mConnection.sendMessage("Ready");
                    {
                        btn_game1.setEnabled(false);
                        btn_game2.setEnabled(false);
                        btn_game3.setEnabled(false);

                        fl_btn_game1.setBackgroundResource(R.drawable.review_game1_disabled);
                        fl_btn_game2.setBackgroundResource(R.drawable.review_game2_disabled);
                        fl_btn_game3.setBackgroundResource(R.drawable.review_game3_disabled);
                    }
                    Toast.makeText(CreatingRoom.this, "Vui lòng chờ đối thủ sẵn sàng!", Toast.LENGTH_SHORT).show();
                }

            }

        });

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Main.click_button.PlayButtonSound();
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReciever, filter);
        if(CheckPhatNhac!=0) {
            mediaPlayer = MediaPlayer.create(CreatingRoom.this, R.raw.nhacnen2);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void StartingGame() {
        btn_game1.setEnabled(false);
        btn_game2.setEnabled(false);
        btn_game3.setEnabled(false);

        fl_btn_game1.setBackgroundResource(R.drawable.review_game1_disabled);
        fl_btn_game2.setBackgroundResource(R.drawable.review_game2_disabled);
        fl_btn_game3.setBackgroundResource(R.drawable.review_game3_disabled);

        Toast.makeText(CreatingRoom.this, "Trận đấu sắp bắt đầu!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), ChessActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void AnhXa() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBTAdapter == null) {
            setResult(RESULT_CANCELED);
            Toast.makeText(this, "Bluetooth Doesn't Support On This Device!", Toast.LENGTH_SHORT).show();
            finish();
        }

        setResult(RESULT_CANCELED);
        if (!mBTAdapter.isEnabled()) {
            Intent OnInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(OnInten, REQUEST_CODE_ENABLE);
        }

        CheckBTpermission();
        discovery();
    }

    private void discovery() {
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        mBTAdapter.startDiscovery();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckBTpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permistioncheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permistioncheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permistioncheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            } else {

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth On", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter.setName(StartingMenu.mBTAdapterDefaultName);
       CheckPhatNhac=1;
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    @Override
    protected void onDestroy() {

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter.setName(StartingMenu.mBTAdapterDefaultName);
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReciever);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    private final Handler mCreatingRoomHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 != BluetoothConnectionService.STATE_CONNECTED) {
                        Toast.makeText(getApplication(), "Cre: Bạn Đã Mất Kết Nối Tới Phòng Chờ.", Toast.LENGTH_SHORT).show();
                    } else {
                        StartingMenu.mConnection.sendMessage("/&"+Main.thisPlayer.getPlayerName());
                        fl_p2Name.setVisibility(View.VISIBLE);
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
                    if(readMessage.indexOf("-8")!=-1){
                        StringBuilder stringBuilderRead = new StringBuilder();
                        for(int i=2;i<readMessage.length();i++){
                            stringBuilderRead.append(readMessage.charAt(i));
                        }
                        Arr_TinNhan.add(new GuiTinNhan(stringBuilderRead.toString(),"Đối thủ"));
                        adapter_listview.notifyDataSetChanged();
                    }
                    else {
                        if (readMessage.contains("/&")) {
                            enemyPlayer.setPlayerName(readMessage.split("/&")[1]);
                            if (Main.thisPlayer.isHost()) {
                                enemyPlayer.setHostStatus(false);
                                txt_p2Name.setText(enemyPlayer.getPlayerName());
                            } else {
                                txt_p1Name.setText(enemyPlayer.getPlayerName());
                                enemyPlayer.setHostStatus(true);
                            }
                        }
                        if (readMessage.equals("Ready")) {
                            if (Main.thisPlayer.isHost()) {
                                iv_p2Ready.setVisibility(View.VISIBLE);
                            } else {
                                iv_p1Ready.setVisibility(View.VISIBLE);
                            }
                        } else if (readMessage.equals("Start")) {
                            iv_p1Ready.setVisibility(View.VISIBLE);
                            iv_p2Ready.setVisibility(View.VISIBLE);
                            StartingGame();
                        }
                        if (!Main.thisPlayer.isHost()) {
                            switch (readMessage) {
                                case "tick1":
                                    iv_game1Tick.setVisibility(View.VISIBLE);
                                    iv_game2Tick.setVisibility(View.INVISIBLE);
                                    iv_game3Tick.setVisibility(View.INVISIBLE);
                                    break;
                                case "untick1":
                                    iv_game1Tick.setVisibility(View.INVISIBLE);
                                    break;
                                case "tick2":
                                    iv_game1Tick.setVisibility(View.INVISIBLE);
                                    iv_game2Tick.setVisibility(View.VISIBLE);
                                    iv_game3Tick.setVisibility(View.INVISIBLE);
                                    break;
                                case "untick2":
                                    iv_game2Tick.setVisibility(View.INVISIBLE);
                                    break;
                                case "tick3":
                                    iv_game1Tick.setVisibility(View.INVISIBLE);
                                    iv_game2Tick.setVisibility(View.INVISIBLE);
                                    iv_game3Tick.setVisibility(View.VISIBLE);
                                    break;
                                case "untick3":
                                    iv_game3Tick.setVisibility(View.INVISIBLE);
                                    break;

                            }
                        }
                    }
                    break;
                case Messages.MESSAGE_DEVICE_NAME:
                    String mConnectedDeviceName = null;
                    mConnectedDeviceName = msg.getData().getString(Messages.DEVICE_NAME);
                    StartingMenu.mConnection.sendMessage("/&"+Main.thisPlayer.getPlayerName());
                    fl_p2Name.setVisibility(View.VISIBLE);
                    break;

                case Messages.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Messages.TOAST),
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private BroadcastReceiver mBroadcastReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Dialog dialog = new Dialog(context);

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (BluetoothAdapter.STATE_OFF == state) {
                    Toast.makeText(context, "Bluetooth is Off. Return to waitting room", Toast.LENGTH_SHORT).show();
                    dialog.show();
                    dialog.setCancelable(false);
                }
                else if(BluetoothAdapter.STATE_ON == state){
                    dialog.dismiss();
                    Intent intent1 = getIntent();
                    finish();
                    startActivity(intent1);
                }
            }
        }
    };

}
