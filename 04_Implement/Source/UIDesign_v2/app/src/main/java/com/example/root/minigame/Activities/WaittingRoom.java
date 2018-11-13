
package com.example.root.minigame.Activities;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.root.minigame.Player;
import com.example.root.minigame.R;


public class WaittingRoom extends AppCompatActivity {

    Button btn_ready, btn_return, btn_setting;
    ImageView iv_p1Ready, iv_p2Ready, iv_game1Tick, iv_game2Tick, iv_game3Tick;
    FrameLayout fl_p1Name, fl_p2Name, fl_btn_game1,fl_btn_game2,fl_btn_game3;
    TextView txt_p1Name, txt_p2Name;
    Player thisPlayer;

    private BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
    private String mBTAdapterDefaultName;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        final Bundle bun =  intent.getBundleExtra("bundle");

        thisPlayer = new Player(bun.getString("thisPlayerName"));
        mBTAdapterDefaultName = bun.getString("BTDefaultName");
        mBTAdapter.setName(thisPlayer.getPlayerName());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_waiting_room);

        btn_ready = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        iv_p1Ready = (ImageView) findViewById(R.id.iv_p1Ready);
        iv_p2Ready = (ImageView) findViewById(R.id.iv_p2Ready);


        iv_game1Tick = (ImageView) findViewById(R.id.iv_game1Tick);
        iv_game2Tick = (ImageView) findViewById(R.id.iv_game2Tick);
        iv_game3Tick = (ImageView) findViewById(R.id.iv_game2Tick);


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


        fl_p1Name.setVisibility(View.VISIBLE);
        fl_p2Name.setVisibility(View.VISIBLE);

        txt_p2Name.setText(thisPlayer.getPlayerName());

        btn_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

            }
        });

        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_p2Ready.setVisibility(View.VISIBLE);
                fl_btn_game1.setBackgroundResource(R.drawable.review_game1_disabled);
                fl_btn_game2.setBackgroundResource(R.drawable.review_game2_disabled);
                fl_btn_game3.setBackgroundResource(R.drawable.review_game3_disabled);
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter.setName(mBTAdapterDefaultName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter.setName(mBTAdapterDefaultName);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}
