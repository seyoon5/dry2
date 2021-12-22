package com.example.dry.Retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthInterface {
    String AUTH_URL = "http://13.125.206.46/";

    @FormUrlEncoded
    @POST("auth.php")
    Call<String> getExistEmail(
            @Field("email") String email
    );
}
