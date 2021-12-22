package com.example.dry.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dry.Item.BoardSubItem;
import com.example.dry.Item.ViewPagerItem;
import com.example.dry.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private String TAG = "ImageSliderAdapter";
    private ArrayList<ViewPagerItem> imageList;

    public ImageSliderAdapter(ArrayList<ViewPagerItem> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewPagerItem viewPagerItem = imageList.get(position);
       // holder.mImageView.setImageURI(Uri.parse(viewPagerItem.getSubImage()));
        Log.e(TAG, "뷰페이저 이미지 : " +viewPagerItem.getSubImage());
        if(!viewPagerItem.getSubImage().equals("none")) {
            holder.mImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.mImageView)
                    .load("http://13.125.206.46/images/" + viewPagerItem.getSubImage())
                    .fallback(null)
                    .into(holder.mImageView);
        }else{

        }

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageSlider);
        }

    }
}


