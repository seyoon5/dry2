package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.PasswordChangeInterface;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.RetrofitBuilder;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {
    private final String TAG = "ChangePassword";
    ImageView home, board, location, chat, profile;
    PreferenceHelper preferenceHelper;
    TextInputEditText currentPw, newPw, newPwc;
    Button ok, cancel;
    private String cpw, npw, pwc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        preferenceHelper = new PreferenceHelper(ChangePassword.this);
        currentPw = findViewById(R.id.text_currentPW);
        newPw = findViewById(R.id.text_changePW);
        newPwc = findViewById(R.id.text_changePWC);

        ok = findViewById(R.id.btn_changePw_Ok);
        cancel = findViewById(R.id.btn_changePw_cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cpw = currentPw.getText().toString();
                npw = newPw.getText().toString();
                pwc = newPwc.getText().toString();
                passwordToServer();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);

            }
        });
        menu();
    }
    private void passwordToServer(){
        String email = preferenceHelper.getEMAIL();

        RequestBody userEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody usercpw = RequestBody.create(MediaType.parse("multipart/form-data"), cpw);
        RequestBody usernpw = RequestBody.create(MediaType.parse("multipart/form-data"), npw);
        RequestBody userpwc = RequestBody.create(MediaType.parse("multipart/form-data"), pwc);

        PasswordChangeInterface service = RetrofitBuilder.getClient().create(PasswordChangeInterface.class);

        Call<FileModel> call = service.passwordChangeApi(userEmail, usercpw, usernpw, userpwc);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                Log.e(TAG, "내용 : " +response.body().getMessage());
                if (response.body().getStatus() == true) {
                    Intent i = new Intent(ChangePassword.this, Profile.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
                Toast.makeText(ChangePassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
        }
    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Chat.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangePassword.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
    }
    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed : "+System.currentTimeMillis() );
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }else{
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}