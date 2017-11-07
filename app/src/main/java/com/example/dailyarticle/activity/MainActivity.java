package com.example.dailyarticle.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.dailyarticle.Article.ArticleFragment;
import com.example.dailyarticle.BookCase.BookCaseFragment;
import com.example.dailyarticle.R;
import com.example.dailyarticle.Voice.VoiceTestFragment;
import com.example.dailyarticle.utils.Constants;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏没有状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        // 绑定viewpager与tablayout
        // 视图对象
        // 自定义类，导航布局的适配器
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager .setOffscreenPageLimit(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        // 新建适配器
        TabAdaper tabAdaper = new TabAdaper(getSupportFragmentManager());
        // 设置适配器
        viewPager.setAdapter(tabAdaper);
        // 绑定viewpager
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(new Runnable() {
            public void run() {
                viewPager.setVisibility(View.VISIBLE);
            }
        }, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.postDelayed(new Runnable() {
            public void run() {
                viewPager.setVisibility(View.INVISIBLE);
            }
        }, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_ClOSE);
        sendBroadcast(intent);
    }
    // fragment的适配器类
    class TabAdaper extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        // 标题数组
        String[] titles = {"文章", "声音","书架"};

        //主界面三个Fragment
        private TabAdaper(FragmentManager fm) {
            super(fm);
            fragmentList.add(new ArticleFragment());
            fragmentList.add(new VoiceTestFragment());
            fragmentList.add(new BookCaseFragment());

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}
