package com.example.dailyarticle.Voice;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.dailyarticle.R;
import com.example.dailyarticle.activity.MainActivity;
import com.example.dailyarticle.utils.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.dailyarticle.utils.MyApplication.getContext;

public class VoiceService extends Service {
    String TAG = "zzp";
    MyReceiver serviceReceiver;
    MediaPlayer mediaPlayer;
    //stopTag标志停止，加载好后不自动播放
    int stopTag = 0,state,isPrepared;
    //信息
    String voiceName,voiceId,voiceAuthor,voiceURL;
    Timer mTimer ;
    TimerTask mTimerTask;
    private NotificationManager manager;
    public  RemoteViews remoteViews;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播接收器
        regFilter();
        //初始化播放器
        mediaPlayer = new MediaPlayer();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(),
                R.layout.customnotice);
        mTimer = new Timer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //获取id异步加载
        voiceId = intent.getStringExtra("voiceId");
        stopTag = 0;
        onLoad(voiceId);
        setListener();
        return super.onStartCommand(intent, flags, startId);
    }


    //解析声音信息
    public void onLoad(final String Id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc = Jsoup.connect("http://voice.meiriyiwen.com/voice/show?vid="+Id).get();
                    //获取声音网址
                    Elements URLs = doc.getElementsByTag("audio");
                    for(Element URL : URLs){
                        voiceURL = URL.attr("src");
                    }
                    //获取作者、主播
                    Elements Authors = doc.getElementsByClass("p_author");
                    for(Element Author :Authors){
                        voiceAuthor = Author.text().replaceAll("&nbsp;","\t");
                    }
                    //获取标题
                    Elements titles = doc.getElementsByClass("p_title");
                    for(Element title :titles){
                        voiceName = title.text();
                    }
                    //初始化播放器
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(voiceURL);
                    isPrepared=0;
                    //异步准备
                    mediaPlayer.prepareAsync();
                    //解析完成后更新活动和通知栏显示
                    setView();
                    Log.w(TAG, "initPlayer: yes");
                }catch (Exception e){e.printStackTrace();}
            }

            private void setView() {
                //发送广播更新活动界面
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_VIEW);
                intent.putExtra("voiceName",voiceName);
                intent.putExtra("voiceId",voiceId);
                intent.putExtra("voiceAuthor",voiceAuthor);
                sendBroadcast(intent);

                //设置通知栏显示
                remoteViews.setTextViewText(R.id.widget_title, voiceName);
                remoteViews.setTextViewText(R.id.widget_artist,voiceAuthor);
                remoteViews.setImageViewResource(R.id.widget_play, R.drawable.play);
                remoteViews.setImageViewBitmap(R.id.widget_album,getBitmap());
                setNotification();
            }
            public Bitmap getBitmap() {
                Bitmap bitmap = null;
                try {
                    URL url = new URL("http://voice.meiriyiwen.com/upload_imgs/"+Id+"_img_250.jpg");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    int code = conn.getResponseCode();

                    if(code==200) {
                        InputStream is = conn.getInputStream();//获得图片的数据流
                        bitmap = BitmapFactory.decodeStream(is);
                    }

                } catch (Exception e ){e.printStackTrace();}
                return bitmap;
            }
        }).start();
    }

    private void setListener() {
        //ErrorListener加载出错三次后播放下一首
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int a, int b) {
                Log.w("zzp", "Error");
                state++;
                if (state == 3) {
                    Log.e(TAG, "onError: " );
                    priAndNext(1);
                    state = 0;
                } else if (state < 3)
                    //重试3次
                    priAndNext(0);
                return false;
            }

        });

        //OnPrepareListener加载好播放和设置播放界面，并一直发送播放进度更新SeekBar进度条
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = mediaPlayer.getDuration()/1000;
                Log.w("zzp","voice start?"+duration);
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_DURATION);
                intent.putExtra("duration",duration);
                sendBroadcast(intent);

                //SeekBar更新广播
                final Intent timeIntent = new Intent();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null&&mediaPlayer.isPlaying()){
                            timeIntent.setAction(Constants.ACTION_TIME);
                            int position =mediaPlayer.getCurrentPosition()/1000;
                            Log.w(TAG, "run: "+position );
                            timeIntent.putExtra("position",position);
                            sendBroadcast(timeIntent);
                        }}
                };
                mTimer.schedule(mTimerTask,0,1000);


                //加载好后自动播放，但点停止后只加载不自动播放
                if(stopTag == 0){
                    //设置播放按钮为播放
                    remoteViews.setImageViewResource(R.id.widget_play, R.drawable.play);
                    mediaPlayer.start();
                }
                else {
                    //播放按钮为暂停
                    remoteViews.setImageViewResource(R.id.widget_play, R.drawable.pause);
                }
                setNotification();

                isPrepared=1;
                Log.w("zzp","voice start");
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //放完后默认播放下一首
                if(mTimerTask!=null)mTimerTask.cancel();
                priAndNext(1);
            }
        });
    }

    //更换播放曲目
    private void priAndNext(int i) {
        //i=1下一首；i=-1上一首
        int x=Integer.parseInt(voiceId);
        if(x==461&&i==-1)Toast.makeText(getContext(),"已经是第一首了",Toast.LENGTH_SHORT).show();
        else if(x==1&&i==1)Toast.makeText(getContext(),"已经是最后一首了",Toast.LENGTH_SHORT).show();
        else x=x-i;
        voiceId = String.valueOf(x);
        onLoad(voiceId);
    }

    //广播接收器
    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_PAUSE);
        filter.addAction(Constants.ACTION_PLAY);
        filter.addAction(Constants.ACTION_NEXT);
        filter.addAction(Constants.ACTION_PRV);
        filter.addAction(Constants.ACTION_STOP);
        filter.addAction(Constants.ACTION_SEEK);
        filter.addAction(Constants.ACTION_ClOSE);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.setPriority(1000);
        serviceReceiver = new MyReceiver();
        registerReceiver(serviceReceiver, filter);
    }


    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();

//        unregisterReceiver
        if(mTimer!=null)mTimer.cancel();
        if (serviceReceiver != null) {
            unregisterReceiver(serviceReceiver);
        }
        //release mediaPlayer
        if (mediaPlayer != null) {
            if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //关闭通知栏
        if (remoteViews != null) {
            manager.cancel(100);
        }
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String control = intent.getAction();
            //暂停，更换通知栏按钮为暂停
            if (control.equals(Constants.ACTION_PAUSE)) {
                Log.i(TAG, "action_pause");
                if (mediaPlayer.isPlaying()) {
                    remoteViews.setImageViewResource(R.id.widget_play, R.drawable.pause);
                    mediaPlayer.pause();
                    setNotification();
                }
            }
            //播放
            else if (control.equals(Constants.ACTION_PLAY)) {
                Log.i(TAG, "action_play");
                if (mediaPlayer != null&&isPrepared==1) {
                    remoteViews.setImageViewResource(R.id.widget_play, R.drawable.play);
                    mediaPlayer.start();
                    setNotification();
                }
            }
            //停止，设置stopTag=1，重新加载本次音乐
            else if (control.equals(Constants.ACTION_STOP)) {
                Log.i(TAG, "action_stop");
//                mTimer.cancel();
//                setNotification();
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                stopTag = 1;
                priAndNext(0);
            }
            //播放下一首，stopTag=0,加载好后自动播放
            else if (control.equals(Constants.ACTION_NEXT)) {
                Log.i(TAG, "action_next");
                stopTag = 0;
                priAndNext(1);
            }
            //播放上一首，stopTag=0,加载好后自动播放
            else if (control.equals(Constants.ACTION_PRV)) {
                Log.i(TAG, "action_prv");
                stopTag = 0;
                priAndNext(-1);
            }
            //播放界面拖动进度条，更新进度
            else if (control.equals(Constants.ACTION_SEEK)) {
                int progress = intent.getIntExtra("progress",0);
                Log.w(TAG, "onReceive: "+progress );
                if (mediaPlayer != null)
                    mediaPlayer.seekTo(progress*1000);

            }
            //退出服务
            else if (control.equals(Constants.ACTION_ClOSE)) {
                Log.w(TAG, "onReceive: close?" );
                stopSelf();
            }
        }
    }

    //通知栏设置
    @SuppressLint("NewApi")
    private void setNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //点击进入应用
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent intent_go = PendingIntent.getActivity(this, 5, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notice, intent_go);

        //关闭按钮
        Intent close = new Intent();
        close.setAction(Constants.ACTION_ClOSE);
        PendingIntent intent_close = PendingIntent.getBroadcast(this, 4, close,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_close, intent_close);

        //前一首
        Intent prv = new Intent();
        prv.setAction(Constants.ACTION_PRV);
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, prv,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_prev, intent_prev);

        //播放/暂停切换
        if (mediaPlayer.isPlaying()) {
            Intent playerPause = new Intent();
            playerPause.setAction(Constants.ACTION_PAUSE);
            PendingIntent intent_play = PendingIntent.getBroadcast(this, 2,
                    playerPause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);
        }
        if (!mediaPlayer.isPlaying()) {
            Intent playerPause = new Intent();
            playerPause.setAction(Constants.ACTION_PLAY);
            PendingIntent intent_play = PendingIntent.getBroadcast(this, 6,
                    playerPause, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_play, intent_play);
        }


        //下一首
        Intent next = new Intent();
        next.setAction(Constants.ACTION_NEXT);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, next,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_next, intent_next);

        //图标设置
        builder.setSmallIcon(R.drawable.notification_bar_icon);

        Notification notify = builder.build();
        notify.contentView = remoteViews;
        //自定义高度通知栏
        notify.bigContentView = remoteViews;
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notify.icon = R.drawable.notification_bar_icon;
        manager.notify(100, notify);
    }
}
