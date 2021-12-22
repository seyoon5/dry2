package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dry.Retrofit.LoginInterface;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity {
    private final String TAG = "LoginActivity";
    TextInputEditText text_email, text_pw;
    Button btn_Login, register;
    PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate : " );
        setContentView(R.layout.activity_login);

        preferenceHelper = new PreferenceHelper(this);

        text_email = findViewById(R.id.text_email);
        text_pw = findViewById(R.id.text_Pw);

        btn_Login = findViewById(R.id.btn_Login);
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        register = findViewById(R.id.btn_Regist);
        register.setOnClickListener(v -> {
            Intent i = new Intent(Login.this, Authentication.class);
            startActivity(i);
            overridePendingTransition(0,0);
        });
    }
    private void loginUser(){
        final String email = text_email.getText().toString();
        final String password = text_pw.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(LoginInterface.LOGIN_URL)
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        LoginInterface api = retrofit.create(LoginInterface.class);
        Call<String> call = api.getUserLogin(email, password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null){
                    Log.e(TAG, response.body());
                    String jsonResponse = response.body();
                    parseLoginData(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "서버에러 = "+t.getMessage());
            }
        });
    }
    private void parseLoginData(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("true")){
                saveInfo(response);
                Intent i = new Intent(Login.this, Home.class);
                startActivity(i);
                finish();
            }
            Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void saveInfo(String response){
        preferenceHelper.putIsLogin(true);
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("true")){
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for(int i = 0; i<dataArray.length(); i++){
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putNick(dataobj.getString("nick"));
                    preferenceHelper.putEmail(dataobj.getString("email"));
                    preferenceHelper.putImage(dataobj.getString("image"));
                    preferenceHelper.putPw(dataobj.getString("password"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}