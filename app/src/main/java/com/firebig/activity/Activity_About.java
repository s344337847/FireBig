package com.firebig.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * Created by FireBig-CH on 16-8-17.
 */
public class Activity_About extends BaseActivity {
    private ListView list;
    private List<Map<String, String>> data = new ArrayList<>();


    @Override
    public void initUI() {
        list = (ListView) findViewById(R.id.about_list);
    }

    @Override
    public void loadData() {
        String[] str = getResources().getStringArray(R.array.about_list);
        for (int i = 0; i < str.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("data", str[i]);
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.about_list_item, new String[]{"data"}, new int[]{R.id.about_list_item_show1});
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 3) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", i - 3);
                    bundle.putString("title", data.get(i).get("data"));
                    goToActivity(Activity_Web.class, bundle, false);
                } else {
                    if (i == 0) {
                        // check update
                        checkUpdate();
                    } else if (i == 1) {
                        // share software
                        shareSoftware();
                    } else {
                        // 意见反馈页面
                        gotoSuggestActivity();
                    }
                }
            }
        });
    }

    /**
     * 检查更新(未进行任何操作)
     */
    private void checkUpdate() {
        toast(getResources().getString(R.string.check_update));
//        FIR.checkForUpdateInFIR("312dfcfd6efde6d80be46dcb820dc57e", new VersionCheckCallback() {
//            @Override
//            public void onSuccess(String s) {
//                Log.d("Version", s);
//
//                super.onSuccess(s);
//            }
//        });

    }

    /**
     * 分享软件操作
     */
    private void shareSoftware() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "我发现一款很有趣到APP，想和你分享哦！下载地址：http://fir.im/5ba7");
        shareIntent.setType("text/plain");
        //设置分享列表的标题，并且每次都显示分享列表
        startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    /**
     * 意见反馈
     */
    private void gotoSuggestActivity() {
        goToActivity(Activity_Suggest.class, null, false);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_about);
        setToolBar(R.string.nva_about);
    }

    @Override
    public void onClick(View view) {

    }


}
