package com.example.root.minigame.mBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;


public class BluetoothConnection {

    public BluetoothConnectionService mBTconnection = null;
    public BluetoothAdapter mBTadapter;
    public String mConnectedDeviceAddress;


    public BluetoothConnection( ) {
        this.mBTadapter = BluetoothAdapter.getDefaultAdapter();
        mConnectedDeviceAddress = "";
    }

    public void StartConnection(Handler mHandel){
        if(mBTconnection == null){
            mBTconnection = new BluetoothConnectionService(mHandel);
        }
        mBTconnection.start();
    }

    public void Connect(String address){
        mConnectedDeviceAddress = address;
        BluetoothDevice device = mBTadapter.getRemoteDevice(address);
        mBTconnection.connect(device);
    }
    public int sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBTconnection.getState() != BluetoothConnectionService.STATE_CONNECTED) {
            return -1;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBTconnection.write(send);
            // Reset out string buffer to zero and clear the edit text field
        }
        return 0;
    }

    public void OnResume(){
        if(mBTconnection != null){
            if (mBTconnection.getState() == BluetoothConnectionService.STATE_NONE) {
                // Start the Bluetooth chat services
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

    public void Reconnect(){
        if(!mConnectedDeviceAddress.equals("")){
            if(mBTconnection != null){
                    this.mBTconnection.start();
                    this.Connect(mConnectedDeviceAddress);
            }
        }
    }

    public String getmConnectedDeviceAddress() {
        return mConnectedDeviceAddress;
    }

    public void setmConnectedDeviceAddress(String mConnectedDeviceAddress) {
        this.mConnectedDeviceAddress = mConnectedDeviceAddress;
    }
}
