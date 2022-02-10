package com.example.dry.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.Buffer;

public class ConnectionService extends Service {
    private static final String TAG = "ConnectionService";
    // 소켓의 상태를 표현하기 위한 상수
    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;
    // 소켓 연결 대기시간 (5초)
    final int TIME_OUT = 5000;

    private int status = STATUS_DISCONNECTED;
    private Socket socket = null;
    private SocketAddress socketAddress = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;
    private int port = 8080;


    IConnectionService.Stub binder = new IConnectionService.Stub() {
        @Override
        public int getStatus() throws RemoteException {
            return status;
        }

        @Override
        public void setSocket(String ip) throws RemoteException {
            mySetSocket(ip);
        }

        @Override
        public void connect() throws RemoteException {
            myConnect();
        }

        @Override
        public void disconnect() throws RemoteException {
            myDisconnect();
        }

        @Override
        public void send() throws RemoteException {
            mySend();
        }

        @Override
        public void receive() throws RemoteException {
            myReceive();
        }
    };

    public ConnectionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate : " );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand : " );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy : " );
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.e(TAG, "onBind : " );
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind : " );
        return super.onUnbind(intent);
    }

    void mySetSocket(String ip){
        Log.e(TAG, "mySetSocket : "+ip );
        socketAddress = new InetSocketAddress(ip, port);
        Log.e(TAG, "mySetSocket : " );
    }

    void myConnect(){
        Log.e(TAG, "myConnect 1" );
        socket = new Socket();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket.connect(socketAddress, TIME_OUT);
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.e(TAG, "myConnect 2 : " );
                } catch (IOException e) {
                    e.printStackTrace();
                }
                status = STATUS_CONNECTED;
            }
        }).start();
    }

    void myDisconnect(){
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        status = STATUS_DISCONNECTED;
    }

    void mySend(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msg = "hello, world !";
                try {
                    writer.write(msg, 0, msg.length());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    void myReceive(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "ConnectionService : "+reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}