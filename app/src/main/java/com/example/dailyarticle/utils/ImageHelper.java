package com.example.dailyarticle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ImageHelper {
    private Jstr str = new Jstr();
    private String TAG = "zzp";
    private String name;
    // 内存缓存池
    // private Map<String, SoftReference<Bitmap>> mCache = new
    // LinkedHashMap<String, SoftReference<Bitmap>>();

    // LRUCahce 池子
    // Used to load the 'native-lib' library on application startup.

    private static LruCache<String, Bitmap> mCache;
    private static Handler mHandler;
    private static ExecutorService mThreadPool;
    private static Map<ImageView, Future<?>> mTaskTags = new LinkedHashMap<ImageView, Future<?>>();
    private Context mContext;

    public ImageHelper(Context context) {
        this.mContext = context;
        if (mCache == null) {
            // 最大使用的内存空间
            int maxSize = (int) (Runtime.getRuntime().freeMemory()/(8*1024));
            if(maxSize<1)maxSize = 1;
            //int maxSize = (int) (Runtime.getRuntime().freeMemory() / 4);
            mCache = new LruCache<String, Bitmap>(maxSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        if (mHandler == null) {
            mHandler = new Handler();
        }

        if (mThreadPool == null) {
            // 最多同时允许的线程数为3个
            mThreadPool = Executors.newFixedThreadPool(3);
        }
    }

    public void display(final ImageView iv, String url) {
        //NDK加密URL作文件名和LRUCache key
        name = str.getJniString(url);

        // 1.去内存中取
        Bitmap bitmap = mCache.get(name);
        if (bitmap != null) {
            // 直接显示
            Log.e(TAG, "display: 内存" );
            final Bitmap bitmapFinal = bitmap;
            //UI线程中设置imageView
            iv.post(new Runnable() {
                @Override
                public void run() {
                    iv.setImageBitmap(bitmapFinal);
                }
            });

            return;
        }

        // 2.去硬盘上取
        bitmap = loadBitmapFromLocal();
        if (bitmap != null) {
            // 直接显示
            final Bitmap bitmapFinal = bitmap;
            //返回UI线程
            iv.post(new Runnable() {
                @Override
                public void run() {
                    iv.setImageBitmap(bitmapFinal);
                }
            });
            Log.e(TAG, "display: 硬盘" );
            return;
        }

        // 3. 去网络获取图片
        loadBitmapFromNet(iv, url);
        Log.e(TAG, "display: 网络" );
    }

    private void loadBitmapFromNet(ImageView iv, String url) {
        // 开线程去网络获取
        // 使用线程池管理
        // new Thread(new ImageLoadTask(iv, url)).start();

        // 判断是否有线程在为 imageView加载数据
        Future<?> futrue = mTaskTags.get(iv);
        if (futrue != null && !futrue.isCancelled() && !futrue.isDone()) {
            // 线程正在执行
            futrue.cancel(true);
        }

        // mThreadPool.execute(new ImageLoadTask(iv, url));
        futrue = mThreadPool.submit(new ImageLoadTask(iv, url));
        // Future 和 callback/Runable
        // 返回值，持有正在执行的线程
        // 保存
        mTaskTags.put(iv, futrue);
    }

    class ImageLoadTask implements Runnable {

        private String mUrl;
        private ImageView iv;

        public ImageLoadTask(ImageView iv, String url) {
            this.mUrl = url;
            this.iv = iv;
        }

        @Override
        public void run() {
            // HttpUrlConnection
            try {
                // 获取连接
                HttpURLConnection conn = (HttpURLConnection) new URL(mUrl).openConnection();
                conn.setConnectTimeout(30 * 1000);// 设置连接服务器超时时间
                conn.setReadTimeout(30 * 1000);// 设置读取响应超时时间
                // 连接网络
                conn.connect();
                // 获取响应码
                int code = conn.getResponseCode();
                if (200 == code) {
                    InputStream is = conn.getInputStream();
                    // 将流转换为bitmap
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    // 存储到本地
                    write2Local(bitmap);
                    // 存储到内存
//                    Log.e(TAG, "run: "+mUrl );
                    mCache.put(name, bitmap);
                    iv.post(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                        }
                    });

                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private Bitmap loadBitmapFromLocal() {
        // 去找文件，将文件转换为bitmap

        try {
            File file = new File(getCacheDir(), name);
            if (file.exists()) {
                //todo 压缩
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                // 存储到内存
                mCache.put(name, bitmap);
//                Log.e(TAG, "run: "+name );
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void write2Local( Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            File file = new File(getCacheDir(), name);
            fos = new FileOutputStream(file);
            // 将图像写到流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    private String getCacheDir() {
        File dir = null;
            // 没有sd卡
            dir = new File(mContext.getCacheDir(), "/icon");

        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir.getAbsolutePath();
    }
}