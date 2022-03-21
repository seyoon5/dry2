package com.example.dry.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.dry.Activity.Reply;
import com.example.dry.Item.BoardItem;
import com.example.dry.Item.BoardSubItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
 //클릭 이벤트 처리
    private BoardAdapter.OnItemClickEventListener mItemClickListener;

    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);
    }
    public void setOnItemClickListener(BoardAdapter.OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }

    private PreferenceHelper preferenceHelper;
    private String onNick;
    private String TAG = "boardAdapter";


    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView created, content, nick, idx, replyCnt;
        ImageView edit, reply;
        ViewPager2 viewpager2;
        SpringDotsIndicator dotsIndicator;

        public ViewHolder(@NonNull @NotNull View itemView, final BoardAdapter.OnItemClickEventListener a_itemClickListener) {
            super(itemView);

            preferenceHelper = new PreferenceHelper(itemView.getContext());
            onNick = preferenceHelper.getNICK();

            profile = itemView.findViewById(R.id.iv_board_profile);
            created = itemView.findViewById(R.id.tv_board_created);
            content = itemView.findViewById(R.id.tv_board_content);
            nick = itemView.findViewById(R.id.tv_board_nick);
            idx = itemView.findViewById(R.id.board_idx);
            edit = itemView.findViewById(R.id.iv_board_menu);
            replyCnt = itemView.findViewById(R.id.tv_board_reply_cnt);
            reply = itemView.findViewById(R.id.iv_board_right);

            viewpager2 = itemView.findViewById(R.id.sliderViewPager2); //서브 리사이클러뷰
            dotsIndicator = itemView.findViewById(R.id.spring_dots_indicator);
            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = idx.getText().toString();
                    Intent i = new Intent(v.getContext(), Reply.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    i.putExtra("board_idx", str);
                    Log.e(TAG, "board_idx : " +str);
                    v.getContext().startActivity(i);
                }
            });


            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int posotion = getBindingAdapterPosition();
                    if(posotion != RecyclerView.NO_POSITION){
                        a_itemClickListener.onItemClick(edit, posotion);
                    }
                }
            });
        }
    }


    //    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<BoardItem> mBoardList;   //아이템(data) 클래스와 리사이클러뷰를 연결하기 위해 사용
    private Context mContext;   //아이템(data) 클래스와 리사이클러뷰를 연결하기 위해 사용

    public BoardAdapter(ArrayList<BoardItem> itemList) {    //어댑터 생성자 (를 통해 리사이클러뷰가 아이템(data)클래스와 연결)
        this.mBoardList = itemList;
    }

    @NonNull
    @NotNull
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_board_item, parent, false);
        ViewHolder holder = new BoardAdapter.ViewHolder(view, mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BoardAdapter.ViewHolder holder, int position) {
        BoardItem items = mBoardList.get(position);
        holder.idx.setText(items.getIdx());
        if(items.getReplyCnt().contains("none")){
            holder.replyCnt.setText("댓글");
        }else{
            holder.replyCnt.setText(items.getReplyCnt());
        }

        holder.created.setText(items.getCreated());
        holder.content.setText(items.getContent());
        holder.nick.setText(items.getNick());

        Glide.with(holder.profile.getContext())
                .load("http://3.34.5.22/images/" + items.getProfile())
                .error(R.drawable.user_icon)
                .into(holder.profile);

        //view pager
        Log.e(TAG, "onBindViewHolder : " + items.getViewpager2().get(0).getSubImage());
        if (items.getViewpager2().get(0).getSubImage() != "none") {
            holder.viewpager2.setVisibility(View.VISIBLE);
            holder.viewpager2.setOffscreenPageLimit(1);
            holder.viewpager2.setAdapter(new ImageSliderAdapter(items.getViewpager2()));
            /*indicator*/
            if(items.getViewpager2().get(0).getSubImage().contains("jpg") && items.getViewpager2().size() > 1){
                holder.dotsIndicator.setViewPager2(holder.viewpager2);
                holder.dotsIndicator.setVisibility(View.VISIBLE);
            }
        }
    }

        @Override
        public int getItemCount() {
            //return mBoardList.size();
            return  mBoardList == null ? 0 : mBoardList.size();
        }

//        //자식 레이아웃 매니저
//        LinearLayoutManager layoutManager = new LinearLayoutManager(
//                holder.rvSubItem.getContext(),
//                LinearLayoutManager.HORIZONTAL,
//                false
//        );
//        layoutManager.setInitialPrefetchItemCount(items.getSubItemList().size());
//
//        //하위 어댑터 설정
//        BoardSubAdapter subItemAdapter = new BoardSubAdapter(items.getSubItemList());
//
//        holder.rvSubItem.setLayoutManager(layoutManager);
//        holder.rvSubItem.setAdapter(subItemAdapter);
//        holder.rvSubItem.setRecycledViewPool(viewPool);

}
