package com.example.dry.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dry.Adapter.ReplyAdapter;
import com.example.dry.Item.BoardItem;
import com.example.dry.Item.ReplyItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.BoardInterface;
import com.example.dry.Retrofit.RetrofitBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Reply extends AppCompatActivity {

    private TextView reply;
    private Button btn_write;
    private EditText et_input_reply;
    private String TAG = "Reply";

    private PreferenceHelper preferenceHelper;

    private ArrayList<ReplyItem> mReplyList;
    private RecyclerView mRecyclerView;
    private ReplyAdapter mReplyAdapter;

    private String board_idx;

    private String idx;
    private String content;
    private String alertContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        reply = findViewById(R.id.reply);
        btn_write = findViewById(R.id.reply_btn_write);
        et_input_reply = findViewById(R.id.et_input_reply);
        mRecyclerView = findViewById(R.id.reply_recycler);
        preferenceHelper = new PreferenceHelper(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReplyList = new ArrayList<>();
        mReplyAdapter = new ReplyAdapter(mReplyList);
        mRecyclerView.setAdapter(mReplyAdapter);

        board_idx = getIntent().getStringExtra("board_idx");

        mReplyAdapter.setReplyItemClickListener(new ReplyAdapter.OnReplyItemClickEventListener() {
            @Override
            public void onReplyItemClick(int a_position) {
                idx = mReplyList.get(a_position).getIdx();
                final ReplyItem item = mReplyList.get(a_position);
                AlertDialog.Builder ad = new AlertDialog.Builder(Reply.this);
                ad.setTitle("답글");
                ad.setMessage("내용을 입력하세요");
                final EditText et = new EditText(getApplicationContext());
                et.setText("@"+item.getNick()+" ");
                FrameLayout container = new FrameLayout(Reply.this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 70;
                params.rightMargin= 70;
                et.setLayoutParams(params);
                container.addView(et);
                ad.setView(container);
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertContent = et.getText().toString();
                        reply2ToServer();
                    }
                });
                ad.setNegativeButton("취소", null);
                ad.create().show();
            }
        });

        mReplyAdapter.setOnItemClickListener(new ReplyAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), a_view);
                getMenuInflater().inflate(R.menu.crud_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        idx = mReplyList.get(a_position).getIdx();
                        switch (item.getItemId()){
                            case R.id.update:
                                AlertDialog.Builder ad = new AlertDialog.Builder(Reply.this);
                                ad.setTitle("수정");
                                ad.setMessage("수정할 내용을 입력하세요");
                                final EditText et = new EditText(getApplicationContext());
                                et.setText(mReplyList.get(a_position).getContent());
                                FrameLayout container = new FrameLayout(Reply.this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.leftMargin = 70;
                                params.rightMargin= 70;
                                et.setLayoutParams(params);
                                container.addView(et);
                                ad.setView(container);
                                ad.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertContent = et.getText().toString();
                                        updateToServer();
                                    }
                                });
                                ad.setNegativeButton("취소", null);
                                ad.create().show();
                                return false;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(Reply.this);
                                builder.setTitle("삭제");
                                builder.setMessage("정말 삭제하시겠습니까?");
                                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteToServer();
                                    }
                                });
                                builder.setNegativeButton("아니오", null);
                                builder.create().show();
                                return false;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        btn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replyToServer();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar_reply);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showReplyContent();

    }

    private void reply2ToServer() {
        String boardIdx = board_idx;
        String content = alertContent;
        String nick = preferenceHelper.getNICK();
        String profile = preferenceHelper.getImage();
        String parent = idx+1;

        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Log.e(TAG, "reply2ToServer : " +idx);
        Call<String> call = service.reply2Api(idx, content, parent, boardIdx, profile, nick);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Log.e(TAG, "onResponse : " +response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
    }

    private void updateToServer() {
        String content = alertContent;

        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Log.e(TAG, "updateToserver : " +idx);
        Call<String> call = service.updateReply(idx, content);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                finish();//인텐트 종료
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Intent intent = getIntent(); //인텐트
                startActivity(intent); //액티비티 열기
                overridePendingTransition(0, 0);//인텐트 효과 없애기
                Log.e(TAG, "onResponse : " +response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
    }

    private void deleteToServer() {
        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        String deleted = "deleted";
        Call<String> call = service.deleteReply(idx, board_idx, deleted);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().contains("content is not exist")){
                    Toast.makeText(Reply.this, "내용을 작성해 주세요", Toast.LENGTH_SHORT).show();
                }else {
                    finish();//인텐트 종료
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                    Intent intent = getIntent(); //인텐트
                    startActivity(intent); //액티비티 열기
                    overridePendingTransition(0, 0);//인텐트 효과 없애기
                }
                Log.e(TAG, "onResponse : " +response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
}

    private void replyToServer() {
        String boardIdx = board_idx;
        String content = et_input_reply.getText().toString();
        String nick = preferenceHelper.getNICK();
        String profile = preferenceHelper.getImage();

                //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
                Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                        .addConverterFactory(ScalarsConverterFactory.create()).build();
                ApiInterface service = retrofit.create(ApiInterface.class);
                Call<String> call = service.writeReply(boardIdx, profile, nick, content);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.e(TAG, "내용 : " +response.body());
                        if(response.body().contains("content is not exist")){
                            Toast.makeText(Reply.this, "내용을 작성해 주세요", Toast.LENGTH_SHORT).show();
                        }else {
                            finish();//인텐트 종료
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                            Intent intent = getIntent(); //인텐트
                            startActivity(intent); //액티비티 열기
                            overridePendingTransition(0, 0);//인텐트 효과 없애기
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "내용 : " +t.getMessage());
                    }
                });


    }

    //댓글 내용 불러오기
    private void showReplyContent() {
        //Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://3.34.5.22/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.getReplyContent(board_idx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "onResponse : " + response.body());
                String data = response.body();
                parseBoardData(data);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Reply.this, "실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure : " + t.getMessage());
            }
        });
    }

    private void parseBoardData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("replyData");
            Log.e(TAG, "parseBoardData : " + jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String created = item.optString("created");
                String content = item.optString("content");
                String nick = item.optString("nick");
                String profile = item.optString("profile");
                String idx = item.optString("idx");
                String board_idx = item.optString("board_idx");
                String parent = item.optString("parent");
                String deleted = item.optString("deleted");

                ReplyItem items = new ReplyItem(created, content, nick, profile, idx, board_idx, parent, deleted);

                mReplyList.add(items);
                mRecyclerView.setAdapter(mReplyAdapter);
            }
            if(mReplyList.size() > 0){
                reply.setVisibility(View.GONE);
            }
            mReplyAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // toolbar 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.basic_topbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                Intent i = new Intent(Reply.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0,0);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //back 버튼
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