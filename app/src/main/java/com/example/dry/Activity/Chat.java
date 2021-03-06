
package com.example.dry.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dry.Adapter.ChatAdapter;
import com.example.dry.Item.BoardItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Chat extends AppCompatActivity {

    private static final int CODE_ALBUM_REQUEST = 100;
    private PreferenceHelper preferenceHelper;
    private static final String TAG = "Chat";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;

    private ImageButton btn_gallery;
    private ImageButton btn_send;
    private EditText ed_chat;

    private Socket mSocket;
    private DataInputStream read = null;
    private DataOutputStream write = null;

    //private String IP = "13.125.206.46";
    private String IP = "3.34.5.22";
    private int PORT = 8080;
    private Handler mHandler;

    private String senderProfile;
    private String receiverProfile;
    private String sender;
    private String receiver;
    private String chatListRoomIdx;
    private String getRoomIdx;
    private String roomInfo;

    private String chatImage;

//    private String s_sender;
//    private String s_msg;
//    private String s_time;
//    private String s_user;
//    private String update;

    private String dbSender;

    private ArrayList<ChatRoomItem> roomItems = new ArrayList<>();

    private AlertDialog.Builder builder;
    private String[] select;
    private int CAMERA_REQUEST_CODE = 1001;
    private String mCurrentPhotoPath;
    private File f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate" );
        setContentView(R.layout.activity_chat);
        mHandler = new Handler();
        preferenceHelper = new PreferenceHelper(this);
        sender = preferenceHelper.getNICK();
        senderProfile = preferenceHelper.getImage();
        recyclerView = findViewById(R.id.recyclerView_chat);
        btn_gallery = findViewById(R.id.image_btn_chat);
        btn_send = findViewById(R.id.send_btn_chat);
        ed_chat = findViewById(R.id.content_edit_chat);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("server-msg"));
        //new SocketClient().start();   ????????? ?????? intent.startService ??? ?????? MyService onCreate ??? ???????????? ?????? ??????????????? ??????

        Intent intent = getIntent();
        String receiverList = intent.getStringExtra("receiver");
        String receiverBoard = intent.getStringExtra("username");
        String receiverProfileList = intent.getStringExtra("receiverProfile");
        String receiverProfileBoard = intent.getStringExtra("userProfile");
        String dbReceiver = intent.getStringExtra("dbReceiver");
        String dbReceiver_profile = intent.getStringExtra("dbReceiver_profile");
        String dbSender = intent.getStringExtra("dbSender");
        String dbSender_profile = intent.getStringExtra("dbSender_profile");

        Log.e(TAG, "receiverList : " +receiverList);
        Log.e(TAG, "receiverBoard : " +receiverBoard);
        Log.e(TAG, "receiverProfileList : " +receiverProfileList);
        Log.e(TAG, "receiverProfileBoard : " +receiverProfileBoard);
        Log.e(TAG, "dbReceiver : " +dbReceiver);
        Log.e(TAG, "dbReceiver_profile : " +dbReceiver_profile);
        Log.e(TAG, "dbSender : " +dbSender);
        Log.e(TAG, "dbSender_profile : " +dbSender_profile);

        if(receiverList == null){
            receiver = receiverBoard;
            receiverProfile = receiverProfileBoard;
        }
        if(receiverBoard == null){
            receiver = receiverList;
            receiverProfile = receiverProfileList;
        }
        if(receiver == null){
            Log.e(TAG, "?????????????????? ??? ?????? ????????? ??? : " +receiver);
            if(sender.equals(dbSender)){
                receiver = dbReceiver;
                receiverProfile = dbReceiver_profile;
                Log.e(TAG, "?????????????????? ??? ?????? ????????? ??? 1 sender.equals(dbSender): " +receiver);
            }else{
                receiver = dbSender;
                receiverProfile = dbSender_profile;
                Log.e(TAG, "?????????????????? ??? ?????? ????????? ??? 1: " +receiver);
            }

        }

        Log.e(TAG, "receiver ????????? ?????? : " +receiver);
        Log.e(TAG, "receiver profile : " +receiverProfile);
        Log.e(TAG, "sender ??? ?????? : " +sender);
        Log.e(TAG, "sender profile : " +senderProfile);

        adapter = new ChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        roomInfo = receiver+"@"+sender+"@"+receiverProfile+"@"+senderProfile;

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = ed_chat.getText().toString();
                ed_chat.setText("");
                makeRoom(msg);
            }
        });
    }

    private void showDialog() {
        select = getResources().getStringArray(R.array.selectImage);
        builder = new AlertDialog.Builder(Chat.this);
        builder.setTitle("?????? ????????????");
        builder.setItems(select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(select[which]){
                    case "?????????"  :
                        ActivityCompat.requestPermissions(Chat.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                        dispatchTakePictureIntent();
                    break;

                    case "?????????" :
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent, CODE_ALBUM_REQUEST);
                    break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Log.e(TAG, "2.7  photoFile:1 ="+photoFile );
            try {
                photoFile = createImageFile();
                Log.e(TAG, "2.7  photoFile:2 ="+photoFile );
            } catch (IOException ex) {
                Log.e(TAG, "?????? : dispatchTakePictureIntent,IOException" );
            } if(photoFile != null) {
                Log.e(TAG, "2.7  photoFile:3 ="+photoFile );
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.dry.fileprovider", photoFile);
                // FileProvider ??? (content)provider ?????? ??????????????? ???????????? ??????????????? ?????? ???????????? ??? ??????.
                // ????????? ??????????????? ??????????????? ???????????? authority ??? "com.example.dry.fileprovider" ??? ?????????????????? ????????? ?????? ???????????? ?????????.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // EXTRA_OUTPUT ????????? ???????????? ???????????? ???????????? ??? ????????? ????????? ????????? Uri??? ???????????? ??? ???????????? Intent-extra??? ??????
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE); }
        }
    }

    private File createImageFile() throws IOException {
        Log.e(TAG, "createImageFile : 1" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // getExternalFilesDir() ?????? ??????????????? ????????? ?????? ????????? ????????? ??? ?????? ?????? ??????????????? ?????? ??????????????? ?????????.
        // DIRECTORY_PICTURES ???????????? ????????? ??? ?????? ????????? ????????? ?????? ????????????, ?????? ????????? ?????? ??????????????? ?????? ??????.
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e(TAG, "createImageFile : image="+image );

        return image;
    }

    //?????????, ????????? ????????? ????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_ALBUM_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            chatImage = String.valueOf(imageUri);
            Log.e(TAG, "0128 ????????? ????????? :Uri =" +imageUri);
            makeRoom("??????");
        }else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            //f = new File(mCurrentPhotoPath);
            //Log.e(TAG, "  ???????????? ????????? ??????????????? f = new File(mCurrentPhotoPath); : " +f);
            makeRoom("??????");
            // ?????? ????????? ???????????? ???????????? ??????????????? ????????? ???????????? ??????????????? ?????????!
        } else {
            Log.e(TAG, "?????? //?????????, ????????? ????????? ???????????? : ?????????" );
        }

    }

    private void entered(String idx) {
                //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                        .addConverterFactory(ScalarsConverterFactory.create()).build();
                ApiInterface service = retrofit.create(ApiInterface.class);
                Call<String> call = service.enteredApi(idx);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e(TAG, "entered : onResponse" +response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "entered  onFailure: " +t.getMessage());
                    }
                });
    }

    private void makeRoom(String content) {

        SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");
        long mNow;
        Date mDate;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        String time = mFormat.format(mDate);
        String user = receiver+"#"+sender;
        Log.e(TAG, "makeRoom : user" +user);
        int count = 0;

        if(receiver.equals(sender)){
            Log.e(TAG, "??? ???????????? : " );
            return;
        }

        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.makeRoomApi(user, receiverProfile, sender, receiver, content, senderProfile, time, count);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "makeRoom : onResponse" );
                String data = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Log.e(TAG, "makeRoom msg :"+jsonObject.optString("message") );
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        getRoomIdx = item.optString("idx");
                        Log.e(TAG, "makeRoom ????????? ????????? : "+getRoomIdx );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent i = new Intent(Chat.this, MyService.class);
                i.putExtra("msg", roomInfo+"@"+content+"@"+getRoomIdx+"@"+time);
                i.putExtra("image", chatImage);
                i.putExtra("file", mCurrentPhotoPath);
                startService(i);
                chatImage = null;
                mCurrentPhotoPath = null;


//                Intent i = new Intent(getApplicationContext(), MyService.class);
//                i.putExtra("msg", roomInfo+"@"+content+"@"+getRoomIdx);
//                startService(i);

                //sendThread ??? ??????????????? ??????.
                //new SendThread(roomInfo+"@"+content).start();                // 0 receiver, 1 sender, 2 receiverProfile 3 senderProfile 4 msg
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "makeRoom onFailure : " +t.getMessage());
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = getIntent();
//                processCommand(intent);
//            }
//        }).start();
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        processCommand(intent);
//        super.onNewIntent(intent);
//    }

//    private void processCommand(Intent intent) {
//
//        if(intent != null){
//
//            String roomNo = intent.getStringExtra("roomNo");
//            String s_sender = intent.getStringExtra("s_sender");
//            String s_msg = intent.getStringExtra("s_msg");
//            String s_time = intent.getStringExtra("s_time");
//            String s_user = intent.getStringExtra("s_user");
//            String updateMsg = intent.getStringExtra("update");
//            Log.e(TAG, "updateMsg : "+updateMsg );
////            if(updateMsg != null){
////                Log.e(TAG, "?????? ??? ???????????? s_user : "+s_user );
////                Log.e(TAG, "receiver : "+receiver );
////                Log.e(TAG, "sender : "+sender );
////
////                new Thread(new Runnable() {
////                    @Override
////                    public void run() {
////                        mHandler.post(new Runnable() {
////                            @Override
////                            public void run() {
////                                if(s_user.contains(receiver) && s_user.contains(sender)){
////                                    if (sender.equals(s_sender)) {
////                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.RIGHT_MESSAGE));
////                                    } else {
////                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.LEFT_MESSAGE));
////                                    }
////                                    adapter.notifyDataSetChanged();
////                                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
////                                }
////                            }
////                        });
////                    }
////                }).start();
////            }
//        }
//    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "BroadcastReceiver " );
            String roomNo = intent.getStringExtra("roomNo");
            String s_sender = intent.getStringExtra("s_sender");
            String s_msg = intent.getStringExtra("s_msg");
            String s_time = intent.getStringExtra("s_time");
            String s_user = intent.getStringExtra("s_user");
            String s_receiver = intent.getStringExtra("s_receiver");
            String s_receiverProfile = intent.getStringExtra("s_receiverProfile");
            String update = intent.getStringExtra("update");

            if (update != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "null ?????? : sender "+sender );
                                Log.e(TAG, "null ?????? : s_sender "+s_sender );
                                Log.e(TAG, "null ?????? : receiver "+receiver );
                                Log.e(TAG, "null ?????? : s_receiver "+s_receiver );
                                Log.e(TAG, "null ?????? : s_msg "+s_msg );
                                    if (sender.equals(s_sender) && receiver.equals(s_receiver)) {
                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.RIGHT_MESSAGE));
                                    } else if(sender.equals(s_receiver) && receiver.equals(s_sender)){
                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.LEFT_MESSAGE));
                                    }
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            }
                        });
                    }
                }).start();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume : " );

        Intent intent = getIntent();
        chatListRoomIdx = intent.getStringExtra("roomIdx");
//        String dbSender = intent.getStringExtra("dbSender");
//        String dbSender_profile = intent.getStringExtra("dbSender_profile");
//        String dbReceiver = intent.getStringExtra("dbReceiver");
//        String dbReceiver_profile = intent.getStringExtra("dbReceiver_profile");
//
//        Log.e(TAG, "chatListRoomIdx : " +chatListRoomIdx);
//        Log.e(TAG, "dbSender : " +dbSender);
//        Log.e(TAG, "dbSender_profile : " +dbSender_profile);
//        Log.e(TAG, "dbReceiver : " +dbReceiver);
//        Log.e(TAG, "dbReceiver_profile : " +dbReceiver_profile);

        if(chatListRoomIdx != null){
            getChatMsg(chatListRoomIdx);
            entered(chatListRoomIdx);
        }
//        if(s_sender == null){
//            s_sender = dbSender;
//        }
//        if(receiver == null){
//            receiver = dbReceiver;
//            receiverProfile = dbReceiver_profile;
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause : " );
        exit(chatListRoomIdx);
        if(sender.equals(dbSender)){
            senderCount();
        }else{
            receiverCount();
        }
    }

    private void exit(String idx) {

                //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                        .addConverterFactory(ScalarsConverterFactory.create()).build();
                ApiInterface service = retrofit.create(ApiInterface.class);
                Call<String> call = service.exitApi(idx);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e(TAG, "exit : onResponse" +response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "exit : onFailure" +t.getMessage() );
                    }
                });

    }

    private void receiverCount() {
        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.readMsgApi(chatListRoomIdx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "receiverCount : " +response.isSuccessful());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "receiverCount : "+t.getMessage() );
            }
        });

    }

    private void senderCount() {
        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.readMsg2Api(chatListRoomIdx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "senderCount : " +response.isSuccessful());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "senderCount : "+t.getMessage() );
            }
        });

    }

//    private void readMsg() {
//
//        String read = "0";
//        // client nick ??? ???????????? server ?????? ??? ???????????? ?????? ??????
//                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
//                        .addConverterFactory(ScalarsConverterFactory.create()).build();
//                ApiInterface service = retrofit.create(ApiInterface.class);
//                Call<String> call = service.readMsgApi(chatListRoomIdx, read, sender);
//                Log.e(TAG, "readMsg, getRoomIdx: "+chatListRoomIdx );
//                call.enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        Log.e(TAG, "?????? ????????? ?????? ?????? : " );
//                        if(response.body().contains("readMsg is not operated")){
//                            Log.e(TAG, " readMsg is not operated" );
//                        }else if(response.body().contains("readMsg updated")){
//                            Log.e(TAG, "readMsg updated");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        Log.e(TAG, "?????? ????????? ?????? ?????? : " );
//                    }
//                });
//
//    }

    private void getChatMsg(String idx) {

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);

        Call<List<ChatModel>> call = service.getChatMsgApi(idx);
        Log.e(TAG, "chatListRoomIdx : "+idx );
        Log.e(TAG, "notiRoomIdx : "+idx );
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
            Log.e(TAG, "????????? ???????????? ?????????????????? ????????? roomNo: " +room_num);
            String receiver_profile = model.get(i).getReceiver_profile();
            String and_receiver = model.get(i).getReceiver();
            dbSender = model.get(i).getSender();
            String sender_profile = model.get(i).getSender_profile();
            String contents = model.get(i).getContents();
            String time = model.get(i).getTime();
            String identity = model.get(i).getIdentity();
            String profile = model.get(i).getProfile();

//            if(receiver == null || receiverProfile == null){
//                receiver = and_receiver;
//                receiverProfile = receiver_profile;
//                Log.e(TAG, "?????? ??? receiver : "+receiver );
//                Log.e(TAG, "?????? ??? receiverProfile : "+receiverProfile );
//            }

            if(room_num.equals(chatListRoomIdx)){
                if(contents != null) {
                    if (sender.equals(identity)) {
                        adapter.addItem(new ChatItem(room_num, profile, identity, contents, time, ChatType.RIGHT_MESSAGE, room_num));
                    } else  { //if(and_receiver.equals(identity))
                        adapter.addItem(new ChatItem(room_num, profile, identity, contents, time, ChatType.LEFT_MESSAGE, room_num));
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

        public SocketClient(){
        }

        public void run(){
            try {
                // ?????? ????????? ?????? ( ?????? )  ( ????????? ip??? ?????? )
                mSocket = new Socket(IP,PORT);
                // ???????????? ????????? ?????? ??? ??? ?????? ?????? ( ????????? )
                // ?????? ????????? ???????????? ????????? ??????  ????????? ??????.
                //write = new DataOutputStream (mSocket.getOutputStream());
                read = new DataInputStream(mSocket.getInputStream());
                Log.e(TAG, "1 socket ?????? ok : " );

                //write.writeUTF(roomInfo);
                //roomNo =read.readUTF();
                while(read != null) {
                    String msg =read.readUTF();
                    Log.e(TAG, "2 ???????????? ?????? msg : "+msg );

                    if (msg != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //  0 roomNO, 1????????????, 2?????????, 3??????
                                String[] filter = msg.split("@");
                                String roomNo = filter[0];
                                String s_sender = filter[1];
                                String s_msg = filter[2];
                                String s_time = filter[3];
                                String s_user = filter[4];
                                Log.e(TAG, "3 split ??? ????????? : " +roomNo+", "+s_sender+", "+s_msg+", "+s_time);

//                                Intent intent = new Intent(getApplicationContext(), MyService.class);
//                                intent.putExtra("msg", s_msg);
//                                startService(intent);

                                Log.e(TAG, "s_user : "+s_user );
                                Log.e(TAG, "s_sender : "+s_sender );
                                Log.e(TAG, "sender : "+sender );
                                if(s_user.contains(receiver) && s_user.contains(sender)){
                                    if (sender.equals(s_sender)) {
                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.RIGHT_MESSAGE));
                                    } else {
                                        adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.LEFT_MESSAGE));
                                    }
                                    adapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                                }
                            }
                        });
                    }
                }
                //  Thread receive = new ReceiveThread();
                //  receive.start();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * ????????? ?????????
     */
    class SendThread extends  Thread{

        String sendmsg;

        public
        SendThread(String sendmsg ){
            this.sendmsg =sendmsg;
            try {
                write = new DataOutputStream (mSocket.getOutputStream());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // ????????? ????????? ?????? ( ???????????? ??????????????? temp ??? ????????? ??????.
        public  void run(){
            try {
                if ( write != null){
                    if (sendmsg != null){

                        // ????????? ???????????? ????????? ????????? ?????? ?????? ???????????? ???????????? .
                        // ????????? ????????? ???????????? ??????
                        write.writeUTF(sendmsg+"@"+getRoomIdx);    //// 0 receiver, 1 sender, 2 receiverProfile 3 senderProfile 4 msg 5roomIdx
                        write.flush();
                        Log.e(TAG, "???????????? ????????? ?????? : "+sendmsg );
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

        public ReceiveThread() {
            try {
                // ?????? ??????????????? ???????????? ?????? ?????? ????????? ??????.

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (read != null){
                    // ?????? ????????? ?????? ?????? ?????????
//                    String msg =read.readUTF();
//                    Log.e(TAG, "3 ???????????? ?????? msg(roomNo + \"@\" + sender + \"@\" + msg + \"@\" + time) : "+msg );
//
//                    if (msg != null){
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //  0 roomNO, 1????????????, 2?????????, 3??????
//                                String[] filter = msg.split("@");
//                                roomNo = filter[0];
//                                String s_sender = filter[1];
//                                String s_msg = filter[2];
//                                String s_time = filter[3];
////
////                                Intent intent = new Intent(getApplicationContext(), MyService.class);
////                                intent.putExtra("msg", s_msg);
////                                startService(intent);
//
//                                if (sender.equals(s_sender)) {
//                                    adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.RIGHT_MESSAGE));
//                                } else {
//                                    adapter.addItem(new ChatItem(receiverProfile, receiver, s_msg, s_time, ChatType.LEFT_MESSAGE));
//                                }
//                                adapter.notifyDataSetChanged();
//                                recyclerView.scrollToPosition(adapter.getItemCount()-1);
//                            }
//                        });
//                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop : " );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            System.out.println("onDestroy ????????????");
            if(mSocket != null){
                mSocket.close();
            }
        } catch (IOException e) {
            System.out.println("??????????????? : Chat.class - onDestroy");
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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