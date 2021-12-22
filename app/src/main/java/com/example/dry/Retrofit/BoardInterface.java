package com.example.dry.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface BoardInterface {
        @FormUrlEncoded
        @POST("board.php")
        Call<String> boardApi(
                @Field("page") int page,
                @Field("limit") int limit
        );
}
