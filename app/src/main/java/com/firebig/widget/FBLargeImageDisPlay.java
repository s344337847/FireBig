package com.firebig.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.PinchImageView.PinchImageView;
import com.firebig.Data.FBImageLoader;
import com.firebig.Data.FileUtil;
import com.firebig.activity.R;

/**
 * Created by FireBig-CH on 16-9-11.
 * 显示大图类
 */
public class FBLargeImageDisPlay implements View.OnClickListener, Animation.AnimationListener {

    /**
     * 上下文
     */
    private Context context;

    /**
     * 图片Uri
     */
    private String Url;
    /**
     * 图片名
     */
    private String im_name;

    /**
     * View
     */
    private View rootView;

    private LinearLayout show_high_image_layout;
    private PinchImageView high_image;

    /**
     * 加载图片
     */
    private FBImageLoader loader;

    /**
     * 消息处理
     */
    private FBLargeHander hander;
    /**
     * 加载进度
     */
    private ProgressBar home_show_large_progressBar;

    /**
     * 位图
     */
    private Bitmap bitmap;

    /**
     * 动画
     */
    private ScaleAnimation show;
    private AnimationSet hide;
    private AlphaAnimation hide1;

    private class FBLargeHander extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setBitmapToView();
        }
    }

    /**
     * 构造器
     *
     * @param context
     */
    public FBLargeImageDisPlay(@NonNull Context context) {
        this.context = context;
        initAnimation();
    }

    /**
     * 图片下载地址url
     *
     * @param url
     */
    public void setUrl(@NonNull String url, @NonNull String im_name) {
        this.Url = url;
        this.im_name = im_name;
    }

    /**
     * 获取视图
     *
     * @return
     */
    public View getRootView() {
        if (rootView == null) {
            rootView = LayoutInflater.from(context).inflate(R.layout.home_show_image, null);
        }
        return rootView;
    }

    /**
     * 实例动画
     */
    private void initAnimation() {
        hander = new FBLargeHander();
        show = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        show.setDuration(300);

        hide = new AnimationSet(false);
        hide1 = new AlphaAnimation(1, 0);
        hide1.setDuration(400);
        ScaleAnimation hide2 = new ScaleAnimation(1f, 0.3f, 1f, 0.03f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hide2.setDuration(400);
        hide.addAnimation(hide1);
        hide.addAnimation(hide2);
    }

    /**
     * 显示大图
     */
    public void Show() {
        if (rootView == null)
            return;
        rootView.setVisibility(View.VISIBLE);
        if (show_high_image_layout == null) {
            show_high_image_layout = (LinearLayout) rootView.findViewById(R.id.home_h_i_l);
            high_image = (PinchImageView) rootView.findViewById(R.id.home_show_hightimage);
            high_image.setOnClickListener(this);
            home_show_large_progressBar = (ProgressBar) rootView.findViewById(R.id.home_show_large_progressBar);
            startFBImageLoader();
            show_high_image_layout.setOnClickListener(this);
        } else {
            home_show_large_progressBar.setVisibility(View.VISIBLE);
            startFBImageLoader();
        }
    }

    /**
     * 显示大图
     */
    public void Show(@NonNull String imagerandomName) {
        if (rootView == null)
            return;
        rootView.setVisibility(View.VISIBLE);
        if (show_high_image_layout == null) {
            show_high_image_layout = (LinearLayout) rootView.findViewById(R.id.home_h_i_l);
            high_image = (PinchImageView) rootView.findViewById(R.id.home_show_hightimage);
            high_image.setOnClickListener(this);
            home_show_large_progressBar = (ProgressBar) rootView.findViewById(R.id.home_show_large_progressBar);
            home_show_large_progressBar.setVisibility(View.GONE);
            high_image.setImageBitmap(FileUtil.getBitmapFromApp(imagerandomName, 300, 300));
            high_image.setVisibility(View.VISIBLE);
            high_image.startAnimation(show);
            show_high_image_layout.setOnClickListener(this);
        } else {
            home_show_large_progressBar.setVisibility(View.VISIBLE);
            high_image.setImageBitmap(FileUtil.getBitmapFromApp(imagerandomName, 300, 300));
            high_image.setVisibility(View.VISIBLE);
            high_image.startAnimation(show);
            home_show_large_progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 加载图片
     */
    private void startFBImageLoader() {
        loader = new FBImageLoader(context, im_name, false, Url, false);
        new Thread(loader).start();
        loader.download(new FBImageLoader.loadListener() {
            @Override
            public void done(Bitmap bitmap) {
                FBLargeImageDisPlay.this.bitmap = bitmap;
                hander.sendEmptyMessage(0);
            }
        });
    }

    /**
     * 设置图片
     */
    private void setBitmapToView() {
        if (bitmap != null) {
            home_show_large_progressBar.setVisibility(View.GONE);
            high_image.setImageBitmap(bitmap);
            high_image.setVisibility(View.VISIBLE);
            high_image.startAnimation(show);
        }
    }

    /**
     * 隐藏大图
     */
    public void hide() {
        bitmap = null;
        if (loader != null)
            loader.download(null);
        high_image.startAnimation(hide);
        show_high_image_layout.startAnimation(hide1);
        hide.setAnimationListener(this);
    }

    @Override
    public void onClick(View view) {
        hide();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        rootView.setVisibility(View.GONE);
        high_image.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

