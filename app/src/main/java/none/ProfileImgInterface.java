package none;

import javax.xml.transform.Result;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

interface ProfileImgInterface {
    String BASE_URL = "http://13.124.243.157/";

    @Multipart
    @POST("upload.php")
    Call<Result> uploadImage(
            @Part MultipartBody.Part File
            );

}