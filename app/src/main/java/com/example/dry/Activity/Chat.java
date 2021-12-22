
package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dry.Adapter.ChatAdapter;
import com.example.dry.Item.ChatItem;
import com.example.dry.Item.ChatType;
import com.example.dry.MainActivity;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Scanner;


public class Chat extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;
    private static final String TAG = "Chat";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private ImageButton btn_gallery;
    private ImageButton btn_send;
    private EditText ed_chat;

    private Socket mSocket;
    private String username;
    private String roomNumber;

    private String IP = "13.125.206.46";
    private int PORT = 8080;
    private PrintWriter sendWriter;
    private Handler mHandler;

    private String read;
    private String sendmsg;
    private String nick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        preferenceHelper = new PreferenceHelper(this);
        nick = preferenceHelper.getNICK();
        recyclerView = findViewById(R.id.recyclerView_chat);
        btn_gallery = findViewById(R.id.image_btn_chat);
        btn_send = findViewById(R.id.send_btn_chat);
        ed_chat = findViewById(R.id.content_edit_chat);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Log.e(TAG, "username 상대방 이름 : " +username);
        Log.e(TAG, "nick 내 이름 : " +nick);

        adapter = new ChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        //adapter.addItem(new ChatItem("welcome", "대화방이 ", "time", ChatType.CENTER_MESSAGE));

        new Thread() {
            public void run() {
                try {
                    mSocket = new Socket(IP, PORT);
                    sendWriter = new PrintWriter(mSocket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    sendWriter.println(nick);
                    sendWriter.flush();
                    sendWriter.println(username);
                    sendWriter.flush();
                    while (true) {
                        read = input.readLine();
                        Log.e(TAG, "read, 받은메세지 : " +read);
                        if (read != null) {
                            System.out.println(read);
                            mHandler.post(new msgUpdate(read, username));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg = ed_chat.getText().toString();
                adapter.addItem(new ChatItem(username, sendmsg, "time", ChatType.RIGHT_MESSAGE));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sendWriter.println(sendmsg);
                            sendWriter.flush();
                            ed_chat.setText("");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    class msgUpdate implements Runnable {

        private String msg;
        private String nick;

        public msgUpdate(String str, String nick) {
            this.msg = str;
            this.nick = nick;
        }

        @Override
        public void run() {
            Log.e(TAG, "쉐어드 닉 : " +Chat.this.nick);
            Log.e(TAG, "post nick : " +nick);
            adapter.addItem(new ChatItem(username, msg, "time", ChatType.LEFT_MESSAGE));
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.getItemCount());
        }
    }
    //    private void sendMessage() {
//        mSocket.emit("newMessage", gson.toJson(new MessageData("MESSAGE",
//                username,
//                roomNumber,
//                binding.contentEdit.getText().toString(),
//                System.currentTimeMillis())));
//        binding.contentEdit.setText("");
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("연결종료");
            if(mSocket != null){
                mSocket.close();
            }
        } catch (IOException e) {
            System.out.println("오류메세지 : Chat.class - onDestroy");
            e.printStackTrace();
        }
    }
}
