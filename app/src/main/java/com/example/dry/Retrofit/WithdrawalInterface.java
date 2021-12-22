package com.example.dry.Retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface WithdrawalInterface {
    @Multipart
    @POST("withdrawal_password.php")
    Call<FileModel> withdrawalApi(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("nick") RequestBody nick
    );
}
