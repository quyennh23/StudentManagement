package com.quyennh.studentmanagement.retrofit;

public class APIUtils {

    public static final String BASE_URL = "http://192.168.16.53:6005/studentmanagement/";

    public static DataClient getData() {
        return RetrofitClient.getClient(BASE_URL).create(DataClient.class);
    }
}
