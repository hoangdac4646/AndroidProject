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
import android.os.Handler;
import android.os.Message;
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
import com.example.root.minigame.Interface.Messages;
import com.example.root.minigame.Main;
import com.example.root.minigame.Player;
import com.example.root.minigame.R;
import com.example.root.minigame.Room;
import com.example.root.minigame.mBluetooth.BluetoothConnectionService;

import java.util.ArrayList;


public class FindingRoom extends AppCompatActivity {

    Button btn_ok, btn_return, btn_setting, btn_refresh;
    public static final String DEVICE_ADDRESS = "device_address";
    private static final String DEVICE_CONNECTED_NAME = "device_name";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice DeviceSelected = null;// phai gan gia tri cho DeviceSelected

    private ListView DeviceList;
    private final int REQUEST_CODE_ENABLE = 101;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    private ArrayList<Room> Names = new ArrayList();
    Message msg1;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_finding_room);

        StartingMenu.mConnection.setHandle(mFindingRoomHandler);

        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_return = (Button) findViewById(R.id.btn_return);
        btn_setting = (Button) findViewById(R.id.btn_setting);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);


        DeviceList = (ListView) findViewById(R.id.lv_listRoom);

        DeviceList.setOnItemClickListener(mDeviceCLick);

        btn_refresh.callOnClick();

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
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
                if (DeviceSelected != null) {
                    String address = DeviceSelected.getAddress();
                    if (StartingMenu.mConnection.mBTconnection.getState() == BluetoothConnectionService.STATE_CONNECTED) {
                        String[] dvName = DeviceSelected.getName().split("&");
                        Toast.makeText(FindingRoom.this, "Bạn đã vào phòng của " + dvName[0], Toast.LENGTH_SHORT).show();
                        StartingMenu.enemyPlayer = new Player(dvName[0]);
                        StartingMenu.enemyPlayer.setHostStatus(true);
                        Intent intent = new Intent(FindingRoom.this, CreatingRoom.class);
                        startActivity(intent);
                    }

                    if (DeviceSelected.getBondState() != BluetoothDevice.BOND_NONE) {
                        StartingMenu.mConnection.Connect(address);


                    }

                } else {
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

        mBTAdapter.setName(Main.thisPlayer.getPlayerName());
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
            if (DeviceSelected.getBondState() == BluetoothDevice.BOND_NONE) {
                Toast.makeText(FindingRoom.this, "Cái này chua paired", Toast.LENGTH_SHORT).show();
                DeviceSelected.createBond();
            }
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
        mBTAdapter.setName(StartingMenu.mBTAdapterDefaultName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTAdapter.setName(StartingMenu.mBTAdapterDefaultName);
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
                if (device.getBondState() == BluetoothDevice.BOND_BONDED || device.getBondState() == BluetoothDevice.BOND_NONE) {
                    //list devices paired
                    mDevices.add(device);
                    if (device.getName() != null && !device.getName().equals("")) {
                        String[] dName = device.getName().split("&");
                        if (dName.length > 1)
                        {
                            switch (dName[1]) {
                                case "Caro":
                                    Names.add(new Room(R.drawable.review_game1_rounded, dName[0], dName[1]));
                                    break;
                                case "Sudoku":
                                    Names.add(new Room(R.drawable.review_game3_rounded, dName[0], dName[1]));
                                    break;
                                case "Tàu chiến":
                                    Names.add(new Room(R.drawable.review_game2_rounded, dName[0], dName[1]));
                                    break;
                                default:
                                    Names.add(new Room(R.drawable.logo_normal, dName[0], dName[1]));
                                    break;
                            }
                        }
                        DeviceList.setAdapter(new CustomListAdapter(FindingRoom.this, Names));
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

    private final Handler mFindingRoomHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            msg1 = msg;
            switch (msg.what) {
                case Messages.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothConnectionService.STATE_CONNECTED:
                            break;
                        case BluetoothConnectionService.STATE_CONNECTING:
                            Toast.makeText(FindingRoom.this, "Finding: Connecting", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothConnectionService.STATE_LISTEN:
                        case BluetoothConnectionService.STATE_NONE:
                            break;
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
                    break;
                case Messages.MESSAGE_TOAST:
                    Toast.makeText(FindingRoom.this, msg.getData().getString(Messages.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Messages.MESSAGE_DEVICE_NAME:
                    if (StartingMenu.mConnection.mBTconnection.getState() == BluetoothConnectionService.STATE_CONNECTED) {
                        Toast.makeText(FindingRoom.this, "Bạn đã vào phòng của " + DeviceSelected.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FindingRoom.this, CreatingRoom.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

}
