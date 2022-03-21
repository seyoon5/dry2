package com.example.dry.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dry.Item.ChatListModel;
import com.example.dry.Item.ChatRoomItem;
import com.example.dry.PreferenceHelper;
import com.example.dry.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private PreferenceHelper preferenceHelper;
    private String TAG = "ChatListAdapter";
    private ChatListAdapter.OnItemClickEventListener mItemClickListener;

    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);
    }
    public void setOnItemClickListener(ChatListAdapter.OnItemClickEventListener a_listener){
        this.mItemClickListener = a_listener;
    }


    //아이템(data)와 리사이클러뷰를 연결하기 위해 사용
    private ArrayList<ChatRoomItem> mChatItems;

    //어댑터 생성자 (를 통해 리사이클러뷰가 아이템(data)와 연결)
    public ChatListAdapter(ArrayList<ChatRoomItem> itemList) {
        this.mChatItems = itemList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile;
        private TextView nick;
        private TextView content;
        private TextView time;
        private CardView cardView;
        private TextView readCnt;

        public ViewHolder(@NonNull @NotNull View itemView, final ChatListAdapter.OnItemClickEventListener a_itemClickListener) {
            super(itemView);

            profile = itemView.findViewById(R.id.iv_chat_list);
            nick = itemView.findViewById(R.id.tv_nick_chatList);
            content = itemView.findViewById(R.id.tv_content_chatList);
            time = itemView.findViewById(R.id.tv_time_chatList);
            cardView = itemView.findViewById(R.id.cv_chat_list);
            readCnt = itemView.findViewById(R.id.tv_readCnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int posotion = getBindingAdapterPosition();
                    if(posotion != RecyclerView.NO_POSITION){
                        a_itemClickListener.onItemClick(itemView, posotion);
                    }
                }
            });     // 각각의 뷰만을 클릭하고 싶으면 title.setOnClickListener , content.setOnClickListener 등으로 활용
        }
    }

    @NonNull
    @NotNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat_list, parent, false);
        ViewHolder holder = new ChatListAdapter.ViewHolder(view, mItemClickListener); //  클릭 이벤트시 추가할 parameter
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Log.e(TAG, "채팅 리스트 onBindViewHolder 몇번 실행될까" );
        preferenceHelper = new PreferenceHelper(holder.nick.getContext());
        String me = preferenceHelper.getNICK();
        Log.e(TAG, "me : " +me);

        ChatRoomItem items = mChatItems.get(position);
        Log.e(TAG, "2.9 onBindViewHolder : getSender=" +items.getSender());
        Log.e(TAG, "2.9 onBindViewHolder : getReceiver=" +items.getReceiver());
        Log.e(TAG, "2.9 onBindViewHolder : getUser=" +items.getUser());
        Log.e(TAG, "2.9 onBindViewHolder : getIdx=" +items.getIdx());
        holder.cardView.setVisibility(View.VISIBLE);

        if(items.getUser().contains(me)){
            // 같은 방에 들어와서 클라이언트 nick(sender) 이랑 dbReceiver 랑 같은 경우
            if(me.equals(items.getReceiver())){
                Log.e(TAG, "items.getReceiver() : " +items.getReceiver());
                holder.nick.setText(items.getSender());
                holder.readCnt.setText(items.getRead_cnt_receiver());
                holder.readCnt.setVisibility(View.VISIBLE);

                Log.e(TAG, "items.getRead_cnt_receiver() : " +items.getRead_cnt_receiver());
                if(items.getRead_cnt_receiver().equals("0")){
                    Log.e(TAG, ").equals(\"0\") : " );
                    holder.readCnt.setVisibility(View.GONE);
                }
                Glide.with(holder.profile.getContext())
                        .load("http://3.34.5.22/images/" + items.getSender_profile())
                        .error(R.drawable.user_icon)
                        .into(holder.profile);
            }else if(me.equals(items.getSender())){
                Log.e(TAG, "items.getSender() : " +items.getSender());
                holder.nick.setText(items.getReceiver());
                holder.readCnt.setText(items.getRead_cnt_sender());
                holder.readCnt.setVisibility(View.VISIBLE);
                Log.e(TAG, "items.getRead_cnt_sender() : " +items.getRead_cnt_sender());

                if(items.getRead_cnt_sender().equals("0")){
                    Log.e(TAG, ").equals(\"0\") : " );

                    holder.readCnt.setVisibility(View.GONE);
                }
                Glide.with(holder.profile.getContext())
                        .load("http://3.34.5.22/images/" + items.getReceiver_profile())
                        .error(R.drawable.user_icon)
                        .into(holder.profile);
            }
            holder.content.setText(items.getContent());
            holder.time.setText(items.getTime());
        }else{
            holder.cardView.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        //return mChatItems.size();
        return  mChatItems == null ? 0 : mChatItems.size();
        //arrayList.size error 나오면 리턴 값 대체
    }
}
// 클릭이벤트 interface 설정, 어댑터에서 설정하고 main 에서 interface 활용해 클릭 이벤트 넣으면 됨.
//    private BoardAdapter.OnItemClickEventListener mItemClickListener;
//    public interface OnItemClickEventListener{
//        void onItemClick(View a_view, int a_position);
//    }
//    public void setOnItemClickListener(BoardAdapter.OnItemClickEventListener a_listener){
//        mItemClickListener = a_listener;
//    }

