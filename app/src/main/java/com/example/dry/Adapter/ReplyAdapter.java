package com.example.dry.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dry.Activity.Reply;
import com.example.dry.Item.ReplyItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    //클릭 이벤트 처리
    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);   //하나의 아이템 클릭리스너
    }
    public interface OnReplyItemClickEventListener {    // 여러 아이템 위젯 클릭리스너
        void onReplyItemClick(int a_position);
    }
    public interface OnMenuItemClickEventListener {    // 여러 아이템 위젯 클릭리스너
        void onMenuItemClick(int a_position);
    }

    private ArrayList<ReplyItem> mReplyList;   //아이템(data) 클래스와 리사이클러뷰를 연결하기 위해 사용

    private ReplyAdapter.OnItemClickEventListener mItemClickListener;
    private OnReplyItemClickEventListener mReplyItemClickEventListener;
    private OnMenuItemClickEventListener mMenuItemClickEventListener;

    public ReplyAdapter(ArrayList<ReplyItem> itemList) {    //어댑터 생성자 (를 통해 리사이클러뷰가 아이템(data)클래스와 연결)
        this.mReplyList = itemList;

    }
    public void setOnItemClickListener(ReplyAdapter.OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }
    public void setReplyItemClickListener(ReplyAdapter.OnReplyItemClickEventListener a_listener) {
        mReplyItemClickEventListener = a_listener;
    }
    public void setMenuItemClickListener(ReplyAdapter.OnMenuItemClickEventListener a_listener) {
        mMenuItemClickEventListener = a_listener;
    }


    private PreferenceHelper preferenceHelper;
    private String onNick;
    private String TAG = "ReplyAdapter";


    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cv_reply;
        CircleImageView profile;
        TextView created, content, nick, idx, reComment;
        ImageView edit, right;
        LinearLayout gone, gone2;

        public ViewHolder(@NonNull @NotNull View itemView,
                          final ReplyAdapter.OnItemClickEventListener a_itemClickListener,
                          final ReplyAdapter.OnReplyItemClickEventListener a_replyClickListener,
                          final ReplyAdapter.OnMenuItemClickEventListener a_menuClickListener) {
            super(itemView);

            preferenceHelper = new PreferenceHelper(itemView.getContext());
            onNick = preferenceHelper.getNICK();

            gone = itemView.findViewById(R.id.reply_gone);
            gone2 = itemView.findViewById(R.id.reply_gone2);
            cv_reply = itemView.findViewById(R.id.cv_reply);
            profile = itemView.findViewById(R.id.iv_reply_profile);
            created = itemView.findViewById(R.id.tv_reply_created);
            content = itemView.findViewById(R.id.tv_reply_content);
            reComment = itemView.findViewById(R.id.tv_reComment);
            nick = itemView.findViewById(R.id.tv_reply_nick);
            idx = itemView.findViewById(R.id.reply_idx);
            edit = itemView.findViewById(R.id.iv_reply_menu);
            right = itemView.findViewById(R.id.iv_reply_right);

            reComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int posotion = getBindingAdapterPosition();
                    if(posotion != RecyclerView.NO_POSITION){
                        a_replyClickListener.onReplyItemClick(posotion);
                    }
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

    @NonNull
    @NotNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_reply_item, parent, false);
        ViewHolder holder = new ReplyAdapter.ViewHolder(view, mItemClickListener,mReplyItemClickEventListener, mMenuItemClickEventListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReplyAdapter.ViewHolder holder, int position) {
        ReplyItem items = mReplyList.get(position);

//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.cv_reply.getLayoutParams();

            if(!items.getParent().equals("null") && !items.getParent().equals("")){
                holder.right.setVisibility(View.VISIBLE);
//            layoutParams.leftMargin = 100;
//            holder.cv_reply.setLayoutParams(layoutParams);
                holder.reComment.setVisibility(View.GONE);
            }else{
                holder.right.setVisibility(View.GONE);
                holder.reComment.setVisibility(View.VISIBLE);
//            layoutParams.leftMargin = 0;
//            holder.cv_reply.setLayoutParams(layoutParams);
            }

            holder.idx.setText(items.getIdx());
            holder.created.setText(items.getCreated());
            holder.content.setText(items.getContent());
            holder.nick.setText(items.getNick());

            if (items.getNick().equals(onNick)) {
                holder.edit.setVisibility(View.VISIBLE);
            }
            Glide.with(holder.profile.getContext())
                    .load("http://13.125.206.46/images/" + items.getProfile())
                    .error(R.drawable.user_icon)
                    .fallback(R.drawable.user_icon)
                    .into(holder.profile);
        if(items.getDelete().equals("deleted")){
            holder.content.setText("삭제된 글 입니다.");
            holder.gone.setVisibility(View.GONE);


            holder.gone2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        //return mBoardList.size();
        return  mReplyList == null ? 0 : mReplyList.size();
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
