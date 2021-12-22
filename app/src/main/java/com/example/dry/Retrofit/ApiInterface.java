package com.example.dry.Retrofit;

import com.example.dry.Item.LocationDTO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

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