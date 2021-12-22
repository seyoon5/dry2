package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.WithdrawalInterface;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.RetrofitBuilder;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Withdrawal extends AppCompatActivity {
    ImageView home, board, location, chat, profile;
    PreferenceHelper preferenceHelper;
    TextView tv_profile_withdrawal;
    TextInputEditText text_withdrawalPw;
    Button btn_withdrawalOk, btn_withdrawalCancel;
    private String pw;
    private String TAG = "Withdrawal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);
        preferenceHelper = new PreferenceHelper(this);
        tv_profile_withdrawal = findViewById(R.id.tv_profile_withdrawal);
        text_withdrawalPw = findViewById(R.id.text_withdrawalPw);
        btn_withdrawalOk = findViewById(R.id.btn_withdrawal_Ok);
        btn_withdrawalCancel = findViewById(R.id.btn_withdrawal_cancel);

        btn_withdrawalOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = text_withdrawalPw.getText().toString();
                withdrawalToServer();
            }
        });
        btn_withdrawalCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        menu();
    }
    private void withdrawalToServer(){
        String email = preferenceHelper.getEMAIL();
        String nick = preferenceHelper.getNICK();

        RequestBody userEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody userpw = RequestBody.create(MediaType.parse("multipart/form-data"), pw);
        RequestBody userNick = RequestBody.create(MediaType.parse("multipart/form-data"), nick);

        WithdrawalInterface service = RetrofitBuilder.getClient().create(WithdrawalInterface.class);

        Call<FileModel> call = service.withdrawalApi(userEmail, userpw, userNick);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                if(response.body().getStatus() == true){
                    Intent i = new Intent(Withdrawal.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
                Toast.makeText(Withdrawal.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Toast.makeText(Withdrawal.this, "error : 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "내용 : "+t.getMessage() );
            }
        });
        }

    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Chat.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Withdrawal.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
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