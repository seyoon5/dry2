package com.example.dry;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dry.Activity.Chat;
import com.example.dry.Activity.ChatList;
import com.example.dry.Item.ChatDTO;
import com.example.dry.Item.ChatItem;
import com.example.dry.Item.ChatType;
import com.example.dry.Item.ReplyItem;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.ChatModel;
import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.RetrofitBuilder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyService extends Service {
    private static final String TAG = "MyService";

    //private String IP = "13.125.206.46";
    private String IP = "3.34.5.22";
    private int PORT = 8080;
    private Socket mSocket;
    private DataOutputStream write = null;
    private DataInputStream read = null;

    private String senderProfile;
    private String receiverProfile;
    private String sender;
    private String receiver;
    private String getMsg;
    private String chatListRoomIdx;
    private PreferenceHelper preferenceHelper;

    private String one;
    private String two;
    private String three;
    private String four;
    private String five;
    private String six;
    private String seven;

    private String dbSender;
    private String dbReceiver;
    private String dbSender_profile;
    private String dbReceiver_profile;
    private String dbImage;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private Bitmap image;

    private String persons;
    private String getIdx;

    private String fileImage;

    public MyService() {
        Log.e(TAG, "public MyService() : " );
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "IBinder onBind : " );
        throw new UnsupportedOperationException("Not yet Implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        new SocketClient().start();
        Log.e(TAG, "onCreate : " );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand : " );
        preferenceHelper = new PreferenceHelper(getApplicationContext());
        sender = preferenceHelper.getNICK();
        senderProfile = preferenceHelper.getImage();

        if(intent == null){
            return Service.START_STICKY;    // ???????????? ???????????? ?????? ??????
        }else{
            String receiverList = intent.getStringExtra("receiver");
            String receiverBoard = intent.getStringExtra("username");
            String receiverProfileList = intent.getStringExtra("receiverProfile");
            String receiverProfileBoard = intent.getStringExtra("userProfile");
            String msg = intent.getStringExtra("msg");
            String chatImg = intent.getStringExtra("image");
            fileImage = intent.getStringExtra("file");

            if(msg != null){
                String[] filter = msg.split("@");
                one = filter[0];
                two = filter[1];
                three = filter[2];
                four = filter[3];
                five = filter[4];
                six = filter[5];
                seven = filter[6];
            }

            Log.e(TAG, "1.30 : one="+one );
            Log.e(TAG, "1.30 : two="+two );
            Log.e(TAG, "1.30 : three="+three );
            Log.e(TAG, "1.30 : four="+four );
            Log.e(TAG, "1.30 : five="+five );
            Log.e(TAG, "1.30 : six="+six );
            Log.e(TAG, "1.30 : seven="+seven );
            Log.e(TAG, "1.30 : image="+chatImg );
            Log.e(TAG, "1.30 : msg="+msg );
            Log.e(TAG, "2.8 : fileImage="+fileImage );

            if(receiverList == null){
                receiver = receiverBoard;
                receiverProfile = receiverProfileBoard;
            }
            if(receiverBoard == null){
                receiver = receiverList;
                receiverProfile = receiverProfileList;
            }

            Log.e(TAG, "receiver ????????? ?????? : " +receiver);
            Log.e(TAG, "receiver profile : " +receiverProfile);
            Log.e(TAG, "sender ??? ?????? : " +sender);
            Log.e(TAG, "sender profile : " +senderProfile);
//
//            if(msg != null){
//                String[] filter = msg.split("@");
//                String getRoomIdx = filter[5];
//                getIdx(getRoomIdx);
//                Log.e(TAG, "???getRoomIdx getRoomIdx??? : "+getRoomIdx );
//            }
            Log.e(TAG, " 1.30 ????????? ????????? ?????? ???: "+chatImg );
            if(chatImg != null || fileImage != null){
                Log.e(TAG, "1.30 ????????? ????????? ="+chatImg );
                if(chatImg == null){
                    chatImg = fileImage;
                }
                saveImage(chatImg);
            }else{
                Log.e(TAG, "1.30 ?????? ????????? : " );
                new SendThread(msg).start();
            }
//            if(five != null && five.equals("??????")){
//                Log.e(TAG, "1.30 ?????? ????????? ????????? ??? ??????2 : " );
//                Log.e(TAG, "1.30 ?????? ????????? ????????? ??? ??????2 :dbImage= " +dbImage);
//                Log.e(TAG, "1.30 ?????? ????????? ????????? ??? ??????2 five: ="+five );
//                if(dbImage != null){
//                    five = dbImage;
//                }
//
//            }

        }

        return super.onStartCommand(intent, flags, startId);  // = START_STICKY
    }

    private void saveImage(String image) {
        Log.e(TAG, "saveImage() " );
        Log.e(TAG, "saveImage()image= "+image );
        Log.e(TAG, "saveImage()fileImage= "+fileImage );

        File file;

        if(!image.contains("/storage/emulated/")){

            file = new File(FileUtils.getPath(this, Uri.parse(image)));
            // ??????????????? ????????? image ????????? content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F35/ORIGINAL/NONE/image%2Fjpeg/1144762230
            // FileUtils ???????????? ???????????? ????????????(contentprovider) ????????? ???????????? ????????? ????????? ??? ????????? /storage/emulated ... ??? ???????????? ???. '???????????? ?????? ????????????? ???????????? ????????? ??? ????????????'
            // ??? ????????? ??????????????? ????????? ??????????????? ????????? ????????? ????????? ??? ??????.
            Log.e(TAG, "2.8 ????????? ???????????? Uri ??? ?????? ?????? : " +file);
        }else {
            file = new File(fileImage);
            Log.e(TAG, "2.8 ????????? ???????????? Uri ??? ?????? ?????? : " +file);
        }
        Log.e(TAG, "2.8 ????????? ?????? Uri ??????  : ="+file );
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                // multipart ??? ????????? ??????????????? ????????? ?????? ????????? /storage/emulated/0/Pictures/IMG_20220130_050335.jpg ?????? ??????.
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", "chatImage_.jpg", requestBody);
                Log.e(TAG, "1.29 ?????? saveImage :file=" +file);
        //1.29 ?????? saveImage :file=/storage/emulated/0/Pictures/IMG_20220129_012309.jpg
                ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);
                Call<FileModel> call = service.makeImageApi(filePart);
                call.enqueue(new Callback<FileModel>() {
                    @Override
                    public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                        FileModel model = response.body();
                        dbImage = model.getImage();
                        new SendThread(one+"@"+two+"@"+three+"@"+four+"@"+dbImage+"@"+six+"@"+seven).start();
                        Log.e(TAG, "saveImage :response.getImage() " +model.getImage());

                    }

                    @Override
                    public void onFailure(Call<FileModel> call, Throwable t) {
                        Log.e(TAG, "saveImage :onFailure " +t.getMessage());
                    }
                });

    }

    private void sendMsgToChatList(String roomNo, String msg) {
        Log.e(TAG, "sendMsgToChatList ??????????????? ???????????? ???????????? ?????????" );
        Intent intent = new Intent("server-message");
        intent.putExtra("message", msg);
        intent.putExtra("roomNo", roomNo);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "service onDestroy : " );
    }

//    public class MyTestService extends IntentService{
//        public MyTestService(String name) {
//            super(name);
//        }
//
//        @Override
//        protected void onHandleIntent(@Nullable Intent intent) {
//
//        }
//    }



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

                    // 0????????????, 1?????????, 2??????
                    String[] filter = msg.split("@");
                    String roomNo = filter[0];
                    String s_sender = filter[1];
                    String s_msg = filter[2];
                    String s_time = filter[3];
                    String s_user = filter[4];
                    String s_receiver = filter[5];
                    String s_receiverProfile = filter[6];
                    String s_senderProfile = filter[7];

                    Log.e(TAG, "roomNo : " +roomNo);
                    Log.e(TAG, "s_sender : " +s_sender);
                    Log.e(TAG, "s_msg : " +s_msg);
                    Log.e(TAG, "s_time : " +s_time);
                    Log.e(TAG, "s_user : " +s_user);
                    Log.e(TAG, "s_receiver : " +s_receiver);
                    Log.e(TAG, "s_receiverProfile : " +s_receiverProfile);
                    Log.e(TAG, "s_senderProfile : " +s_senderProfile);

                    ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);
                    Call<ChatDTO> call = service.getIdxApi(roomNo);
                    call.enqueue(new Callback<ChatDTO>() {
                        @Override
                        public void onResponse(Call<ChatDTO> call, Response<ChatDTO> response) {
                            Log.e(TAG, "getIdxApi :onResponse "+response.isSuccessful() );
                            ChatDTO model = response.body();
                            String getIdx = model.getIdx();
                            String getSender = model.getSender();
                            String getSender_profile = model.getSender_profile();
                            String getReceiver = model.getReceiver();
                            String getReceiver_profile = model.getReceiver_profile();

                            Log.e(TAG, "???????????? ??????  roomNo: "+roomNo );
                            Log.e(TAG, "???????????? ??????  getIdx: "+getIdx );
                            Log.e(TAG, "???????????? ??????  sender: "+sender );
                            Log.e(TAG, "???????????? ??????  s_receiver: "+s_receiver );
                            Log.e(TAG, "???????????? ??????  getReceiver: "+getReceiver );
                            Log.e(TAG, "???????????? ??????  getSender: "+getSender );

                            if(sender.equals(getSender) || sender.equals(getReceiver)){    // if(roomNo.equals(getIdx))
                                Log.e(TAG, "??? ?????? ??????, ????????? ????????? ?????? : " );
                                sendMsgToChat(roomNo, s_sender, s_msg, s_time, s_user, s_receiver, s_receiverProfile);
                                sendMsgToChatList(roomNo, s_msg);
                                if(sender.equals(s_receiver)){
                                    getRoomUsers(roomNo, s_sender, s_msg, s_time, s_receiver, s_receiverProfile, s_senderProfile);

                                    // noti ????????? ????????? ????????????.
                                    if(sender.equals(s_sender)){
                                        Glide.with(getApplicationContext()).asBitmap().load("http://3.34.5.22/images/"+s_receiverProfile).into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                                image = resource;
                                            }
                                        });
                                    }else{
                                        Glide.with(getApplicationContext()).asBitmap().load("http://3.34.5.22/images/"+s_senderProfile).into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                                                image = resource;
                                            }
                                        });
                                    }

                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<ChatDTO> call, Throwable t) {
                            Log.e(TAG, "getIdxApi :onFailure "+t.getMessage());
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

//    private void getIdx(String roomNo) {
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
//                .addConverterFactory(ScalarsConverterFactory.create()).build();
//        ApiInterface service = retrofit.create(ApiInterface.class);
//        Call<String> call = service.getIdxApi(roomNo);
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                Log.e(TAG, "getRoomUsers :onResponse "+response.isSuccessful() );
//                String data = response.body();
//                getIdxParseData(data);
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });
//    }
//
//    private void getIdxParseData(String data) {
//        try {
//            JSONObject jsonObject = new JSONObject(data);
//            JSONArray jsonArray = jsonObject.getJSONArray("data");
//
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject item = jsonArray.getJSONObject(i);
//
//                getIdx = item.optString("idx");
//                Log.e(TAG, "getIdxParseData : "+getIdx );
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void getRoomUsers(String idx, String s_sender, String s_msg, String s_time, String s_receiver, String s_receiverProfile, String s_senderProfile) {

        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.getRoomUsersApi(idx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "getRoomUsers :onResponse "+response.isSuccessful() );
                String data = response.body();
                parseData(data, idx, s_sender, s_msg, s_time, s_receiver, s_receiverProfile, s_senderProfile);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void parseData(String data, String idx, String s_sender, String s_msg, String s_time, String s_receiver, String s_receiverProfile, String s_senderProfile) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                dbSender = item.optString("sender");
                dbReceiver = item.optString("receiver");
                dbSender_profile = item.optString("sender_profile");
                dbReceiver_profile = item.optString("receiver_profile");
                String dbPeople = item.optString("people");
                persons = dbPeople;
                Log.e(TAG, "getRoomUsers parseData persons : "+persons );
                Log.e(TAG, "getRoomUsers parseData dbSender : "+dbSender );
                Log.e(TAG, "getRoomUsers parseData dbReceiver : "+dbReceiver );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "2.4 ?????? :  ????????? ?????????" );
        Log.e(TAG, "2.4 ?????? :  ????????? ????????? :sender= "+sender );
        Log.e(TAG, "2.4 ?????? :  ????????? ????????? :s_sender= "+s_sender );
        if(!sender.equals(s_sender)){
            // ??? ????????? 0 ?????? ?????? ????????? ?????? x
            int pers = Integer.parseInt(persons);
            if(pers < 2 ){
                Log.e(TAG, "2.4?????? sendNotify ??? : " );
                sendNotify(idx, s_sender, s_msg, s_time, s_receiver, s_receiverProfile, s_senderProfile,
                        dbSender, dbSender_profile, dbReceiver, dbReceiver_profile);
            }
        }
    }

    private void sendNotify(String roomNo, String s_sender, String s_msg, String s_time, String s_receiver, String s_receivePro, String s_sendPro,
                            String dbSender, String dbSender_profile, String dbReceiver, String dbReceiver_profile) {
        if(s_msg.contains("chatImage_")){
            s_msg = "??????";
        }
        Log.e(TAG, "2.4?????? sendNotify ??? : " );
        Log.e(TAG, "1.27 ????????? : image" +image);
        // ????????? ?????? ??? ????????? ?????? ?????? NotificationManager??? ????????????.
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        // ??????????????? ??????????????? ???????????????.
        Intent notificationIntent = new Intent(this, Chat.class);
        notificationIntent.putExtra("roomIdx", roomNo);
        notificationIntent.putExtra("dbSender", dbSender);
        notificationIntent.putExtra("dbSender_profile", dbSender_profile);
        notificationIntent.putExtra("dbReceiver", dbReceiver);
        notificationIntent.putExtra("dbReceiver_profile", dbReceiver_profile);
        Log.e(TAG, "sendNotify : " +roomNo);
        Log.e(TAG, "sendNotify : " +dbSender);
        Log.e(TAG, "sendNotify : " +dbSender_profile);
        Log.e(TAG, "sendNotify : " +dbReceiver);
        Log.e(TAG, "sendNotify : " +dbReceiver_profile);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        if(sender.equals(s_sender)){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(image) //BitMap ????????? ??????
                    .setContentTitle(s_receiver)
                    .setContentText(s_msg)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // ???????????? ????????????????????? ?????? ResultActivity??? ??????????????? ??????
                    .setAutoCancel(true); // ????????? ????????? ??????

            //OREO API 26 ??????????????? ?????? ??????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                builder.setSmallIcon(R.drawable.chat_icon); //mipmap ????????? Oreo ???????????? ????????? UI ?????????
                CharSequence channelName  = "?????????????????? ??????";
                String description = "????????? ????????? ?????? ??????(????????????)";
                int importance = NotificationManager.IMPORTANCE_HIGH;// ???????????? ??????

                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
                channel.setDescription(description);

                // ?????????????????? ????????? ???????????? ??????
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo ???????????? mipmap ???????????? ????????? Couldn't create icon: StatusBarIcon ?????????

            assert notificationManager != null;
            notificationManager.notify(1234, builder.build()); // ??????????????? ?????????????????? ????????????

        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setLargeIcon(image) //BitMap ????????? ??????
                    .setContentTitle(s_sender)
                    .setContentText(s_msg)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent) // ???????????? ????????????????????? ?????? ResultActivity??? ??????????????? ??????
                    .setAutoCancel(true); // ????????? ????????? ??????

            //OREO API 26 ??????????????? ?????? ??????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                builder.setSmallIcon(R.drawable.chat_icon); //mipmap ????????? Oreo ???????????? ????????? UI ?????????
                CharSequence channelName  = "?????????????????? ??????";
                String description = "????????? ????????? ?????? ??????(????????????)";
                int importance = NotificationManager.IMPORTANCE_HIGH;// ???????????? ??????

                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
                channel.setDescription(description);

                // ?????????????????? ????????? ???????????? ??????
                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

            }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo ???????????? mipmap ???????????? ????????? Couldn't create icon: StatusBarIcon ?????????

            assert notificationManager != null;
            notificationManager.notify(1234, builder.build()); // ??????????????? ?????????????????? ????????????
        }

    }


    private void sendMsgToChat(String roomNo, String s_sender, String s_msg, String s_time, String s_user, String s_receiver, String s_receiverProfile) {

        Log.e(TAG, "sendMsgToChat" );
        String update = "update";

        Intent intent = new Intent("server-msg");
        intent.putExtra("roomNo", roomNo);
        intent.putExtra("s_sender", s_sender);
        intent.putExtra("s_msg", s_msg);
        intent.putExtra("s_time", s_time);
        intent.putExtra("s_user", s_user);
        intent.putExtra("s_receiver", s_receiver);
        intent.putExtra("s_receiverProfile", s_receiverProfile);
        intent.putExtra("update", update);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    /**
     * ????????? ?????????
     */
    class SendThread extends  Thread{

        String sendmsg;

        public
        SendThread(String sendmsg ){
            Log.e(TAG, "1.30 :SendThread ??????" );
            this.sendmsg =sendmsg;
            try {
                    write = new DataOutputStream (mSocket.getOutputStream());
            }catch (Exception e){
                Log.e(TAG, "SendThread ???????????? : " );
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
                        write.writeUTF(sendmsg);    //// 0 receiver, 1 sender, 2 receiverProfile 3 senderProfile 4 msg 5roomIdx
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
        }

        @Override
        public void run() {
            Log.e(TAG, "ReceiveThread 1" );
            try {
                StringBuffer in = new StringBuffer();
                String tmp;
                Log.e(TAG, "readUTF : "+read.readUTF() );
                while ((tmp = read.readUTF()) != null){
                    in.append(tmp);
                    Log.e(TAG, "tmp : " +tmp);
                    // ?????? ????????? ?????? ?????? ?????????
                    Log.e(TAG, " while (msg != null){ 3: " );
                    String massage =read.readUTF();

                    Log.e(TAG, "????????? ?????? read.readUTF(); 4: "+massage );

                    if (massage != null){

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}