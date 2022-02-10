        package com.example.dry.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dry.Adapter.BoardWriteAdapter;
import com.example.dry.FileUtils;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.RetrofitBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardWrite extends AppCompatActivity {

    private String TAG = "BoardWrite";
    private PreferenceHelper preferenceHelper;
    private EditText content;
    private ImageView gallery, camera;
    private RecyclerView mRecyclerView;
    private ArrayList<Uri> mImgList;
    private BoardWriteAdapter adapter;
    private int CODE_ALBUM_REQUEST = 111;
    private int CAMERA_REQUEST_CODE = 101;
    String mCurrentPhotoPath;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);
        preferenceHelper = new PreferenceHelper(this);
        content = findViewById(R.id.et_boardWrite_input);
        mRecyclerView = findViewById(R.id.board_write_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImgList = new ArrayList<>();
        adapter = new BoardWriteAdapter(mImgList, BoardWrite.this);
        mRecyclerView.setAdapter(adapter);
        gallery = findViewById(R.id.board_write_gallery);
        camera = findViewById(R.id.board_write_camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(BoardWrite.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                dispatchTakePictureIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, CODE_ALBUM_REQUEST);
            }
        });

        adapter.setOnItemClickListener(new BoardWriteAdapter.OnItemClickEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(View a_view, int a_position) {
                mImgList.remove(a_position);
                adapter.notifyDataSetChanged();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //안드로이드 캐시 임시파일에 저장
   /* private void saveImg() {
        try {
            //저장할 파일 경로
            File storageDir = new File(getFilesDir() + "/capture");
            if (!storageDir.exists()){//폴더가 없으면 생성.
                storageDir.mkdirs();}


            String filename = "캡쳐파일" + ".jpg";
            Log.e(TAG, "캡쳐파일 : "+filename );

            // 기존에 있다면 삭제
            File file = new File(storageDir, filename);
            boolean deleted = file.delete();
            Log.w(TAG, "Delete Dup Check : " + deleted);
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output); //해상도에 맞추어 Compress
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert output != null;
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.e(TAG, "Captured Saved");
        } catch (Exception e) {
            Log.w(TAG, "Capture Saving Error!", e);
        }
    }*/


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Log.e(TAG, "2.7  photoFile:1 ="+photoFile );
            try {
                photoFile = createImageFile();
                Log.e(TAG, "2.7  photoFile:2 ="+photoFile );
            } catch (IOException ex) {
                Log.e(TAG, "내용 : dispatchTakePictureIntent,IOException" );
            } if(photoFile != null) {
                Log.e(TAG, "2.7  photoFile:3 ="+photoFile );
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.dry.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE); }
        }
    }

    private File createImageFile() throws IOException {
        Log.e(TAG, "createImageFile : 1" );
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);     // 저장경로 이름 확인
        Log.e(TAG, "createImageFile : 2" );
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        Log.e(TAG, "createImageFile : 3" );

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e(TAG, "createImageFile : 4" );
        Log.e(TAG, "createImageFile : image="+image );

        return image;
    }

    //카메라, 갤러리 이미지 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_ALBUM_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                if (clipData.getItemCount() > 0) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Log.e(TAG, "CODE_ALBUM_REQUEST : " +clipData.getItemAt(i).getUri() );
                        mImgList.add(clipData.getItemAt(i).getUri());
                    }
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(mCurrentPhotoPath);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(f));
                    if(bitmap != null){
                        Log.e(TAG, "CAMERA_REQUEST_CODE; : "+Uri.fromFile(f) );
                        mImgList.add(Uri.fromFile(f));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
            //saveImg();
        }adapter.notifyDataSetChanged();
    }

    // toolbar 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                Intent i = new Intent(BoardWrite.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.write_create:{
                addBoard();
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        Log.e(TAG, "fileUri : " +fileUri );
        File file = new File(FileUtils.getPath(this, fileUri));
        RequestBody requestFile = RequestBody.create (MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    // 게시글 내용 서버로 전달
    private void addBoard(){
        String nick = preferenceHelper.getNICK();
        String cont = content.getText().toString();

        List<MultipartBody.Part> parts = new ArrayList<>();

        if (mImgList != null) {
            for (int i = 0; i < mImgList.size(); i++) {
                Log.e(TAG, "prepareFilePart : " +mImgList.get(i).toString());
                parts.add(prepareFilePart("image"+i, mImgList.get(i)));
            }
        }

        RequestBody userNick = RequestBody.create(MediaType.parse("multipart/form-data"), nick);
        RequestBody userCont = RequestBody.create(MediaType.parse("multipart/form-data"), cont);
        RequestBody size = RequestBody.create(MediaType.parse("multipart/form-data"),""+parts.size());

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);
        Call<FileModel> call = service.writeApi(parts, userNick, userCont, size);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                FileModel fileModel = response.body();
                Log.e(TAG, "fileModel : " +fileModel.getMessage());
                Log.e(TAG, "fileModel : " +fileModel.getStatus());
                Log.e(TAG, "fileModel : " +response.toString());
                Log.e(TAG, "fileModel : " +response.body().toString());
                if(fileModel.getMessage().equals("content is not exist")){
                    Toast.makeText(BoardWrite.this, "내용을 작성해 주세요", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(BoardWrite.this, Board.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e(TAG, "onFailure(Call<FileModel> cal : " +t.getMessage());
            }
        });

    }

    private long lastTimeBackPressed;
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }else{
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
}