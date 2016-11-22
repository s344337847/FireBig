package com.firebig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebig.util.AppUtil;

/**
 * Created by FireBig-CH on 16-8-16.
 * Activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 当前屏幕的高
     */
    protected int screen_height;
    /**
     * 当前屏幕的宽
     */
    protected int screen_width;

    public static int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        this.screen_width = localDisplayMetrics.widthPixels;
        this.screen_height = localDisplayMetrics.heightPixels;
        width = localDisplayMetrics.widthPixels;
        setContentView();
        initUI();
        loadData();
    }

    /**
     * 加载UI
     */
    public abstract void initUI();

    /**
     * 加载数据
     */
    public abstract void loadData();

    /**
     * 设置layout
     */
    public abstract void setContentView();

    /**
     * go to other Activity
     *
     * @param toclass  terget class
     * @param bundle   with data
     * @param isfinish kill this activity
     */
    public void goToActivity(Class<?> toclass, Bundle bundle, boolean isfinish) {
        if (toclass == null)
            return;
        Intent intent = new Intent();
        if (bundle != null)
            intent.putExtras(bundle);
        intent.setClass(this, toclass);
        startActivity(intent);
        if (isfinish)
            finish();
    }

    /**
     * open new Activity and can Result Data
     *
     * @param toClass
     * @param paramBundle
     */
    public void goToActivityForResult(Class<?> toClass, Bundle paramBundle) {
        if (toClass == null)
            return;
        Intent localIntent = new Intent();
        localIntent.setClass(this, toClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivityForResult(localIntent, 0);
    }

    /**
     * show toast
     *
     * @param msg meessage
     */
    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 是否全屏
     */
//    public void fullScreen() {
//        // judge android system version
//        if (AppUtil.getVersion() > 21) {
//            ActionBar bar = getSupportActionBar();
//            bar.hide();
//        } else {
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//        }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }


    /**
     * setting toobar
     *
     * @param titlenameid
     */
    public void setToolBar(int titlenameid) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(titlenameid));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * save String to SharedPreferences
     *
     * @param sp
     * @param key
     * @param value
     */
    public void saveSharedPreferencesCache(SharedPreferences sp, String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * save String to SharedPreferences
     *
     * @param sp
     * @param key
     * @param value
     */
    public void saveSharedPreferencesCache(SharedPreferences sp, String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}
