package com.quyennh.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quyennh.studentmanagement.retrofit.APIUtils;
import com.quyennh.studentmanagement.retrofit.DataClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Button btLogin;
    private Button btRegister;
    private EditText edtUsername;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btLogin = findViewById(R.id.bt_login);
        btRegister = findViewById(R.id.bt_register);
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);

        btLogin.setOnClickListener(this);
        btRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                handleLogin();
                break;

            case R.id.bt_register:
                startActivity(new Intent(this, SigninActivity.class));
                break;
            default:
                break;
        }
    }

    private void handleLogin() {
        final String userName = edtUsername.getText().toString();
        final String pass = edtPassword.getText().toString();
        if (userName.length() > 0 && pass.length() > 0) {
            DataClient dataClient = APIUtils.getData();
            final Call<List<Student>> requestLogin = dataClient.requestLogin(userName, pass);
            requestLogin.enqueue(new Callback<List<Student>>() {
                @Override
                public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                    if (response != null) {
                        List<Student> listStuden = response.body();
                        Log.d(TAG, "onResponse: listStuden.size() = " + listStuden.size());
                        if (listStuden.size() > 0) {
                            Log.d(TAG, "onResponse: " +
                                    "id = " + listStuden.get(0).getId() +
                                    "user = " + listStuden.get(0).getUser() +
                                    "pass = " + listStuden.get(0).getPass() +
                                    "avatar: " + listStuden.get(0).getAvatar());

                            Intent intent = new Intent(LoginActivity.this, StudentInfoActivity.class);
                            intent.putExtra("student",listStuden.get(0));
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Student>> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Please input", Toast.LENGTH_LONG).show();
        }
    }
}
