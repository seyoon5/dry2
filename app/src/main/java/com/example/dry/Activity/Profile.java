package com.example.dry.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dry.FileUtils;
import com.example.dry.MyService;
import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.ProfileImgUploadInterface;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.RetrofitBuilder;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {
    private final String TAG = "Profile";
    PreferenceHelper preferenceHelper;
    ImageButton ibPick;
    CircleImageView civProfile;
    Button btnConfirm, btnCancel, btn_changePW, btn_logout, btn_readUse, btn_withdrawal;
    TextView nick;
    ImageView home, board, location, chat, profile;
    private int REQUEST_CODE = 102;
    Bitmap bitmap;
    String selectedImage;
    private String userCurrentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        preferenceHelper = new PreferenceHelper(this);
        String usernick = preferenceHelper.getNICK();
        ibPick = findViewById(R.id.btn_profile_pick);
        btn_changePW = findViewById(R.id.btn_profile_changePW);
        btn_logout = findViewById(R.id.btn_profile_logout);
        btn_readUse = findViewById(R.id.btn_profile_readUse);
        btn_withdrawal = findViewById(R.id.btn_profile_withdrawal);
        civProfile = findViewById(R.id.civ_profile_userPhoto);
        btnConfirm = findViewById(R.id.btn_profile_confirm);
        btnCancel = findViewById(R.id.btn_profile_cancel);
        nick = findViewById(R.id.tv_profile_nick);
        nick.setText(usernick);

        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);

        userCurrentImage = preferenceHelper.getImage();
        requirePermission();

        if(!userCurrentImage.equals("null") && !userCurrentImage.equals("")){
        Glide.with(Profile.this)
                .load("http://13.125.206.46/images/"+userCurrentImage)
                .into(civProfile);
        }

        ibPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        btn_changePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ChangePassword.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Profile.this)
                        .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Profile.this, Login.class);
                                preferenceHelper.putIsLogin(false);
                                preferenceHelper.putEmail("");
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                overridePendingTransition(0,0);
                            }
                        }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        btn_readUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("이용약관");
                builder.setMessage(R.string.btn_agr);
                builder.setPositiveButton("확인", null);
                builder.create().show();
            }
        });
        btn_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Withdrawal.class);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
       menu();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            Uri path = data.getData();
            Glide.with(Profile.this)
                    .load(path)
                    .into(civProfile);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectedImage = FileUtils.getPath(Profile.this, path);
                btnConfirm.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadFileToServer();
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Glide.with(Profile.this)
                                .load("http://13.125.206.46/images/"+userCurrentImage)
                                .into(civProfile);
                        btnConfirm.setVisibility(View.INVISIBLE);
                        btnCancel.setVisibility(View.INVISIBLE);
                    }
                });
        }
    }
    private void uploadFileToServer(){
        String email = preferenceHelper.getEMAIL();
        String nick = preferenceHelper.getNICK();
        File file = new File(Uri.parse(selectedImage).getPath());
        Log.e(TAG, "uploadFileToServer :selectedImage " +selectedImage);
        Log.e(TAG, "uploadFileToServer :file " +file);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("sendimage", file.getName(), requestBody);

        RequestBody userEmail = RequestBody.create(MediaType.parse("multipart/form-data"), email);
        RequestBody userNick = RequestBody.create(MediaType.parse("multipart/form-data"), nick);

        ProfileImgUploadInterface service = RetrofitBuilder.getClient().create(ProfileImgUploadInterface.class);

        Call<FileModel> call = service.profileImageApi(filePart, userEmail, userNick);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                FileModel fileModel = response.body();
                preferenceHelper.putImage(fileModel.getImage());
                Log.e(TAG, "내용1 : " +fileModel.getImage());

                Toast.makeText(Profile.this, "프로필 사진이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                btnConfirm.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                Log.e(TAG, "내용2 : " +fileModel.getMessage());
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Toast.makeText(Profile.this, "실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
    }

    public Uri getImageUri(Context context, Bitmap bitmap){
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "myImg", "");

        return Uri.parse(path);
    }

    public void requirePermission(){
        ActivityCompat.requestPermissions(Profile.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
