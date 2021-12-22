package none;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIRequest {
    static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitURL.IPADDRESS)
                    .client(createOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }

    public static OkHttpClient createOkHttpClient() {
        // 네트워크 통신 로그(서버로 보내는 파라미터 및 받는 파라미터) 보기
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        return builder.build();
    }

}
