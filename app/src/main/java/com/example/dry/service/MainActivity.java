package com.example.dry.service;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dry.R;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MainActivity";

    // 소켓의 상태를 표현하기 위한 상수
    final int STATUS_DISCONNECTED = 0;
    final int STATUS_CONNECTED = 1;

    String ip = "13.125.206.46";
    // ConnectionService 의 binder 를 가지고 있는 SocketManager instance
    SocketManager manager = null;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_service);
        Log.e(TAG, "onCreate : " );
        // get SocketManager getInstance
        manager = SocketManager.getInstance();
        Button btn1 = findViewById(R.id.btn_connectToServer);
        Button btn2 = findViewById(R.id.btn_sendData);
        Button btn3 = findViewById(R.id.btn_receiveData);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    connectToServer();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendData();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    receiveData();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume : " );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void connectToServer() throws RemoteException {
        manager.setSocket(ip);
        manager.connect();
    }
    public void sendData() throws RemoteException{
        if(manager.getStatus() == STATUS_CONNECTED){
            manager.receive();
        }else{
            Toast.makeText(this, "not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    public void receiveData() throws RemoteException{
        if(manager.getStatus() == STATUS_CONNECTED){
            manager.receive();
        }else{
            Toast.makeText(this, "not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

}
