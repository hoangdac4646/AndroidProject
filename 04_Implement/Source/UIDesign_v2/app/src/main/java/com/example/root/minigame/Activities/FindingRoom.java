package com.example.root.minigame.Activities;


import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.root.minigame.CustomListAdapter;
import com.example.root.minigame.Player;
import com.example.root.minigame.R;
import com.example.root.minigame.Room;

import java.util.ArrayList;


public class FindingRoom extends AppCompatActivity {

    Button btn_ok, btn_return, btn_setting, btn_refresh;
    public static final String DEVICE_ADDRESS = "device_address";
    private static final String DEVICE_CONNECTED_NAME = "device_name";
    private BluetoothAdapter mBTAdapter;
    private String mBTAdapterDefaultName;
    private BluetoothDevice DeviceSelected = null;// phai gan gia tri cho DeviceSelected

    private ListView DeviceList;
    private final int REQUEST_CODE_ENABLE = 101;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    private ArrayList<Room> Names = new ArrayList();
    Player thisPlayer;


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bun = intent.getBundleExtra("bundle");

        thisPlayer = new Player(bun.getString("thisPlayerName"));
        mBTAdapterDefaultName = bun.getString("BTDefaultName");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_finding_room);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);


        DeviceList = (ListView) findViewById(R.id.lv_listRoom);

        DeviceList.setOnItemClickListener(mDeviceCLick);

        btn_refresh.callOnClick();

        btn_setting.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mDevices.clear();
                Names.clear();
                DeviceList.setAdapter(null);
                AnhXa();
            }
        });


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DeviceSelected != null)
                {
                    String address = DeviceSelected.getAddress();

                    Intent intent = new Intent();
                    intent.putExtra(DEVICE_ADDRESS, address);
                    intent.putExtra(DEVICE_CONNECTED_NAME, DeviceSelected.getName());
                    setResult(RESULT_OK, intent);

                }
                else 
                {
                    Toast.makeText(FindingRoom.this, "Bạn chưa chọn phòng!", Toast.LENGTH_SHORT).show();
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBRFoundDevices, filter);

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
            } else {

            }
        }
    }


    private AdapterView.OnItemClickListener mDeviceCLick //goi hàm nay de listening event click phai gan gia tri cho DeviceSelected
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBTAdapter.cancelDiscovery();
            DeviceSelected = mDevices.get(position);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBRFoundDevices, filter);

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
        unregisterReceiver(mBRFoundDevices);

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

    private final BroadcastReceiver mBRFoundDevices = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED || device.getBondState() == BluetoothDevice.BOND_NONE){
                    //list devices paired
                    mDevices.add(device);
                    if(device.getName() != "" && device.getName() != null){
                        String [] dName = device.getName().split("&");
                        if (dName != null && !dName[0].equals("")) {
                            if (dName.length < 2)
                                Names.add(new Room(R.drawable.logo_normal, dName[0], "N/A"));
                            else
                            {
                                switch (dName[1]) {
                                    case "Caro":
                                        Names.add(new Room(R.drawable.review_game1_rounded, dName[0], dName[1]));
                                        break;
                                    case "Sudoku":
                                        Names.add(new Room(R.drawable.review_game3_rounded, dName[0], dName[1]));
                                        break;
                                    default:
                                        Names.add(new Room(R.drawable.review_game2_rounded, dName[0], dName[1]));
                                        break;
                                }
                            }
                            DeviceList.setAdapter(new CustomListAdapter(FindingRoom.this, Names));
                        }
                    }

                }
            }
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}
