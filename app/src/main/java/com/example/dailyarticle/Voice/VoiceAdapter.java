package com.example.dailyarticle.Voice;

/**
 * Created by ZZP on 2017/7/7.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dailyarticle.R;
import com.example.dailyarticle.activity.VoiceDetailActivity;
import com.example.dailyarticle.utils.ImageHelper;


import java.util.List;
import static com.example.dailyarticle.utils.MyApplication.getContext;


public class VoiceAdapter extends RecyclerView.Adapter<VoiceAdapter.ViewHolder> {
    private List<VoiceCard> mVoiceCard ;


    static class ViewHolder extends RecyclerView.ViewHolder{
        View voice;
        TextView voiceId;
        ImageView voiceImg;
        TextView voiceAuthor;
        TextView voiceName;

        public   ViewHolder(View view){
            super(view);
            voice =  view;
            voiceId=(TextView) view.findViewById(R.id.voice_id);
//            draweeView=(SimpleDraweeView) view.findViewById(R.id.my_image_view);
            voiceImg=(ImageView) view.findViewById(R.id.voice_img);
            voiceAuthor= (TextView) view.findViewById(R.id.voice_author);
            voiceName = (TextView) view.findViewById(R.id.voice_name);
        }

    }
    public VoiceAdapter(List<VoiceCard> voiceCard){
        mVoiceCard= voiceCard;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_card_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.voice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = viewHolder.getAdapterPosition();
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        VoiceCard voiceCard = mVoiceCard.get(position);
        //设置布局内容
        //标题
        String Id = "第"+voiceCard.getId()+"期";
        holder.voiceId.setText(Id);
        //图片
        String uri = voiceCard.getImage();
        new ImageHelper(getContext()).display(holder.voiceImg,uri);
        //名字、作者、主播
        holder.voiceName.setText(voiceCard.getName());
        holder.voiceAuthor.setText(voiceCard.getAuthor());


        //判断是否设置了监听器
        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition(); // 1
                    VoiceCard voiceCard = mVoiceCard.get(position);
                    Intent intent = new Intent(getContext(),VoiceDetailActivity.class);
                    intent.putExtra("voiceId",voiceCard.getId());
                    intent.putExtra("voiceAuthor",voiceCard.getAuthor());
                    intent.putExtra("voiceImg",voiceCard.getImage());
                    intent.putExtra("voiceName",voiceCard.getName());

                    mOnItemClickListener.onItemClick(holder.itemView,position); // 2
                }
            });
        }

        //长按监听
        if(mOnItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemLongClickListener.onItemLongClick(holder.itemView,position);
                    //返回true 表示消耗了事件 事件不会继续传递
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return mVoiceCard.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }
    public OnItemClickListener mOnItemClickListener;
    public OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

}

