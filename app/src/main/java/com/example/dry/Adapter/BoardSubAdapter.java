package com.example.dry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dry.Item.BoardSubItem;
import com.example.dry.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class BoardSubAdapter extends RecyclerView.Adapter<BoardSubAdapter.SubItemViewHolder> {
    private ArrayList<BoardSubItem> mSubItemList;

    public BoardSubAdapter(ArrayList<BoardSubItem> subItemList) {
        this.mSubItemList = subItemList;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_board_subitem, parent, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubItemViewHolder holder, int position) {
        BoardSubItem subItem = mSubItemList.get(position);
        holder.ivSubImage.setText(subItem.getSubItemImage());
    }

    @Override
    public int getItemCount() {
        return mSubItemList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView ivSubImage;

        public SubItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSubImage = itemView.findViewById(R.id
                    .board_subItem);
        }
    }

}