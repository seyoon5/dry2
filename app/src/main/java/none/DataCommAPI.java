package none;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DataCommAPI {

    @Multipart
    @POST(RetrofitURL.URL_ImageUpload)
    Call<UploadResult> uploadFile(@Part("email") RequestBody email,
                                  @Part MultipartBody.Part uploaded_file);
}
