package com.example.dry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 1. NotificationManager , Notification Channel 생성
 *
 * 2. Notification Builder 생성
 *
 * 3. NotificationManager 로 Notification 전달
 */
public class StartActivity extends AppCompatActivity {

        // Channel에 대한 id 생성
        private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
        // Channel을 생성 및 전달해 줄 수 있는 Manager 생성
        private NotificationManager mNotificationManager;

        // Notification에 대한 ID 생성
        private static final int NOTIFICATION_ID = 0;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);

        // Notification을 호출할 button 변수
        Button button_notify = findViewById(R.id.notify);
            button_notify.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    sendNotification();
                }
            });
            createNotificationChannel();

        }

        //채널을 만드는 메소드
        public void createNotificationChannel()
        {
            //notification manager 생성
            mNotificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
            if(android.os.Build.VERSION.SDK_INT
                    >= android.os.Build.VERSION_CODES.O){
                //Channel 정의 생성자( construct 이용 )
                NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                        ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
                //Channel에 대한 기본 설정
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("Notification from Mascot");
                // Manager을 이용하여 Channel 생성
                mNotificationManager.createNotificationChannel(notificationChannel);
            }

        }

        // Notification Builder를 만드는 메소드
        private NotificationCompat.Builder getNotificationBuilder() {
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                    .setContentTitle("You've been notified!")
                    .setContentText("This is your notification text.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground);
            return notifyBuilder;
        }

        // Notification을 보내는 메소드
        public void sendNotification(){
            // Builder 생성
            NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
            // Manager를 통해 notification 디바이스로 전달
            mNotificationManager.notify(NOTIFICATION_ID,notifyBuilder.build());
        }
    }
