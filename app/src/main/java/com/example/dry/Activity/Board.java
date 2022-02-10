package com.example.dry.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dry.Adapter.BoardAdapter;
import com.example.dry.Item.BoardItem;
import com.example.dry.Item.ViewPagerItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.Retrofit.ApiInterface;
import com.example.dry.Retrofit.BoardInterface;
import com.example.dry.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Board extends AppCompatActivity {
    ImageView home, board, location, chat, profile;
    private String TAG = "board";

    private String idx;
    private String nick;
    private String userProfile;

    private NestedScrollView nestedScrollView;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;

    private PreferenceHelper preferenceHelper;

    private BoardAdapter mBoardAdapter;
    private ArrayList<BoardItem> mBoardItems;

    private int page = 0, limit = 5;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Log.e(TAG, "onCreate : " );
        preferenceHelper = new PreferenceHelper(this);
        nestedScrollView = findViewById(R.id.scroll_view);
        mRecyclerView = findViewById(R.id.board_recyclerView);
        progressBar = findViewById(R.id.progress_bar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBoardItems = new ArrayList<>();
        mBoardAdapter = new BoardAdapter(mBoardItems);
        mRecyclerView.setAdapter(mBoardAdapter);
        mBoardAdapter.setOnItemClickListener(new BoardAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                PreferenceHelper preferenceHelper = new PreferenceHelper(getApplicationContext());

                idx = mBoardItems.get(a_position).getIdx();
                nick = mBoardItems.get(a_position).getNick();
                userProfile = mBoardItems.get(a_position).getProfile();

                final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), a_view);

                if(nick.equals(preferenceHelper.getNICK())){
                    getMenuInflater().inflate(R.menu.crud_menu, popupMenu.getMenu());
                }else {
                    getMenuInflater().inflate(R.menu.chat_menu, popupMenu.getMenu());
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.update:
                                Intent i = new Intent(Board.this, BoardUpdate.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                i.putExtra("idx", idx);
                                startActivity(i);
                                overridePendingTransition(0, 0);
                                return false;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
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
                            case R.id.chat:
                                Intent intent = new Intent(Board.this, Chat.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.putExtra("username", nick);
                                intent.putExtra("userProfile", userProfile);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                return false;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        floatingActionButton = findViewById(R.id.btn_board_floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Board.this, BoardWrite.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        getData(page, limit);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
                    page++;
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData(page, limit);
                        }
                    }, 500);

                }
            }
        });
        menu();
    }



    //뷰 페이저   String image = item.optString("content_image");
    private ArrayList<ViewPagerItem> viewPagerItemList(String originImg) {
        String imgArray[] = originImg.split(" ");
        ArrayList<ViewPagerItem> imageList = new ArrayList<>();

        for(int i = 0; i<imgArray.length; i++){
            ViewPagerItem viewItem = new ViewPagerItem(imgArray[i]);
            imageList.add(viewItem);
        }
        return imageList;
    }

    //게시판 내용 불러오기
    private void getData(int page, int limit) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        BoardInterface service = retrofit.create(BoardInterface.class);
        Call<String> call = service.boardApi(page, limit);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "onResponse : " + response.body());
                if(response.isSuccessful() && response.body() != null){
                    progressBar.setVisibility(View.GONE);
                }
                String data = response.body();
                parseBoardData(data);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Board.this, "실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure : " + t.getMessage());
            }
        });
    }
    private void
    parseBoardData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("d" +
                    "ata");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String created = item.optString("created");
                String content = item.optString("content");
                String nick = item.optString("nick");
                String profile = item.optString("profile_image");
                String idx = item.optString("idx");

                String reply_cnt;
                if(!item.optString("reply_cnt").equals("") && !item.optString("reply_cnt").equals("null")) {
                    reply_cnt = item.optString("reply_cnt");
                }else {
                    reply_cnt = "none";
                }

                String content_image;
                if(!item.optString("content_image").equals("") && !item.optString("content_image").equals("null")) {
                    content_image = item.optString("content_image");

                }else {
                    content_image = "none";
                }

                Log.e("content_image", "내용 : "+content_image);
                Log.e(TAG, "BoardItem : created:"+created+  "content:"+content+"  nick:"+nick+"  profile:"+profile+"  idx:"+idx+"  reply_cnt:"+reply_cnt +"  (ContentImg):"+content_image);
                BoardItem items = new BoardItem(created, content, nick, profile, idx, reply_cnt, viewPagerItemList(content_image));

                mBoardItems.add(items);
                mRecyclerView.setAdapter(mBoardAdapter);

            }
            mBoardAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void deleteToServer(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://13.125.206.46/")
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        ApiInterface service = retrofit.create(ApiInterface.class);
        Call<String> call = service.deleteBoard(idx);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                finish();
                Intent i = new Intent(Board.this, Board.class);
                startActivity(i);
                overridePendingTransition(0, 0);

                Log.e(TAG, "onResponse : " +response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure : " +t.getMessage());
            }
        });
    }

    //메뉴 엑티비티, 앱 종료
    private void menu() {
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Board.this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Board.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Board.this, ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Board.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

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
// 클릭 이벤트 main 에서 처리
//        mBoardAdapter.setOnItemClickListener(new BoardAdapter.OnItemClickEventListener() {
//            @Override
//            public void onItemClick(View a_view, int a_position) {
//
//            }
//        });

//메서드로 만들어도 됨
//        try {
//            JSONObject jsonObject = new JSONObject(jsonobj);
//            JSONArray jsonArray = jsonObject.getJSONArray(document);
//
//            for(int i = 0; i<jsonArray.length(); i++){
//                JSONObject item = jsonArray.getJSONObject(i);
//
//                String title = item.getString(document_title);
//                String content = item.getString(document_content);
//                String nick = item.getString(document_nick);
//
//                BoardItem boardItem = new BoardItem(title, content, nick);
//                mBoardItems.add(boardItem);
//            }
//
//            mBoardAdapter.notifyDataSetChanged(); // 어댑터에 정보 변경시 알림.
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
