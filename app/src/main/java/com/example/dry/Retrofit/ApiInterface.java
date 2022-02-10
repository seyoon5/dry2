package com.example.dry.Retrofit;

import com.example.dry.Item.ChatDTO;
import com.example.dry.Item.ChatListModel;
import com.example.dry.Item.ChatRoomItem;
import com.example.dry.Item.LocationDTO;
import com.example.dry.Item.RoomNoModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @Multipart
    @POST("makeImage.php")
    Call<FileModel> makeImageApi(
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("getIdx.php")
    Call<ChatDTO> getIdxApi(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("getRoomUsers.php")
    Call<String> getRoomUsersApi(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("exit.php")
    Call<String> exitApi(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("entered.php")
    Call<String> enteredApi(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("readMsg2.php")
    Call<String> readMsg2Api(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("readMsg.php")
    Call<String> readMsgApi(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("makeRoom.php")
    Call<String> makeRoomApi(
            @Field("user") String user,
            @Field("receiverProfile") String receiverProfile,
            @Field("sender") String sender,
            @Field("receiver") String receiver,
            @Field("content") String content,
            @Field("senderProfile") String senderProfile,
            @Field("time") String time,
            @Field("count") int count
    );

    @FormUrlEncoded
    @POST("getRoomNo.php")
    Call<RoomNoModel> getRoomNoApi(
            @Field("sender") String sender,
            @Field("receiver") String receiver
    );

    @GET("chatMsg.php")
    Call<List<ChatModel>> getChatMsgApi(
            @Query("room_num") String room_num
    );

    @GET("chatList.php")
    Call<List<ChatListModel>> getChatListApi(
            @Query("sender") String sender
    );

    @FormUrlEncoded
    @POST("chat_info.php")
    Call<String> getChatRoomInfo(
            @Field("receiver") String usernick,
            @Field("sender") String nick
    );

    @GET("location.php")
    Call<LocationDTO> locationApi(
            @Query("lat") String lat,
            @Query("lng") String lng,
            @Query("m") String m
    );

    @FormUrlEncoded
    @POST("boardEdit.php")
    Call<String> getBoardContent(
            @Field("idx") String idx
    );

    @FormUrlEncoded
    @POST("replyWrite.php")
    Call<String> writeReply(
            @Field("board_idx") String board_idx,
            @Field("profile") String profile,
            @Field("nick") String nick,
            @Field("content") String content
    );

    @FormUrlEncoded
    @POST("reply.php")
    Call<String> getReplyContent(
            @Field("board_idx") String idx
    );
    @Multipart
    @POST("boardUpdate.php")
    Call<FileModel> updateApi(
            @Part List<MultipartBody.Part> image,
            @Part("idx") RequestBody idx,
            @Part("content") RequestBody content,
            @Part("size") RequestBody size,
            @Part("originImg") RequestBody originImg
    );

    @FormUrlEncoded
    @POST("boardDelete.php")
    Call<String> deleteBoard(
            @Field("idx") String idx
    );
    @FormUrlEncoded
    @POST("replyDelete.php")
    Call<String> deleteReply(
            @Field("idx") String idx,
            @Field("board_idx") String board_idx,
            @Field("deleted") String deleted
    );
    @FormUrlEncoded
    @POST("replyUpdate.php")
    Call<String> updateReply(
            @Field("idx") String idx,
            @Field("content") String content
    );
    @FormUrlEncoded
    @POST("reply2.php")
    Call<String> reply2Api(
            @Field("idx") String idx,
            @Field("content") String content,
            @Field("parent") String parent,
            @Field("board_idx") String boardIdx,
            @Field("profile") String profile,
            @Field("nick") String nick
    );
    @Multipart
    @POST("boardWrite.php")
    Call<FileModel> writeApi(
            @Part List<MultipartBody.Part> image,
            @Part("nick") RequestBody nick,
            @Part("content") RequestBody content,
            @Part("size") RequestBody size
    );

    @Multipart
    @POST("boardEdit.php")
    Call<FileModel> editApi(
            @Part("idx") RequestBody idx
    );

}