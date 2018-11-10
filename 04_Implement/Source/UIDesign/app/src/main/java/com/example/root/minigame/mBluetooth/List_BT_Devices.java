package com.example.root.minigame.mBluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.root.minigame.R;

import java.util.ArrayList;

public class List_BT_Devices extends Activity {

    public static final String DEVICE_ADDRESS = "device_address";
    private static final String DEVICE_CONNECTED_NAME = "device_name";
    private BluetoothAdapter mBTAdapter;
    private BluetoothDevice DeviceSelected = null;// phai gan gia tri cho DeviceSelected

    private ListView DeviceList;
    private final int REQUEST_CODE_ENABLE = 101;
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<>();
    private ArrayList<String> Names = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_list__bt__devices);

        AnhXa();

    }



    private void AnhXa() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBTAdapter == null){
            setResult(RESULT_CANCELED);
            Toast.makeText(this, "Bluetooth Doesn't Support On This Device!", Toast.LENGTH_SHORT).show();
            finish();
        }

        DeviceList.setOnItemClickListener(mDeviceCLick);

        setResult(RESULT_CANCELED);
        if(!mBTAdapter.isEnabled()){
            Intent OnInten = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(OnInten,REQUEST_CODE_ENABLE);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mBRFoundDevices, filter);
        CheckBTpermission();
        discovery();
    }

    private void discovery() {
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
        }
        mBTAdapter.startDiscovery();

    }

    private void CheckBTpermission() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permistioncheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permistioncheck+= this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permistioncheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
            else
            {
            }
        }
    }


    private AdapterView.OnItemClickListener mDeviceCLick //goi hàm nay de listening event click phai gan gia tri cho DeviceSelected
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mBTAdapter.cancelDiscovery();
            DeviceSelected = mDevices.get(position);
            String address = DeviceSelected.getAddress();

            Intent intent = new Intent();
            intent.putExtra(DEVICE_ADDRESS, address);
            intent.putExtra(DEVICE_CONNECTED_NAME,DeviceSelected.getName());
            setResult(RESULT_OK, intent);
            finish();
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
    protected void onDestroy() {
        super.onDestroy();
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
        }
        unregisterReceiver(mBRFoundDevices);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_OK){
            Toast.makeText(this, "Bluetooth On", Toast.LENGTH_SHORT).show();
        }
        else if(requestCode == REQUEST_CODE_ENABLE && resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private final BroadcastReceiver mBRFoundDevices = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    //list devices paired
                    mDevices.add(device);
                    Names.add(device.getName());
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getApplication(),android.R.layout.simple_list_item_1, Names);
                    DeviceList.setAdapter(arrayAdapter);
                }
                else if (device.getBondState() == BluetoothDevice.BOND_NONE){
                    //list new devices
                }
            }
        }
    };

}
