package com.example.root.minigame.mBluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.root.minigame.Interface.Messages;

public class BluetoothConnection {


    private String Connected_Device_Name= null;

    private BluetoothAdapter mBTAdapter;
    private BuetoothConnectionService mBTServer;
    Context mContext;

    public BluetoothConnection(Context context){
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = context;
        if(mBTAdapter == null){

        }
    }
    //goi trong hàm onstart cua activity
    public int StartConnection(){
        if(!mBTAdapter.isEnabled() || mBTAdapter == null){
           return Messages.BLUETOOTH_OFF;
        }
        else if(mBTServer == null){
            mBTServer = new BuetoothConnectionService(mHandler);
            mBTServer.start();
        }
        return Messages.OK_OK;
    }

    //goi trong ham ondestroy
    public void OnDestroy(){
        if(mBTServer != null){
            mBTServer.stop();
        }
    }

    //goi trong ham onresume
    public void OnResume(){
        if(mBTServer != null){
            if(mBTServer.getState() == BuetoothConnectionService.STATE_NONE){
                mBTServer.start();
            }
        }
    }

    public void Discoverable(){
        if(mBTAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            mContext.startActivity(discoverableIntent);
        }
    }

    public void SendData(String data){
        if(mBTServer.getState() != BuetoothConnectionService.STATE_CONNECTED){
            Toast.makeText(mContext, "You Are Not Connected To Any Devices", Toast.LENGTH_SHORT).show();
            return;
        }
        if(data.length() > 0){
            byte[] send = data.getBytes();
            mBTServer.write(send);// gui du lieu sang
        }

    }
    public void HandleMSG(){
    }
    //day chi la mẫu vui lòng coppy vào class ban để xử lý
    public final Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case Messages.MESSAGE_STATE_CHANGE:
                {
                    switch (msg.arg1){// dung de thong bao cho nguoi dung biet trang thai cua connection
                        case BuetoothConnectionService.STATE_CONNECTED:
                            //do something
                            break;
                        case BuetoothConnectionService.STATE_CONNECTING:
                            //do something
                            break;
                        case BuetoothConnectionService.STATE_NONE:
                            //do something
                            break;
                        case BuetoothConnectionService.STATE_LISTENNING:
                            //do something
                            break;
                    }//arg1
                    break;
                }
                case Messages.MESSAGE_READ:// nhan data sẽ làm gì vào đây
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    break;
                case Messages.MESSAGE_WRITE:// ghi data sẽ làm gì vào đây
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);

                    break;
                case Messages.MESSAGE_DEVICE_NAME:
                    if (null != mContext) {
                        Toast.makeText(mContext, "Connected to "
                                + Connected_Device_Name, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Messages.MESSAGE_TOAST:
                    if (null != mContext) {
                        Toast.makeText(mContext, msg.getData().getString(Messages.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


}
