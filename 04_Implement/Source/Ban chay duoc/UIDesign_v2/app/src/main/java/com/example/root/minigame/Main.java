package com.example.root.minigame;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.minigame.Activities.CreatingRoom;
import com.example.root.minigame.Sound.AmThanh;
import com.example.root.minigame.Sound.Click_button;
import com.example.root.minigame.Activities.StartingMenu;
import com.example.root.minigame.Classes.Player;

import static android.view.Window.FEATURE_NO_TITLE;


public class Main extends AppCompatActivity {

    public static Click_button click_button;
    public static int CheckNhac=0;
    Button btn_ok, btn_return, btn_setting;
    public static Player thisPlayer;
    public static Intent  NhacCho;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisPlayer = new Player();
        click_button = new Click_button(Main.this);
        final Dialog dialog_setName = new Dialog(Main.this);
        dialog_setName.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_setName.setContentView(R.layout.dialog_set_nickname);
        dialog_setName.show();
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.pingping);
        Button btn_confirm = (Button) dialog_setName.findViewById(R.id.btn_confirm);
        final EditText edt_playerName = (EditText) dialog_setName.findViewById(R.id.edt_playerName);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_button.PlayButtonSound();
                String playerName = edt_playerName.getText().toString();
                if (!playerName.equals(""))
                {
                    thisPlayer.setPlayerName(playerName);
                    dialog_setName.dismiss();
                    Toast.makeText(Main.this, "Đặt tên nhân vật thành công!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(Main.this, "Tên không hơp lệ!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_menu);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReciever, filter);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                click_button.PlayButtonSound();
                if (thisPlayer.getPlayerName().equals(""))
                {
                    Toast.makeText(Main.this, "Vui lòng đặt tên nhân vật!", Toast.LENGTH_SHORT).show();
                    dialog_setName.show();
                }
                else {
                    Intent intent = new Intent(Main.this, StartingMenu.class);
                    Bundle bun = new Bundle();
                    bun.putString("thisPlayerName", thisPlayer.getPlayerName());
                    intent.putExtra("bundle", bun);
                    startActivity(intent);
                }
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                click_button.PlayButtonSound();
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                click_button.PlayButtonSound();
                finishAndRemoveTask();
            }
        });


        // Set up the user interaction to manually show or hide the system UI.

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    @Override
    protected void onResume() {
        if(Main.CheckNhac==0) {
            NhacCho = new Intent(Main.this, AmThanh.class);
            NhacCho.putExtra("TenLoai", "PhongCho1");
            startService(NhacCho);
            CheckNhac = 0;
        }

        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReciever, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReciever);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    public static class CustomListApdater_Chat {
    }

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

