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

import com.example.root.minigame.Player;
import com.example.root.minigame.R;
import com.example.root.minigame.mBluetooth.BluetoothConnection;


public class StartingMenu extends AppCompatActivity {

    Button btn_create, btn_find, btn_setting;
    Player thisPlayer;

    private BluetoothAdapter mBTAdapter;
    private final int REQUEST_CODE_ENABLE = 101;
    private final int REQUEST_CODE_DISCOVERABLE = 1001;

    private String mBTAdapterDefaultName;
    public static BluetoothConnection mConnection = new BluetoothConnection();
    private Handler mHandler = new Handler();
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        final Bundle bun =  intent.getBundleExtra("bundle");

        thisPlayer = new Player(bun.getString("thisPlayerName"));

        AnhXa();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_starting_menu);
        mConnection.StartConnection(mHandler);

        btn_create = (Button) findViewById(R.id.btn_create);
        btn_find = (Button) findViewById(R.id.btn_find);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_create.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {



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
                Intent intent =  new Intent(StartingMenu.this,FindingRoom.class);
                Bundle bundle = new Bundle();
                bundle.putString("BTDefaultName",mBTAdapterDefaultName);
                bundle.putString("thisPlayerName",thisPlayer.getPlayerName());
                intent.putExtra("bundle",bundle);
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

        mBTAdapterDefaultName =  mBTAdapter.getName();
        mBTAdapter.setName(thisPlayer.getPlayerName());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth On", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(requestCode == REQUEST_CODE_DISCOVERABLE && resultCode == 400){
            Intent intent =  new Intent(StartingMenu.this,CreatingRoom.class);
            Bundle bundle = new Bundle();
            thisPlayer.setHostStatus(true);
            bundle.putString("BTDefaultName",mBTAdapterDefaultName);
            bundle.putString("thisPlayerName",thisPlayer.getPlayerName());
            intent.putExtra("bundle",bundle);
            startActivity(intent);
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
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}