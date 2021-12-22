package com.example.dry.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.misc.AsyncTask;
import com.example.dry.Adapter.HomeAdapter;
import com.example.dry.Item.HomeItem;
import com.example.dry.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Home extends AppCompatActivity {
    ImageView home, board, location, chat, profile;

    private static String TAG = "Home_Activity";

    private static final String DOC = "documents";
    private static final String ADDRESS_NAME = "address_name";
    private static final String PHONE = "phone";
    private static final String PLACE_NAME = "place_name";
    private static final String PLACE_URL = "place_url";

    String mJsonString;

    private RecyclerView mRecyclerView;
    private HomeAdapter mHomeAdapter;
    private ArrayList<HomeItem> mHomeItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mRecyclerView = findViewById(R.id.laundry_recyclerView);
        mHomeAdapter = new HomeAdapter();

        mRecyclerView.setAdapter(mHomeAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHomeItems = new ArrayList<>();
        GetData task = new GetData();
        task.execute("http://13.125.206.46/home.php");

        mHomeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View a_view, int a_position) {
                final HomeItem item = mHomeItems.get(a_position);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getInfo()));
                startActivity(i);
            }
        });
        menu();
    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "onPreExecute : 1" );
            progressDialog = ProgressDialog.show(Home.this, "로딩중입니다.", null, true, true);
            Log.e(TAG, "onPreExecute : 2" );
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "onPostExecute : " );
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 500);
            Log.e(TAG, "onPostExecute, response : "+s);

            if(s == null){
                Toast.makeText(Home.this, "onPostExecute 실패", Toast.LENGTH_SHORT).show();
            }else {
                mJsonString = s;
                Log.e(TAG, "showResult : " );
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e(TAG, "doInBackground : " );
            String serverURL = strings[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.e(TAG, "doInBackground, responseStatusCode : " +responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){ //httpURLConnection 도 되는가
                    inputStream = httpURLConnection.getInputStream();
                }else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) !=  null){
                    sb.append(line);
                }
                bufferedReader.close();
                Log.e(TAG, "doInBackground : " +sb.toString().trim());
                return sb.toString().trim();

            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException : " +e.toString());
                e.printStackTrace();
                errorString = e.toString();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(DOC);

            for(int i = 0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(PLACE_NAME);
                String address = item.getString(ADDRESS_NAME);
                String phone = item.getString(PHONE);
                String info = item.getString(PLACE_URL);

                HomeItem homeItem = new HomeItem(name, address, phone, info);
                mHomeItems.add(homeItem);
            }
            mHomeAdapter.setLaundryList(mHomeItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void menu(){
        home = findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        board = findViewById(R.id.btn_board);
        board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Board.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });
        location = findViewById(R.id.btn_location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Location.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        chat = findViewById(R.id.btn_chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, ChatList.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

            }
        });
        profile = findViewById(R.id.btn_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Profile.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(0, 0);

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