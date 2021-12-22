package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dry.GMailSender;
import com.example.dry.Retrofit.AuthInterface;
import com.example.dry.R;

import org.json.JSONObject;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Authentication extends AppCompatActivity {

    Button btn_Auth_Transfer, btn_Auth_Ok, btn_Auth_Next;
    EditText et_Email, et_AuthNum;

    MainHandler mainHandler;
    String GmailCode;

    int value;
    int mailSend=0;
    int next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticaion);

        btn_Auth_Transfer = findViewById(R.id.btn_Auth_Transfer);
        btn_Auth_Ok = findViewById(R.id.btn_Auth_Ok);
        btn_Auth_Next = findViewById(R.id.btn_Auth_Next);

        et_Email= findViewById(R.id.et_Email);
        et_AuthNum= findViewById(R.id.et_AuthNum);

        btn_Auth_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String email = et_Email.getText().toString();
                    if(next == 1){
                    Intent i = new Intent(Authentication.this, Register.class);
                        i.putExtra("email", email);
                        startActivity(i);
                        overridePendingTransition(0,0);
                    }else {
                        Toast.makeText(Authentication.this, "이메일 인증이 필요합니다.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        //smtp 동작
        //인증코드 시간초가 흐르는데 이때 인증을 마치지 못하면 인증 코드를 지우게 만든다.
        btn_Auth_Transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existEmail();
                    //메일을 보내주는 쓰레드
                    MailTread mailTread = new MailTread();
                    mailTread.start();
                    if(mailSend==0){
                        value=180;
                        BackgrounThread backgroundThread = new BackgrounThread();
                        backgroundThread.start();
                        mailSend+=1;
                    }else{
                        value = 180;
                    }
                    mainHandler=new MainHandler();
            }
        });

        btn_Auth_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이메일로 전송한 인증코드와 내가 입력한 인증코드가 같을 때
                if(et_AuthNum.getText().toString().equals(GmailCode)){
                    Toast.makeText(Authentication.this, "인증성공", Toast.LENGTH_SHORT).show();
                    next=1;
                }else{
                    Toast.makeText(getApplicationContext(), "인증번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void existEmail(){
        final String email = et_Email.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(AuthInterface.AUTH_URL)
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        AuthInterface api = retrofit.create(AuthInterface.class);
        Call<String> call = api.getExistEmail(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null){
                    Log.e("응답성공", response.body());
                    String jsonResponse = response.body();
                    parseExistData(jsonResponse);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("응답실패", "서버에러 = "+t.getMessage());
            }
        });
    }

    private void parseExistData(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equals("exist")){
                Toast.makeText(this, "이미 가입된 email 입니다.", Toast.LENGTH_SHORT).show();
                btn_Auth_Next.setClickable(false);
                btn_Auth_Next.setBackgroundColor(Color.GRAY);
            }else {
                btn_Auth_Next.setClickable(true);
                btn_Auth_Next.setBackgroundColor(Color.parseColor("#008000"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //메일 보내는 쓰레드
    class MailTread extends Thread{

        public void run(){
            GMailSender gMailSender = new GMailSender("seyoon3003@gmail.com", "tkskdl1!");

            //인증코드
            GmailCode=gMailSender.getEmailCode();
            try {
                gMailSender.sendMail("인증번호", GmailCode , et_Email.getText().toString());
            } catch (SendFailedException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                System.out.println("인터넷 문제"+e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //시간초가 카운트 되는 쓰레드
    class BackgrounThread extends Thread{

        public void run(){
            while(true){
                value-=1;
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                Message message = mainHandler.obtainMessage();
                //메세지는 번들의 객체 담아서 메인 핸들러에 전달한다.
                Bundle bundle = new Bundle();
                bundle.putInt("value", value);
                message.setData(bundle);

                //핸들러에 메세지 객체 보내기기

                mainHandler.sendMessage(message);

                if(value<=0){
                    GmailCode="";
                    break;
                }
            }
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message message){
            super.handleMessage(message);
            int min, sec;

            Bundle bundle = message.getData();
            int value = bundle.getInt("value");

            min = value/60;
            sec = value % 60;
            //초가 10보다 작으면 앞에 0이 더 붙어서 나오도록한다.
            if(sec<10){
                //텍스트뷰에 시간초가 카운팅
                et_AuthNum.setHint("0"+min+" : 0"+sec);
            }else {
                et_AuthNum.setHint("0"+min+" : "+sec);
            }
        }
    }



}
