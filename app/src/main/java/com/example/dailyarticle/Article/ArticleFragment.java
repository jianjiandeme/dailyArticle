package com.example.dailyarticle.Article;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dailyarticle.BookCase.ArticleDatabase;
import com.example.dailyarticle.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ArticleFragment extends Fragment {
    TextView Author,Text,Title;
    String title,author,article;
    String getTitle,getAuthor,date;
    StringBuffer getText = new StringBuffer();
    private ArticleDatabase dbHelper;
    SwipeRefreshLayout articleRefresh;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article,container,false);
        //加载布局
        Text = (TextView) view.findViewById(R.id.response_text);
        Author = (TextView) view.findViewById(R.id.response_author);
        Title = (TextView) view.findViewById(R.id.response_title);
        //下拉
        articleRefresh = (SwipeRefreshLayout) view.findViewById(R.id.article_refresh);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        //延迟加载内容
        handler.postDelayed(new Runnable() {
            public void run() {
                setData();
            }
        }, 50);
        setListener();
    }

    public void setData(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-DD");
        //先从数据库取
        date=sdf.format(new Date());
        dbHelper = new ArticleDatabase(getContext(),"Article.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //根据日期查找
        Cursor cursor = db.query("Article",null,"date=?",new String[]{date},null,null,null);
        if(cursor.moveToLast()){
            //查到直接显示
            title  = cursor.getString(cursor.getColumnIndex("title"));
            author = cursor.getString(cursor.getColumnIndex("author"));
            article = cursor.getString(cursor.getColumnIndex("article"));
            cursor.close();
            //显示
            initArticle(title,author,article);
        }else{
            //今日没有则异步加载
            MyTask task = new MyTask();
            task.execute("https://meiriyiwen.com/","Daily");
        }
    }

    public void setListener(){
        //下拉监听器，显示随即一篇
        articleRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyTask task = new MyTask();
                task.execute("https://meiriyiwen.com/random/iphone","Random");
                articleRefresh.setRefreshing(false);
            }
        });

    }

    class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            try {
                //Jsoup解析网页
                Document doc = Jsoup.connect(params[0]).get();
                getText.append("\n");
                Elements Titles = doc.getElementsByClass("articleTitle");
                for(Element title:Titles){
                    getTitle = title.text();
                }
                Elements Authors = doc.getElementsByClass("articleAuthorName");
                for(Element Author:Authors){
                    getAuthor = Author.text();
                }
                Elements sections = doc.getElementsByTag("p");
                for(Element section:sections){
                    if(!section.text().isEmpty()) getText.append("    "+section.text()+"\n\n");
                }

            } catch (
                    Exception e) {
                e.printStackTrace();
            }
            return  params[1];
        }

        @Override
        protected void onPostExecute(String Tag) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Tag",Tag);
            values.put("title",getTitle);
            values.put("author",getAuthor);
            values.put("article",getText.toString());

            //存入数据库中
            if(Tag.equals("Daily")){
                //Tag为Daily则加上日期，查询时无日期的为随机一篇
                values.put("date",date);
            }
            db.insert("Article",null,values);
            values.clear();
            initArticle(getTitle,getAuthor,getText.toString());
        }
    }

    public void initArticle(final String title,final String author,final String article){
        //根据内容进行显示
                Title.setText(title);
                Author.setText(author);
                Text.setText(article);
                getText = new StringBuffer();
    }
}
