package com.tradingsupervisor.webApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tradingsupervisor.data.entity.AuthToken;
import com.tradingsupervisor.data.entity.Product;
import com.tradingsupervisor.data.entity.Shop;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public class HttpClient {
    private static HttpApi api;

    public static HttpApi getApi() {
        if (api == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    //.baseUrl("http://192.168.43.86:8000")
                    .baseUrl("https://trading-control.tk")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            api =  retrofit.create(HttpApi.class);
        }
        return api;
    }


    public interface HttpApi {

        @Multipart
        @POST("/api/v1/core/upload/photos/")
        Call<ResponseBody> postPhotos(
                @Header("Authorization") String authHeader,
                @Part List<MultipartBody.Part> files,
                @Part("t_start") String t_start,
                @Part("t_finish") String t_finish,
                @Part("t_delta") String t_delta,
                @Part("store_id") Integer shopID);


        @GET("/api/v1/stores/")
        Call<List<Shop>> getShops(
                @Header("Authorization") String authHeader);

        @GET("/api/v1/stores/{shopID}/goods/")
        Call<List<Product>> getProducts(
                @Header("Authorization") String authHeader,
                @Path("shopID") int shopID);


        @FormUrlEncoded
        @POST("/api/v1/token-auth/")
        Call<AuthToken> authenticate(@Field("username") String username,
                                     @Field("password") String password);
    }
}
