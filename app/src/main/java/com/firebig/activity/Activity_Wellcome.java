package com.firebig.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.firebig.util.AppUtil;
import com.firebig.util.CommandUtil;
import com.firebig.util.StringUtils;

import net.youmi.android.AdManager;
import net.youmi.android.normal.spot.SplashViewSettings;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录和展示广告页
 */
public class Activity_Wellcome extends BaseActivity {

    private ImageView header;
    private ImageView show_image;
    private Button login;
    private Button register;
    private static final int GETPERMISSION_SUCCESS = 1;//获取权限成功
    private static final int GETPERMISSION_FAILER = 2;//获取权限失败
    private MyHandler myHandler = new MyHandler();
    private RelativeLayout root_view;

    @Override
    public void initUI() {
        header = (ImageView) findViewById(R.id.well_header);
        show_image = (ImageView) findViewById(R.id.well_title);
        login = (Button) findViewById(R.id.well_btn_login);
        register = (Button) findViewById(R.id.well_btn_register);
        root_view = (RelativeLayout) findViewById(R.id.root_view);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void loadData() {
        // 1318ef6ca171f8a6
        // 730ff37b5592dbd8
        // check frist use
        final String account = getSharedPreferences("account", 0).getString("user_account", null);
        if (account != null) {
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    // go to home Activity
                    if (StringUtils.getNowYear() < 2016) {
                        toast("当前系统时间不正确！可能无法正常使用FireBig！");
                    }
                    goToActivity(Activity_Home.class, null, true);
                }
            };
            timer.schedule(task, 1500);
            return;
        } else {
            login.setVisibility(View.VISIBLE);
            register.setVisibility(View.VISIBLE);
            header.startAnimation(AnimationUtils.loadAnimation(this, R.anim.header_move));
            show_image.startAnimation(AnimationUtils.loadAnimation(this, R.anim.show_image_move));
            login.startAnimation(AnimationUtils.loadAnimation(this, R.anim.well_login_btn_move));
            register.startAnimation(AnimationUtils.loadAnimation(this, R.anim.well_register_btn_move));
        }
    }

    @Override
    public void setContentView() {
        if (AppUtil.getVersion() > 21) {
            ActionBar bar = getSupportActionBar();
            bar.hide();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_wellcome);
        // Android 6.0 Permission
        if (AppUtil.getVersion() >= 23)
            requestAllPermission();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GETPERMISSION_SUCCESS:
                    break;
                case GETPERMISSION_FAILER:
                    toast("");
                    break;
            }
        }
    }

    private void requestAllPermission() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(Activity_Wellcome.this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                myHandler.sendEmptyMessage(GETPERMISSION_SUCCESS);
            }

            @Override
            public void onDenied(String permission) {
                myHandler.sendEmptyMessage(GETPERMISSION_FAILER);
            }
        });
    }

    //因为权限管理类无法监听系统，所以需要重写onRequestPermissionResult方法，更新权限管理类，并回调结果。这个是必须要有的。
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.well_btn_login:
                goToActivity(Activity_Login.class, null, true);
                break;
            case R.id.well_btn_register:
                goToActivityForResult(Activity_Register.class, null);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CommandUtil.RESULCODE_FINISH) {
            // colose this Activity
            finish();
        }
    }
}
