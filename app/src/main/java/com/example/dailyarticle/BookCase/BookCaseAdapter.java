package com.example.dailyarticle.BookCase;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.dailyarticle.R;
import com.example.dailyarticle.utils.ImageHelper;
import com.example.dailyarticle.utils.MyApplication;

import java.util.List;

/**
 * Created by ZZP on 2017/7/4.
 */

public class BookCaseAdapter extends RecyclerView.Adapter<BookCaseAdapter.ViewHolder> {
    private List<BookCase> mBookCase ;

    static class ViewHolder extends RecyclerView.ViewHolder{
       ImageView bookImg;
        TextView bookAuthor,bookName;

        public  ViewHolder(View view){
            super(view);
            bookImg = (ImageView) view.findViewById(R.id.book_img);
            bookName = (TextView) view.findViewById(R.id.book_name);
            bookAuthor = (TextView) view.findViewById(R.id.book_author);
        }

    }
    public BookCaseAdapter(List<BookCase> bookCaseList){
        mBookCase= bookCaseList;
        Log.w("zzp","adapter onCreate2");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        Log.w("zzp","adapter onCreate3");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookcase_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        Log.w("zzp","adapter onCreate4");
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        BookCase bookCase = mBookCase.get(position);
        holder.bookName.setText(bookCase.getBookName());
        String uri = bookCase.getImageAddress();
        new ImageHelper(MyApplication.getContext()).display(holder.bookImg,uri);



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
        return mBookCase.size();
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
