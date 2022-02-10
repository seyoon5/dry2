package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.example.dry.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class ChatImage extends AppCompatActivity {

    private static final String TAG = "ChatImage";
    private ImageView imgView;
    private ImageButton imgBtn;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_image);

        imgView = findViewById(R.id.iv_download);
        imgBtn = findViewById(R.id.btn_download);

        // img 보여주기
        Intent i = getIntent();
        String img = i.getStringExtra("img");
        url = "http://13.125.206.46/images/"+img;
        Log.e(TAG, "내용 : img="+img );
        Glide.with(this)
                .load(url)
                .into(imgView);

        PRDownloader.initialize(getApplicationContext());

        // glide 통해 이미지 파일 불러오고 다운로드 (권한 설정이 되어 있어야 함, dexter)
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(img);
            }
        });
    }

    private void downloadImage(String img) {

//        ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("다운로드 중 ...");
//        pd.setCancelable(false);
//        pd.show();

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PRDownloader.download(url, file.getPath(), URLUtil.guessFileName(url, null, null))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

//                        long per = progress.currentBytes*100 / progress.totalBytes;
//                        pd.setMessage("다운로드 중 : "+per+"%");

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        //pd.dismiss();
                        Toast.makeText(ChatImage.this, "다운로드 완료", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(ChatImage.this, "다운로드 실패", Toast.LENGTH_SHORT).show();
                    }
                    
                });

    }
}