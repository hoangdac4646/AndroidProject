package com.example.root.minigame.mBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;


public class BluetoothConnection {

    public BluetoothConnectionService mBTconnection = null;
    public BluetoothAdapter mBTadapter;

    public BluetoothConnection( ) {
        this.mBTadapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void StartConnection(Handler mHandel){
        if(mBTconnection == null){
            mBTconnection = new BluetoothConnectionService(mHandel);
        }
        if (mBTadapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        }
        mBTconnection.start();
    }

    public void Connect(String address){
        BluetoothDevice device = mBTadapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBTconnection.connect(device);
    }


    public int sendMessage(String message) {
        if (mBTconnection.getState() != BluetoothConnectionService.STATE_CONNECTED) {
            return -1;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mBTconnection.write(send);
        }
        return 0;
    }

    public void OnResume(){
        if(mBTconnection != null){
            if (mBTconnection.getState() == BluetoothConnectionService.STATE_NONE) {
                mBTconnection.start();
            }
        }

    }
    public void OnDestroy(){
        if(mBTconnection != null){
            mBTconnection.stop();
        }
    }
    public void setHandle(Handler mhandle){
        mBTconnection.setmHandler(mhandle);
    }

}
