//package com.example.dry;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.w3c.dom.Text;
//
//import java.io.DataOutputStream;
//
//public class MainActivity2 extends AppCompatActivity{
//
//    MyService myService;
//    boolean isService = false; // 서비스 실행여부 확인용
//    ServiceConnection conn = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            // 서비스와 연결되었을 때 호출되는 매서드
//            // 서비스 객체를 전역변수로 저장
//            MyService.LocalBinder mb = (MyService.LocalBinder) service;
//            myService = mb.getService(); //서비스가 제공하는 메소드를 호출하여 서비스쪽 객체를 전달받을 수 있음
//            isService = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            // 서비스와 연결이 끊겼을 떄 호출되는 매소드
//            isService = false;
//            Toast.makeText(getApplicationContext(), "서비스 연결 해재ㅔ", Toast.LENGTH_SHORT).show();
//        }
//    };
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//
//        Button b1 = (Button) findViewById(R.id.button11);
//        Button b2 = (Button) findViewById(R.id.button22);
//        Button b3 = (Button) findViewById(R.id.button33);
//        TextView tv = findViewById(R.id.textView11);
//        Intent intent = new Intent(MainActivity2.this, MyService.class);
//        bindService(intent, conn, Context.BIND_AUTO_CREATE);
//
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity2.this, MyService.class);
//                bindService(intent, conn, Context.BIND_AUTO_CREATE);
//            }
//        });
//
//        b2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isService){
//                    unbindService(conn);
//                    isService = false;
//                    Toast.makeText(MainActivity2.this, "서비스 종료, 연결된 서비스 없음", Toast.LENGTH_SHORT).show();
//                } else{
//                    Toast.makeText(MainActivity2.this, "연결된 서비스 없음", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        b3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isService){
//                    Toast.makeText(getApplicationContext(), "서비스중이 아님. 데이터 받을 수 없음", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                int num = myService.getNo(); // 서비스쪽 메소드로 값 전달 받아 호출
//                int test = MyService.number;
//
//                Toast.makeText(getApplicationContext(), "받은 데이터 메소드 getNo : "+num+" 필드변수 직접 : "+test, Toast.LENGTH_SHORT).show();
//            }
//        });
//        b1.performClick(); // 클릭 이벤트 강제 발생시키기
//
//    }
//
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(isService){
//            unbindService(conn);
//            isService = false;
//        }
//    }
//}