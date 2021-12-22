package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dry.R;

public class ChatList extends AppCompatActivity {
    ImageView home, board, location, chat, profile;
    private Button enter_btn;
    private EditText username, roomNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        enter_btn = findViewById(R.id.enter_btn);

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Chat.class);
                startActivity(intent);
            }
        });
        menu();
    }


    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
    }
    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }else{
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}