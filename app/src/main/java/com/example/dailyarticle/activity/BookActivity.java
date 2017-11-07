package com.example.dailyarticle.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyarticle.BookCase.ArticleActivity;
import com.example.dailyarticle.BookCase.ArticleAdapter;
import com.example.dailyarticle.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.example.dailyarticle.utils.MyApplication.getContext;

public class BookActivity extends AppCompatActivity  {
    TextView bookName,bookAuthor;
    //书对应网址
    String url;
    ArticleAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView recyclerView;
    GestureDetector detector ;
    List<Article> articles= new ArrayList<>();

    //整个Activity视图的根视图
    View decorView;
    //手指按下时的坐标
    float downX, downY;
    //手机屏幕的宽度和高
    float screenWidth, screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_book);
        // 获得decorView
        decorView = getWindow().getDecorView();
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


        //从上一层获取内容
        Intent intent = getIntent();
        bookName = (TextView)findViewById(R.id.bookName);
        bookName.setText(intent.getStringExtra("bookName"));
        bookAuthor = (TextView)findViewById(R.id.bookAuthor);
        String Author= intent.getStringExtra("bookAuthor")+"\n";
        bookAuthor.setText(Author);
        url = intent.getStringExtra("bookAddress");
        //创建异步任务解析网页
        MyTask task = new MyTask();
        task.execute(url);

        //为recyclerView添加适配器
        recyclerView = (RecyclerView) findViewById(R.id.book_recycle);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ArticleAdapter(articles);
        recyclerView.setAdapter(adapter);


        //手势操作
       // detector = new GestureDetector(this,this);

        //点击项目进入详情页
        adapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = articles.get(position);
                Intent intent = new Intent(BookActivity.this, ArticleActivity.class);
                intent.putExtra("articleName",article.getName());
                intent.putExtra("articleAddress",article.getAddress());
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            downX = event.getX();

        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            float moveDistanceX = event.getX() - downX;
            if(moveDistanceX > 0){
                decorView.setX(moveDistanceX);
            }

        }else if(event.getAction() == MotionEvent.ACTION_UP){
            float moveDistanceX = event.getX() - downX;
            if(moveDistanceX > screenWidth /2.5){
                // 如果滑动的距离超过了手机屏幕的一半, 滑动处屏幕后再结束当前Activity
                continueMove(moveDistanceX);
            }else{
                // 如果滑动距离没有超过一半, 往回滑动
                rebackToLeft(moveDistanceX);
            }
        }
        return super.onTouchEvent(event);
    }

    private void continueMove(float moveDistanceX){
        // 从当前位置移动到右侧。
        ValueAnimator anim = ValueAnimator.ofFloat(moveDistanceX, screenWidth);
        anim.setDuration(100); // 一秒的时间结束, 为了简单这里固定为1秒
        anim.start();

        anim.addUpdateListener((animation)->{
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);

        });

        // 动画结束时结束当前Activity
        anim.addListener(new  AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

        });
    }


     //Activity被滑动到中途时，滑回去
    private void rebackToLeft(float moveDistanceX){
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(100).start();
    }





    class MyTask extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... params){
            try{
                Document doc = Jsoup.connect(params[0]).get();
                Article article ;
                Elements pages = doc.getElementsByClass("chapter-list");
                for(Element page :pages){

                    Elements arts = page.getElementsByTag("a");
                    for(Element art :arts){
                        article = new Article();
                        article.setAddress(art.attr("href"));
                        article.setName(art.text());
                        articles.add(article);
                    }
                }
            }catch (Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool){
            if(bool){
                //更新内容
                adapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getContext(),"未知错误",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Created by ZZP on 2017/7/9.
     */

    public static class Article {
        private String Name;
        private String Address;


        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        private String getAddress() {
            return Address;
        }

        private void setAddress(String address) {
            Address = address;
        }

    }
}
