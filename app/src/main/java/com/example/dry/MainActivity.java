package com.example.dry;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    MyService2 myService2; // 서비스 객체
    boolean isService = false; // 서비스 중인 확인용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
// 데이터를 전달할 수 있는 서비스 사용하기
// 1. 다음 Service (*.java)를 작성한다
// 2. Service 를 등록한다 AndroidManifest.xml
// 3. Service 를 시작한다

        Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        Button b3 = (Button) findViewById(R.id.button3);
        Button b4 = (Button) findViewById(R.id.button4);

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // 서비스 시작
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면
                        MyService.class); // 다음넘어갈 컴퍼넌트
                startService(intent);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // 서비스 종료
                Intent intent = new Intent(
                        getApplicationContext(), // 현재 화면
                        MyService.class); // 다음넘어갈 컴퍼넌트
                stopService(intent);

            }
        });


    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            MyService2.LocalBinder mb = (MyService2.LocalBinder) service;
            myService2 = mb.getService(); // 서비스가 제공하는 메소드 호출하여 서비스쪽 객체를 전달받을 수 있음.
            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isService = false;
            Toast.makeText(myService2, "disconnected service", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isService){
            unbindService(conn);
            isService = false;
        }
    }
}