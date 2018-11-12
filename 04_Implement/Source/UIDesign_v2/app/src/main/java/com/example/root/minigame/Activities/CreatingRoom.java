
package com.example.root.minigame.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.minigame.Player;
import com.example.root.minigame.R;


public class CreatingRoom extends AppCompatActivity {

    Button btn_ready, btn_return, btn_game1, btn_game2, btn_mode1, btn_mode2, btn_setting;
    ImageView iv_p1Ready, iv_p2Ready, iv_game1Tick, iv_game2Tick, iv_mode1Tick, iv_mode2Tick;
    FrameLayout fl_p1Name, fl_p2Name;
    TextView txt_p1Name, txt_p2Name;
    Player thisPlayer;

    private BluetoothAdapter mBTAdapter;
    private String mBTAdapterDefaultName;

    private final int REQUEST_CODE_ENABLE = 101;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        final Bundle bun = intent.getBundleExtra("bundle");

        thisPlayer = new Player(bun.getString("thisPlayerName"),false,true);

        mBTAdapterDefaultName = bun.getString("BTDefaultName");

        AnhXa();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_waiting_room);

        btn_ready = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_mode1 = (Button) findViewById(R.id.btn_mode1);
        btn_mode2 = (Button) findViewById(R.id.btn_mode2);
        btn_game1 = (Button) findViewById(R.id.btn_game1);
        btn_game2 = (Button) findViewById(R.id.btn_game2);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        iv_p1Ready = (ImageView) findViewById(R.id.iv_p1Ready);
        iv_p2Ready = (ImageView) findViewById(R.id.iv_p2Ready);

        iv_mode1Tick = (ImageView) findViewById(R.id.iv_mode1Tick);
        iv_mode2Tick = (ImageView) findViewById(R.id.iv_mode2Tick);

        iv_game1Tick = (ImageView) findViewById(R.id.iv_game1Tick);
        iv_game2Tick = (ImageView) findViewById(R.id.iv_game2Tick);

        fl_p1Name = (FrameLayout) findViewById(R.id.fl_p1Name);
        fl_p2Name = (FrameLayout) findViewById(R.id.fl_p2Name);

        txt_p1Name = (TextView) findViewById(R.id.txt_p1Name);
        txt_p2Name = (TextView) findViewById(R.id.txt_p2Name);


        iv_p1Ready.setVisibility(View.INVISIBLE);
        iv_p2Ready.setVisibility(View.INVISIBLE);

        iv_game1Tick.setVisibility(View.INVISIBLE);
        iv_game2Tick.setVisibility(View.INVISIBLE);

        iv_mode1Tick.setVisibility(View.INVISIBLE);
        iv_mode2Tick.setVisibility(View.INVISIBLE);

        fl_p1Name.setVisibility(View.VISIBLE);
        fl_p2Name.setVisibility(View.INVISIBLE);

        txt_p1Name.setText(thisPlayer.getName());

        btn_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

            }
        });

        btn_game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_game1Tick.getVisibility() == View.VISIBLE) {
                    iv_game1Tick.setVisibility(View.INVISIBLE);
                    mBTAdapter.setName(thisPlayer.getName());
                }

                else {
                    iv_game1Tick.setVisibility(View.VISIBLE);
                    iv_game2Tick.setVisibility(View.INVISIBLE);
                    mBTAdapter.setName(thisPlayer.getName()+"&"+"Tàu chiến");
                }
                AnhXa();
            }
        });

        btn_game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_game2Tick.getVisibility() == View.VISIBLE) {
                    iv_game2Tick.setVisibility(View.INVISIBLE);
                    mBTAdapter.setName(thisPlayer.getName());
                }
                else {
                    iv_game2Tick.setVisibility(View.VISIBLE);
                    iv_game1Tick.setVisibility(View.INVISIBLE);
                    mBTAdapter.setName(thisPlayer.getName()+"&"+"Caro");
                }
                AnhXa();
            }
        });

        btn_mode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_mode1Tick.getVisibility() == View.VISIBLE)
                    iv_mode1Tick.setVisibility(View.INVISIBLE);
                else {
                    iv_mode1Tick.setVisibility(View.VISIBLE);
                    iv_mode2Tick.setVisibility(View.INVISIBLE);
                }
            }
        });

        btn_mode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_mode2Tick.getVisibility() == View.VISIBLE)
                    iv_mode2Tick.setVisibility(View.INVISIBLE);
                else {
                    iv_mode2Tick.setVisibility(View.VISIBLE);
                    iv_mode1Tick.setVisibility(View.INVISIBLE);
                }
            }
        });


        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (iv_p2Ready.getVisibility() == View.VISIBLE
                        && (iv_game1Tick.getVisibility() == View.VISIBLE || iv_game2Tick.getVisibility() == View.VISIBLE)
                        && (iv_mode1Tick.getVisibility() == View.VISIBLE || iv_mode2Tick.getVisibility() == View.VISIBLE)) {
                    iv_p1Ready.setVisibility(View.VISIBLE);
                    btn_game1.setEnabled(false);
                    btn_game2.setEnabled(false);
                    btn_mode1.setEnabled(false);
                    btn_mode2.setEnabled(false);
                    Toast.makeText(CreatingRoom.this, "Trận đấu sắp bắt đầu!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else if (iv_game1Tick.getVisibility() == View.INVISIBLE && iv_game2Tick.getVisibility() == View.INVISIBLE)
                    Toast.makeText(CreatingRoom.this, "Bạn chưa chọn game!", Toast.LENGTH_SHORT).show();
                else if (iv_mode1Tick.getVisibility() == View.INVISIBLE && iv_mode2Tick.getVisibility() == View.INVISIBLE)
                    Toast.makeText(CreatingRoom.this, "Bạn chưa chọn mode!", Toast.LENGTH_SHORT).show();
                else
                {
                    iv_p1Ready.setVisibility(View.VISIBLE);
                    Toast.makeText(CreatingRoom.this, "Vui lòng chờ đối thủ sẵn sàng!", Toast.LENGTH_SHORT).show();
                    thisPlayer.setReadyStatus(true);
                    btn_game1.setEnabled(false);
                    btn_game2.setEnabled(false);
                    btn_mode1.setEnabled(false);
                    btn_mode2.setEnabled(false);
                }
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
            }
            else
                {

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
        mBTAdapter.setName(mBTAdapterDefaultName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
