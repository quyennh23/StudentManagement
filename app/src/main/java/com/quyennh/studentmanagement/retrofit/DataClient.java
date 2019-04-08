package com.quyennh.studentmanagement.retrofit;

import com.quyennh.studentmanagement.Student;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface DataClient {

    @Multipart
    @POST("upload_file.php")
    Call<String> uploadPhoto(@Part MultipartBody.Part photo);

    @FormUrlEncoded
    @POST("insert.php")
    Call<String> insertUser(@Field("username") String username,
                            @Field("password") String password,
                            @Field("avatar") String avatar);

    @FormUrlEncoded
    @POST("login.php")
    Call<List<Student>> requestLogin(@Field("username") String username,
                                      @Field("password") String password);

    @GET("delete.php")
    Call<String> requestDeleteUser(@Query("id") int id, @Query("avatar") String avatarPath);
}
