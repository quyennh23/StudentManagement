package com.quyennh.studentmanagement;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.quyennh.studentmanagement.retrofit.APIUtils;
import com.quyennh.studentmanagement.retrofit.DataClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQEST_CODE_IMAGE = 123;
    private static final String TAG = "SigninActivity";
    private Button btSignin;
    private Button btCancel;
    private EditText edtUsername;
    private EditText edtPassword;
    private ImageView ivAvatar;
    private String realPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        btSignin = findViewById(R.id.bt_signin);
        btCancel = findViewById(R.id.bt_cancle);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        ivAvatar = findViewById(R.id.iv_avatar);

        btSignin.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_signin:
                handleSignin();
                break;
            case R.id.bt_cancle:

                break;
            case R.id.iv_avatar:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQEST_CODE_IMAGE);
                break;
            default:
                break;
        }
    }

    private void handleSignin() {
        final String userName = edtUsername.getText().toString();
        final String pass = edtPassword.getText().toString();
        if (userName.length() > 0 && pass.length() > 0) {
            File file = new File(realPath);
            String filePath = file.getAbsolutePath();
            String[] fileNameArray = filePath.split("\\.");
            Log.d(TAG, "handleSignin: filePath = " + filePath);
            Log.d(TAG, "handleSignin: realPath = " + realPath);

            filePath = fileNameArray[0] + "_" + System.currentTimeMillis() + "." + fileNameArray[1];
            Log.d(TAG, "handleSignin: filePath = " + filePath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", filePath, requestBody);

            final DataClient dataClient = APIUtils.getData();
            Call<String> callBack = dataClient.uploadPhoto(body);


            callBack.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.d(TAG, "onResponse: ");
                    if (response != null) {
                        String msg = response.body();
                        String avatarPath = APIUtils.BASE_URL + "image/" + msg;
                        Log.d(TAG, "onResponse: reponse = " + msg + " avatarPath = " + avatarPath);
                        Call<String> addUserCal = dataClient.insertUser(userName, pass, avatarPath);
                        addUserCal.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response != null) {
                                    if (response.body().equals("Success")) {
                                        Toast.makeText(SigninActivity.this, "Registation success", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SigninActivity.this, "Registation error " + response.body(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    t.printStackTrace();
                }
            });
        } else {
            Toast.makeText(this, "Please input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            realPath = getRealPathFromURI(uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivAvatar.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onActivityResult: request image failure");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(column_index);
        }
        cursor.close();
        return path;
    }
}
