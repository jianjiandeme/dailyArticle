package com.example.dailyarticle.BookCase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dailyarticle.R;
import com.example.dailyarticle.activity.BookActivity;

import java.util.List;

/**
 * Created by ZZP on 2017/7/4.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<BookActivity.Article> mArticle ;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView articleName;


        public  ViewHolder(View view){
            super(view);
            articleName = (TextView) view.findViewById(R.id.ArticleName);
        }

    }
    public ArticleAdapter(List<BookActivity.Article> articles){
        mArticle= articles;
        Log.w("zzp","adapter onCreate2");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        BookActivity.Article article = mArticle.get(position);
        holder.articleName.setText("   "+article.getName());


        //判断是否设置了监听器
        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition(); // 1
                    mOnItemClickListener.onItemClick(holder.itemView,position); // 2
                }
            });
        }
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
        return mArticle.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

}
