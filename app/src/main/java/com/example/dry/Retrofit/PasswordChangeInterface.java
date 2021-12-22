package com.example.dry.Retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PasswordChangeInterface {
    @Multipart
    @POST("changePassword.php")
    Call<FileModel> passwordChangeApi(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("newPassword") RequestBody newPassword,
            @Part("passwordCheck") RequestBody passwordCheck
    );


}

