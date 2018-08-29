package com.tradingsupervisor.webApi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class HttpClient {
    private static HttpApi api;

    public static HttpApi getApi(String url) {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            api =  retrofit.create(HttpApi.class);
        }
        return api;
    }

    public interface HttpApi {
        /*
        @Multipart
        @POST("/upload/")
        Call<ResponseBody> postPhotos(@Part List<MultipartBody.Part> photos,
                                     @Part("time") String time,
                                     @Part("shopID") String shopID,
                                     @Part("coords") String coords);*/

        @Multipart
        @POST("/scenes/")
        Call<ResponseBody> postImage(@Part MultipartBody.Part image,
                                     @Part("name") RequestBody name,
                                     @Part("id") String id);
    }
}
