package com.example.dry.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dry.Adapter.BoardUpdateAdapter;
import com.example.dry.Adapter.BoardWriteAdapter;
import com.example.dry.FileUtils;
import com.example.dry.Item.BoardItem;
import com.example.dry.Item.BoardUpdateItem;
import com.example.dry.Item.ViewPagerItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.BoardWriteInterface;
import com.example.dry.Retrofit.FileModel;
import com.example.dry.Retrofit.RetrofitBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class BoardUpdate extends AppCompatActivity {
    private String TAG = "BoardUpdate";

    private String idx, content, contentImage;
    private EditText et_content;
    private ImageView camera, gallery;
    private RecyclerView recyclerView;
    private PreferenceHelper preferenceHelper;
    private ArrayList<BoardUpdateItem> mImgList;
    private BoardUpdateAdapter adapter;
    private String mCurrentPhotoPath;
    private int GALLERY_CODE = 111;
    private int CAMERA_CODE = 112;

    private ArrayList<String> mOriginList;
    private ArrayList<String> mNewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_update);
        et_content = findViewById(R.id.et_boardUpdate_input);
        camera = findViewById(R.id.board_update_camera);
        gallery = findViewById(R.id.board_update_gallery);
        recyclerView = findViewById(R.id.board_update_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mImgList = new ArrayList<>();
        adapter = new BoardUpdateAdapter(mImgList, BoardUpdate.this);
        gallery = findViewById(R.id.board_update_gallery);
        camera = findViewById(R.id.board_update_camera);

        mOriginList = new ArrayList<>();
        mNewList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbarUpdate);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter.setOnItemClickListener(new BoardUpdateAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                mImgList.remove(a_position);
                adapter.notifyDataSetChanged();
            }
        });

        idx = getIntent().getStringExtra("idx");
        showContent();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(BoardUpdate.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_CODE);
                dispatchTakePictureIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });
    }
    //카메라, 갤러리 이미지 가져오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                if (clipData.getItemCount() > 0) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        BoardUpdateItem galleryImg = new BoardUpdateItem(String.valueOf(clipData.getItemAt(i).getUri()));
                        mImgList.add(galleryImg);
                    }
                }
            }
        } else if (requestCode == CAMERA_CODE && resultCode == RESULT_OK && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(mCurrentPhotoPath);
                BoardUpdateItem cameraImg = new BoardUpdateItem(String.valueOf(Uri.fromFile(f)));
                Log.e(TAG, "cameraImg : " +cameraImg);
                mImgList.add(cameraImg);
            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, "내용 : dispatchTakePictureIntent,IOException" );
            } if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.dry.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODE); }
        }
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStorageDirectory();     // 저장경로 이름 확인
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

// idx 에 맞는 게시판 내용 수정할 화면에 보여주기
    private void showContent(){
        RequestBody boardIdx = RequestBody.create(MediaType.parse("multipart/form-data"), idx);

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);

        Call<FileModel> call = service.editApi(boardIdx);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                FileModel fileModel = response.body();
                Log.e(TAG, "showContent  onResponse: " +fileModel.getMessage());
                String content = fileModel.getContent();
                String contentImage = fileModel.getContentImage();

                et_content.setText(content);

                if(!contentImage.equals("") && !contentImage.equals("null")){
                    ArrayList<BoardUpdateItem> images = splitConvertToImages(contentImage);
                    for (int i = 0; i < images.size(); i++){
                        BoardUpdateItem imgItem = new BoardUpdateItem(images.get(i).getImage());
                        mImgList.add(imgItem);
                    }
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e(TAG, "showContent onFailure : " +t.getMessage());
            }
        });
    }

    private ArrayList<BoardUpdateItem> splitConvertToImages(String contentImage) {
            String imgArray[] = contentImage.split(" ");
            ArrayList<BoardUpdateItem> imageList = new ArrayList<>();

            for(int i = 0; i<imgArray.length; i++){
                BoardUpdateItem imageItem = new BoardUpdateItem(imgArray[i]);
                imageList.add(imageItem);
            }
            return imageList;
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
                Intent i = new Intent(BoardUpdate.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
                return true;
            }
            case R.id.write_create:{
                updateToServer();
            }
        }
        return super.onOptionsItemSelected(item);
    }



    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(FileUtils.getPath(this, fileUri));
        RequestBody requestFile = RequestBody.create (MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    //수정내용 업로드
    private void updateToServer(){
        String cont = et_content.getText().toString();
        List<MultipartBody.Part> parts = new ArrayList<>();
        StringBuilder str = new StringBuilder();

        //기존이미지 새로운이미지 구분
        if (mImgList != null) {
            for (int i = 0; i < mImgList.size(); i++) {
                if(!mImgList.get(i).getImage().contains("content") && !mImgList.get(i).getImage().contains("file")){
                    mOriginList.add(mImgList.get(i).getImage());
                }else{
                   mNewList.add(mImgList.get(i).getImage());
                }
            }
            Log.e(TAG, "mOriginList : " +mOriginList.toString());
            Log.e(TAG, "mNewList : " +mNewList.toString());
            for (int i = 0; i < mNewList.size(); i++){
                parts.add(prepareFilePart("image"+i, Uri.parse(mNewList.get(i))));
            }
            for (String originImg : mOriginList){
                str.append(originImg);
                str.append(" ");
            }
        }
        RequestBody boardIdx = RequestBody.create(MediaType.parse("multipart/form-data"), idx);
        RequestBody userCont = RequestBody.create(MediaType.parse("multipart/form-data"), cont);
        RequestBody originImg = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(str));
        RequestBody size = RequestBody.create(MediaType.parse("multipart/form-data"),""+parts.size());

        ApiInterface service = RetrofitBuilder.getClient().create(ApiInterface.class);
        Call<FileModel> call = service.updateApi(parts, boardIdx, userCont, size, originImg);
        call.enqueue(new Callback<FileModel>() {
            @Override
            public void onResponse(Call<FileModel> call, Response<FileModel> response) {
                FileModel fileModel = response.body();
                Log.e(TAG, "updateToServer onResponse: " +fileModel.getMessage());

                if(fileModel.getMessage().equals("content is not exist")){
                    Toast.makeText(BoardUpdate.this, "내용을 작성해 주세요", Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(BoardUpdate.this, Board.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    overridePendingTransition(0,0);
                }
            }

            @Override
            public void onFailure(Call<FileModel> call, Throwable t) {
                Log.e(TAG, "updateToServer onFailure : " +t.getMessage());
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
