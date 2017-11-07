package com.example.dailyarticle.Voice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyarticle.R;

import java.util.ArrayList;
import java.util.List;

public class VoiceTestFragment extends Fragment {
    View mView=null;
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_voice_test, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.voiceViewPager);

        viewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabVoiceTest);
        // 新建适配器
        TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
        // 设置适配器
        viewPager.setAdapter(tabAdapter);

        // 直接绑定viewpager，消除了以前的需要设置监听器的繁杂工作
        tabLayout.setupWithViewPager(viewPager,true);
    }


    // fragment的适配器类
    class TabAdapter extends FragmentStatePagerAdapter {
        List<String> titles= new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();


        public TabAdapter(FragmentManager fm) {
            super(fm);
            for(int i=0;i<38;i++){
                fragmentList.add(new VoiceDetailFragment().newInstance(i+1));
            }
            for(int i=0,j;i<38;i++){
                j=i+1;
                titles.add("第"+j+"页");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
//            return VoiceDetailFragment.newInstance(position + 1);
            Log.e("zzp", "getItem: "+position );
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }



    }


