package com.example.root.minigame.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.root.minigame.Main;
import com.example.root.minigame.R;
import com.example.root.minigame.Sound.Sound;
import com.example.root.minigame.Sound.Click_button;
import com.example.root.minigame.mBluetooth.BluetoothConnection;



public class StartingMenu extends AppCompatActivity {
    Sound Sound;
    Button btn_create, btn_find, btn_setting;
    private BluetoothAdapter mBTAdapter;
    private final int REQUEST_CODE_ENABLE = 10133;
    private final int REQUEST_CODE_DISCOVERABLE = 10012;
    public final static String mBTAdapterDefaultName = BluetoothAdapter.getDefaultAdapter().getName();
    public static BluetoothConnection mConnection = new BluetoothConnection();
    private Handler mHandler = new Handler();
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothAdapter.getDefaultAdapter().setName(Main.thisPlayer.getPlayerName());
        AnhXa();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_starting_menu);
        if(mBTAdapter.isEnabled()){
            mConnection.StartConnection(mHandler);

        }
            Main.click_button = new Click_button(StartingMenu.this);

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main.click_button.PlayButtonSound();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Main.click_button.PlayButtonSound();
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 400);
                startActivityForResult(discoverableIntent, REQUEST_CODE_DISCOVERABLE);

            }
        });

        btn_find.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Main.click_button.PlayButtonSound();
                Main.thisPlayer.setHostStatus(false);
                Intent intent =  new Intent(StartingMenu.this,FindingRoom.class);
                startActivity(intent);

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
            else {

            }
        }
    }
    //bat dau manhinhcho 2


    @Override
    protected void onResume() {
        super.onResume();

        if(Main.isMusicExist==1){
            Main.isMusicExist=2;
           // CreatingRoom.mediaPlayer.release();
           // CreatingRoom.mediaPlayer.stop();
            Intent intent = new Intent(StartingMenu.this,Sound.class);
            intent.putExtra("TenLoai","PhongCho1");
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth On", Toast.LENGTH_SHORT).show();
            mConnection.StartConnection(mHandler);
        }
        if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(requestCode == REQUEST_CODE_DISCOVERABLE && resultCode == 400){
            Main.thisPlayer.setHostStatus(true);
            Intent intent =  new Intent(StartingMenu.this,CreatingRoom.class);
            startActivity(intent);
        }

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
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}