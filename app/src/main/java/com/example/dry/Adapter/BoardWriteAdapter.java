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

import com.example.dry.R;


import java.util.ArrayList;

public class BoardWriteAdapter extends RecyclerView.Adapter<BoardWriteAdapter.ViewHolder> {
        private BoardWriteAdapter.OnItemClickEventListener mItemClickListener;
    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);
    }
    public void setOnItemClickListener(BoardWriteAdapter.OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }


    public ArrayList<Uri> mImgList;
    public Context mContext;

    public BoardWriteAdapter(ArrayList<Uri> mImgList, Context mContext){
        this.mImgList = mImgList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BoardWriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_board_wite_item,parent,false);
        BoardWriteAdapter.ViewHolder viewHolder = new BoardWriteAdapter.ViewHolder(view, mItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardWriteAdapter.ViewHolder holder, int position) {
        //앨범에서 가져온 이미지 표시
        holder.thumbNail.setImageURI(mImgList.get(position));
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbNail;
        private ImageButton cancel;

        public ViewHolder(View view, final BoardWriteAdapter.OnItemClickEventListener a_itemClickListener) {
            super(view);
            thumbNail = view.findViewById(R.id.board_write_iv);
            cancel = view.findViewById(R.id.board_write_cancel);
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