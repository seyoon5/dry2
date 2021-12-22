package com.example.dry.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dry.Item.BoardItem;
import com.example.dry.Item.BoardUpdateItem;
import com.example.dry.R;


import java.util.ArrayList;

public class BoardUpdateAdapter extends RecyclerView.Adapter<BoardUpdateAdapter.ViewHolder> {
    private String TAG = "BoardUpdateAdapter";
    private BoardUpdateAdapter.OnItemClickEventListener mItemClickListener;
    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);
    }
    public void setOnItemClickListener(BoardUpdateAdapter.OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }


    public ArrayList<BoardUpdateItem> mImgList;
    public Context mContext;

    public BoardUpdateAdapter(ArrayList<BoardUpdateItem> mImgList, Context mContext){
        this.mImgList = mImgList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BoardUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_update_item,parent,false);
        BoardUpdateAdapter.ViewHolder viewHolder = new BoardUpdateAdapter.ViewHolder(view, mItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardUpdateAdapter.ViewHolder holder, int position) {
        BoardUpdateItem items = mImgList.get(position);
        Log.e(TAG, "onBindViewHolder : " +items.getImage());
        if(items.getImage().contains("content") || items.getImage().contains("file")){
            holder.thumbNail.setImageURI(Uri.parse(items.getImage()));
        }else {
            Glide.with(holder.thumbNail.getContext())
                    .load("http://13.125.206.46/images/" + items.getImage())
                    .error(R.drawable.user_icon)
                    .fallback(R.drawable.user_icon)
                    .into(holder.thumbNail);
        }
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbNail;
        private ImageButton cancel;

        public ViewHolder(View view, final BoardUpdateAdapter.OnItemClickEventListener a_itemClickListener) {
            super(view);
            thumbNail = view.findViewById(R.id.board_update_iv);
            cancel = view.findViewById(R.id.board_update_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        a_itemClickListener.onItemClick(cancel, position);
                    }
                }
            });
        }
    }
}