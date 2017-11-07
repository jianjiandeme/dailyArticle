package com.example.dailyarticle.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dailyarticle.R;
import com.example.dailyarticle.Voice.VoiceService;
import com.example.dailyarticle.utils.Constants;
import com.example.dailyarticle.utils.ImageHelper;

import static com.example.dailyarticle.utils.MyApplication.getContext;



public class VoiceDetailActivity extends AppCompatActivity implements View.OnClickListener{

    SeekBar skbProgress;
    private ImageView backImg;
    private TextView Id,Name,Author;
    ActivityReceiver receiver;
    private String voiceName;
    private String voiceId;
    private String voiceAuthor;
    private int duration,position;

    //整个Activity视图的根视图
    View decorView;
    //手指按下时的坐标
    float downX, downY;
    //手机屏幕的宽度和高
    float screenWidth, screenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_voice_deatail);
        // 获得decorView
        decorView = getWindow().getDecorView();
        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        //从上一层获取信息
        Intent intent = getIntent();
        voiceName = intent.getStringExtra("voiceName");
        voiceId= intent.getStringExtra("voiceId");
        voiceAuthor= intent.getStringExtra("voiceAuthor");
        //注册广播接收器
        regFilter();

        //获取view
        Id = (TextView) findViewById(R.id.Id);
        Name = (TextView) findViewById(R.id.Name);
        Author = (TextView) findViewById(R.id.Author);
        backImg = (ImageView) findViewById(R.id.detail_img);
        //通知栏更新

        //启动服务
        initView();
        Intent serviceIntent = new Intent(getContext(), VoiceService.class);
        serviceIntent.putExtra("voiceId",voiceId);
        startService(serviceIntent);

        //
        Button play = (Button) findViewById(R.id.play);
        Button pause = (Button) findViewById(R.id.pause);
        Button stop = (Button) findViewById(R.id.stop);
        Button pri = (Button) findViewById(R.id.pri);
        Button next = (Button) findViewById(R.id.next);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        pri.setOnClickListener(this);
        next.setOnClickListener(this);
        skbProgress = (SeekBar)findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (duration >0)
                    this.progress = progress * duration / seekBar.getMax();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent seekIntent = new Intent();
                seekIntent.setAction(Constants.ACTION_SEEK);
                seekIntent.putExtra("progress",progress);
                sendBroadcast(seekIntent);
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
            if(moveDistanceX > screenWidth / 2){
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

        anim.addUpdateListener((animation) -> {
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);
        });

        // 动画结束时结束当前Activity
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

        });
    }

    /**
     * Activity被滑动到中途时，滑回去~
     */
    private void rebackToLeft(float moveDistanceX){
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(300).start();
    }
    private void initView() {
        //设置显示内容
        String idText = "\n第"+voiceId+"期";
        Id.setText(idText);
        Name.setText(voiceName);
        Author.setText(voiceAuthor);
        new ImageHelper(getContext()).display(backImg,"http://voice.meiriyiwen.com/upload_imgs/"+voiceId+"_img.jpg");
    }

    @Override
    public void onClick(View v) {
        Intent broadcast = new Intent();
        switch (v.getId()) {
            case R.id.play:
                broadcast.setAction(Constants.ACTION_PLAY);
                sendBroadcast(broadcast);
                break;
            case R.id.pause:
                    broadcast.setAction(Constants.ACTION_PAUSE);
                    sendBroadcast(broadcast); // 暂停播放
                break;
            case R.id.stop:
                    broadcast.setAction(Constants.ACTION_STOP);
                    sendBroadcast(broadcast); // 暂停播放
                break;
            case R.id.pri:
                broadcast.setAction(Constants.ACTION_PRV);
                sendBroadcast(broadcast); // 暂停播放
                break;
            case R.id.next:
                broadcast.setAction(Constants.ACTION_NEXT);
                sendBroadcast(broadcast); // 暂停播放
                break;
            default: break;
        }
    }

    //返回键的处理，MainActivity启动模式为singleTask
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode== KeyEvent.KEYCODE_BACK)){
            Intent intent = new Intent(getContext(),MainActivity.class);
            startActivity(intent);
            return true;
        }
        else return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消广播接收
        unregisterReceiver(receiver);
    }


    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context, Intent intent){
            String action = intent.getAction();
            //更新View
            if(action.equals(Constants.ACTION_VIEW)){
                voiceName=intent.getStringExtra("voiceName");
                voiceId=intent.getStringExtra("voiceId");
                voiceAuthor=intent.getStringExtra("voiceAuthor");
                initView();
            }
            //获取duration
            else if(action.equals(Constants.ACTION_DURATION)){
                duration = intent.getIntExtra("duration",0);
                initView();
            }
            //关闭播放器
            else if(action.equals(Constants.ACTION_ClOSE)){
                Intent intent2 = new Intent(getContext(),MainActivity.class);
                startActivity(intent2);
            }
            //更新进度条
            else if(action.equals(Constants.ACTION_TIME)&&!skbProgress.isPressed()){
                position =intent.getIntExtra("position",0);
                if (duration > 0) {
                    long pos = skbProgress.getMax() * position / duration;
                    skbProgress.setProgress((int) pos);
                }
            }
        }
    }

    //注册广播接收器
    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        receiver = new ActivityReceiver();
        filter.addAction(Constants.ACTION_PAUSE);
        filter.addAction(Constants.ACTION_PLAY);
        filter.addAction(Constants.ACTION_VIEW);
        filter.addAction(Constants.ACTION_SEEK);
        filter.addAction(Constants.ACTION_TIME);
        filter.addAction(Constants.ACTION_DURATION);
        filter.addAction(Constants.ACTION_ClOSE);
        filter.setPriority(800);
        registerReceiver(receiver, filter);
    }
}
