
package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dry.Adapter.ChatAdapter;
import com.example.dry.Item.ChatItem;
import com.example.dry.Item.ChatListModel;
import com.example.dry.Item.ChatRoomItem;
import com.example.dry.Item.ChatType;
import com.example.dry.MyService;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.ChatModel;
import com.example.dry.Retrofit.RetrofitBuilder;
import com.example.dry.databinding.ChatLeftItemBinding;
import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class NetworkClient extends AppCompatActivity {

    private PreferenceHelper preferenceHelper;
    private static final String TAG = "Chat";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private ImageButton btn_gallery;
    private ImageButton btn_send;
    private EditText ed_chat;

    private Socket mSocket;

    private String IP = "13.125.206.46";
    private int PORT = 8080;
    private Handler mHandler;

    private String senderProfile;
    private String receiverProfile;
    private String sender;
    private String receiver;
    private String roomNo;

    private ArrayList<ChatRoomItem> roomItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        preferenceHelper = new PreferenceHelper(this);
        sender = preferenceHelper.getNICK();
        senderProfile = preferenceHelper.getImage();
        recyclerView = findViewById(R.id.recyclerView_chat);
        btn_gallery = findViewById(R.id.image_btn_chat);
        btn_send = findViewById(R.id.send_btn_chat);
        ed_chat = findViewById(R.id.content_edit_chat);

        Intent intent = getIntent();
        String receiverList = intent.getStringExtra("receiver");
        String receiverBoard = intent.getStringExtra("username");
        String receiverProfileList = intent.getStringExtra("receiverProfile");
        String receiverProfileBoard = intent.getStringExtra("userProfile");

        if(receiverList == null){
            receiver = receiverBoard;
            receiverProfile = receiverProfileBoard;
            Log.e(TAG, "receiverList == null) : " );
        }
        if(receiverBoard == null){
            receiver = receiverList;
            receiverProfile = receiverProfileList;
            Log.e(TAG, "(receiverBoard == null : " );
        }
        Log.e(TAG, "receiver ????????? ?????? : " +receiver);
        Log.e(TAG, "receiver profile : " +receiverProfile);
        Log.e(TAG, "sender ??? ?????? : " +sender);
        Log.e(TAG, "sender profile : " +senderProfile);

        adapter = new ChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String roomInfo = receiver+"@"+sender+"@"+receiverProfile+"@"+senderProfile;
        new SocketClient(roomInfo).start();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = ed_chat.getText().toString();
                ed_chat.setText("");
                new SendThread(mSocket, roomNo+"@"+receiver+"@"+sender+"@"+msg).start();                // 0?????????, 1?????? ??????, 2????????????, 3?????????

            }
        });
    }

    private void getChatMsg() {

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);

        Call<List<ChatModel>> call = service.getChatMsgApi(roomNo);
        Log.e(TAG, "getChatMsgApi(roomNo) : "+roomNo );
        call.enqueue(new Callback<List<ChatModel>>() {
            @Override
            public void onResponse(Call<List<ChatModel>> call, Response<List<ChatModel>> response) {
                if(response.isSuccessful() && response.body() != null){
                    updateChatMsg(response.body());
                }
                Log.e(TAG, "getChatMsg onResponse(Call<List<ChatModel>>  : " +response.isSuccessful());

            }

            @Override
            public void onFailure(Call<List<ChatModel>> call, Throwable t) {
                Log.e(TAG, "getChatMsg onFailure(Call<List<ChatModel>>  : " +t.getMessage());
            }
        });
    }

    private void updateChatMsg(List<ChatModel> model) {
        for (int i = 0; i < model.size(); i++){
            String room_num = model.get(i).getIdx();
            Log.e(TAG, "room_num : " +room_num);
            String receiver_profile = model.get(i).getReceiver_profile();
            String and_receiver = model.get(i).getReceiver();
            String and_sender = model.get(i).getSender();
            String sender_profile = model.get(i).getSender_profile();
            String contents = model.get(i).getContents();
            String time = model.get(i).getTime();
            String identity = model.get(i).getIdentity();

            Log.e(TAG, "?????? : and_receiver:"+and_receiver+" and_sender:"+and_sender );
            Log.e(TAG, "?????? sender : "+sender );

            if(room_num.equals(roomNo)){
                if(contents != null) {
                    if (sender.equals(identity)) {
                        adapter.addItem(new ChatItem(room_num, receiverProfile, receiver, contents, time, ChatType.RIGHT_MESSAGE, room_num));
                        Log.e(TAG, "1 : receiver : "+receiver );
                    } else {
                        adapter.addItem(new ChatItem(room_num, receiverProfile, receiver, contents, time, ChatType.LEFT_MESSAGE, room_num));
                        Log.e(TAG, "2 : sender : "+sender );
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }


    /**
     * ?????? ???????????? ????????? ???????????? ??????
     */
    class SocketClient extends Thread{

        String userInfo; // ??? ?????? ( ????????? /  ????????? ????????? )

        DataInputStream in = null;
        DataOutputStream output = null;

        public SocketClient(String userInfo){
            this.userInfo = userInfo;
        }


        public void run(){
            try {
                // ?????? ????????? ?????? ( ?????? )  ( ????????? ip??? ?????? )
                mSocket = new Socket(IP,PORT);

                // ???????????? ????????? ?????? ??? ??? ?????? ?????? ( ????????? )
                output = new DataOutputStream(mSocket.getOutputStream());
                in = new DataInputStream(mSocket.getInputStream());

                // ????????? ?????? ????????? ??????  ( ???????????? ????????? ???????????? ????????? ?????? ) -  ????????? ????????? ?????? ??? ??????.
                output.writeUTF(userInfo);
                roomNo =in.readUTF();
                Log.e(TAG, "roomNo MSG2  : +" +roomNo);
                getChatMsg();
                // (????????? ????????? ????????? ?????? ) ????????? ????????? ??????
                Thread receive = new ReceiveThread(mSocket);
                receive.start();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????? ?????????
     */
    class SendThread extends  Thread{
        Socket socket;
        String sendmsg;
        DataOutputStream  writer;

        public SendThread(Socket socket, String sendmsg ){
            this.socket = socket;
            this.sendmsg =sendmsg;
            try {
                // ?????? ????????? ???????????? ????????? ??????  ????????? ??????.
                writer = new DataOutputStream (socket.getOutputStream());

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // ????????? ????????? ?????? ( ???????????? ??????????????? temp ??? ????????? ??????.
        public  void run(){
            try {
                if ( writer != null){
                    if (sendmsg != null){

                        // ????????? ???????????? ????????? ????????? ?????? ?????? ???????????? ???????????? .
                        // ????????? ????????? ???????????? ??????
                        writer.writeUTF(sendmsg);
                        Log.e(TAG, "3 writer.writeUTF(sendmsg); : "+sendmsg );
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * ????????? ?????????
     */
    class ReceiveThread extends Thread{

        Socket socket = null;
        DataInputStream  read = null;

        public ReceiveThread(Socket socket) {
            this.socket = socket;

            try {
                // ?????? ??????????????? ???????????? ?????? ?????? ????????? ??????.
                read = new DataInputStream(socket.getInputStream());
                Log.e(TAG, "new DataInputStream(socket.getInputStream()); : "+read );
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (read != null){
                    // ?????? ????????? ?????? ?????? ?????????
                    String msg =read.readUTF();

                    Log.e(TAG, "????????? ?????? read.readUTF(); : "+msg );

                    if (msg != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 0????????????, 1?????????, 2??????
                                Log.e(TAG, "msg : "+msg);
                                String[] filter = msg.split("@");
                                String s_sender = filter[0];
                                String s_msg = filter[1];
                                String s_time = filter[2];
//
//                                Intent intent = new Intent(getApplicationContext(), MyService.class);
//                                intent.putExtra("msg", s_msg);
//                                startService(intent);

                                Log.e(TAG, "s_sender : " +s_sender);
                                if (sender.equals(s_sender)) {
                                    adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.RIGHT_MESSAGE));
                                } else {
                                    adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.LEFT_MESSAGE));
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            }
                        });
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("????????????");
            if(mSocket != null){
                mSocket.close();
            }
        } catch (IOException e) {
            System.out.println("??????????????? : Chat.class - onDestroy");
            e.printStackTrace();
        }
    }
}
// ????????? ?????? ????????? ???????????? ??????
//    @Override
//    public void run() {
//        Log.e(TAG, "main run()");
//        try {
//                while (true) {
//                    if (in != null) {
//                        msg = in.readLine();    //msg: ????????? ?????? ?????????
//                        //msg==> "nick,msg"
//                        String msgs[] = msg.split(",");
//                        String user = msgs[0];
//                        String message = msgs[1];
//                        Log.e(TAG, "user :"+user+ "/message :"+message );
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.e(TAG, "user : "+user+"  /nick : "+nick);
//                                if (!nick.equals(user)) {
//                                    adapter.addItem(new ChatItem(username, message, "time", ChatType.RIGHT_MESSAGE));
//                                } else {
//                                    adapter.addItem(new ChatItem(username, message, "time", ChatType.LEFT_MESSAGE));
//                                }
//                                adapter.notifyDataSetChanged();
//                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
//                            }
//                        });
//                    }
////                switch (user) {
////                    case "":
////                        cc.ta.append(msgs[1] + "\n");
////                        cc.ta.setCaretPosition(cc.ta.getText().length());
////                        break;
////                }
//                }
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }

//?????? ????????? ??????
//adapter.addItem(new ChatItem("welcome", "???????????? ", "time", ChatType.CENTER_MESSAGE));

//        new Thread() {
//            public void run() {
//                try {
//                    mSocket = new Socket(IP, PORT);
//                    sendWriter = new PrintWriter(mSocket.getOutputStream());
//                    BufferedReader input = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
//                    sendWriter.println(nick);
//                    sendWriter.flush();
//                    sendWriter.println(username);
//                    sendWriter.flush();
//                    while (true) {
//                        read = input.readLine();
//                        Log.e(TAG, "read, ??????????????? : " +read);
//                        if (read != null) {
//                            System.out.println(read);
//                            mHandler.post(new msgUpdate(read, username));
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

// retrofit ?????? ??? ?????? ????????????
//    private void getRoomInfo(String username, String nick) {
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
//                .addConverterFactory(ScalarsConverterFactory.create()).build();
//        ApiInterface service = retrofit.create(ApiInterface.class);
//        Call<String> call = service.getChatRoomInfo(username, nick);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                String data = response.body();
//                Log.e(TAG, "onResponse : "+data);
//                try {
//                    JSONObject jsonObject = new JSONObject(data);
//                    String message = jsonObject.getString("message");
//                    if(message.equals("none")){
//                        ChatRoomItem roomItem = new ChatRoomItem("null");
//                        roomNo = roomItem.getIdx();
//                        Log.e(TAG, "message.equals(\"none\") : " +roomNo);
//                    }else{
//                        Log.e(TAG, "parseChatRoomData(data) : ??????, ????????? ???????????? ??????" );
//                        parseChatRoomData(data);
//                        for (int i = 0; i < roomItems.size(); i++){
//                            String users = roomItems.get(i).getUser();
//                            Log.e(TAG, "users : " +users);
//                            if(users.contains(username) && users.contains(nick)){
//                                roomNo = roomItems.get(i).getIdx();
//                            }
//                        }
//                        Log.e(TAG, "receiver is not null, roomNo : " +roomNo);
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e(TAG, "getChatInfo,getChatInfo : " +t.getMessage());
//            }
//        });
//
//    }
//    private void parseChatRoomData(String response) {
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            JSONArray jsonArray = jsonObject.getJSONArray("data");
//            Log.e(TAG, "parseBoardData : " + jsonArray);
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject item = jsonArray.getJSONObject(i);
//
//                String idx = item.optString("idx");
//                String user = item.optString("user");
//                String sender = item.optString("sender");
//                String receiver = item.optString("receiver");
//
//                Log.e(TAG, "parseChatRoomData : idx:" + idx + " /user:" + user + " / sender:" + sender + " / receiver:" + receiver);
//                ChatRoomItem items = new ChatRoomItem(idx, user, sender, receiver, idx);
//                roomItems.add(items);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }