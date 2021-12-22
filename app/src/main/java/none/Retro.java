////BoardActivity.java
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.PopupMenu;
//import android.widget.TextView;
//
//import com.example.jaowitaskapplication.R;
//import com.example.jaowitaskapplication.adapter.board_Adapter;
//import com.example.jaowitaskapplication.DTP.board_Response;
//import com.example.jaowitaskapplication.retrofitutil.ApiClient;
//import com.example.jaowitaskapplication.retrofitutil.ApiInterface;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.snackbar.Snackbar;
//
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class BoardActivity extends AppCompatActivity {
//
//    public static final String TAG = "BoardActivity";
//
//    // 회원정보 보여주는곳
//    TextView tv_user_email, tv_user_nick, tv_user_password;
//    // 바텀 네이게이션 메뉴  -> 하단바
//    BottomNavigationView bottomNavigationView;
//    // 게시물 등록 변수
//    ImageButton btn_add;
//    // 게시물 보여줄 리사이클러뷰 변수
//    RecyclerView recyclerView;
//    // 리사이클러뷰 어탭터
//    board_Adapter adapter;
//    // 아이템 클릭 이벤트
//    board_Adapter.OnItemClickListener mListener;
//    // 데이터 보관 변수
//    ArrayList<board_Response> list = new ArrayList<>();
//    // 리사이클러뷰 정렬 변수
//    LinearLayoutManager linearLayoutManager;
//
//    // 하단바
//    ImageView home, chat, mypage;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_board);
//
//        // 레이아웃 연결
//        tv_user_email = findViewById(R.id.tv_user_email);
//        tv_user_nick = findViewById(R.id.tv_user_nick);
//        tv_user_password = findViewById(R.id.tv_user_password);
//        bottomNavigationView = findViewById(R.id.bottomNavi);
//        btn_add = (ImageButton) findViewById(R.id.btn_add);
//        home =  findViewById(R.id.home);
//        chat = findViewById(R.id.chat);
//        mypage =  findViewById(R.id.mypage);
//
//        // String으로 전환
//        String user_email = getIntent().getStringExtra("user_email");
//        String user_nick = getIntent().getStringExtra("user_nick");
//        String user_password = getIntent().getStringExtra("user_password");
//
//        // 정보 보여주는곳
//        tv_user_email.setText(user_email);
//        tv_user_nick.setText(user_nick);
//        tv_user_password.setText(user_password);
//
//        // 하단바
//        mypage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mypage.setColorFilter(Color.parseColor("#55ff0000"));
//                Intent intent_mypage = new Intent(BoardActivity.this, HomeActivity.class);
//                intent_mypage.putExtra("user_email", user_email);
//                intent_mypage.putExtra("user_nick", user_nick);
//                intent_mypage.putExtra("user_password", user_password);
//                startActivity(intent_mypage);
//                finish();
//                overridePendingTransition(0, 0);
//            }
//        });
//
//        //당겨서 새로고침 기능 구현 (build.fradle에 라이브러리 선언해 줘야한다.
//        // 당겨서 새로고침하는 레이아웃 연결
//        SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
//        // 당기면 실행된다.
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.d("새로고침, onRefresh()", "실행됨");
//                //새로고침 코드 구현
//                //대량의 데이터 초기화
////                mItems.clear();
//                selectBoard();
//                // 새로고침 완료
//                refreshLayout.setRefreshing(false);
//                Log.d("새로고침", "완료");
//            }
//        });
//
//
//        // 레이아웃 연결
//        recyclerView = (RecyclerView) findViewById(R.id.board_recyclerview);
////        linearLayoutManager = new LinearLayoutManager(this);
////        linearLayoutManager.setReverseLayout(true);
////        linearLayoutManager.setStackFromEnd(true);
////        // 세로 배치
////        recyclerView.setLayoutManager(linearLayoutManager);
////        // 아이템 구별하기 쉽게 선 보이게 하기
////        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
//        // todo
//        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
////        recyclerView.setHasFixedSize(true);
//        // 세로 배치
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//
//        // 아이템 구별하기 쉽게 선 보이게 하기
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//
////        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
//        // todo
//
//        // db데이터 불러오기 메소드 실행
//        selectBoard();
//
//        // 게시물 추가 페이지로 이동
//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(BoardActivity.this, AddActivity.class);
//                intent.putExtra("user_email", user_email);
//                intent.putExtra("user_nick", user_nick);
//                intent.putExtra("user_password", user_password);
//                startActivity(intent);
////                finish();
////                Log.e("BoardActivity a ", "게시물 추가 페이지로 이동 : " + user_email);
////                Log.e("BoardActivity b ", "게시물 추가 페이지로 이동 : " + user_nick);
////                Log.e("BoardActivity c ", "게시물 추가 페이지로 이동 : " + user_password);
//            }
//        });
//
//        // 리사이클러뷰 아이템에 있는 우측 상단 다이얼로그 메뉴 누르는 클릭 리스너 and 이미지 클릭 리스너
//        mListener = new board_Adapter.OnItemClickListener() {
//            // 아이템 클릭시 수정 삭제 버튼
//            @Override
//            public void onItemClick(View v, final int position) {
//                PopupMenu popup = new PopupMenu(getApplicationContext(), v);  //v는 클릭된 뷰를 의미
//
//                getMenuInflater().inflate(R.menu.board_menu, popup.getMenu());
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_edit:
//                                // 수정하기 눌렸을 떄
//                                // 익텍스, 제목, 내용물 데이터
//                                int board_idx = list.get(position).getBoard_idx();
//                                String board_title = list.get(position).getBoard_title();
//                                String board_created = list.get(position).getBoard_created();
//                                String board_image = list.get(position).getBoard_image();
//                                Log.e(TAG, "board_idx : " + board_idx + ", board_title : " + board_title + ", board_created : " + board_created);
//                                Intent intent = new Intent(BoardActivity.this, EditActivity.class);
//                                // 데이터 보내기
//                                intent.putExtra("board_idx", board_idx);
//                                intent.putExtra("board_title", board_title);
//                                intent.putExtra("board_created", board_created);
//                                intent.putExtra("board_image", board_image);
//                                intent.putExtra("user_email", user_email);
//                                intent.putExtra("user_nick", user_nick);
//                                intent.putExtra("user_password", user_password);
//                                startActivity(intent);
////                                finish();
//                                break;
//                            // 삭제하기
//                            case R.id.action_delete:
//
//                                final AlertDialog.Builder mBuilder2 = new AlertDialog.Builder(BoardActivity.this);
//                                View mView2 = getLayoutInflater().inflate(R.layout.board_delete_dialog, null);
//
//                                Button btn_no = mView2.findViewById(R.id.btn_no);
//                                Button btn_yes = mView2.findViewById(R.id.btn_yes);
//
//                                mBuilder2.setView(mView2);
//                                final AlertDialog dialog2 = mBuilder2.create();
//                                dialog2.show();
//                                btn_yes.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        // 삭제 메서드
//                                        // 삭제할 index값만 넘기기
//                                        int board_idx = list.get(position).getBoard_idx();
//                                        deletedata(board_idx);
////                                        Intent delete_intent = new Intent(BoardActivity.this, BoardActivity.class);
////                                        delete_intent.putExtra("user_email", user_email);
////                                        delete_intent.putExtra("user_nick", user_nick);
////                                        delete_intent.putExtra("user_password", user_password);
////                                        startActivity(delete_intent);
//                                        dialog2.dismiss();
//
//                                    }
//                                });
//
//                                btn_no.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog2.dismiss();
//                                    }
//                                });
//                                break;
//                            default:
//                                break;
//                        }
//                        return false;
//                    }
//                });
//                // 메뉴 보이기
//                popup.show();
//            }
//
//            // 내용물 보여주기 액티비티로 이동
//            @Override
//            public void onContentClick(View view, final int pos) {
//                int board_idx = list.get(pos).getBoard_idx();
//                String board_title = list.get(pos).getBoard_title();
//                String board_created = list.get(pos).getBoard_created();
//                Log.e(TAG, "board_idx : " + board_idx + ", board_title : " + board_title + ", board_created : " + board_created);
//                Intent intent = new Intent(BoardActivity.this, DetailActivity.class);
//                // 데이터 보내기
//                intent.putExtra("board_idx", board_idx);
//                intent.putExtra("board_title", board_title);
//                intent.putExtra("board_created", board_created);
//                intent.putExtra("user_email", user_email);
//                intent.putExtra("user_nick", user_nick);
//                intent.putExtra("user_password", user_password);
//                startActivity(intent);
//                finish();
//            }
//
//
//        };
//
//        // todo qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
//
//
//        // todo 하단바 없애기 이유는 깜빡거림 있음
//        // 하단바 실행
////        bottomNavigationView.setOnItemSelectedListener(item -> {
////            switch (item.getItemId()) {
////                // 게시판 화면 이동
////                case R.id.action_home:
//////                    Intent home_intent = new Intent(BoardActivity.this, BoardActivity.class);
//////                    home_intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
//////                    home_intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
//////                    home_intent.putExtra("user_email", user_email);
//////                    home_intent.putExtra("user_nick", user_nick);
//////                    home_intent.putExtra("user_password", user_password);
//////                    startActivity(home_intent);//액티비티 띄우기
//////                    finish();
////                    break;
////
////                case R.id.action_people:  // 검색 화면으로 이동
//////                    Intent search_intent = new Intent(BoardActivity.this, BoardActivity.class);
//////                    search_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//////                    search_intent.putExtra("user_email", user_email);
//////                    search_intent.putExtra("user_nick", user_nick);
//////                    search_intent.putExtra("user_password", user_password);
//////                    startActivity(search_intent);//액티비티 띄우기
//////                    finish();
////                    break;
////                // 마이페이지 화면 이동
////                case R.id.action_mypage:
////                    Intent mypage_intent = new Intent(BoardActivity.this, HomeActivity.class);
////                    mypage_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                    mypage_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
////                    mypage_intent.putExtra("user_email", user_email);
////                    mypage_intent.putExtra("user_nick", user_nick);
////                    mypage_intent.putExtra("user_password", user_password);
////                    startActivity(mypage_intent); // 마의페이지
////                    finish();
////                    break;
////
////            }
////            return false;
////        });
//
//
//    } // oncreate 닫는곳
//
//
//    // db 데이터 불러오기 메소드 실행
//    private void selectBoard() {
//
//        // http 연동
//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        // 받아오기
//        Call<ArrayList<board_Response>> call = apiInterface.getdataselect();
//
//        call.enqueue(new Callback<ArrayList<board_Response>>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<ArrayList<board_Response>> call, @NonNull Response<ArrayList<board_Response>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // 통신 성공하고 onGetResult메소드 실행
//                    Log.e("boardActivity select 정보 불러오기 ", "통신 성공 : " + call);
//                    onGetResult(response.body());
//
//
//                }
//            }  // onResponse 닫는곳
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<ArrayList<board_Response>> call, @NonNull Throwable t) {
//                Log.e("boardActivity select 정보 불러오기 ", "통신 실패 : " + t.getMessage());
//            }
//        });  // call.enqueue 닫는곳
//    }
//
//    private void onGetResult(ArrayList<board_Response> lists) {
//        // 어탭터 실행
//        adapter = new board_Adapter(this, lists, mListener, this);
//        adapter.notifyDataSetChanged();
//        recyclerView.setAdapter(adapter);
//        list = lists;
//    } // onGetResult 닫는곳
//
//    // 삭제 메소드 실행
//    private void deletedata(int id) {
//        // http 통신 시작
//        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        // 통신 되는지 확인
//        Call<board_Response> call = apiInterface.deleteData(id);
//        call.enqueue(new Callback<board_Response>() {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(@NonNull Call<board_Response> call, @NonNull Response<board_Response> response) {
//                Log.e("댓글 삭제 통신 ", "통신 성공 : " + call);
//                selectBoard();
//
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(@NonNull Call<board_Response> call, @NonNull Throwable t) {
//                Log.e("댓글 삭제 통신 ", "통신 실패 : " + t.getMessage());
//            }
//        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        selectBoard();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//}
//
//
//board_Adapter
//
//        import android.annotation.SuppressLint;
//        import android.content.Context;
//        import android.content.SharedPreferences;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.ImageButton;
//        import android.widget.ImageView;
//        import android.widget.LinearLayout;
//        import android.widget.TextView;
//
//        import androidx.annotation.NonNull;
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.PagerSnapHelper;
//        import androidx.recyclerview.widget.RecyclerView;
//        import androidx.recyclerview.widget.SnapHelper;
//
//        import com.bumptech.glide.Glide;
//        import com.bumptech.glide.load.engine.DiskCacheStrategy;
//        import com.bumptech.glide.request.RequestOptions;
//        import com.bumptech.glide.signature.ObjectKey;
//        import com.example.jaowitaskapplication.CirclePagerIndicatorDecoration;
//        import com.example.jaowitaskapplication.R;
//        import com.example.jaowitaskapplication.DTP.board_Response;
//
//        import java.util.ArrayList;
//
//        import de.hdodenhof.circleimageview.CircleImageView;
//
//public class board_Adapter extends RecyclerView.Adapter<board_Adapter.PersonViewHolder> {
//    // 내용물
//    private Context context;
//    // 데이터
//    private ArrayList<board_Response> lists;
//    // 클릭 이벤트
//    private OnItemClickListener mListener = null;
//    // 이미지 Context 를 활용해서 넣기 위해 추가
//    private Context mContext;
//    String readData_memberEmail;
//    // String 으로 이미지 받기
//    String image_store;
//    // 다중 이미지 넣는 어댑터
//    SubItemAdapter subItemAdapter;
//
//    // 어탭터 객체를 보여지는 board_Response연결하는 다리역할
//    public board_Adapter(Context context, ArrayList<board_Response> lists, OnItemClickListener mListener, Context mContext) {
//        this.context = context;
//        this.lists = lists;
//        this.mListener = mListener;
//        this.mContext = mContext; // 이미지 Context 를 활용해서 넣기 위해 추가
//    }  // PersonAdapter 닫는곳
//
//    @NonNull
//    @Override
//    public board_Adapter.PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // 레이아웃 연결
//        View view = LayoutInflater.from(context).inflate(R.layout.board_item, parent, false);
//        return new PersonViewHolder(view, mListener);
//    }  // onCreateViewHolder 닫는곳
//
//    @Override
//    public void onBindViewHolder(@NonNull board_Adapter.PersonViewHolder holder, int position) {
//        // datalist 변수 생성
//        board_Response datalist = lists.get(position);
//        // 데이터 설정
//        holder.tv_board_title.setText(datalist.getBoard_title());
//        holder.tv_board_created.setText(datalist.getBoard_created());
//        holder.tv_board_user_id.setText(datalist.getBoard_user_id());
//        holder.tv_board_user_nick.setText(datalist.getBoard_user_nick());
//
//        // todo 실시간 시간
////        long reply_post_time_millis = Long.parseLong(null);
////        holder.tv_board_created.setText(formatTimeString(reply_post_time_millis));
//
//
//        // todo shared 수정 삭제 안보이게 하기
////        if(datalist.getUser_id() == id`){
////            holder.tv_content.setVisibility(View.VISIBLE);
////        }else{
////            holder.tv_content.setVisibility(View.GONE);
////
////        }
//        // todo 여기까지
//        // 글라이드 캐시메모리 삭제
////        RequestOptions requestOptions = new RequestOptions();
////        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
////        requestOptions.skipMemoryCache(false);
////        requestOptions.centerCrop();
////        requestOptions.centerInside();
////        requestOptions.signature(new ObjectKey(System.currentTimeMillis()));
////
////        // 게시판 이미지
////        Glide.with(mContext)
////                .load("https://homeinteriordesigner.tk/jaowitask/board_image/"+datalist.getBoard_image())
////                .apply(requestOptions)
////                .placeholder(R.drawable.ic_baseline_person_24)
////                .error(R.drawable.ic_baseline_person_24)
////                .into(holder.iv_board_image);
////        Picasso.get()
////                .load("https://homeinteriordesigner.tk/retrofit_task2/"+datalist.getBoard_image())
////                .into(holder.iv_board_image);
//
//        // 글라이드 캐시메모리 삭제
//        RequestOptions requestOptions2 = new RequestOptions();
//        requestOptions2.diskCacheStrategy(DiskCacheStrategy.NONE);
//        requestOptions2.skipMemoryCache(false);
//        requestOptions2.centerCrop();
//        requestOptions2.circleCrop();
//        requestOptions2.signature(new ObjectKey(System.currentTimeMillis()));
//
//        // 회원 이미지
//        Glide.with(mContext)
//                .load("https://homeinteriordesigner.tk/jaowitask/"+datalist.getBoard_user_image())
//                .apply(requestOptions2)
//                .placeholder(R.drawable.ic_baseline_person_24)
//                .error(R.drawable.ic_baseline_person_24)
//                .into(holder.iv_users_image);
////        Picasso.get()
////                .load("https://homeinteriordesigner.tk/retrofit_task2/"+datalist.getBoard_user_image())
////                .placeholder(R.drawable.ic_baseline_person_24)
////                .error(R.drawable.ic_baseline_person_24)
////                .into(holder.iv_users_image);
//
//        // todo 다중이미지
//        // String 으로 이미지 정보 받는다
//        image_store = datalist.getBoard_image();
//        // 이미지 정보들을 split 으로 짤라준다.
//        String[] imagelist = image_store.split(" ");
//        // String 으로 배열 객체 생성 짤라준 이미지 넣기 위해서
//        ArrayList<String> imagesdata = new ArrayList<>();
//        // String[]의 길이만큼 for문을 돌면서
//        for (int i = 0; i < imagelist.length; i++)
//        {
//            // 이미지 여러장 생성
//            imagesdata.add(imagelist[i]);
//        }
//
//        // 자식 레이아웃 매니저 설정 || 가로로 생성
//        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.sub_recylcerview_list.getContext(), LinearLayoutManager.HORIZONTAL, false);
//        layoutManager.setInitialPrefetchItemCount(datalist.getBoard_idx());
//        // 리사이클러뷰 안 아이템들의 크기를 가변적으로 바꿀지 아니면 일정한 크기를 사용할지를 지정한다.
//        holder.sub_recylcerview_list.setHasFixedSize(true);
////        holder.sub_recylcerview_list.setLayoutManager(layoutManager);
//        //snaphelper에서 발생하는 onflinglistener already set 오류 방지용 코드
//        holder.sub_recylcerview_list.setOnFlingListener(null);
//        // 리사이클러뷰를 좌우로 스크롤할 때 item들이 아무데서나 멈추는게 아니라 자석이 끌어 당기는것 처럼 position 1번 자리의 좌표에서 멈추는 것을 도와준다.
//        SnapHelper snapHelper = new PagerSnapHelper();
////        holder.sub_recylcerview_list.setLayoutManager(layoutManager);
//        // viewpager 처럼 작용 || snaphelprt를 리사이클러뷰 에 실행한다는 뜻
//        snapHelper.attachToRecyclerView(holder.sub_recylcerview_list);
//        // paint 적용
//        holder.sub_recylcerview_list.addItemDecoration(new CirclePagerIndicatorDecoration());
//        // 하위 어답터 설정
//        subItemAdapter = new SubItemAdapter(imagesdata);
//        // 어댑터랑 리사이클러뷰 배치
//        holder.sub_recylcerview_list.setLayoutManager(layoutManager);
//        // 어댑터 생팅
//        holder.sub_recylcerview_list.setAdapter(subItemAdapter);
//        // todo 분석하기
//
//        // 로그인한 이메일 값 가져오기
//        SharedPreferences logined_user = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);   // 현재 로그인한 회원의 정보만 담겨있는 쉐어드를 불러와서
//        readData_memberEmail=logined_user.getString("user_email","");
//
//        //게시글 편집, 삭제 버튼 (작성자만 보이게 한다)
//        if(datalist.getBoard_user_id().equals(readData_memberEmail) ){
//            holder.imageButton_spinner.setVisibility(ImageView.VISIBLE);
//
//        }else{
//            holder.imageButton_spinner.setVisibility(ImageView.GONE);
//        }
//
//    }  // onBindViewHolder 닫는곳
//
//    // 반환하기
//    @Override
//    public int getItemCount() {
//        return lists.size();
//    }  // getItemCount() 닫는곳
//
//    public class PersonViewHolder extends RecyclerView.ViewHolder  {  // implements View.OnClickListener
//        // 레이아웃 연결할 변수 설정
//        public LinearLayout linearLayout;
//        public TextView tv_board_title, tv_board_created, tv_board_user_id, tv_board_user_nick;
//        // 클린 이벤트
//        OnItemClickListener mListener;
//        // 아이템에 있는 버튼
//        ImageButton imageButton_spinner;
//        ImageView iv_reply_content;    //댓글 페이지로 이동
//        CircleImageView iv_users_image;
//        // 아이템 안에 리사이클러뷰 생성
//        private RecyclerView sub_recylcerview_list;
//
//        public PersonViewHolder(@NonNull View view, OnItemClickListener mListener) {
//            super(view);
//            linearLayout = view.findViewById(R.id.linear_layout);
//            tv_board_title = view.findViewById(R.id.tv_board_title);
//            tv_board_created = view.findViewById(R.id.tv_board_created);
//            tv_board_user_id = view.findViewById(R.id.tv_board_user_id);
//            tv_board_user_nick = view.findViewById(R.id.tv_board_user_nick);
//            imageButton_spinner =view.findViewById(R.id.imageButton_spinner);
//            iv_reply_content =view.findViewById(R.id.iv_reply_content);
//            iv_users_image =view.findViewById(R.id.iv_users_image);
//            sub_recylcerview_list = view.findViewById(R.id.sub_recylcerview_list);
//
//
//            // 클릭 이벤트
//            this.mListener = mListener;
//
//            // todo 아이템 전체 클릭되는 부분
//            view.setOnClickListener(v -> {
//
//            });
//
//            // 댓글 이미지 버튼 클릭시
//            iv_reply_content.setOnClickListener(new View.OnClickListener() {
//                @SuppressLint("LongLogTag")
//                @Override
//                public void onClick(View view) {
//                    // 여기에서 어댑터 positon을 get한다.
//                    int pos = getAdapterPosition();
//                    Log.e("게시판 어댑터 작동 ", "이미지 클릭시 상세페이지로 이동 : "); // 내가 커스텀한 onItemClick 리스너에서 getAdapterPostion했습니다
//
//                    // 아이템클릭 이벤트 메서드에서 리스너 객체 메서드 (onItemClick)호출
//                    if (pos != RecyclerView.NO_POSITION) {  // != pos가 RecyclerView.NO_POSITION가 같지 않으면 true, 그렇지 않으면 false
//                        if (mListener != null) {          // mListener은 null;
//                            mListener.onContentClick(view, pos);
//                        }
//                    }
//                }
//            });
//
//            // 수정 삭제 버튼
//            imageButton_spinner.setOnClickListener(new View.OnClickListener() {
//                //            itemView.setOnClickListener(new View.OnClickListener() {  // 이렇게 하면 해당 버튼을 눌렀다는 코드가 아니라 itemView를 눌렀다는 코드임.
//                @SuppressLint("LongLogTag")
//                @Override
//                public void onClick(View view) {
//                    int pos = getAdapterPosition();  // 여기서 어댑터 Postion을 get하면
//                    Log.e("게시판 어댑터 작동 ", "수정, 삭제 버튼 클릭시 실행 : ");  // 내가 커스텀한 onItemClick 리스너에서 getAdapterPostion했습니다
//
//                    // 아이템클릭 이벤트 메서드에서 리스너 객체 메서드 (onItemClick) 호출.
//                    if (pos != RecyclerView.NO_POSITION) {  // NO_POSITION 말 그래도 position된 상태가 아니라 위치값을 가져올 수 없다는 말이다.
//                        if (mListener != null) {
//                            mListener.onItemClick(view, pos);
//
//
//                        }
//                    }
//                }
//            });
//        }  // public PersonViewHolder 닫는고
//
//    } // PersonViewHolder 닫는곳
//
//    // 인터페이스 어떤것을 선택 하는냐 선택 상황
//    public interface OnItemClickListener {
//        // 수정 삭제
//        void onItemClick(View v, int position);
//        // 내용물 보기
//        void onContentClick(View view, int pos);
//    }
//    // todo 실시간 시간
//    static class TIME_MAXIMUM {
//        static final int SEC = 60;
//        static final int MIN = 60;
//        static final int HOUR = 24;
//        static final int DAY = 30;
//        static final int MONTH = 12;
//    }
//
//    static String formatTimeString(long regTime) {
//        long curTime = System.currentTimeMillis();
//        long diffTime = (curTime - regTime) / 1000;
//        String msg = null;
//        if (diffTime < TIME_MAXIMUM.SEC) {
//            msg = "방금 전";
//        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
//            msg = diffTime + "분 전";
//        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
//            msg = (diffTime) + "시간 전";
//        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
//            msg = (diffTime) + "일 전";
//        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
//            msg = (diffTime) + "달 전";
//        } else {
//            msg = (diffTime) + "년 전";
//        }
//        return msg;
//    }
//    // todo 실시간 시간 여기까지
//
//
//} // adapter 닫는곳곳
//
//
//
//SubItemAdapter.java
//
//        import android.content.Context;
//        import android.net.Uri;
//        import android.util.Log;
//        import android.view.LayoutInflater;
//        import android.view.View;
//        import android.view.ViewGroup;
//        import android.widget.ImageView;
//        import android.widget.TextView;
//
//        import androidx.annotation.NonNull;
//        import androidx.recyclerview.widget.RecyclerView;
//
//
//        import com.bumptech.glide.Glide;
//        import com.bumptech.glide.load.engine.DiskCacheStrategy;
//        import com.bumptech.glide.request.RequestOptions;
//        import com.bumptech.glide.signature.ObjectKey;
//        import com.example.jaowitaskapplication.R;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//// 하위 어답터
//public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {
//
//    private ArrayList<String> lists;
//
//
////    private ArrayList<imagedata.imagelist> lists;
//
//    List test = new ArrayList();
//
//    String url;
//
//
//    private Context mContext2;
//    // 내용물
//
//    SubItemAdapter(ArrayList<String> lists) {
//        this.lists = lists;
//    }
//
//    @NonNull
//    @Override
//    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_sub_item, viewGroup, false);
//        return new SubItemViewHolder(view);
//
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SubItemViewHolder subItemViewHolder, int po) {
//
//        // todo 여기까지
//        // 글라이드 캐시메모리 삭제
//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
//        requestOptions.skipMemoryCache(false);
//        requestOptions.centerCrop();
//        requestOptions.centerInside();
//        requestOptions.signature(new ObjectKey(System.currentTimeMillis()));
//
//        Glide.with(subItemViewHolder.img_sub_item)
//                .load("https://homeinteriordesigner.tk/jaowitask/board_image/"+lists.get(po))
//                .apply(requestOptions)
//                .centerCrop()
//                .placeholder(R.drawable.ic_baseline_person_24)
//                .error(R.drawable.ic_baseline_person_24)
//                .into(subItemViewHolder.img_sub_item);
//    }
//
//    @Override
//    public int getItemCount() {
//        Log.e("서브 count 1 ", " 정보: " + lists.size());
//        return lists.size();
//    }
//
//    class SubItemViewHolder extends RecyclerView.ViewHolder {
//        ImageView img_sub_item;
//
//        SubItemViewHolder(View itemView) {
//            super(itemView);
//            img_sub_item = itemView.findViewById(R.id.img_sub_item);
//        }
//    }
//}
//
//
//
//
//CirclePagerIndicatorDecoration
//
//        import android.content.res.Resources;
//        import android.graphics.Canvas;
//        import android.graphics.Color;
//        import android.graphics.Paint;
//        import android.graphics.Rect;
//        import android.util.Log;
//        import android.view.View;
//        import android.view.animation.AccelerateDecelerateInterpolator;
//
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.RecyclerView;
//
//public class CirclePagerIndicatorDecoration extends RecyclerView.ItemDecoration {
//
//    private int colorActive = 0xFF607D90;
//    private int colorInactive = 0xFFCFD8DC;
//
//    private static final float DP = Resources.getSystem().getDisplayMetrics().density;
//
//    /**
//     * Height of the space the indicator takes up at the bottom of the view.
//     */
//    private final int mIndicatorHeight = (int) (DP * 16);
//
//    /**
//     * Indicator stroke width.
//     */
//    private final float mIndicatorStrokeWidth = DP * 2;
//
//    /**
//     * Indicator width.
//     */
//    private final float mIndicatorItemLength = DP * 16;
//    /**
//     * Padding between indicators.
//     */
//    private final float mIndicatorItemPadding = DP * 4;
//
//    /**
//     * Some more natural animation interpolation
//     */
//    private final android.view.animation.Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
//
//    // 그리기(Draw)하기 위해 쓰여지는 도구 이다. 쉽게 말해 Canvas가 도화지라면 Pain는 붓이라고 한다. 도화지에 그림을 그릴 떄 우리들은 여러가지 붓을 사용하여 효과를 준다. 붓을 굵기, 색상, 모양 등
//    private final Paint mPaint = new Paint();
//
//    public CirclePagerIndicatorDecoration() {
//        // 선의 끝나는 지점의 장시을 설정
//        mPaint.setStrokeCap(Paint.Cap.ROUND); // 동근 모양
//        // pain의 굵기를 설정
//        mPaint.setStrokeWidth(mIndicatorStrokeWidth);
//        // pain 스타일 지정
//        mPaint.setStyle(Paint.Style.FILL); // fill 색상이 채워지고 테두리는 그려지지 않는다.
//        // pain의 경계면을 부드럽게 처리한다.
//        mPaint.setAntiAlias(true);
//    }
//
//    @Override
//    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        super.onDrawOver(c, parent, state);
//
//        int itemCount = parent.getAdapter().getItemCount();
//
//        // center horizontally, calculate width and subtract half from center
//        float totalLength = mIndicatorItemLength * itemCount;
//        float paddingBetweenItems = Math.max(0, itemCount - 1) * mIndicatorItemPadding;
//        float indicatorTotalWidth = totalLength + paddingBetweenItems;
//        float indicatorStartX = (parent.getWidth() - indicatorTotalWidth) / 2F;
//
//        // center vertically in the allotted space
//        float indicatorPosY = parent.getHeight() - mIndicatorHeight / 2F;
//
//        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount);
//
//
//        // find active page (which should be highlighted)
//        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
//        int activePosition = layoutManager.findFirstVisibleItemPosition();
//        if (activePosition == RecyclerView.NO_POSITION) {
//            return;
//        }
//
//        // find offset of active page (if the user is scrolling)
//        final View activeChild = layoutManager.findViewByPosition(activePosition);
//        int left = activeChild.getLeft();
//        int width = activeChild.getWidth();
//
//        // on swipe the active item will be positioned from [-width, 0]
//        // interpolate offset for smooth animation
//        float progress = mInterpolator.getInterpolation(left * -1 / (float) width);
//
//        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount);
//    }
//
//    private void drawInactiveIndicators(Canvas c, float indicatorStartX, float indicatorPosY, int itemCount) {
//        mPaint.setColor(Color.GRAY);
//
//        // width of item indicator including padding
//        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;
//
//        float start = indicatorStartX;
//
//        //아이템이 한개면 버튼을 출력하지 않는다.
//        if(itemCount > 1) {
//            for (int i = 0; i < itemCount; i++) {
//                // draw the line for every item
//                c.drawCircle(start, indicatorPosY, itemWidth / 6, mPaint);
//                //  c.drawLine(start, indicatorPosY, start + mIndicatorItemLength, indicatorPosY, mPaint);
//                start += itemWidth;
//            }
//        }
//    }
//
//    private void drawHighlights(Canvas c, float indicatorStartX, float indicatorPosY,
//                                int highlightPosition, float progress, int itemCount) {
//        mPaint.setColor(Color.RED);
//
//        // width of item indicator including padding
//        final float itemWidth = mIndicatorItemLength + mIndicatorItemPadding;
//
//        if (progress == 0F) {
//            // no swipe, draw a normal indicator
//            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
//
//         /*
//            c.drawLine(highlightStart, indicatorPosY,
//                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
//        */
//
//            //아이템이 한개면 버튼을 출력하지 않는다.
//            if(itemCount > 1) {
//                c.drawCircle(highlightStart, indicatorPosY, itemWidth / 6, mPaint);
//            }
//
//        } else {
//            float highlightStart = indicatorStartX + itemWidth * highlightPosition;
//            // calculate partial highlight
//            float partialLength = mIndicatorItemLength * progress;
//
//
//            // draw the cut off highlight
//           /* c.drawLine(highlightStart + partialLength, indicatorPosY,
//                    highlightStart + mIndicatorItemLength, indicatorPosY, mPaint);
//*/
//            // draw the highlight overlapping to the next item as well
//            if (highlightPosition < itemCount - 1) {
//                highlightStart += itemWidth;
//                /*c.drawLine(highlightStart, indicatorPosY,
//                        highlightStart + partialLength, indicatorPosY, mPaint);*/
//
//                Log.d("dot", "drawHighlights: itemcount: " + itemCount);
//                c.drawCircle(highlightStart ,indicatorPosY,itemWidth / 6,mPaint);
//
//            }
//        }
//    }
//
//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
//        outRect.bottom = mIndicatorHeight;
//    }
//}
//
//
//</a
//        select.php
//
//<?php
//
//        header("Content-type:application/json");
//
//        require_once 'connect.php';
//
//        $board_title = $_GET['board_title'];
//        $board_user_id = $_GET['board_user_id'];
//        $board_user_nick = $_GET['board_user_nick'];
//        $board_image = $_GET['board_image'];
//        $board_user_image = $_GET['board_user_image'];
//
//        $sql = mysqli_query($con, "SELECT * FROM board_life LEFT JOIN jusers on board_life.board_user_id = jusers.user_email order by board_idx");
//
//        $response = array();
//
//        while($row = mysqli_fetch_assoc($sql))
//        {
//        array_push($response, array(
//        'board_idx' => $row['board_idx'],
//        'board_title' => $row['board_title'],
//        'board_created' => $row['board_created'],
//        'board_user_id' => $row['user_email'],
//        'board_user_nick' => $row['user_nick'],
//        'board_image' => $row['board_image'],
//        'board_user_image' => $row['user_image'],
//        ));
//        }
//
//        echo json_encode($response);
//        mysqli_close($con);
//        ?>
//
//
//        image_upload_add.php
//
//<?php
//// Path to move uploaded files
//        $target_path = dirname(__FILE__).'/board_image/';
//        $dbimagelist = "";
//
//// $userid = $_POST['userid']; //작성자의 고유 아이디 값 또한 가지고 온다.
//// $username = $_POST['username'];
//        $size = $_POST['size']; //size라는 이름으로 요청을 post로 받아온다.
//        $board_title = $_POST['board_title'];
//        $board_user_id = $_POST['board_user_id'];
//        $board_user_nick = $_POST['board_user_nick'];
//        $board_user_image = $_POST['board_user_image'];
//
//
//        if (!empty($_FILES)) {
//        for ($x = 0; $x < $size; $x++) {
//        try {
//        //$newname = basename( $_FILES["fileToUpload"]["name"]);
//        $newname = date('YmdHis',time()).mt_rand().'.jpg';
//
//        $dbimagelist.=$newname." ";
//        //$dbimagelist =. $newname. " ";
//        // Throws exception incase file is not being moved
//        // $target_path .$newname
//        if (!move_uploaded_file($_FILES['image'.$x]['tmp_name'], $target_path .$newname)) {  // $target_path
//        // make error flag true
//        echo json_encode(array('status'=>'fail', 'message'=>'could not move file'));
//        }
//        // File successfully uploaded //이 부분에서 데이터베이스 등록작업을 실행해주어야 한다.
//        echo json_encode(array('status'=>'success', 'message'=>'File Uploaded'));
//        } catch (Exception $e) {
//        // Exception occurred. Make error flag true
//        echo json_encode(array('status'=>'fail', 'message'=>$e->getMessage()));
//        }
//        }
//
//        $conn = mysqli_connect("localhost", "root", "123", "jaowi");
//        mysqli_set_charset($conn,'utf8');
//        $insert_query = "INSERT INTO board_life (board_title, board_created, board_user_id ,board_user_nick,board_image, board_user_image) VALUES ($board_title, NOW(),$board_user_id,$board_user_nick,'$dbimagelist', null)";
//        mysqli_query($conn, $insert_query);
//
//
//        $myfile=fopen("newfile.txt","w") or die("Unable to open file");  // $target_path
//        // fwrite($myfile,$size);
//        // fwrite($myfile,$name);
//        //해당 내용들이 잘 가지고 와졌는지에 대해서 확인
//        fwrite($myfile,$dbimagelist);
//        fwrite($myfile,$title);
//        fclose($myfile);
//
//
//        } else {
//        // File parameter is missing
//        echo json_encode(array('status'=>'fail', 'message'=>'Not received any file'));
//        }
//        ?>
//
//
//        A
//        import com.google.gson.annotations.Expose;
//        import com.google.gson.annotations.SerializedName;
//
//public class board_Response
//{
//
//    @Expose
//    @SerializedName("board_idx") private int board_idx;
//
//    @Expose
//    @SerializedName("board_title") private String board_title;
//
//    @Expose
//    @SerializedName("board_created") private String board_created;
//
//    @Expose
//    @SerializedName("board_user_nick") private String board_user_nick;
//
//    @Expose
//    @SerializedName("board_user_id") private String board_user_id;
//
//    @Expose
//    @SerializedName("success") private Boolean success;
//
//    @Expose
//    @SerializedName("message") private String message;
//
//    @Expose
//    @SerializedName("board_image") private String board_image;
//
//    @Expose
//    @SerializedName("board_user_image") private String board_user_image;
//
//    @Expose
//    @SerializedName("test") private String test;
//
//    @Expose
//    @SerializedName("page") private int page;
//
//    @Expose
//    @SerializedName("limit") private int limit;
//
//    public int getBoard_idx() {
//        return board_idx;
//    }
//
//    public void setBoard_idx(int board_idx) {
//        this.board_idx = board_idx;
//    }
//
//    public String getBoard_title() {
//        return board_title;
//    }
//
//    public void setBoard_title(String board_title) {
//        this.board_title = board_title;
//    }
//
//    public String getBoard_created() {
//        return board_created;
//    }
//
//    public void setBoard_created(String board_created) {
//        this.board_created = board_created;
//    }
//
//    public String getBoard_user_nick() {
//        return board_user_nick;
//    }
//
//    public void setBoard_user_nick(String board_user_nick) {
//        this.board_user_nick = board_user_nick;
//    }
//
//    public String getBoard_user_id() {
//        return board_user_id;
//    }
//
//    public void setBoard_user_id(String board_user_id) {
//        this.board_user_id = board_user_id;
//    }
//
//    public Boolean getSuccess() {
//        return success;
//    }
//
//    public void setSuccess(Boolean success) {
//        this.success = success;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public String getBoard_image() {
//        return board_image;
//    }
//
//    public void setBoard_image(String board_image) {
//        this.board_image = board_image;
//    }
//
//    public String getBoard_user_image() {
//        return board_user_image;
//    }
//
//    public void setBoard_user_image(String board_user_image) {
//        this.board_user_image = board_user_image;
//    }
//
//    public String getTest() {
//        return test;
//    }
//
//    public void setTest(String test) {
//        this.test = test;
//    }
//
//    public int getPage() {
//        return page;
//    }
//
//    public void setPage(int page) {
//        this.page = page;
//    }
//
//    public int getLimit() {
//        return limit;
//    }
//
//    public void setLimit(int limit) {
//        this.limit = limit;
//    }
//}
//
//
//ApiClient
//
//        import android.util.Log;
//
//        import com.google.gson.Gson;
//        import com.google.gson.GsonBuilder;
//
//        import retrofit2.Retrofit;
//        import retrofit2.converter.gson.GsonConverterFactory;
//        import retrofit2.converter.scalars.ScalarsConverterFactory;
//
//public class ApiClient {
//    // URL 받기
//    private static final String BASE_URL = "https://homeinteriordesigner.tk/jaowitask/";   // URL(프로토콜+URL):https://homeinteriordesigner.tk/retrofit_task/
//    private static Retrofit retrofit;
//
//    // 메소드 실행
//    public static Retrofit getApiClient()
//    {
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
////        Log.e("ApiClient 1 ", "getApiClient : " + gson);
//        if(retrofit==null)
//        {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)  // 요청을 보낼 base url을 설정한다.
////                    .addConverterFactory(new NullOnEmptyConverterFactory()) // <- 이 문장을 추가한다
//                    .addConverterFactory(GsonConverterFactory.create(gson)) // JSON 파싱을 위한 GsonConverterFactory를 추가한다.   ||    Json을 변환해줄 Gson 변환기 등록
////                    .addConverterFactory(ScalarsConverterFactory.create())
////                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .build();
////            Log.e("ApiClient 1 ","순서확인"+retrofit);
////            Log.e("ApiClient 2 ","순서확인"+BASE_URL);
//        }
//        return retrofit;
//    }
//}
//
//
//FileUtils
//
//        import android.content.ContentUris;
//        import android.content.Context;
//        import android.database.Cursor;
//        import android.net.Uri;
//        import android.os.Build;
//        import android.os.Environment;
//        import android.provider.DocumentsContract;
//        import android.provider.MediaStore;
//        import android.util.Log;
//
//        import androidx.annotation.RequiresApi;
//
//        import java.io.File;
//
//public class FileUtils {
//    private FileUtils() {} //private constructor to enforce Singleton pattern
//
//    /** TAG for log messages. */
//    private static final String TAG = "FileUtils";
//    private static final boolean DEBUG = false; // Set to true to enable logging
//
//    //제품 파일 타입을 설정해주는 공용 변수.
//    static final String MIME_TYPE_AUDIO = "audio/*";
//    static public String MIME_TYPE_TEXT = "text/*";
//    static public String MIME_TYPE_IMAGE = "image/*";
//    static final String MIME_TYPE_VIDEO = "video/*";
//    static final String MIME_TYPE_APP = "application/*";
//
//    public static final String HIDDEN_PREFIX = ".";
//
//    /**
//     * Gets the extension of a file name, like ".png" or ".jpg".
//     *
//     * @param uri Image URI
//     * @return Extension including the dot("."); "" if there is no extension;
//     *         null if uri was null.
//     */
//    public static String getExtension(String uri) {
//        if (uri == null) {
//            return null;
//        }
//
//        int dot = uri.lastIndexOf(".");
//        if (dot >= 0) {
//            return uri.substring(dot);
//        } else {
//            // No extension.
//            return "";
//        }
//    }
//
//    //해당 파일을 받아옴.
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public static File getFile (Context context, Uri uri) {
//        String path = getPath(context, uri);
//        Log.e(TAG,"GET FILE 이 호출 됌." + path);
//        assert path != null; // 파일패스 값이 없어서는 안된다는 것을 나타낸다. 이를 바로 에러처리를 한다.
//        return new File(path);
//    }
//
//    //해당 스트링을 경로를 받아옴.
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public static String getPath(Context context, Uri uri) {
//        // DocumentProvider
//        if (DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory().getPath() + "/" + split[1];
//                }
//                // TODO handle non-primary volumes
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[] {split[1]};
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // Return the remote address
//            if (isGooglePhotosUri(uri))
//                return uri.getLastPathSegment();
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
//    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = { column };
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//    private static boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     */
//    private static boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    private static boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is Google Photos.
//     */
//    private static boolean isGooglePhotosUri(Uri uri) {
//        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
//    }
//}
//
//
//
//InternetConnection
//
//
//        import android.content.Context;
//        import android.net.ConnectivityManager;
//
//        import androidx.annotation.NonNull;
//
//        import java.util.Objects;
//
//public class InternetConnection {
//    /**
//     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
//     */
//    public static boolean checkConnection(@NonNull Context context) {
//        return ((ConnectivityManager) Objects.requireNonNull(context.getSystemService
//                (Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo() != null;
//    }
//}
//
//    // todo 다중 이미지 추가
//    @Multipart
//    @POST("image_upload_add.php")
//    Call<ResponseBody> uploadMultiple(
//            @Part("board_title") String board_title,
//            @Part("board_user_id") String board_user_id,
//            @Part("board_user_nick") String board_user_nick,
//            @Part("board_user_image") String board_user_image,
//            @Part("size") RequestBody size,
//            @Part List<MultipartBody.Part> files);
//    // todo 다중 이미지 여기까지
//
//    @FormUrlEncoded
//    @POST("select.php")
//        // data 받고 보냄
//    Call<ArrayList<board_Response>> getdataselect(
//            @Field("page") int page,
//            @Field("limit") int limit
//
//    );