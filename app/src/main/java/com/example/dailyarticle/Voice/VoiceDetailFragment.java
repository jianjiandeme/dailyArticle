package com.example.dailyarticle.Voice;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.example.dailyarticle.activity.VoiceDetailActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZZP on 2017/7/14.
 */

public class VoiceDetailFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";
    //布局变量
    View mView;
    GridLayoutManager gridLayoutManager;
    private VoiceAdapter adapter;
    RecyclerView recyclerView;
    //判断是否已加载过，避免重复加载
    int haveViewed =0;
    //网址前缀
    String URL = "http://voice.meiriyiwen.com/voice/past?page=";
    //页数
    int mPage;
    List<VoiceCard> voiceCards= new ArrayList<>();


    //构造方法
    public  VoiceDetailFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE, page);
        VoiceDetailFragment voiceDetailFragment = new VoiceDetailFragment();
        voiceDetailFragment.setArguments(args);
        return voiceDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获得页数
        mPage = getArguments().getInt(ARGS_PAGE);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_voice_detail,container,false);
        //绘制布局
        initView(mView);
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置内容
        setData(mView);
        //设置监听
        setListener();
    }

    private void setListener() {
        adapter.setOnItemClickListener(new VoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VoiceCard voiceCard = voiceCards.get(position);
                Intent intent = new Intent (getContext(),VoiceDetailActivity.class);
                intent.putExtra("voiceId",voiceCard.getId());
                intent.putExtra("voiceAuthor",voiceCard.getAuthor());
                intent.putExtra("voiceSmallImg",voiceCard.getImage());
                intent.putExtra("voiceName",voiceCard.getName());
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new VoiceAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getContext(), "long click ", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void setData(View mView) {

        if(haveViewed==0){
        MyTask task = new MyTask();
        task.execute(URL+mPage);}
        adapter.notifyDataSetChanged();
    }

    private void initView(View view ) {
        recyclerView = (RecyclerView) view.findViewById(R.id.voice_recycle_test);
        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        int i = voiceCards.size();
        adapter = new VoiceAdapter(voiceCards);
        recyclerView.setAdapter(adapter);
    }

        class MyTask extends AsyncTask<String, Void, Boolean> {
            @Override
            protected Boolean doInBackground(String... params) {

                //if(voiceCards.size()>0) return true;
                try {
//                    voiceCards.clear();
                    Document doc = Jsoup.connect(params[0]).get();
                    VoiceCard voiceCard;
                    Elements pages = doc.getElementsByClass("voice_card");
                    for (Element page : pages) {
                        voiceCard = new VoiceCard();
                        Elements imgs = page.getElementsByTag("img");
                        for (Element img : imgs) {
                            String urlLast=img.attr("src").replaceAll("\\.","_250.");
                            voiceCard.setImage("http://voice.meiriyiwen.com" + urlLast);
                        }

                        Elements Details = page.getElementsByClass("voice_title");
                        {
                            for (Element detail : Details) {
                                Elements Names = detail.getElementsByTag("a");
                                for (Element name : Names) {
                                    voiceCard.setName(name.text());
                                    voiceCard.setId(name.attr("href").substring(16));
                                }
                            }
                        }
                        Elements authors = page.getElementsByClass("voice_author");
                        for (Element author : authors) {
                            voiceCard.setAuthor(author.text());
                        }
                        voiceCards.add(voiceCard);
//                        adapter.notifyDataSetChanged();
                    }
//                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean bool) {
                if (bool) {
                    haveViewed = 1;
                    int i = voiceCards.size();

                    Log.e("zzp",i+"ge"+mPage);

                    //更新内容
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("zzp","VoiceFragmentError");
                    //Toast.makeText(getContext(), "未知错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
    public void onDestroy(){
        super.onDestroy();
            Log.e("zzp", "onDestroy: "+mPage  );
        haveViewed=0;
        }
    }