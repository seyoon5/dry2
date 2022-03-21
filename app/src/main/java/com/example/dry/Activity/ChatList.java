package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Half;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dry.Adapter.ChatListAdapter;
import com.example.dry.Item.ChatItem;
import com.example.dry.Item.ChatListModel;
import com.example.dry.Item.ChatRoomItem;
import com.example.dry.Item.ChatType;
import com.example.dry.MyService;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.RetrofitBuilder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatList extends AppCompatActivity {

    ImageView home, board, location, chat, profile;

    private RecyclerView mRecyclerView;
    private ChatListAdapter mChatListAdapter;
    private PreferenceHelper preferences;
    private ArrayList<ChatRoomItem> mChatListItems;
    private String TAG = "ChatList";
    private String clientNick;

    private Handler mHandler;

    private String receiver;
    private String receiverProfile;
    private String roomIdx;

    private DataInputStream read = null;
    private DataOutputStream write = null;

    //private String IP = "13.125.206.46";
    private String IP = "3.34.5.22";
    private int PORT = 8080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        Log.e(TAG, "onCreate : " );

        mHandler = new Handler();
        preferences = new PreferenceHelper(this);
        mRecyclerView = findViewById(R.id.recyclerView_chatList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatListItems = new ArrayList<>();
        mChatListAdapter = new ChatListAdapter(mChatListItems);
        mRecyclerView.setAdapter(mChatListAdapter);

       // new SocketClient().start();

        mChatListAdapter.setOnItemClickListener(new ChatListAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {

                String me = preferences.getNICK();
                roomIdx = mChatListItems.get(a_position).getIdx();

                if(me.equals(mChatListItems.get(a_position).getReceiver())){
                    receiver = mChatListItems.get(a_position).getSender();
                    receiverProfile = mChatListItems.get(a_position).getSender_profile();

                } else if(me.equals(mChatListItems.get(a_position).getSender())){
                    receiver = mChatListItems.get(a_position).getReceiver();
                    receiverProfile = mChatListItems.get(a_position).getReceiver_profile();
                }

//                Intent intent = new Intent(getApplicationContext(), MyService.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.putExtra("receiver", receiver);
//                intent.putExtra("receiverProfile", receiverProfile);
//                intent.putExtra("roomIdx", roomIdx);
//                intent.putExtra("chatList", chatList);
//                startService(intent);

                Intent intent2 = new Intent(ChatList.this, Chat.class);
                //intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent2.putExtra("receiver", receiver);
                intent2.putExtra("receiverProfile", receiverProfile);
                intent2.putExtra("roomIdx", roomIdx);
                startActivity(intent2);
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = getIntent();
//                processCommand(intent);
//            }
//        }).start();
        startService(new Intent(ChatList.this, MyService.class));
        menu();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume : " );
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getChatLIst();
            }
        }, 10);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("server-message"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause : " );
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String roomNo = intent.getStringExtra("roomNo");
            Log.e(TAG, "2.9 리시버 동작, getList 작동 : " );
            getChatLIst();
        }
    };
//    @Override
//    protected void onNewIntent(Intent intent) {
//        processCommand(intent);
//        super.onNewIntent(intent);
//    }
//
//    private void
//    processCommand(Intent passedIntent) {
//        if(passedIntent != null){
//
//            String msg = passedIntent.getStringExtra("msg");
//            Log.e(TAG, "msg : "+msg );
//
//            if(msg != null){
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                       //content.setText(msg);
//                            }
//                        });
//                    }
//                }).start();
//            }
//        }
//    }

    private void getChatLIst() {
        clientNick = preferences.getNICK();

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);

        Call<List<ChatListModel>> call = service.getChatListApi(clientNick);
        Log.e(TAG, "clientNick : " +clientNick);
        call.enqueue(new Callback<List<ChatListModel>>() {
            @Override
            public void onResponse(Call<List<ChatListModel>> call, Response<List<ChatListModel>> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.e(TAG, "onResponse(Call<ArrayList<ChatListModel : " +response.isSuccessful());
                    Log.e(TAG, "2.9 채팅방 목록 몇개인지 불러오기 : " +response.body().size());
                    updateChatList(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<ChatListModel>> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
    }

    private void updateChatList(List<ChatListModel> model) {
        Log.e(TAG, "리시버가 불러온 getList : " );
        mChatListItems.removeAll(mChatListItems);
        mChatListAdapter.notifyDataSetChanged();
        for (int i = 0; i < model.size(); i++){
            ChatRoomItem items = new ChatRoomItem(model.get(i).getIdx(), model.get(i).getUser(), model.get(i).getReceiver_profile(),
                    model.get(i).getSender(), model.get(i).getContent(), model.get(i).getTime(), model.get(i).getReceiver(),
                    model.get(i).getSender_profile(), model.get(i).getRead_cnt_receiver(), model.get(i).getRead_cnt_sender());
            Log.e(TAG, "2.9 레트로핏으로 db 에서 받아온 채팅방 목록 업데이트  " );
            Log.e(TAG, "getIdx : "+model.get(i).getIdx() );
            Log.e(TAG, "getUser : "+model.get(i).getUser() );
            Log.e(TAG, "getReceiver_profile : "+model.get(i).getReceiver_profile() );
            Log.e(TAG, "getSender : "+model.get(i).getSender() );
            Log.e(TAG, "getContent : "+model.get(i).getContent() );
            Log.e(TAG, "getTime : "+model.get(i).getTime() );
            Log.e(TAG, "getReceiver : "+model.get(i).getReceiver() );
            Log.e(TAG, "getSender_profile : "+model.get(i).getSender_profile() );
            Log.e(TAG, "getRead_cnt_receiver : "+model.get(i).getRead_cnt_receiver() );
            Log.e(TAG, "getRead_cnt_sender : "+model.get(i).getRead_cnt_sender() );
            mChatListItems.add(items);
        }
        mChatListAdapter.notifyDataSetChanged(); // 어댑터에 정보 변경시 알림.
    }

    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChatList.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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



    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart : " );
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop : " );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy : " );
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
}