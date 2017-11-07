package com.example.dailyarticle.BookCase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dailyarticle.R;
import com.example.dailyarticle.activity.BookActivity;
import com.example.dailyarticle.utils.EndlessRecyclerOnScrollListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZP on 2017/7/12.
 */

public class BookCaseFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    View mView;
    RecyclerView recyclerView;
    GridLayoutManager mGridLayoutManager;


    List<BookCase> bookCases = new ArrayList<>();
    BookCaseAdapter adapter;
    String URL = "http://book.meiriyiwen.com/book?page=";
    int page=1;
    private Handler handler = new Handler();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView  = inflater.inflate(R.layout.fragment_bookcase,container,false);
        initView(mView);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            public void run() {
                setData();
            }
        }, 200);
        setListener();
    }

    private void setListener() {
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if(page<=8){
                    page++;
                    MyTask task = new MyTask();
                    task.execute(URL+page);}
                else Toast.makeText(getContext(),"没有更多数据了",Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(new BookCaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                BookCase book = bookCases.get(position);
                Intent intent = new Intent(getContext(), BookActivity.class);
                intent.putExtra("bookName",book.getBookName());
                intent.putExtra("bookAuthor",book.getBookAuthor());
                intent.putExtra("imageAddres",book.getImageAddress());
                intent.putExtra("bookAddress",book.getBookAddress());
                startActivity(intent);
            }
        });
    }

    private void setData() {
        MyTask task = new MyTask();
        task.execute(URL+page);
    }

    private void initView(View view ){
        recyclerView  = (RecyclerView) view.findViewById(R.id.recycle_view);
        mGridLayoutManager=new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(mGridLayoutManager);
        adapter = new BookCaseAdapter(bookCases);
        recyclerView.setAdapter(adapter);

    }
    class MyTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            try {
                Document doc = Jsoup.connect(params[0]).get();
                Elements books = doc.getElementsByTag("tr");
                for(Element book : books){
                    BookCase bookCase = new BookCase();
                    Elements titles = book.getElementsByClass("book-name");
                    for(Element title :titles){
                        bookCase.setBookName(title.text());
                    }
                    Elements Address = book.getElementsByClass("book-img");
                    for(Element addr :Address){
                        bookCase.setBookAddress("http://book.meiriyiwen.com"+addr.attr("href"));
                    }
                    Elements imgs = book.getElementsByTag("img");
                    for(Element img :imgs){
                        bookCase.setImageAddress("http://book.meiriyiwen.com"+img.attr("src"));
                    }
                    Elements authors = book.getElementsByClass("book-author");
                    for(Element author :authors){
                        bookCase.setBookAuthor(author.text());
                    }
                    bookCases.add(bookCase);
                }
            } catch (
                    Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (bool) {
                adapter.notifyDataSetChanged();
            } else {
                Log.w("zzp","BookFragmentError");
                //Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
