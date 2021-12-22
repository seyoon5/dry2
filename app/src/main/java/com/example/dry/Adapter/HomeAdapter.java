package com.example.dry.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dry.Item.HomeItem;
import com.example.dry.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    public interface OnItemClickEventListener{
        void onItemClick(View a_view, int a_position);
    }
    private OnItemClickEventListener mItemClickListener;
    private ArrayList<HomeItem> mLaundryList;
    private String TAG = "adapter";

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, address, tele;

        public ViewHolder(@NonNull @NotNull View itemView, final OnItemClickEventListener a_itemClickListener) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_home_name);
            address = itemView.findViewById(R.id.tv_home_address);
            tele = itemView.findViewById(R.id.tv_home_tele);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int posotion = getBindingAdapterPosition();
                    if(posotion != RecyclerView.NO_POSITION){
                        a_itemClickListener.onItemClick(itemView, posotion);
                    }
                }
            });
        }
    }


    public void setLaundryList(ArrayList<HomeItem> list){
        Log.e(TAG, "setLaundryList : ArrayList<LaundryItem> list" );
        this.mLaundryList = list;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickEventListener a_listener){
        mItemClickListener = a_listener;
    }

    @NonNull
    @NotNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Log.e(TAG, "onCreateViewHolder : " );
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_home_item, parent, false);
        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeAdapter.ViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder : " );
        HomeItem items = mLaundryList.get(position);

        holder.name.setText(items.getName());
        holder.address.setText(items.getAddress());
        holder.tele.setText(items.getTele());

    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount : " );
        //return mLaundryList.size();
        return  mLaundryList == null ? 0 : mLaundryList.size();
    }
}
