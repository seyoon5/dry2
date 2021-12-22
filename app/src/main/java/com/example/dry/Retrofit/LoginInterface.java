package com.example.dry.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginInterface {
    String LOGIN_URL = "http://13.125.206.46/";

    @FormUrlEncoded
    @POST("login.php")
    Call<String> getUserLogin(
            @Field("email") String email,
            @Field("password") String password
            );
}
