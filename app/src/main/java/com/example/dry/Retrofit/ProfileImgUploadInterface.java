package com.example.dry.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ProfileImgUploadInterface {
    @Multipart
    @POST("profile_image.php")
    Call<FileModel> profileImageApi(
            @Part MultipartBody.Part image,
            @Part("email") RequestBody email,
            @Part("nick") RequestBody nick
            );


}
