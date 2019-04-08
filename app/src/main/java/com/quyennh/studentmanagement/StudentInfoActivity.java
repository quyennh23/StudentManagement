package com.quyennh.studentmanagement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.quyennh.studentmanagement.retrofit.APIUtils;
import com.quyennh.studentmanagement.retrofit.DataClient;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentInfoActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvPassword;
    private Button btDelete;
    private ImageView ivAvatar;
    private String TAG = "StudentInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);
        tvUsername = findViewById(R.id.tv_username);
        tvPassword = findViewById(R.id.tv_pass);
        btDelete = findViewById(R.id.bt_delete);
        ivAvatar = findViewById(R.id.iv_avatar);

        final Student student = getIntent().getParcelableExtra("student");
        tvUsername.setText(tvUsername.getText().toString() + student.getUser());
        tvPassword.setText(tvPassword.getText().toString() + student.getPass());
        Picasso.get().load(student.getAvatar()).into(ivAvatar);
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataClient dataClient = APIUtils.getData();
                String[] listAvatarNamePart = student.getAvatar().split("/");

                String avatarName = "/" + listAvatarNamePart[listAvatarNamePart.length - 1];
                Call<String> requestDelete = dataClient.requestDeleteUser(Integer.parseInt(student.getId()), avatarName);
                requestDelete.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "onResponse: response = " + response);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                    }
                });
            }
        });
    }
}
