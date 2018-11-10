package com.example.root.minigame.mBluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.root.minigame.Interface.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//Tham Khao tu: https://developer.android.com/guide/topics/connectivity/bluetooth
//              https://www.youtube.com/results?search_query=bluetooth+p2p+android
//              Và một số tai lieu tren github
public class BuetoothConnectionService {




        private static final String TAG = "DEBUG_LOG";
        private static final String NAME_DEVICE = "MYBLUETOOTHSECURE";

        private static final UUID MY_UUID_SECURE =
                UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

        public static final int STATE_NONE = 0;       //Không làm gì cả
        public static final int STATE_LISTENNING = 1;     //Lắng nghe kết nối
        public static final int STATE_CONNECTING = 2; //Khởi tạo kết nối
        public static final int STATE_CONNECTED = 3;  //Đã kết nối

        private int mState, mNewState; //mNewstate: trang thai moi, mState: trang thai ket noi
        private ConnectThread mConnectThread;
        private AcceptThread mAcceptThread;
        private ConnectedThread mConnectedThread;

        private BluetoothAdapter mBTadapter;
        Context mContext;
        private final Handler mHandler;

        public BuetoothConnectionService(Context mContext, Handler mHandle) {
            String id = UUID.randomUUID().toString();
            this.mContext = mContext;
            mBTadapter = BluetoothAdapter.getDefaultAdapter();
            mState = STATE_NONE;
            this.mHandler = mHandle;
        }

        private synchronized void UpdateState(){
            mState = getState();// get state

            mNewState = mState;

            mHandler.obtainMessage(Messages.MESSAGE_STATE_CHANGE, mNewState, -1).sendToTarget();// send state to handler
        }

        public synchronized void start(){
            if(mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if(mConnectedThread != null){
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if(mAcceptThread == null){
                mAcceptThread = new AcceptThread();
                mAcceptThread.start();
            }
            UpdateState();
        }

        public synchronized void stop(){
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }

            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }
            if(mAcceptThread != null){
                mAcceptThread.cancel();;
                mAcceptThread = null;
            }
            mState = STATE_NONE;
            UpdateState();
        }

        public synchronized void connect(BluetoothDevice device){
            if (mState == STATE_CONNECTING) {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }

            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;
            }

            mConnectThread = new ConnectThread(device);
            mConnectThread.start();

            UpdateState();

        }

        public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
            if(mConnectThread != null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if(mConnectThread!=null){
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if(mAcceptThread != null){
                mAcceptThread.cancel();
                mAcceptThread = null;
            }

            mConnectedThread = new ConnectedThread(socket);
            mConnectedThread.start();

            Message msg = mHandler.obtainMessage(Messages.MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(Messages.DEVICE_NAME, device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);

            UpdateState();
        }

        public void write(byte[] outPut){
            ConnectedThread connectedThread;
            synchronized (this){
                if(mState != STATE_CONNECTED){return;}
                connectedThread = mConnectedThread;
            }
            connectedThread.write(outPut);
        }

        private void ConnectionFailed(){
            Message msg = mHandler.obtainMessage(Messages.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(Messages.TOAST, "Unable To Connect To Device");
            msg.setData(bundle);

            mState= STATE_NONE;
            UpdateState();
            BuetoothConnectionService.this.start();
        }

        private void ConnectionLost(){
            Message msg = mHandler.obtainMessage(Messages.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(Messages.TOAST, "Device Connection was lost");
            msg.setData(bundle);

            mState = STATE_NONE;

            UpdateState();

            BuetoothConnectionService.this.start();
        }

        public synchronized int getState(){return mState;}
        //this thread run while listennig for incomming connection
        private class AcceptThread extends Thread{
            private final BluetoothServerSocket mServerSocket;

            public AcceptThread(){
                BluetoothServerSocket temp = null;
                try {
                    temp = mBTadapter.listenUsingInsecureRfcommWithServiceRecord(NAME_DEVICE,MY_UUID_SECURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mServerSocket = temp;
                mState = STATE_LISTENNING;
            }

            public void Run(){
                BluetoothSocket mSocket = null;

                while(mState != STATE_CONNECTED){
                    try {
                        mSocket = mServerSocket.accept();
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                if(mSocket != null){
                    synchronized (BuetoothConnectionService.this){
                        switch (mState){
                            case STATE_LISTENNING:
                            case STATE_NONE:
                            case STATE_CONNECTING:
                                connected(mSocket, mSocket.getRemoteDevice());
                            case STATE_CONNECTED:
                                try {
                                    mSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }//switch
                    }//syn
                }
            }//run
            public void cancel(){
                try {
                    mServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //this thread run while try to make a connect.
        private class ConnectThread extends Thread{
            BluetoothSocket mSocket;
            BluetoothDevice mDevice;

            public ConnectThread(BluetoothDevice device){
                mDevice = device;
                BluetoothSocket temp = null;
                try {
                    temp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mSocket = temp;
                mState = STATE_CONNECTING;
            }

            public void  Run(){
                mBTadapter.cancelDiscovery();

                try {
                    mSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        mSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ConnectionFailed();
                    return;
                }
                synchronized (BuetoothConnectionService.this) {
                    mConnectThread = null;
                }// khoi tao lai connectThread.

                connected(mSocket, mDevice);
            }

            public void cancel(){
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        //this thread handle all tranmission.
        private class ConnectedThread extends Thread{
            BluetoothSocket mSocket;
            InputStream mInputStream;
            OutputStream mOutputStream;

            public ConnectedThread(BluetoothSocket socket){
                mSocket = socket;
                InputStream tempIn = null;
                OutputStream tempOut = null;

                try {
                    tempIn = socket.getInputStream();
                    tempOut = socket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Temp sockets not created", e);

                }

                mInputStream    = tempIn;
                mOutputStream   = tempOut;

                mState = STATE_CONNECTED;

            }

            public void Run(){
                Byte[] buffer = new Byte[1024];

                int bytes;

                while (mState == STATE_CONNECTED){
                    try {
                        bytes = mInputStream.read();// doc du lieu

                        mHandler.obtainMessage(Messages.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Disconnected", e);
                        ConnectionLost();
                        break;
                    }//catch

                }//while
            }//run

            public void write(byte[] buffer){

                try {
                    mOutputStream.write(buffer);

                    mHandler.obtainMessage(Messages.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Exception during write", e);
                }//catch

            }//write

            public void cancel(){
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"Close Failed",e);
                }
            }//cancel
        }



}
