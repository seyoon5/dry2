package com.example.dry.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dry.Activity.Chat;
import com.example.dry.Activity.ChatImage;
import com.example.dry.Item.ChatItem;
import com.example.dry.Item.ChatType;
import com.example.dry.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private String TAG = "ChatAdapter";
    private ArrayList<ChatItem> items = new ArrayList<ChatItem>();
    private Context context;

//    public interface OnImageItemClickEventListener {    // 여러 아이템 위젯 클릭리스너
//        void onImageItemClick(int a_position);
//    }
//    private ChatAdapter.OnImageItemClickEventListener mImageItemClickEventListener;
//
//    public void setImageItemClickListener(ChatAdapter.OnImageItemClickEventListener a_listener) {
//        mImageItemClickEventListener = a_listener;
//    }

    public ChatAdapter(Context context) { //, ArrayList<ChatItem> items
        this.context = context;
        //this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "크리에이트 뷰 홀더 : ");

        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        if (viewType == ChatType.CENTER_MESSAGE) {
//            view = inflater.inflate(R.layout.chat_center_item, parent, false);
//            return new CenterViewHolder(view); }
        if (viewType == ChatType.LEFT_MESSAGE) {
            view = inflater.inflate(R.layout.chat_left_item, parent, false);
            return new LeftViewHolder(view); // , mImageItemClickEventListener
        } else  {  //(viewType == ChatType.RIGHT_MESSAGE)
            view = inflater.inflate(R.layout.chat_right_item, parent, false);
            return new RightViewHolder(view); //, mImageItemClickEventListener
        }
//        else if (viewType == ChatType.LEFT_IMAGE){
//            view = inflater.inflate(R.layout.chat_left_image, parent, false);
//            return new LeftImageViewHolder(view);
//        } else {
//            view = inflater.inflate(R.layout.chat_right_image, parent, false);
//            return new RightImageViewHolder(view);
//        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Log.e(TAG, "바인드 뷰 홀더 : " +items.size());
//        if (viewHolder instanceof CenterViewHolder) {
//            ChatItem item = items.get(position);
//            Log.e(TAG, "내용 : " +item);
//            ((CenterViewHolder) viewHolder).setItem(item);
//        }
        if (viewHolder instanceof LeftViewHolder) {
            ChatItem item = items.get(position);
            ((LeftViewHolder) viewHolder).setItem(item);
        } else {    // if (viewHolder instanceof RightViewHolder)
            ChatItem item = items.get(position);
            ((RightViewHolder) viewHolder).setItem(item);
        }
//        else if (viewHolder instanceof LeftImageViewHolder) {
//            ChatItem item = items.get(position);
//            ((LeftImageViewHolder) viewHolder).setItem(item, context);
//        } else {
//            ChatItem item = items.get(position);
//            ((RightImageViewHolder) viewHolder).setItem(item, context);
//        }
    }

    public int getItemCount() {
//        items.size();
        return  items == null ? 0 : items.size();

    }

    public void addItem(ChatItem item){
        items.add(item);
    }

    public void setItems(ArrayList<ChatItem> items){
        this.items = items;
    }

    public ChatItem getItem(int position){
        return items.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

//    public class CenterViewHolder extends RecyclerView.ViewHolder{
//        TextView contentText;
//
//        public CenterViewHolder(View itemView) {
//            super(itemView);
//
//            contentText = itemView.findViewById(R.id.content_text);
//        }
//
//        public void setItem(ChatItem item){
//            contentText.setText(item.getContent());
//        }
//    }

    public class LeftViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile;
        TextView nameText;
        TextView contentText;
        TextView sendTimeText;
        TextView sendTimeTextLeft;
        ImageView iv_chatLeft;

        public LeftViewHolder(View itemView) { //, final ChatAdapter.OnImageItemClickEventListener a_imageClickListener
            super(itemView);
            Log.e(TAG, "뷰홀더 : LeftViewHolder" );

            profile = itemView.findViewById(R.id.civ_profile_chatLeftItem);
            nameText = itemView.findViewById(R.id.name_text);
            contentText = itemView.findViewById(R.id.msg_text);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
            sendTimeTextLeft = itemView.findViewById(R.id.send_time_leftImg);
            iv_chatLeft = itemView.findViewById(R.id.iv_chat_left);

//            iv_chatLeft.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int position = getBindingAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION){
//                        a_imageClickListener.onImageItemClick(position);
//                    }
//                }
//            });

            iv_chatLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ChatImage.class);
                    i.putExtra("img", contentText.getText());
                    v.getContext().startActivity(i);
                }
            });
        }

        public void setItem(ChatItem item){
            Glide.with(itemView)
                    .load("http://13.125.206.46/images/" + item.getProfile())
                    .error(R.drawable.user_icon)
                    .fallback(R.drawable.user_icon)
                    .into(profile);
            nameText.setText(item.getName());
            if(item.getContent().contains("chatImage_")){

                iv_chatLeft.setVisibility(View.VISIBLE);
                sendTimeTextLeft.setVisibility(View.VISIBLE);
                contentText.setVisibility(View.GONE);
                sendTimeText.setVisibility(View.GONE);
                Glide.with(itemView)
                        .load("http://13.125.206.46/images/" + item.getContent())
                        .into(iv_chatLeft);
                sendTimeTextLeft.setText(item.getSendTime());
                contentText.setText(item.getContent());
            }else{
                contentText.setVisibility(View.VISIBLE);
                sendTimeText.setVisibility(View.VISIBLE);
                iv_chatLeft.setVisibility(View.GONE);
                sendTimeTextLeft.setVisibility(View.GONE);

                contentText.setText(item.getContent());
                sendTimeText.setText(item.getSendTime());
            }

        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView contentText;
        TextView sendTimeText;
        TextView sendTimeImg;

        public RightViewHolder(View itemView) {  //, final ChatAdapter.OnImageItemClickEventListener a_imageClickListener
            super(itemView);
            Log.e(TAG, "뷰홀더 : RightViewHolder" );

            img = itemView.findViewById(R.id.iv_chat_right);
            sendTimeImg = itemView.findViewById(R.id.send_time_rightImg);
            contentText = itemView.findViewById(R.id.msg_text);
            sendTimeText = itemView.findViewById(R.id.send_time_text);

//            img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int position = getBindingAdapterPosition();
//                    if(position != RecyclerView.NO_POSITION){
//                        a_imageClickListener.onImageItemClick(position);
//                    }
//                }
//            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ChatImage.class);
                    i.putExtra("img", contentText.getText());
                    v.getContext().startActivity(i);
                }
            });
        }

        public void setItem(ChatItem item){
            if(item.getContent().contains("chatImage_")) {

                img.setVisibility(View.VISIBLE);
                sendTimeImg.setVisibility(View.VISIBLE);
                contentText.setVisibility(View.GONE);
                sendTimeText.setVisibility(View.GONE);

                Glide.with(itemView)
                        .load("http://13.125.206.46/images/" + item.getContent())
                        .into(img);
                sendTimeImg.setText(item.getSendTime());
                contentText.setText(item.getContent());
            }else {

                img.setVisibility(View.GONE);
                sendTimeImg.setVisibility(View.GONE);
                contentText.setVisibility(View.VISIBLE);
                sendTimeText.setVisibility(View.VISIBLE);

                contentText.setText(item.getContent());
                sendTimeText.setText(item.getSendTime());
            }


            
        }
    }

   /* public class LeftImageViewHolder extends RecyclerView.ViewHolder{
        TextView nameText;
        //ImageView image;
        TextView sendTimeText;

        public LeftImageViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name_text);
            //image = itemView.findViewById(R.id.image_view);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item, Context context){
            MultiTransformation option = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));

            Glide.with(context)
                    .load(item.getContent())
                    .apply(RequestOptions.bitmapTransform(option))
                    .into(image);
            nameText.setText(item.getName());
            sendTimeText.setText(item.getSendTime());
        }
    }

    public class RightImageViewHolder extends RecyclerView.ViewHolder{
        //ImageView image;
        TextView sendTimeText;

        public RightImageViewHolder(View itemView) {
            super(itemView);

            //image = itemView.findViewById(R.id.image_view);
            sendTimeText = itemView.findViewById(R.id.send_time_text);
        }

        public void setItem(ChatItem item, Context context){
//            MultiTransformation option = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));
//
//            Glide.with(context)
//                    .load(item.getContent())
//                    .apply(RequestOptions.bitmapTransform(option))
//                    .into(image);
            sendTimeText.setText(item.getSendTime());
        }
    }*/
}