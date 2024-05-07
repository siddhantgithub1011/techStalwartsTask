package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<LoginResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("users")
    Call<UsersList> getUsers(@Query("page") int page);

}
