package com.example.dry.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dry.Retrofit.CheckNickInterface;
import com.example.dry.Retrofit.RegisterInterface;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Register extends AppCompatActivity {

    private final String TAG = "Register";

    Button btn_agr, btn_register, btn_CheckNick;
    CheckBox checkBox;
    EditText et_nick, et_pw, et_pwc;
    TextView tv_email;
    String email;
    int nick;
    int isChecked;
    int pwc;
    int nickCheck =1;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferenceHelper = new PreferenceHelper(this);

        Intent i = getIntent();
        email = i.getStringExtra("email");

        tv_email = findViewById(R.id.tv_Email);
        tv_email.setText(email);
        et_nick = findViewById(R.id.et_Nick);
        et_pw = findViewById(R.id.et_pw);
        et_pwc = findViewById(R.id.et_pwc);

        checkBox = findViewById(R.id.checkBox);


        btn_CheckNick = findViewById(R.id.btn_CheckNick);
        btn_CheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkNick();
               nickCheck = 0;
            }
        });
        btn_agr = findViewById(R.id.btn_agr);
        btn_agr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                builder.setTitle("이용약관");
                builder.setMessage(R.string.btn_agr);
                builder.setPositiveButton("확인", null);
                builder.create().show();

            }
        });


        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordCheck();
                checkBox();
                if(isChecked == 0 && pwc == 0 && nickCheck == 0){
                    registOk();
                }
            }
        });
    }

    private void checkBox(){
        if(!checkBox.isChecked()){
            Toast.makeText(this, "이용약관에 동의해 주세요.", Toast.LENGTH_SHORT).show();
            isChecked = 1;
        }else {
            isChecked = 0;
        }
    }
    private void passwordCheck(){
        String num = et_pw.getText().toString();
        String numc = et_pwc.getText().toString();
        if(!num.equals(numc)){
            Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
            pwc = 1;
        }else{
            pwc = 0;
        }
    }

    private void checkNick(){
        final String nick = et_nick.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CheckNickInterface.CHECKNICK_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        CheckNickInterface api = retrofit.create(CheckNickInterface.class);
        Call<String> call = api.getCheckNick(nick);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.e("ok_nick", response.body());
                    String jsonResponse = response.body();
                    try{
                        parseNickData(jsonResponse);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error_nick", "에러 = "+t.getMessage());
            }
        });
    }
    private void parseNickData(String response) throws JSONException{
        JSONObject jsonObject = new JSONObject(response);
        if(jsonObject.optString("status").equals("exist")){
            Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show();
            nick = 1;
        }else if(jsonObject.optString("status").equals("non_exist")){
            Toast.makeText(this, "사용 가능합니다.", Toast.LENGTH_SHORT).show();
            nick = 0;
        }
    }

    private void registOk(){
        final String nick = et_nick.getText().toString();
        final String pw = et_pw.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RegisterInterface.REGIST_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RegisterInterface api = retrofit.create(RegisterInterface.class);
        Call<String> call = api.getUserRegist(email, nick, pw);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful() && response.body() != null){
                    Log.e("onSuccess", response.body());
                    String jsonResponse = response.body();
                    try{
                        parseRegeData(jsonResponse);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "에러 = "+t.getMessage());
            }
        });
    }
    private void parseRegeData(String response) throws JSONException{
        JSONObject jsonObject = new JSONObject(response);
        if(jsonObject.optString("status").equals("true")){
            Intent i = new Intent(Register.this, Login.class);
            startActivity(i);
            overridePendingTransition(0,0);
            Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
            saveInfo(response);
        }else if(jsonObject.optString("status").equals("false")){
            Toast.makeText(this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInfo(String response){
        preferenceHelper.putIsLogin(true);
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("true")){
                JSONArray dataArray = jsonObject.getJSONArray("data");
                for(int i = 0; i<dataArray.length(); i++){
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    preferenceHelper.putNick(dataobj.getString("nick"));
                    preferenceHelper.putEmail(dataobj.getString("email"));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}