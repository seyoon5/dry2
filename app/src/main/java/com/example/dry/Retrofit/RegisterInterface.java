package com.example.dry.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {
    String REGIST_URL = "http://13.125.206.46/";

    @FormUrlEncoded
    @POST("register.php")
    Call<String> getUserRegist(
            @Field("email") String email,
            @Field("nick") String nick,
            @Field("password") String password
    );
}
