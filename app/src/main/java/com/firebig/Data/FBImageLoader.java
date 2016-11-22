package com.firebig.Data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by FireBig-CH on 16-8-22.
 */
public class FBImageLoader implements Runnable {
    /**
     * 图片保存路径
     **/
    public static String Path = Environment.getDataDirectory().toString() + File.separator + "data" + File.separator + "com.firebig.activity" + File.separator + "data" + File.separator;
    /**
     * 图片名
     **/
    private String imagename;
    private Context context;
    /**
     * 是否为缩略图
     **/
    private boolean isThumbnail = true;
    /**
     * 缩略图大小
     **/
    private int width = 100;
    private int height = 100;
    /**
     * 加载监听
     **/
    private loadListener loadListener;
    /**
     * download Url
     **/
    private String downurl;

    /**
     * @param context
     * @param imagename
     * @param isThumbnail
     * @param downurl
     * @param small
     */
    public FBImageLoader(Context context, String imagename, boolean isThumbnail, String downurl, boolean small) {
        this.imagename = imagename;
        this.context = context;
        this.isThumbnail = isThumbnail;
        this.downurl = downurl;
        if (small) {
            this.width = 50;
            this.height = 50;
        }
    }

    @Override
    public void run() {
        if (imagename == null) {
            Log.d("Lag", "图片名不能为空");
            return;
        }

        dowmLoadImage();
    }

    /**
     * 没有从本地找到图片 ，进行下载
     */
    private void dowmLoadImage() {
        File file = new File(Path + imagename);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            // 把图片压缩成100x100的缩略图
            BitmapFactory.Options options = new BitmapFactory.Options();
            if (isThumbnail) {
                options.inJustDecodeBounds = true;//不加载bitmap到内存中
                BitmapFactory.decodeFile(Path + imagename, options);
                int outWidth = options.outWidth;
                int outHeight = options.outHeight;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = (outWidth / width + outHeight / height) / 2;
                options.inJustDecodeBounds = false;//把bitmap重新加载到内存中
            }
            Bitmap bitmap = BitmapFactory.decodeFile(Path + imagename, options);
            if (bitmap == null) {
                return;
            }
            if (loadListener != null)
                loadListener.done(bitmap);

        } else {
            // 从网络上获取图片
            URL url = null;
            try {
                url = new URL(downurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                if (conn.getResponseCode() == 200) {

                    InputStream is = conn.getInputStream();
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();

                    dowmLoadImage();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param loadListener
     */
    public void download(loadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface loadListener {
        void done(Bitmap bitmap);
    }
}
