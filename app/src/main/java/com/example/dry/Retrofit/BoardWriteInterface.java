package com.example.dry.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BoardWriteInterface {

    @FormUrlEncoded
    @POST("boardWrite.php")
    Call<String> boardWriteApi(
            @Field("nick") String nick,
            @Field("content") String content
    );
}




