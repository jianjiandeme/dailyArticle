package com.example.dailyarticle.BookCase;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dailyarticle.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static com.example.dailyarticle.utils.MyApplication.getContext;

public class ArticleActivity extends AppCompatActivity {
    String name;
    String address;
    String authorName;
    StringBuffer article = new StringBuffer();
    TextView Name, Author, Article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_article);
        Intent intent = getIntent();
        //从上一层获得文章标题和地址
        name = intent.getStringExtra("articleName");
        address = intent.getStringExtra("articleAddress");
        //设置文章标题
        Name = (TextView) findViewById(R.id.articleName);
        Name.setText(name);
        Author = (TextView) findViewById(R.id.articleAuthor);
        Article = (TextView) findViewById(R.id.article);
        //异步解析网页
        MyTask task = new MyTask();
        task.execute("http://book.meiriyiwen.com/" + address);
    }


    class MyTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {

            try {

                Document doc = Jsoup.connect(params[0]).get();
                Elements sections = doc.getElementsByTag("p");
                article.append("\n");
                for (Element section : sections) {
                    //文章段落拼接
                    article.append("\t\t\t\t" + section.text() + "\n\n");
                }
                Elements authors = doc.getElementsByClass("book-author");
                for (Element author : authors) {
                    //作者
                    authorName = author.text();
                }
                article.append("\n\n\n");
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
                //设置作者和内容
                Author.setText(authorName);
                Article.setText(article);
            } else {
                Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
            }
        }

    }
}